package com.example.exploro;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.exploro.databinding.ActivityTripResultBinding;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TripResultActivity extends AppCompatActivity {

    private final List<AttractionsInfo> attractions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_result);

        ActivityTripResultBinding binding = ActivityTripResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String destinationID = intent.getStringExtra("destination");
        ArrayList<String> selectedAttractions = intent.getStringArrayListExtra("selectedAttractions");
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
                        if (selectedAttractions.contains(attractionID)) {
                            String attractionName = snapshot.child("name").getValue(String.class);
                            int openingHours = snapshot.child("hours").child("opening").getValue(Integer.class);
                            int closingHours = snapshot.child("hours").child("closing").getValue(Integer.class);
                            int adultPrice = snapshot.child("price").child("adult").getValue(Integer.class);
                            int studentPrice = snapshot.child("price").child("student").getValue(Integer.class);
                            int timeSpent = snapshot.child("time").getValue(Integer.class);
                            double latitude = snapshot.child("coordinates").child("lat").getValue(Double.class);
                            double longitude = snapshot.child("coordinates").child("long").getValue(Double.class);

                            AttractionsInfo attraction = new AttractionsInfo(attractionID, attractionName, openingHours, closingHours, adultPrice, studentPrice, timeSpent, latitude, longitude);
                            attractions.add(attraction);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.e("TripResultActivity", "Error reading data from Firebase", databaseError.toException());
            }
        });
    }
}