package com.example.exploro.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.os.Bundle;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploro.data.repositories.AttractionRemoteDataSource;
import com.example.exploro.data.models.Attraction;
import com.example.exploro.data.repositories.DestinationRemoteDataSource;
import com.example.exploro.domain.ItineraryTripPlanner;
import com.example.exploro.data.models.Trip;
import com.example.exploro.databinding.ActivityItineraryBinding;
import com.example.exploro.R;

import com.example.exploro.domain.MapManager;
import com.example.exploro.ui.adapters.ItineraryListAdapter;
import com.example.exploro.data.repositories.TripRemoteDataSource;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItineraryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final List<Attraction> selectedAttractions = new ArrayList<>();
    private ItineraryListAdapter itineraryListAdapter;
    private ActivityItineraryBinding binding;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = ItineraryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItineraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Places.initialize(getApplicationContext(), "AIzaSyBdG4NHb0oSCpAifUzVyho8Mdc-OzKyj8c");

        Intent intent = getIntent();
        Trip trip = (Trip) intent.getSerializableExtra("trip");

        if (trip != null) {

            String destinationID = trip.getDestinationID();
            String startDate = trip.getStartDate();
            List<String> selectedAttractionsID = trip.getSelectedAttractions();
            TextView destinationTextView = binding.destinationTextView;

            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            DestinationRemoteDataSource.fetchDestinationName(destinationID, destinationTextView, null);

            DatabaseReference mAttractionsReference = FirebaseDatabase.getInstance().getReference().child("planning_data/" + destinationID);

            mAttractionsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        selectedAttractions.clear();

                        AttractionRemoteDataSource.fetchAttractionPlanningData(ItineraryActivity.this, dataSnapshot, destinationTextView.getText().toString() , selectedAttractionsID)
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
                                            ItineraryActivity.this.mMap = googleMap;
                                            MapManager.addMarkersToMap(googleMap, itinerary);
                                        });
                                        mapFragment.getMapAsync(ItineraryActivity.this);
                                    }
                                })
                                .exceptionally(throwable -> {
                                    Log.e(TAG, "Error fetching data", throwable);
                                    return null;
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("DATABASE", "Failed to get database data.", error.toException());
                }
            });

            binding.saveButton.setOnClickListener(v -> TripRemoteDataSource.saveTrip(this, trip, selectedAttractions));
        }
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;
        MapManager.enableMyLocation(this, mMap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MapManager.enableMyLocation(this, mMap);
            }
        }
    }
}