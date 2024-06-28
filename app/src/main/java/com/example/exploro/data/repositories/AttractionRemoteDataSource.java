package com.example.exploro.data.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.exploro.domain.AttractionManager;
import com.example.exploro.data.models.Attraction;
import com.example.exploro.utils.VariousUtils;

import com.example.exploro.databinding.ActivityPlanningBinding;
import com.example.exploro.ui.adapters.PlanningAttractionsAdapter;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.*;

import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AttractionRemoteDataSource {

    private static final List<String> attractionsNames = new ArrayList<>();
    private static final List<String> attractionsIDs = new ArrayList<>();
    private static final String TAG = AttractionRemoteDataSource.class.getSimpleName();

    public static void displayAttractions(String destinationID, List<String> selectedAttractions, ActivityPlanningBinding binding, androidx.recyclerview.widget.RecyclerView recyclerView) {

        DatabaseReference mDestinationNameReference = FirebaseDatabase.getInstance().getReference("destinations/" + destinationID + "/name");

        mDestinationNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String destinationName = "Plan your trip to " + dataSnapshot.getValue(String.class);
                    TextView destinationTextView = binding.textViewDestination;
                    destinationTextView.setText(destinationName);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        attractionsIDs.clear();
        attractionsNames.clear();

        DatabaseReference mAttractionsReference = FirebaseDatabase.getInstance().getReference("attractions/" + destinationID);
        mAttractionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String attractionID = snapshot.getKey();
                        attractionsIDs.add(attractionID);
                        String attractionName = snapshot.child("name").getValue(String.class);
                        attractionsNames.add(attractionName);
                    }
                    PlanningAttractionsAdapter adapter = new PlanningAttractionsAdapter(attractionsNames, selectedAttractions, binding.overlay, destinationID, attractionsIDs);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });
    }

    public static void fetchAttractionDetails(String destinationID, String attractionID, TextView nameTextView,
                                              TextView descriptionTextView, TextView timeSpentTextView,
                                              TextView attractionAdultPriceTextView, TextView studentPriceTextView,
                                              TextView addressTextView, TextView hoursTextView, TextView linkTextView) {

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
                    String attractionLink = dataSnapshot.child("link").getValue(String.class);

                    if (attractionLink != null) {
                        AttractionManager.setHyperlink(linkTextView.getContext(), linkTextView, attractionLink);
                    } else {
                        linkTextView.setText("");
                    }

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

    public static CompletableFuture<List<Attraction>> fetchAttractionPlanningData(Context context, DataSnapshot tripSnapshot, String destination, List<String> selectedAttractionsID) {
        List<CompletableFuture<Attraction>> selectedAttractions = new ArrayList<>();
        PlacesClient placesClient = Places.createClient(context);

        for (DataSnapshot snapshot : tripSnapshot.getChildren()) {
            String attractionID = snapshot.getKey();
            if (selectedAttractionsID.contains(attractionID)) {
                String attractionName = snapshot.child("name").getValue(String.class);
                String attractionWithDestination = attractionName + " " + destination;
                Log.d(TAG, "Fetching attraction: " + attractionWithDestination);
                int adultPrice = VariousUtils.getValueOrDefault(snapshot.child("price").child("adult"), Integer.class, -1);
                int studentPrice = VariousUtils.getValueOrDefault(snapshot.child("price").child("student"), Integer.class, -1);
                double timeSpent = VariousUtils.getValueOrDefault(snapshot.child("time"), Double.class, 0.0);
                double[] openingHours = new double[7];
                double[] closingHours = new double[7];
                String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
                for (int i = 0; i < days.length; i++) {
                    openingHours[i] = VariousUtils.getValueOrDefault(snapshot.child("hours").child(days[i]).child("opening"), Double.class, 0.0);
                    closingHours[i] = VariousUtils.getValueOrDefault(snapshot.child("hours").child(days[i]).child("closing"), Double.class, 0.0);
                }

                CompletableFuture<Attraction> attraction = AttractionManager.fetchPlaceDetailsByName(placesClient, attractionWithDestination)
                        .thenApply(place -> {
                            double latitude = 0.0;
                            double longitude = 0.0;

                            if (place.getLatLng() != null) {
                                latitude = place.getLatLng().latitude;
                                longitude = place.getLatLng().longitude;
                            }

                            return new Attraction(attractionID, attractionName, openingHours, closingHours, adultPrice, studentPrice, timeSpent, latitude, longitude);
                        });

                selectedAttractions.add(attraction);
            }
        }

        return CompletableFuture.allOf(selectedAttractions.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<Attraction> attractions = new ArrayList<>();
                    for (CompletableFuture<Attraction> future : selectedAttractions) {
                        try {
                            attractions.add(future.get());
                        } catch (InterruptedException | ExecutionException e) {
                            Log.e(TAG, "Error fetching attraction", e);
                        }
                    }
                    return attractions;
                });
    }
}
