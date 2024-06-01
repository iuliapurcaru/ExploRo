package com.example.exploro.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exploro.TripInfo;
import com.example.exploro.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MutableLiveData<String> displayNameLiveData;
    private MutableLiveData<String> tripsLiveData;
    private TripAdapter tripAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        displayNameLiveData = new MutableLiveData<>();
        tripsLiveData = new MutableLiveData<>();
        List<TripInfo> tripList = new ArrayList<>();
        tripAdapter = new TripAdapter(tripList, binding.overlay);

        final TextView textHome = binding.textHome;
        final TextView textTrips = binding.textTrips;

        displayNameLiveData.observe(getViewLifecycleOwner(), textHome::setText);
        tripsLiveData.observe(getViewLifecycleOwner(), textTrips::setText);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(tripAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;

        DatabaseReference mNameReference = FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/display_name");
        DatabaseReference mTripsReference = FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/trips");

        mNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String displayName = dataSnapshot.getValue(String.class);
                    displayNameLiveData.setValue("Hello, " + displayName + "!");
                } else {
                    displayNameLiveData.setValue("Hello, User!");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        mTripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                List<TripInfo> trips = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    tripsLiveData.setValue("Your trips:");
                    for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                        String tripID = tripSnapshot.getKey();
                        String destinationID = tripSnapshot.child("destination_id").getValue(String.class);
                        String startDate = tripSnapshot.child("start_date").getValue(String.class);
                        String endDate = tripSnapshot.child("end_date").getValue(String.class);
                        int numberOfAdults = getValueOrDefault(tripSnapshot.child("number_adults"));
                        int numberOfStudents = getValueOrDefault(tripSnapshot.child("number_students"));
                        int numberOfDays = getValueOrDefault(tripSnapshot.child("number_days"));
                        List<String> selectedAttractions = new ArrayList<>();
                        for (DataSnapshot attractionSnapshot : tripSnapshot.child("attractions").getChildren()) {
                            selectedAttractions.add(attractionSnapshot.getValue(String.class));
                        }
                        TripInfo trip = new TripInfo(tripID, destinationID, startDate, endDate, numberOfDays, numberOfAdults, numberOfStudents, selectedAttractions);
                        trips.add(trip);
                    }
                    tripAdapter.setTrips(trips);
                    tripAdapter.notifyDataSetChanged();
                } else {
                    tripsLiveData.setValue("You haven't planned any trips yet!");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });

        return root;
    }

    private int getValueOrDefault(DataSnapshot snapshot) {
        Integer value = snapshot.getValue(Integer.class);
        return (value != null) ? value : -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
