package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.firebase.SampleDataSeeder;
import com.campusone.app.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseManager = FirebaseManager.getInstance();

        // Check if user is already logged in
        if (firebaseManager.isUserLoggedIn()) {
            goToMainActivity();
            return;
        }

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_login);

        btnLogin.setOnClickListener(v -> attemptLogin());

        findViewById(R.id.tv_go_to_register).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (!ValidationUtils.isNotEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);

        firebaseManager.getAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser() != null ? authResult.getUser().getUid() : null;
                    if (uid != null) {
                        firebaseManager.fetchUserProfile(uid,
                                user -> {
                                    setLoading(false);
                                    Toast.makeText(LoginActivity.this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                                    // Seed sample data in the background if the database is brand new
                                    SampleDataSeeder.seedInitialDataIfEmpty(firebaseManager.getFirestore());
                                    goToMainActivity();
                                },
                                error -> {
                                    // Profile not yet created in Firestore, still let user enter and create fallback
                                    setLoading(false);
                                    goToMainActivity();
                                });
                    } else {
                        setLoading(false);
                        goToMainActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    String message = e.getMessage();
                    if (message != null && message.contains("no user record")) {
                        Toast.makeText(LoginActivity.this, "No account found with this email. Please register.", Toast.LENGTH_LONG).show();
                    } else if (message != null && message.contains("password is invalid")) {
                        Toast.makeText(LoginActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
