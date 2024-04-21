package com.example.exploro;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.exploro.databinding.ActivityLoginBinding;
import com.example.exploro.ui.PopupMenu;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView forgotPasswordTextView;
    private FirebaseAuth mAuth;
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

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            if (emailEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(ProgressBar.VISIBLE);
            login(emailEditText.getText().toString(), passwordEditText.getText().toString());
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

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (task.isSuccessful()) {
                        Log.d("LOGIN", "signInWithEmail:success");
                        updateUI();
                    } else {
                        Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateUI() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showPopupMenu() {
        PopupMenu.forgotPassword(this, forgotPasswordTextView, () -> overlay.setVisibility(View.GONE));
    }
}