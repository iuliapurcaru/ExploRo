package com.example.exploro.ui.account;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<String> displayNameLiveData;
    private final MutableLiveData<String> emailLiveData;

    public AccountViewModel() {

        displayNameLiveData = new MutableLiveData<>();
        emailLiveData = new MutableLiveData<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        emailLiveData.setValue("Reset email password sent to " + mUser.getEmail());

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mNameReference = mDatabase.getReference("users/" + mUser.getUid() + "/display_name");

        mNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String displayName = dataSnapshot.getValue(String.class);
                    displayNameLiveData.setValue(displayName);
                }
                else {
                    displayNameLiveData.setValue("User");
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w("DATABASE", "Failed to get database data.", error.toException());
            }
        });
    }

    public LiveData<String> getDisplayNameText() {
        return displayNameLiveData;
    }

    public LiveData<String> getEmailText() {
        return emailLiveData;
    }
}