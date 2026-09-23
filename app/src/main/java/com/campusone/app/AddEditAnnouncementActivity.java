package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Announcement;
import com.campusone.app.models.Notification;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.campusone.app.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditAnnouncementActivity extends AppCompatActivity {

    private String announcementId, existingClubId;
    private TextInputEditText etTitle, etDate, etDesc;
    private TextView tvHeader, tvTypeBadge;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_announcement);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_add_edit_announcement));

        firebaseManager = FirebaseManager.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_announcement);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.et_ann_title);
        etDate = findViewById(R.id.et_ann_date);
        etDesc = findViewById(R.id.et_ann_desc);
        tvHeader = findViewById(R.id.tv_announcement_form_header);
        tvTypeBadge = findViewById(R.id.tv_announcement_type_badge);
        btnSave = findViewById(R.id.btn_save_announcement);
        progressBar = findViewById(R.id.progress_announcement);

        Intent intent = getIntent();
        announcementId = intent.getStringExtra("announcementId");
        String initialTitle = intent.getStringExtra("title");
        String initialDesc = intent.getStringExtra("description");
        String initialDate = intent.getStringExtra("date");
        existingClubId = intent.getStringExtra("clubId");

        User currentUser = firebaseManager.getCachedUserProfile();
        boolean isClubMember = currentUser != null && "club_member".equalsIgnoreCase(currentUser.getRole());

        if (isClubMember) {
            tvTypeBadge.setText("CLUB ANNOUNCEMENT (" + (currentUser.getClubId() != null ? currentUser.getClubId().toUpperCase() : "") + ")");
        } else {
            tvTypeBadge.setText("COLLEGE ANNOUNCEMENT");
        }

        if (announcementId != null) {
            // Edit mode
            tvHeader.setText("Edit Announcement");
            btnSave.setText("Update Announcement");
            etTitle.setText(initialTitle);
            etDesc.setText(initialDesc);
            etDate.setText(initialDate);
        } else {
            // Create mode
            tvHeader.setText("Create Announcement");
            btnSave.setText("Publish Announcement");
            etDate.setText(ValidationUtils.getCurrentFormattedDate());
        }

        btnSave.setOnClickListener(v -> saveAnnouncement());
    }

    private void saveAnnouncement() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";

        if (!ValidationUtils.isNotEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(date)) {
            etDate.setError("Date is required");
            etDate.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(desc)) {
            etDesc.setError("Description is required");
            etDesc.requestFocus();
            return;
        }

        setLoading(true);

        User currentUser = firebaseManager.getCachedUserProfile();
        boolean isClubMember = currentUser != null && "club_member".equalsIgnoreCase(currentUser.getRole());

        String targetClubId;
        String category;
        String authorName;

        if (isClubMember) {
            targetClubId = currentUser.getClubId();
            category = "CLUB ANNOUNCEMENT";
            authorName = targetClubId != null ? targetClubId.toUpperCase() + " Team" : "Club Officer";
        } else {
            targetClubId = (existingClubId != null && !existingClubId.isEmpty()) ? existingClubId : "college";
            category = "college".equalsIgnoreCase(targetClubId) ? "COLLEGE ANNOUNCEMENT" : "CLUB ANNOUNCEMENT";
            authorName = "College Administration";
        }

        String docId = announcementId != null ? announcementId : firebaseManager.getAnnouncementsCollection().document().getId();

        Announcement announcement = new Announcement(
                docId,
                title,
                desc,
                date,
                category,
                targetClubId,
                authorName,
                firebaseManager.getCurrentUserId(),
                System.currentTimeMillis()
        );

        firebaseManager.getAnnouncementsCollection().document(docId).set(announcement)
                .addOnSuccessListener(aVoid -> {
                    // Also create notification
                    String notifId = firebaseManager.getNotificationsCollection().document().getId();
                    Notification notif = new Notification(
                            notifId,
                            title,
                            desc,
                            date,
                            category,
                            System.currentTimeMillis()
                    );
                    firebaseManager.getNotificationsCollection().document(notifId).set(notif);

                    setLoading(false);
                    Toast.makeText(AddEditAnnouncementActivity.this, "Announcement published successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(AddEditAnnouncementActivity.this, "Failed to publish: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
        etTitle.setEnabled(!loading);
        etDate.setEnabled(!loading);
        etDesc.setEnabled(!loading);
    }
}
