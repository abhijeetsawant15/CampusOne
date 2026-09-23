package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.firebase.SampleDataSeeder;
import com.campusone.app.models.User;
import com.campusone.app.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etDept, etYear, etDivision, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseManager = FirebaseManager.getInstance();

        etName = findViewById(R.id.et_reg_name);
        etEmail = findViewById(R.id.et_reg_email);
        etDept = findViewById(R.id.et_reg_dept);
        etYear = findViewById(R.id.et_reg_year);
        etDivision = findViewById(R.id.et_reg_division);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirmPassword = findViewById(R.id.et_reg_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_register);

        btnRegister.setOnClickListener(v -> attemptRegistration());

        findViewById(R.id.tv_go_to_login).setOnClickListener(v -> finish());
    }

    private void attemptRegistration() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String dept = etDept.getText() != null ? etDept.getText().toString().trim() : "";
        String year = etYear.getText() != null ? etYear.getText().toString().trim() : "";
        String division = etDivision.getText() != null ? etDivision.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (!ValidationUtils.isNotEmpty(name)) {
            etName.setError("Full name is required");
            etName.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(email)) {
            etEmail.setError("College email is required");
            etEmail.requestFocus();
            return;
        }

        // Strictly enforce college email domain: @student.mes.ac.in
        if (!ValidationUtils.isValidStudentEmail(email)) {
            etEmail.setError("Invalid domain! Student registration requires an email ending with @student.mes.ac.in");
            etEmail.requestFocus();
            Toast.makeText(this, "Student email must end with @student.mes.ac.in (e.g. yourname@student.mes.ac.in)", Toast.LENGTH_LONG).show();
            return;
        }

        if (!ValidationUtils.isNotEmpty(dept)) {
            etDept.setError("Department is required");
            etDept.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(year)) {
            etYear.setError("Year is required (e.g. FE, SE, TE, BE)");
            etYear.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);

        firebaseManager.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser() != null ? authResult.getUser().getUid() : null;
                    if (uid != null) {
                        User newUser = new User(uid, name, email, dept, year, division, "student", null, System.currentTimeMillis());
                        firebaseManager.getUsersCollection().document(uid).set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    firebaseManager.setCachedUserProfile(newUser);
                                    setLoading(false);
                                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    SampleDataSeeder.seedInitialDataIfEmpty(firebaseManager.getFirestore());
                                    goToMainActivity();
                                })
                                .addOnFailureListener(e -> {
                                    setLoading(false);
                                    Toast.makeText(RegisterActivity.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    goToMainActivity();
                                });
                    } else {
                        setLoading(false);
                        goToMainActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        etName.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etDept.setEnabled(!loading);
        etYear.setEnabled(!loading);
        etDivision.setEnabled(!loading);
        etPassword.setEnabled(!loading);
        etConfirmPassword.setEnabled(!loading);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
