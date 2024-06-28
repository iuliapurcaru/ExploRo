package com.example.exploro.data.repositories;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploro.ui.adapters.ExploreDestinationsAdapter;
import com.example.exploro.ui.adapters.HomeSavedTripsAdapter;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DestinationRemoteDataSource {

    private static final List<String> destinationsImageUrls = new ArrayList<>();
    private static final List<String> destinationsIDs = new ArrayList<>();

    public static void displayDestinationsImageUrls(RecyclerView recyclerView) {

        destinationsImageUrls.clear();
        destinationsIDs.clear();

        DatabaseReference mDestinationsImageReference = FirebaseDatabase.getInstance().getReference().child("destinations");
        mDestinationsImageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String destinationID = snapshot.getKey();
                        destinationsIDs.add(destinationID);
                        String destinationImageUrl = snapshot.child("image").getValue(String.class);
                        destinationsImageUrls.add(destinationImageUrl);
                    }
                    ExploreDestinationsAdapter adapter = new ExploreDestinationsAdapter(destinationsImageUrls, destinationsIDs);
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

    public static void fetchDestinationName(String destinationID, android.widget.TextView destinationTextView, HomeSavedTripsAdapter.TripViewHolder holder) {

        DatabaseReference destinationReference = FirebaseDatabase.getInstance().getReference("destinations/" + destinationID + "/name");
        destinationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String destination = dataSnapshot.getValue(String.class);
                    if (holder == null) {
                        destinationTextView.setText(destination);
                    } else {
                        holder.textDestination.setText(destination);
                    }
                } else {
                    Log.w("DATABASE", "Failed to get destination data.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("DATABASE", "Failed to get database data.", databaseError.toException());
            }
        });
    }
}








