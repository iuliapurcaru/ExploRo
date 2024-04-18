package com.example.exploro.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String accountName = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();

        mText = new MutableLiveData<>();
        mText.setValue("Hello, " + accountName + "!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}