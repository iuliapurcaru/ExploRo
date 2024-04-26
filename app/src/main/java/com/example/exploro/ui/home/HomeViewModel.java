package com.example.exploro.ui.home;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> displayNameLiveData;

    public HomeViewModel() {

        displayNameLiveData = new MutableLiveData<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        assert mUser != null;
        DatabaseReference mNameReference = mDatabase.getReference("users/" + mUser.getUid() + "/display_name");

        mNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String displayName = Objects.requireNonNull(dataSnapshot.getValue()).toString();
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
    }

    public LiveData<String> getText() {
        return displayNameLiveData;
    }
}