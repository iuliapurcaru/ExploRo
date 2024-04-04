package com.example.exploro.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.exploro.R;
import com.example.exploro.databinding.ActivityLoginBinding;
import com.example.exploro.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
    final ProgressBar loadingProgressBar = binding.loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setContentView(binding.getRoot());

        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button signupButton = binding.signup;

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}