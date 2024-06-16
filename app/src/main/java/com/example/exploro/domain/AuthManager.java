package com.example.exploro.domain;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.exploro.data.repositories.UserRemoteDataSource;
import com.example.exploro.ui.activities.LoginActivity;
import com.example.exploro.ui.activities.MainActivity;
import com.example.exploro.utils.NetworkUtils;
import com.example.exploro.utils.VariousUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthManager {

    private static FirebaseUser mUser;
    private static FirebaseAuth mAuth;
    private static final String TAG = AuthManager.class.getSimpleName();

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
                        if (mAuth.getCurrentUser() == null) {
                            Log.w(TAG, "signInWithEmail:failure");
                            return;
                        }
                        mUser = mAuth.getCurrentUser();
                        checkEmailVerification(mUser, activity);
                    } else {
                        Toast.makeText(activity, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void checkEmailVerification(FirebaseUser user, Activity activity) {
        if (user.isEmailVerified()) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        } else {
            Toast.makeText(activity, "Please verify your email!", Toast.LENGTH_SHORT).show();
            sendEmailVerification(user, activity);
        }
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
                        mUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        if (mUser == null) {
                            Log.w(TAG, "createUserWithEmail:failure");
                            return;
                        }
                        mUser.updateProfile(profileUpdates);
                        UserRemoteDataSource.addUserToDatabase(mUser, name);
                        sendEmailVerification(mUser, activity);
                        Toast.makeText(activity, "Account created successfully!",Toast.LENGTH_SHORT).show();
                        activity.finish();
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(activity, "Invalid email!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void sendEmailVerification(FirebaseUser user, Activity activity) {
        user.sendEmailVerification().addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "sendEmailVerification:failure", task.getException());
                Toast.makeText(activity, "Failed to send verification email!", Toast.LENGTH_SHORT).show();
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
