package com.example.exploro.domain;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.exploro.ui.activities.MainActivity;
import com.example.exploro.utils.NetworkUtils;
import com.example.exploro.utils.VariousUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthManager {

    private static FirebaseAuth mAuth;

    public static void login(String email, String password, Activity activity, ProgressBar progressBar) {
        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void createAccount(String name, String email, String password, String confirmPassword, Activity activity, ProgressBar progressBar) {
        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(activity, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        if (!VariousUtils.isValidEmail(email) || !VariousUtils.isValidPassword(password) || !confirmPassword.equals(password)){
            Toast.makeText(activity, "Failed to create account!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (task.isSuccessful()) {
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        assert mUser != null;
                        mUser.updateProfile(profileUpdates);
                        addUserData(mUser, name);
                        Toast.makeText(activity, "Account created successfully!",Toast.LENGTH_SHORT).show();
                        activity.finish();
                    } else {
                        Log.w("SIGNUP", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(activity, "Invalid email!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void addUserData(FirebaseUser mUser, String name) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = mDatabase.getReference("users/" + mUser.getUid());
        usersReference.child("display_name").setValue(name);
        usersReference.child("email").setValue(mUser.getEmail());
    }

}
