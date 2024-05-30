package com.example.exploro.ui.planning;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploro.AttractionInfo;
import com.example.exploro.ItineraryTripPlanner;
import com.example.exploro.databinding.ActivityItineraryBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItineraryActivity extends AppCompatActivity {

    private final List<AttractionInfo> selectedAttractions = new ArrayList<>();
    private ItineraryAdapter itineraryAdapter;
    private ActivityItineraryBinding binding;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItineraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        }

        Intent intent = getIntent();
        String destinationID = intent.getStringExtra("destination");
        ArrayList<String> selectedAttractionsID = intent.getStringArrayListExtra("selectedAttractions");
        String startDate = intent.getStringExtra("startDate");
        int numberOfDays = intent.getIntExtra("numberOfDays", 0);
        int numberOfAdults = intent.getIntExtra("numberOfAdults", 0);
        int numberOfStudents = intent.getIntExtra("numberOfStudents", 0);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference mAttractionsReference = FirebaseDatabase.getInstance().getReference().child("planning_data/" + destinationID);
        mAttractionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    selectedAttractions.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String attractionID = snapshot.getKey();
                        if (selectedAttractionsID.contains(attractionID)) {
                            String attractionName = snapshot.child("name").getValue(String.class);
                            double openingHours = getValueOrDefault(snapshot.child("hours").child("opening"), Double.class, 0.0);
                            double closingHours = getValueOrDefault(snapshot.child("hours").child("closing"), Double.class, 0.0);
                            int adultPrice = getValueOrDefault(snapshot.child("price").child("adult"), Integer.class, -1);
                            int studentPrice = getValueOrDefault(snapshot.child("price").child("student"), Integer.class, -1);
                            double timeSpent = getValueOrDefault(snapshot.child("time"), Double.class, 0.0);
                            double latitude = getValueOrDefault(snapshot.child("coordinates").child("lat"), Double.class, 0.0);
                            double longitude = getValueOrDefault(snapshot.child("coordinates").child("long"), Double.class, 0.0);

                            AttractionInfo attraction = new AttractionInfo(attractionID, attractionName, openingHours, closingHours, adultPrice, studentPrice, timeSpent, latitude, longitude);
                            selectedAttractions.add(attraction);
                        }
                    }

                    ItineraryTripPlanner planner = new ItineraryTripPlanner(selectedAttractions, numberOfDays, startDate, numberOfAdults, numberOfStudents);
                    List<List<AttractionInfo>> itinerary = planner.planItinerary();

                    itineraryAdapter = new ItineraryAdapter(itinerary, startDate);
                    recyclerView.setAdapter(itineraryAdapter);

                    double totalPrice = planner.calculateTotalPrice();
                    String totalPriceText = "Total Price: " + totalPrice + " RON";
                    binding.totalPriceTextView.setText(totalPriceText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        binding.saveButton.setOnClickListener(v -> saveTripPlan());
    }

    private <T> T getValueOrDefault(DataSnapshot snapshot, Class<T> clazz, T defaultValue) {
        T value = snapshot.getValue(clazz);
        return (value != null) ? value : defaultValue;
    }

    private void saveTripPlan() {

        String tripId = userRef.child("trips").push().getKey();
        if (tripId == null) {
            Toast.makeText(ItineraryActivity.this, "Failed to generate trip ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> tripPlan = new HashMap<>();
        tripPlan.put("startDate", getIntent().getStringExtra("startDate"));
        tripPlan.put("endDate", getIntent().getStringExtra("endDate"));
        tripPlan.put("numberOfAdults", getIntent().getIntExtra("numberOfAdults", 0));
        tripPlan.put("numberOfStudents", getIntent().getIntExtra("numberOfStudents", 0));
        tripPlan.put("totalPrice", binding.totalPriceTextView.getText().toString());

        List<String> attractionsNames = new ArrayList<>();
        for (AttractionInfo attraction : selectedAttractions) {
            attractionsNames.add(attraction.getId());
        }
        tripPlan.put("selectedAttractions", attractionsNames);

        String destinationID = getIntent().getStringExtra("destination");
        DatabaseReference destinationRef = FirebaseDatabase.getInstance().getReference().child("destinations/" + destinationID + "/text");

        destinationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String destinationName = snapshot.getValue(String.class);
                    tripPlan.put("destination", destinationName);

                    userRef.child("trips").child(tripId).setValue(tripPlan)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ItineraryActivity.this, "Trip plan saved successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ItineraryActivity.this, "Failed to save trip plan.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get destination name.", error.toException());
            }
        });
    }
}
