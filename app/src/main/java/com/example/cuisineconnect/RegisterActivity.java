package com.example.cuisineconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Register button click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            // Navigate to LoginActivity
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            handleRegistrationError(task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRegistrationError(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            Toast.makeText(this, "Password is too weak. Choose a stronger password.", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
