package com.example.exploro.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<String> displayNameLiveData;
    private final MutableLiveData<String> emailLiveData;

    public AccountViewModel() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        displayNameLiveData = new MutableLiveData<>();
        emailLiveData = new MutableLiveData<>();

        if (mUser != null) {
            displayNameLiveData.setValue(mUser.getDisplayName());
            emailLiveData.setValue("Reset email password sent to " + mUser.getEmail());
        }
    }

    public LiveData<String> getDisplayNameText() {
        return displayNameLiveData;
    }

    public LiveData<String> getEmailText() {
        return emailLiveData;
    }
}