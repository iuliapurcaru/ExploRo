package com.example.exploro.domain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AttractionManager {

    public static CompletableFuture<Place> fetchPlaceDetailsByName(PlacesClient placesClient, String placeName) {
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

    public static void setHyperlink(final Context context, android.widget.TextView textView, final String url) {
        SpannableString spannableString = new SpannableString("Visit the official website");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull android.view.View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
