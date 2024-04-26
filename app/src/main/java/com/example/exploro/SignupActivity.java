package com.example.exploro;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.exploro.databinding.ActivitySignupBinding;

import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText nameEditText = binding.displayName;
        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final EditText confirmPassEditText = binding.confirmPassword;
        final Button loginButton = binding.login;
        final Button signupButton = binding.signup;
        progressBar = findViewById(R.id.loading);

        String errorEmail = getString(R.string.invalid_email);
        String errorPassword = getString(R.string.invalid_password);
        String confirmPassword = getString(R.string.passwords_do_not_match);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v ->{
            progressBar.setVisibility(ProgressBar.VISIBLE);
            finish();
        });

        signupButton.setOnClickListener(v -> {
            if (nameEditText.getText().toString().isEmpty() ||
                    emailEditText.getText().toString().isEmpty() ||
                    passwordEditText.getText().toString().isEmpty() ||
                    confirmPassEditText.getText().toString().isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(emailEditText.getText().toString()) || !isValidPassword(passwordEditText.getText().toString()) ||
                    !confirmPassEditText.getText().toString().equals(passwordEditText.getText().toString())){
                Toast.makeText(SignupActivity.this, "Failed to create account!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(ProgressBar.VISIBLE);
            createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString(), nameEditText.getText().toString());
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();
                if (name.isEmpty()) {
                    nameEditText.setError("Name is required!");
                } else {
                    nameEditText.setError(null);
                }
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (isValidEmail(email)) {
                    emailEditText.setError(null);
                } else {
                    emailEditText.setError(errorEmail);
                }
            }
        });


        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                if (isValidPassword(password)) {
                    passwordEditText.setError(null);
                } else {
                    passwordEditText.setError(errorPassword);
                }
            }
        });

        confirmPassEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString().trim();
                String confirmPass = s.toString().trim();
                if (!confirmPass.equals(password)) {
                    confirmPassEditText.setError(confirmPassword);
                } else {
                    confirmPassEditText.setError(null);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        final Pattern textPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*\\d).+$");
        return textPattern.matcher(password).matches() && password.length() >= 8;
    }

    private void createAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (task.isSuccessful()) {
                        Log.d("SIGNUP", "createUserWithEmail:success");
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        assert mUser != null;
                        mUser.updateProfile(profileUpdates);
                        addUserData(mUser, name);
                        Toast.makeText(SignupActivity.this, "Account created successfully!",Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.w("SIGNUP", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Invalid email!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserData(FirebaseUser mUser, String name) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = mDatabase.getReference("users/" + mUser.getUid());
        usersReference.child("display_name").setValue(name);
        usersReference.child("email").setValue(mUser.getEmail());
        usersReference.child("currency").setValue("RON");
        usersReference.child("distance_unit").setValue("kilometers (km)");
    }

    private void reload() { }
}