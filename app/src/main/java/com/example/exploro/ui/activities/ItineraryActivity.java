package com.example.exploro.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.os.Bundle;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploro.domain.AttractionManager;
import com.example.exploro.models.Attraction;
import com.example.exploro.domain.ItineraryTripPlanner;
import com.example.exploro.models.Trip;
import com.example.exploro.databinding.ActivityItineraryBinding;
import com.example.exploro.R;

import com.example.exploro.ui.adapters.ItineraryListAdapter;
import com.example.exploro.domain.TripManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItineraryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final List<Attraction> selectedAttractions = new ArrayList<>();
    private ItineraryListAdapter itineraryListAdapter;
    private ActivityItineraryBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItineraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Places.initialize(getApplicationContext(), "AIzaSyBdG4NHb0oSCpAifUzVyho8Mdc-OzKyj8c");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        Trip trip = intent.getSerializableExtra("trip", Trip.class);

        if (trip != null) {

            String destinationID = trip.getDestinationID();
            String startDate = trip.getStartDate();
            List<String> selectedAttractionsID = trip.getSelectedAttractions();
            TextView destinationTextView = binding.destinationTextView;

            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            DatabaseReference mAttractionsReference = FirebaseDatabase.getInstance().getReference().child("planning_data/" + destinationID);
            mAttractionsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        selectedAttractions.clear();

                        String destinationName = dataSnapshot.child("name").getValue(String.class);
                        destinationTextView.setText(destinationName);

                        AttractionManager.fetchAttractionItineraryData(ItineraryActivity.this, dataSnapshot, destinationName, selectedAttractionsID)
                                .thenAccept(attractions -> {
                                    selectedAttractions.addAll(attractions);
                                    ItineraryTripPlanner planner = new ItineraryTripPlanner(trip, attractions);
                                    List<List<Attraction>> itinerary = planner.planItinerary();

                                    itineraryListAdapter = new ItineraryListAdapter(itinerary, startDate);
                                    recyclerView.setAdapter(itineraryListAdapter);

                                    double totalPrice = planner.calculateTotalPrice();
                                    String totalPriceText = "Total Price: " + totalPrice + " RON";
                                    binding.totalPriceTextView.setText(totalPriceText);

                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                    if (mapFragment != null) {
                                        mapFragment.getMapAsync(googleMap -> {
                                            ItineraryActivity.this.googleMap = googleMap;
                                            addMarkersToMap(itinerary);
                                        });
                                        mapFragment.getMapAsync(ItineraryActivity.this);
                                    }
                                })
                                .exceptionally(throwable -> {
                                    Log.e("AttractionFetcher", "Error fetching data", throwable);
                                    return null;
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("DATABASE", "Failed to get database data.", error.toException());
                }
            });

            binding.saveButton.setOnClickListener(v -> TripManager.saveTrip(this, trip, selectedAttractions));
        }
    }

    @Override
    public void onMapReady(@NotNull GoogleMap mMap) {
        googleMap = mMap;
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
//                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng currentLatLng = new LatLng(45.641925, 25.589182);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here!"));
                    }
                } else {
                    Log.d("ItineraryActivity", "Current location is null. Using defaults.");
                    LatLng defaultLatLng = new LatLng(-34, 151);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15));
                    googleMap.addMarker(new MarkerOptions().position(defaultLatLng).title("Default Location"));
                }
            });
        } catch (SecurityException e) {
            Log.e("ItineraryActivity", "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }


    private void addMarkersToMap(List<List<Attraction>> itinerary) {
        if (googleMap != null) {
            googleMap.clear();
            LatLng lastPosition = null;
            for (List<Attraction> dayPlan : itinerary) {
                for (Attraction attraction : dayPlan) {
                    int hours = (int) attraction.getVisitTime();
                    int minutes = (int) ((attraction.getVisitTime() - hours) * 60);
                    String formattedVisitTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                    LatLng location = new LatLng(attraction.getLatitude(), attraction.getLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(attraction.getName())
                            .snippet("Day: " + attraction.getVisitDay() + " - " + attraction.getVisitDate() + " - Visit time: " + formattedVisitTime)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    lastPosition = location;
                }
            }
            if (lastPosition != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(lastPosition)
                        .zoom(15)
                        .tilt(30)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if (!itinerary.isEmpty() && !itinerary.get(0).isEmpty()) {
                LatLng firstAttraction = new LatLng(itinerary.get(0).get(0).getLatitude(), itinerary.get(0).get(0).getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstAttraction, 15));
            }
        }
    }
}