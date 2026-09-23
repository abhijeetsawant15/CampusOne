package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Resource;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.campusone.app.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditResourceActivity extends AppCompatActivity {

    private String resourceId;
    private TextInputEditText etTitle, etDepartment, etUrl, etDesc;
    private Spinner spinnerCategory;
    private TextView tvHeader;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    private final String[] CATEGORIES = {
            "Notes & Study Material",
            "Student Guide",
            "University Forms",
            "Academic Resources",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_resource);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_add_edit_resource));

        firebaseManager = FirebaseManager.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_resource);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.et_res_title);
        etDepartment = findViewById(R.id.et_res_department);
        etUrl = findViewById(R.id.et_res_url);
        etDesc = findViewById(R.id.et_res_desc);
        spinnerCategory = findViewById(R.id.spinner_res_category);
        tvHeader = findViewById(R.id.tv_resource_form_header);
        btnSave = findViewById(R.id.btn_save_resource);
        progressBar = findViewById(R.id.progress_resource);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIES);
        spinnerCategory.setAdapter(spinnerAdapter);

        Intent intent = getIntent();
        resourceId = intent.getStringExtra("resourceId");
        String initialTitle = intent.getStringExtra("title");
        String initialDept = intent.getStringExtra("department");
        String initialDesc = intent.getStringExtra("description");
        String initialCategory = intent.getStringExtra("category");
        String initialUrl = intent.getStringExtra("url");

        if (resourceId != null) {
            tvHeader.setText("Edit Resource");
            btnSave.setText("Update Resource");
            etTitle.setText(initialTitle);
            if (initialDept != null) etDepartment.setText(initialDept);
            etDesc.setText(initialDesc);
            etUrl.setText(initialUrl);

            if (initialCategory != null) {
                for (int i = 0; i < CATEGORIES.length; i++) {
                    if (CATEGORIES[i].equalsIgnoreCase(initialCategory)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            tvHeader.setText("Share Academic Resource");
            btnSave.setText("Save Resource");
            etUrl.setText("https://drive.google.com/");
        }

        btnSave.setOnClickListener(v -> saveResource());
    }

    private void saveResource() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String department = etDepartment.getText() != null ? etDepartment.getText().toString().trim() : "";
        String url = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
        String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Notes & Study Material";

        if (!ValidationUtils.isNotEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(url)) {
            etUrl.setError("Resource URL / Link is required");
            etUrl.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidUrl(url)) {
            etUrl.setError("Please enter a valid URL (e.g. https://drive.google.com/...)");
            etUrl.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(desc)) {
            etDesc.setError("Description is required");
            etDesc.requestFocus();
            return;
        }

        setLoading(true);

        String docId = resourceId != null ? resourceId : firebaseManager.getResourcesCollection().document().getId();

        User profile = firebaseManager.getCachedUserProfile();
        String uploaderUid = firebaseManager.getCurrentUserId();
        String uploaderName = profile != null ? profile.getName() : "Student";

        Resource resource = new Resource(
                docId,
                title,
                desc,
                category,
                url,
                ValidationUtils.getCurrentFormattedDate(),
                System.currentTimeMillis()
        );
        resource.setDepartment(department);
        resource.setUploaderUid(uploaderUid);
        resource.setUploaderName(uploaderName);
        resource.setStatus("ACTIVE");

        firebaseManager.getResourcesCollection().document(docId).set(resource)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    Toast.makeText(AddEditResourceActivity.this, "Resource saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(AddEditResourceActivity.this, "Failed to save resource: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
        etTitle.setEnabled(!loading);
        etDepartment.setEnabled(!loading);
        etUrl.setEnabled(!loading);
        etDesc.setEnabled(!loading);
        spinnerCategory.setEnabled(!loading);
    }
}
