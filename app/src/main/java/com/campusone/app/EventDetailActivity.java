package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.google.android.material.button.MaterialButton;

public class EventDetailActivity extends AppCompatActivity {

    private String eventId, title, date, time, venue, description, organizer, clubId;
    private TextView tvTitle, tvOrganizer, tvDate, tvTime, tvVenue, tvDesc;
    private LinearLayout layoutActions;
    private MaterialButton btnEdit, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_event_detail));

        Toolbar toolbar = findViewById(R.id.toolbar_event_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.detail_event_title);
        tvOrganizer = findViewById(R.id.detail_event_organizer_badge);
        tvDate = findViewById(R.id.detail_event_date);
        tvTime = findViewById(R.id.detail_event_time);
        tvVenue = findViewById(R.id.detail_event_venue);
        tvDesc = findViewById(R.id.detail_event_desc);
        layoutActions = findViewById(R.id.layout_event_management_actions);
        btnEdit = findViewById(R.id.btn_detail_edit_event);
        btnDelete = findViewById(R.id.btn_detail_delete_event);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        title = intent.getStringExtra("title");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        venue = intent.getStringExtra("venue");
        description = intent.getStringExtra("description");
        organizer = intent.getStringExtra("organizer");
        clubId = intent.getStringExtra("clubId");

        tvTitle.setText(title != null ? title : "Event Details");
        tvOrganizer.setText("Organized by " + (organizer != null ? organizer : "CampusOne"));
        tvDate.setText(date != null ? date : "TBD");
        tvTime.setText(time != null ? time : "TBD");
        tvVenue.setText(venue != null ? venue : "Campus Ground");
        tvDesc.setText(description != null ? description : "No description provided.");

        checkPermissions();

        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(EventDetailActivity.this, AddEditEventActivity.class);
            editIntent.putExtra("eventId", eventId);
            editIntent.putExtra("title", title);
            editIntent.putExtra("date", date);
            editIntent.putExtra("time", time);
            editIntent.putExtra("venue", venue);
            editIntent.putExtra("description", description);
            editIntent.putExtra("organizer", organizer);
            editIntent.putExtra("clubId", clubId);
            startActivity(editIntent);
            finish();
        });

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void checkPermissions() {
        FirebaseManager fm = FirebaseManager.getInstance();
        User user = fm.getCachedUserProfile();
        if (user != null) {
            String role = user.getRole();
            boolean canManage = false;

            if ("admin".equalsIgnoreCase(role)) {
                canManage = true;
            } else if ("club_member".equalsIgnoreCase(role)) {
                if (user.getClubId() != null && user.getClubId().equalsIgnoreCase(clubId)) {
                    canManage = true;
                }
            }

            layoutActions.setVisibility(canManage ? View.VISIBLE : View.GONE);
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (eventId != null) {
                        FirebaseManager.getInstance().getEventsCollection().document(eventId).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EventDetailActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(EventDetailActivity.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
