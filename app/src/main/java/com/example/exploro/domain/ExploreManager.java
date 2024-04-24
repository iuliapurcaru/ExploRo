package com.example.exploro.domain;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploro.ui.adapters.ExploreDestinationsAdapter;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExploreManager {

    private static final List<String> destinationsImageUrls = new ArrayList<>();
    private static final List<String> destinationsIDs = new ArrayList<>();

    public static void displayDestinationsImageUrls(RecyclerView recyclerView) {

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
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });
    }
}
