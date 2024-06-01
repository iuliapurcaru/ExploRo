package com.example.exploro.ui.home;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.exploro.TripInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> displayNameLiveData;
    private final MutableLiveData<String> tripsLiveData;
    private final TripAdapter tripAdapter;

    public HomeViewModel() {

        displayNameLiveData = new MutableLiveData<>();
        tripsLiveData = new MutableLiveData<>();
        List<TripInfo> tripList = new ArrayList<>();
        tripAdapter = new TripAdapter(tripList);

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
                }
                else {
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
                } else {
                    tripsLiveData.setValue("You haven't planned any trips yet!");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });
    }

    private int getValueOrDefault(DataSnapshot snapshot) {
        Integer value = snapshot.getValue(Integer.class);
        return (value != null) ? value : -1;
    }

    public LiveData<String> getHomeText() {
        return displayNameLiveData;
    }

    public LiveData<String> getTripsText() {
        return tripsLiveData;
    }

    public TripAdapter getTripAdapter() {
        return tripAdapter;
    }
}