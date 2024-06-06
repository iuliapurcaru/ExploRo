package com.example.exploro.ui.activities;

import android.content.Intent;
import android.util.Log;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.*;

import java.util.*;

public class ItineraryActivity extends AppCompatActivity {

    private List<Attraction> selectedAttractions = new ArrayList<>();
    private ItineraryListAdapter itineraryListAdapter;
    private ActivityItineraryBinding binding;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItineraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Trip trip = intent.getSerializableExtra("trip", Trip.class);

        if (trip != null) {

            String destinationID = trip.getDestinationID();
            String startDate = trip.getStartDate();
            List<String> selectedAttractionsID = trip.getSelectedAttractions();

            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            DatabaseReference mAttractionsReference = FirebaseDatabase.getInstance().getReference().child("planning_data/" + destinationID);
            mAttractionsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        selectedAttractions.clear();
                        selectedAttractions = AttractionManager.fetchAttractionItineraryData(dataSnapshot, selectedAttractionsID);

                        ItineraryTripPlanner planner = new ItineraryTripPlanner(trip, selectedAttractions);
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
                        }
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