package com.example.exploro.ui.home;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> displayNameLiveData;
    private final MutableLiveData<String> tripsLiveData;

    public HomeViewModel() {

        displayNameLiveData = new MutableLiveData<>();
        tripsLiveData = new MutableLiveData<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        assert mUser != null;
        DatabaseReference mNameReference = mDatabase.getReference("users/" + mUser.getUid() + "/display_name");
        DatabaseReference mTripsReference = mDatabase.getReference("users/" + mUser.getUid() + "/trips");

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

//        mTripsReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String trips = dataSnapshot.getValue(String.class);
//                    assert trips != null;
//                    if (trips.isEmpty()) {
//                        tripsLiveData.setValue("You haven't planned any trips yet!");
//                    }
//                    else {
//                        tripsLiveData.setValue("You have trips!");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NotNull DatabaseError error) {
//                Log.w("DATABASE", "Failed to get database data.", error.toException());
//            }
//        });
    }

    public LiveData<String> getHomeText() {
        return displayNameLiveData;
    }
    public LiveData<String> getTripsText() {
        return tripsLiveData;
    }
}