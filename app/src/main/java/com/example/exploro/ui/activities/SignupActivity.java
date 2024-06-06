package com.example.exploro.ui.activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.exploro.databinding.ActivitySignupBinding;

import com.example.exploro.domain.AuthManager;
import com.example.exploro.utils.VariousUtils;
import com.google.firebase.auth.*;

public class SignupActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.exploro.R.layout.activity_signup);

        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText nameEditText = binding.displayName;
        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final EditText confirmPasswordEditText = binding.confirmPassword;
        final Button loginButton = binding.login;
        final Button signupButton = binding.signup;
        progressBar = findViewById(com.example.exploro.R.id.loading);

        loginButton.setOnClickListener(v ->{
            progressBar.setVisibility(ProgressBar.VISIBLE);
            finish();
        });

        signupButton.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            AuthManager.createAccount(nameEditText.getText().toString(), emailEditText.getText().toString(),
                                      passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString(),
                               this, progressBar);
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
                if (VariousUtils.isValidEmail(email)) {
                    emailEditText.setError(null);
                } else {
                    emailEditText.setError("Invalid email!");
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
                if (VariousUtils.isValidPassword(password)) {
                    passwordEditText.setError(null);
                } else {
                    passwordEditText.setError("Password must contain at least 8 characters, one uppercase letter, one lowercase letter, and one number!");
                }
            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString().trim();
                String confirmPass = s.toString().trim();
                if (!confirmPass.equals(password)) {
                    confirmPasswordEditText.setError("Passwords do not match!");
                } else {
                    confirmPasswordEditText.setError(null);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() { }
}