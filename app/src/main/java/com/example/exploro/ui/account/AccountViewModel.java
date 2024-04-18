package com.example.exploro.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AccountViewModel() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String accountName = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();

        mText = new MutableLiveData<>();
        mText.setValue(accountName);
    }

    public LiveData<String> getText() {
        return mText;
    }
}