package com.example.exploro.ui.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.exploro.databinding.FragmentExploreBinding;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private String[] destinationsArray;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExploreViewModel exploreViewModel = new ViewModelProvider(this).get(ExploreViewModel.class);

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DatabaseReference mDestinationsReference = FirebaseDatabase.getInstance().getReference().child("destinations");

        mDestinationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> destinationNames = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String destinationName = snapshot.getKey();
                        destinationNames.add(destinationName);
                    }
                    destinationsArray = destinationNames.toArray(new String[0]);
                    handleDestinationArray();
                } else {
                    Log.w("DATABASE DESTINATIONS", "Failed to get destinations from database.");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        return root;
    }

        private void handleDestinationArray() {
        if (destinationsArray != null) {

            ImageView[] imageDestination = new ImageView[10];

            for (int i = 0; i < 1; i++) {
                imageDestination[i] = binding.imageView1;
            }

            for (int i = 0; i < 1; i++) {
                int iFinal = i;
                DatabaseReference mTripsImageReference = FirebaseDatabase.getInstance().getReference("destinations/" + destinationsArray[i] + "/image");
                mTripsImageReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String imageUrl = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                            Picasso.get().load(imageUrl).into(imageDestination[iFinal]);
                        } else {
                            Log.w("DATABASE IMAGE", "Failed to get image from database.");
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {
                        Log.w("DATABASE", "Failed to get database data.", error.toException());
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}