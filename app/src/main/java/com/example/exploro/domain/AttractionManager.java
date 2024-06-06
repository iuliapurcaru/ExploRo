package com.example.exploro.domain;

import android.util.Log;
import android.widget.TextView;

import com.example.exploro.models.Attraction;
import com.example.exploro.utils.VariousUtils;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AttractionManager {

    public static void fetchAttractionPlanningDetails(String destinationID, String attractionID, TextView nameTextView,
                                                      TextView descriptionTextView, TextView timeSpentTextView,
                                                      TextView attractionAdultPriceTextView, TextView studentPriceTextView,
                                                      TextView addressTextView, TextView hoursTextView) {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mAttractionsReference = mDatabase.getReference("attractions/" + destinationID + "/" + attractionID);

        mAttractionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String attractionName = dataSnapshot.child("name").getValue(String.class);
                    String attractionDescription = dataSnapshot.child("description").getValue(String.class);
                    String attractionTime = timeSpentTextView.getText() + dataSnapshot.child("time").getValue(String.class);
                    String attractionAdultPrice = attractionAdultPriceTextView.getText() + dataSnapshot.child("prices/adult").getValue(String.class);
                    String attractionStudentPrice = studentPriceTextView.getText() + dataSnapshot.child("prices/student").getValue(String.class);
                    String attractionAddress = addressTextView.getText() + dataSnapshot.child("address").getValue(String.class);

                    String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
                    StringBuilder hoursBuilder = new StringBuilder();
                    for (String day : days) {
                        String hoursString = dataSnapshot.child("hours").child(day).getValue(String.class);
                        if (hoursString != null) {
                            hoursBuilder.append(day.substring(0, 1).toUpperCase())
                                    .append(day.substring(1))
                                    .append(": ")
                                    .append(hoursString)
                                    .append("\n");
                        }
                    }
                    String attractionHours = hoursBuilder.toString().trim();

                    nameTextView.setText(attractionName);
                    descriptionTextView.setText(attractionDescription);
                    timeSpentTextView.setText(attractionTime);
                    attractionAdultPriceTextView.setText(attractionAdultPrice);
                    studentPriceTextView.setText(attractionStudentPrice);
                    addressTextView.setText(attractionAddress);
                    hoursTextView.setText(attractionHours);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });
    }

    public static List<Attraction> fetchAttractionItineraryData(DataSnapshot tripSnapshot, List<String> selectedAttractionsID) {
        List<Attraction> selectedAttractions = new ArrayList<>();
        for (DataSnapshot snapshot : tripSnapshot.getChildren()) {
            String attractionID = snapshot.getKey();
            if (selectedAttractionsID.contains(attractionID)) {

                double[] openingHours = new double[7];
                double[] closingHours = new double[7];
                String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
                for (int i = 0; i < days.length; i++) {
                    openingHours[i] = VariousUtils.getValueOrDefault(snapshot.child("hours").child(days[i]).child("opening"), Double.class, 0.0);
                    closingHours[i] = VariousUtils.getValueOrDefault(snapshot.child("hours").child(days[i]).child("closing"), Double.class, 0.0);
                }

                String attractionName = snapshot.child("name").getValue(String.class);
                int adultPrice = VariousUtils.getValueOrDefault(snapshot.child("price").child("adult"), Integer.class, -1);
                int studentPrice = VariousUtils.getValueOrDefault(snapshot.child("price").child("student"), Integer.class, -1);
                double timeSpent = VariousUtils.getValueOrDefault(snapshot.child("time"), Double.class, 0.0);
                double latitude = VariousUtils.getValueOrDefault(snapshot.child("coordinates").child("lat"), Double.class, 0.0);
                double longitude = VariousUtils.getValueOrDefault(snapshot.child("coordinates").child("long"), Double.class, 0.0);

                Attraction attraction = new Attraction(attractionID, attractionName, openingHours, closingHours, adultPrice, studentPrice, timeSpent, latitude, longitude);
                selectedAttractions.add(attraction);
            }
        }

        return selectedAttractions;
    }

}
