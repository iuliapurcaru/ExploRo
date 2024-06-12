package com.example.exploro.domain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.*;

import androidx.annotation.NonNull;
import com.example.exploro.models.Attraction;
import com.example.exploro.utils.VariousUtils;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AttractionManager {

    private static final String TAG = AttractionManager.class.getSimpleName();

    public static void fetchAttractionPlanningDetails(String destinationID, String attractionID, TextView nameTextView,
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
                        setHyperlink(linkTextView.getContext(), linkTextView, attractionLink);
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

    private static void setHyperlink(final Context context, TextView textView, final String url) {
        SpannableString spannableString = new SpannableString("Visit the official website");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static CompletableFuture<List<Attraction>> fetchAttractionItineraryData(Context context, DataSnapshot tripSnapshot, String destination, List<String> selectedAttractionsID) {
        List<CompletableFuture<Attraction>> selectedAttractions = new ArrayList<>();
        PlacesClient placesClient = Places.createClient(context);

        for (DataSnapshot snapshot : tripSnapshot.getChildren()) {
            String attractionID = snapshot.getKey();
            if (selectedAttractionsID.contains(attractionID)) {
                String attractionName = snapshot.child("name").getValue(String.class);
                String attractionWithDestination = attractionName + " " + destination;
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

                CompletableFuture<Attraction> attraction = fetchPlaceDetailsByName(placesClient, attractionWithDestination)
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

    private static CompletableFuture<Place> fetchPlaceDetailsByName(PlacesClient placesClient, String placeName) {
        CompletableFuture<Place> completableFuture = new CompletableFuture<>();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(placeName)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            if (!response.getAutocompletePredictions().isEmpty()) {
                String placeId = response.getAutocompletePredictions().get(0).getPlaceId();
                fetchPlaceDetailsById(placesClient, placeId).thenAccept(completableFuture::complete)
                        .exceptionally(ex -> {
                            completableFuture.completeExceptionally(ex);
                            return null;
                        });
            } else {
                completableFuture.completeExceptionally(new Exception("Place not found"));
            }
        }).addOnFailureListener(completableFuture::completeExceptionally);

        return completableFuture;
    }

    private static CompletableFuture<Place> fetchPlaceDetailsById(PlacesClient placesClient, String placeId) {
        CompletableFuture<Place> completableFuture = new CompletableFuture<>();

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.OPENING_HOURS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request)
                .addOnSuccessListener(fetchPlaceResponse -> completableFuture
                        .complete(fetchPlaceResponse.getPlace())).addOnFailureListener(completableFuture::completeExceptionally);

        return completableFuture;
    }
}
