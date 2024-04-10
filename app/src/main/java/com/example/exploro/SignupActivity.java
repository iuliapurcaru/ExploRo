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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText nameEditText = binding.fullName;
        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final EditText confirmPassEditText = binding.password2;
        final Button loginButton = binding.login;
        final Button signupButton = binding.signup;
        final ProgressBar progressBar = binding.loading;

        String errorEmail = getString(R.string.invalid_email);
        String errorPassword = getString(R.string.invalid_password);
        String confirmPassword = getString(R.string.passwords_do_not_match);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> finish());

        signupButton.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString());
            progressBar.setVisibility(ProgressBar.INVISIBLE);
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
                if (!isValidEmail(email)) {
                    emailEditText.setError(errorEmail);
                } else {
                    emailEditText.setError(null);
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
                if (!isValidPassword(password)) {
                    passwordEditText.setError(errorPassword);
                } else {
                    passwordEditText.setError(null);
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
        return password.length() >= 8;
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(SignupActivity.this, "Account created successfully!",Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void reload() { }
}