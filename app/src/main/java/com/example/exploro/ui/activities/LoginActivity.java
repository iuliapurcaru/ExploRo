package com.example.exploro.ui.activities;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.exploro.R;
import com.example.exploro.databinding.ActivityLoginBinding;
import com.example.exploro.domain.AuthManager;
import com.example.exploro.utils.PopupMenu;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView forgotPasswordTextView;
    private View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button signupButton = binding.signup;
        forgotPasswordTextView = binding.forgotPassword;
        progressBar = findViewById(R.id.loading);
        overlay = binding.overlay;

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            AuthManager.login(emailEditText.getText().toString(), passwordEditText.getText().toString(), this, progressBar);
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            overlay.setVisibility(View.VISIBLE);
            showPopupMenu();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(progressBar != null) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    private void showPopupMenu() {
        PopupMenu.showForgotPasswordPopup(this, forgotPasswordTextView, () -> overlay.setVisibility(View.GONE));
    }
}