package com.example.exploro.domain;

import android.app.Activity;
import android.content.Intent;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.exploro.ui.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountManager {

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    private static FirebaseDatabase mDatabase;

    public static void forgotPassword(String email, Activity activity, PopupWindow popupWindow) {
        if (email.isEmpty()) {
            Toast.makeText(activity, "Please fill in email field!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity, "Reset password email sent to " + email, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
            }
            popupWindow.dismiss();
        });
    }

    public static void editDisplayName(String displayName, Fragment fragment, PopupWindow popupWindow) {
        if (displayName.isEmpty()) {
            Toast.makeText(fragment.getContext(), "Please fill in name field!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mNameReference = mDatabase.getReference("users/" + mUser.getUid() + "/display_name");

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        mUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(fragment.getContext(), "Display name updated!", Toast.LENGTH_SHORT).show();
                mNameReference.setValue(displayName);
                popupWindow.dismiss();
            } else {
                Toast.makeText(fragment.getContext(), "Failed to update display name!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteAccount(Fragment fragment) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mUserReference = mDatabase.getReference("users/" + mUser.getUid());

        mUserReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                signOut(fragment);
                mUser.delete();
                Toast.makeText(fragment.getContext(), "Account deleted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), "Failed to remove user data from database!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void resetPassword() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            String email = mUser.getEmail();
            if (email != null) {
                mAuth.sendPasswordResetEmail(email);
            }
        }
    }

    public static void signOut(Fragment fragment) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
        fragment.startActivity(intent);
        fragment.requireActivity().finish();
    }
}