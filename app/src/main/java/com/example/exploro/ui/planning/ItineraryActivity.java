package com.example.exploro.ui.planning;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.AttractionInfo;
import com.example.exploro.ItineraryPlanner;
import com.example.exploro.databinding.ActivityTripResultBinding;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ItineraryActivity extends AppCompatActivity {

    private final List<AttractionInfo> selectedAttractions = new ArrayList<>();
    private ItineraryAdapter itineraryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.exploro.databinding.ActivityTripResultBinding binding = ActivityTripResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String destinationID = intent.getStringExtra("destination");
        ArrayList<String> selectedAttractionsID = intent.getStringArrayListExtra("selectedAttractions");
        int numberOfDays = intent.getIntExtra("numberOfDays", 0);

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

                    // Plan the itinerary once the data is loaded
                    ItineraryPlanner planner = new ItineraryPlanner(selectedAttractions, numberOfDays);
                    List<List<AttractionInfo>> itinerary = planner.planItinerary();

                    itineraryAdapter = new ItineraryAdapter(itinerary);
                    recyclerView.setAdapter(itineraryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TripResultActivity", "Error reading data from Firebase", databaseError.toException());
            }
        });
    }

    private <T> T getValueOrDefault(DataSnapshot snapshot, Class<T> clazz, T defaultValue) {
        T value = snapshot.getValue(clazz);
        return (value != null) ? value : defaultValue;
    }
}
