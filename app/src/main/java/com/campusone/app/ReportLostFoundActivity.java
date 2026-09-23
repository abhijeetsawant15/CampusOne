package com.campusone.app;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.LostFoundItem;
import com.campusone.app.models.Notification;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.campusone.app.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;

public class ReportLostFoundActivity extends AppCompatActivity {

    private RadioGroup rgStatus;
    private RadioButton rbLost, rbFound;
    private TextInputEditText etTitle, etLocation, etDate, etContact, etDesc;
    private ImageView ivPreview;
    private MaterialButton btnUploadPhoto, btnRemovePhoto, btnSubmit;
    private ProgressBar progressBar;
    private TextView tvUploadStatus;
    private FirebaseManager firebaseManager;

    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost_found);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_report_lf));

        firebaseManager = FirebaseManager.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_report_lf);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rgStatus = findViewById(R.id.rg_lf_status);
        rbLost = findViewById(R.id.rb_lost);
        rbFound = findViewById(R.id.rb_found);
        etTitle = findViewById(R.id.et_lf_title);
        etLocation = findViewById(R.id.et_lf_location);
        etDate = findViewById(R.id.et_lf_date);
        etContact = findViewById(R.id.et_lf_contact);
        etDesc = findViewById(R.id.et_lf_desc);
        ivPreview = findViewById(R.id.iv_lf_image_preview);
        btnUploadPhoto = findViewById(R.id.btn_upload_photo);
        btnRemovePhoto = findViewById(R.id.btn_remove_photo);
        btnSubmit = findViewById(R.id.btn_submit_lf);
        progressBar = findViewById(R.id.progress_report_lf);
        tvUploadStatus = findViewById(R.id.tv_upload_status);

        // Prepopulate date with today
        etDate.setText(ValidationUtils.getCurrentFormattedDate());

        // Prepopulate contact with logged-in user email
        User user = firebaseManager.getCachedUserProfile();
        if (user != null && user.getEmail() != null) {
            etContact.setText(user.getEmail());
        }

        // Setup image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                ivPreview.setImageURI(uri);
                ivPreview.setVisibility(View.VISIBLE);
                btnRemovePhoto.setVisibility(View.VISIBLE);
                btnUploadPhoto.setText("Change Photo");
            }
        });

        btnUploadPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnRemovePhoto.setOnClickListener(v -> {
            selectedImageUri = null;
            ivPreview.setImageURI(null);
            ivPreview.setVisibility(View.GONE);
            btnRemovePhoto.setVisibility(View.GONE);
            btnUploadPhoto.setText("Add Photo");
        });

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String contact = etContact.getText() != null ? etContact.getText().toString().trim() : "";
        String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
        String status = rbFound.isChecked() ? "FOUND" : "LOST";

        if (!ValidationUtils.isNotEmpty(title)) {
            etTitle.setError("Item title is required");
            etTitle.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(date)) {
            etDate.setError("Date is required");
            etDate.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(contact)) {
            etContact.setError("Contact info is required");
            etContact.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(desc)) {
            etDesc.setError("Description is required");
            etDesc.requestFocus();
            return;
        }

        setLoading(true);

        User currentUser = firebaseManager.getCachedUserProfile();
        String reporterName = currentUser != null ? currentUser.getName() : "Campus Student";
        String currentUid = firebaseManager.getCurrentUserId();
        String itemId = firebaseManager.getLostFoundCollection().document().getId();

        if (selectedImageUri != null && currentUid != null) {
            tvUploadStatus.setVisibility(View.VISIBLE);
            tvUploadStatus.setText("Uploading photo to cloud storage...");

            // Storage structure: lost_found/{userId}/{itemId}/image.jpg
            String storagePath = "lost_found/" + currentUid + "/" + itemId + "/image.jpg";
            StorageReference storageRef = firebaseManager.getStorageReference().child(storagePath);

            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(downloadUri -> {
                                    tvUploadStatus.setText("Saving item details...");
                                    saveItemToFirestore(itemId, title, desc, location, date, status, reporterName, contact, downloadUri.toString(), storagePath, currentUid);
                                })
                                .addOnFailureListener(e -> {
                                    // If getting download URL fails, still save item without failing completely
                                    saveItemToFirestore(itemId, title, desc, location, date, status, reporterName, contact, "", storagePath, currentUid);
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Viva safety: If storage fails or is unavailable, still save the text report and notify the user gracefully
                        saveItemToFirestore(itemId, title, desc, location, date, status, reporterName, contact, "", "", currentUid);
                        Toast.makeText(ReportLostFoundActivity.this, "Item reported! (Photo storage unavailable: " + e.getMessage() + ")", Toast.LENGTH_LONG).show();
                    });
        } else {
            // Save directly without image
            saveItemToFirestore(itemId, title, desc, location, date, status, reporterName, contact, "", "", currentUid);
        }
    }

    private void saveItemToFirestore(String itemId, String title, String desc, String location, String date, String status, String reporterName, String contact, String imageUrl, String storagePath, String currentUid) {
        LostFoundItem newItem = new LostFoundItem(
                itemId,
                title,
                desc,
                location,
                date,
                status,
                reporterName,
                contact,
                imageUrl,
                currentUid,
                System.currentTimeMillis()
        );
        newItem.setImageStoragePath(storagePath);

        firebaseManager.getLostFoundCollection().document(itemId).set(newItem)
                .addOnSuccessListener(aVoid -> {
                    // Create notification entry
                    String notifId = firebaseManager.getNotificationsCollection().document().getId();
                    Notification notification = new Notification(
                            notifId,
                            "Lost & Found: " + title + " (" + status + ")",
                            "Reported " + status.toLowerCase() + " at " + location + ". Contact: " + contact,
                            date,
                            "LOST & FOUND UPDATE",
                            System.currentTimeMillis()
                    );
                    firebaseManager.getNotificationsCollection().document(notifId).set(notification);

                    setLoading(false);
                    tvUploadStatus.setVisibility(View.GONE);
                    Toast.makeText(ReportLostFoundActivity.this, "Item report submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    tvUploadStatus.setVisibility(View.GONE);
                    Toast.makeText(ReportLostFoundActivity.this, "Failed to submit report: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
        btnUploadPhoto.setEnabled(!loading);
        btnRemovePhoto.setEnabled(!loading);
        etTitle.setEnabled(!loading);
        etLocation.setEnabled(!loading);
        etDate.setEnabled(!loading);
        etContact.setEnabled(!loading);
        etDesc.setEnabled(!loading);
        rbLost.setEnabled(!loading);
        rbFound.setEnabled(!loading);
    }
}
