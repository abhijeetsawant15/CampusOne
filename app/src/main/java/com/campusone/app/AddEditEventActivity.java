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
import com.campusone.app.models.Event;
import com.campusone.app.models.Notification;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.campusone.app.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditEventActivity extends AppCompatActivity {

    private String eventId, passedClubId;
    private TextInputEditText etTitle, etDate, etTime, etVenue, etOrganizer, etDesc;
    private TextView tvHeader;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_add_edit_event));

        firebaseManager = FirebaseManager.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_event);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.et_evt_title);
        etDate = findViewById(R.id.et_evt_date);
        etTime = findViewById(R.id.et_evt_time);
        etVenue = findViewById(R.id.et_evt_venue);
        etOrganizer = findViewById(R.id.et_evt_organizer);
        etDesc = findViewById(R.id.et_evt_desc);
        tvHeader = findViewById(R.id.tv_event_form_header);
        btnSave = findViewById(R.id.btn_save_event);
        progressBar = findViewById(R.id.progress_event);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        String initialTitle = intent.getStringExtra("title");
        String initialDate = intent.getStringExtra("date");
        String initialTime = intent.getStringExtra("time");
        String initialVenue = intent.getStringExtra("venue");
        String initialOrganizer = intent.getStringExtra("organizer");
        String initialDesc = intent.getStringExtra("description");
        passedClubId = intent.getStringExtra("clubId");

        User currentUser = firebaseManager.getCachedUserProfile();
        boolean isClubMember = currentUser != null && "club_member".equalsIgnoreCase(currentUser.getRole());

        if (eventId != null) {
            tvHeader.setText("Edit Campus Event");
            btnSave.setText("Update Event");
            etTitle.setText(initialTitle);
            etDate.setText(initialDate);
            etTime.setText(initialTime);
            etVenue.setText(initialVenue);
            etOrganizer.setText(initialOrganizer);
            etDesc.setText(initialDesc);
        } else {
            tvHeader.setText("Create Campus Event");
            btnSave.setText("Publish Event");
            if (isClubMember && currentUser.getClubId() != null) {
                etOrganizer.setText(currentUser.getClubId().toUpperCase() + " Team");
            } else if (initialOrganizer != null) {
                etOrganizer.setText(initialOrganizer);
            } else {
                etOrganizer.setText("College Administration");
            }
        }

        btnSave.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String time = etTime.getText() != null ? etTime.getText().toString().trim() : "";
        String venue = etVenue.getText() != null ? etVenue.getText().toString().trim() : "";
        String organizer = etOrganizer.getText() != null ? etOrganizer.getText().toString().trim() : "";
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

        if (!ValidationUtils.isNotEmpty(time)) {
            etTime.setError("Time is required");
            etTime.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(venue)) {
            etVenue.setError("Venue is required");
            etVenue.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(organizer)) {
            etOrganizer.setError("Organizer is required");
            etOrganizer.requestFocus();
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
        if (isClubMember) {
            targetClubId = currentUser.getClubId();
        } else if (passedClubId != null && !passedClubId.isEmpty()) {
            targetClubId = passedClubId;
        } else {
            targetClubId = "college";
        }

        String docId = eventId != null ? eventId : firebaseManager.getEventsCollection().document().getId();

        Event event = new Event(
                docId,
                title,
                date,
                time,
                venue,
                desc,
                organizer,
                targetClubId,
                System.currentTimeMillis()
        );

        firebaseManager.getEventsCollection().document(docId).set(event)
                .addOnSuccessListener(aVoid -> {
                    // Also create a notification entry for upcoming event
                    String notifId = firebaseManager.getNotificationsCollection().document().getId();
                    Notification notif = new Notification(
                            notifId,
                            "New Event: " + title,
                            date + " at " + time + " | Venue: " + venue,
                            date,
                            "CAMPUS UPDATE",
                            System.currentTimeMillis()
                    );
                    firebaseManager.getNotificationsCollection().document(notifId).set(notif);

                    setLoading(false);
                    Toast.makeText(AddEditEventActivity.this, "Event saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(AddEditEventActivity.this, "Failed to save event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
        etTitle.setEnabled(!loading);
        etDate.setEnabled(!loading);
        etTime.setEnabled(!loading);
        etVenue.setEnabled(!loading);
        etOrganizer.setEnabled(!loading);
        etDesc.setEnabled(!loading);
    }
}
