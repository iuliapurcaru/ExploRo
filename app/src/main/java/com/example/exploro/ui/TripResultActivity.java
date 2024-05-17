package com.example.exploro.ui;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.exploro.AttractionInfo;
import com.example.exploro.ItineraryPlanner;
import com.example.exploro.R;
import com.example.exploro.databinding.ActivityTripResultBinding;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TripResultActivity extends AppCompatActivity {

    private final List<AttractionInfo> selectedAttractions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_result);

        ActivityTripResultBinding binding = ActivityTripResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String destinationID = intent.getStringExtra("destination");
        ArrayList<String> selectedAttractionsID = intent.getStringArrayListExtra("selectedAttractions");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        int numberOfDays = intent.getIntExtra("numberOfDays", 0);

        DatabaseReference mAttractionsReference = FirebaseDatabase.getInstance().getReference().child("planning_data/" + destinationID);
        mAttractionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.e("TripResultActivity", "Error reading data from Firebase", databaseError.toException());
            }
        });

        final Button testButton = binding.testButton1;

        testButton.setOnClickListener(v -> {
            ItineraryPlanner planner = new ItineraryPlanner(selectedAttractions, numberOfDays);
            List<List<AttractionInfo>> itinerary = planner.planItinerary();

            for (int i = 0; i < itinerary.size(); i++) {
                Log.d("TripResultActivity", "Day " + (i + 1) + ":");
                for (AttractionInfo attraction : itinerary.get(i)) {
                    Log.d("TripResultActivity", "- " + attraction.getName());
                }
            }
        });
    }

    private <T> T getValueOrDefault(DataSnapshot snapshot, Class<T> clazz, T defaultValue) {
        T value = snapshot.getValue(clazz);
        return (value != null) ? value : defaultValue;
    }
}