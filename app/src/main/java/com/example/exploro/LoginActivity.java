package com.example.exploro;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.exploro.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button signupButton = binding.signup;
        progressBar = findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            if (usernameEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(ProgressBar.VISIBLE);
            login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
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
}