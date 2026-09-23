package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.adapters.AnnouncementAdapter;
import com.campusone.app.adapters.EventAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Announcement;
import com.campusone.app.models.Club;
import com.campusone.app.models.Event;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClubMemberDashboardActivity extends AppCompatActivity {

    private String assignedClubId;
    private String clubName = "Assigned Club";
    private ImageView ivClubLogo;
    private TextView tvClubName, tvClubDesc, tvNoAnnouncements, tvNoEvents;
    private RecyclerView rvAnnouncements, rvEvents;
    private MaterialButton btnCreateAnnouncement, btnCreateEvent;

    private AnnouncementAdapter announcementAdapter;
    private List<Announcement> announcementList;

    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_member_dashboard);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_club_member_dashboard));

        Toolbar toolbar = findViewById(R.id.toolbar_club_member_dash);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivClubLogo = findViewById(R.id.iv_assigned_club_logo);
        tvClubName = findViewById(R.id.tv_assigned_club_name);
        tvClubDesc = findViewById(R.id.tv_assigned_club_desc);
        tvNoAnnouncements = findViewById(R.id.tv_no_assigned_announcements);
        tvNoEvents = findViewById(R.id.tv_no_assigned_events);
        rvAnnouncements = findViewById(R.id.rv_assigned_club_announcements);
        rvEvents = findViewById(R.id.rv_assigned_club_events);
        btnCreateAnnouncement = findViewById(R.id.btn_club_create_announcement);
        btnCreateEvent = findViewById(R.id.btn_club_create_event);

        // Verify assigned club from current user profile
        FirebaseManager fm = FirebaseManager.getInstance();
        User currentUser = fm.getCachedUserProfile();
        if (currentUser == null || (!"club_member".equalsIgnoreCase(currentUser.getRole()) && !"admin".equalsIgnoreCase(currentUser.getRole()))) {
            Toast.makeText(this, "Access restricted to authorized Club Officers", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        assignedClubId = currentUser.getClubId();
        if (assignedClubId == null || assignedClubId.trim().isEmpty()) {
            if ("admin".equalsIgnoreCase(currentUser.getRole())) {
                assignedClubId = "nss"; // Fallback demo club for admin inspecting club dash
            } else {
                Toast.makeText(this, "No specific club assigned to your account. Contact Admin.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        Club tempClub = new Club();
        tempClub.setClubId(assignedClubId);
        ivClubLogo.setImageResource(tempClub.getIconResId());
        tvClubName.setText("Assigned Club: " + assignedClubId.toUpperCase());

        // Setup Announcements Recycler
        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(announcementList);
        rvAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        rvAnnouncements.setAdapter(announcementAdapter);

        announcementAdapter.setManagementPermissions(true, assignedClubId, new AnnouncementAdapter.OnAnnouncementActionListener() {
            @Override
            public void onEditAnnouncement(Announcement announcement) {
                Intent intent = new Intent(ClubMemberDashboardActivity.this, AddEditAnnouncementActivity.class);
                intent.putExtra("announcementId", announcement.getId());
                intent.putExtra("title", announcement.getTitle());
                intent.putExtra("description", announcement.getDescription());
                intent.putExtra("category", announcement.getCategory());
                intent.putExtra("date", announcement.getDate());
                intent.putExtra("clubId", assignedClubId);
                startActivity(intent);
            }

            @Override
            public void onDeleteAnnouncement(Announcement announcement) {
                confirmDeleteAnnouncement(announcement);
            }
        });

        // Setup Events Recycler
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this::openEventDetails);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);

        eventAdapter.setManagementPermissions(true, assignedClubId, new EventAdapter.OnEventActionListener() {
            @Override
            public void onEditEvent(Event event) {
                Intent intent = new Intent(ClubMemberDashboardActivity.this, AddEditEventActivity.class);
                intent.putExtra("eventId", event.getId());
                intent.putExtra("title", event.getTitle());
                intent.putExtra("date", event.getDate());
                intent.putExtra("time", event.getTime());
                intent.putExtra("venue", event.getVenue());
                intent.putExtra("description", event.getDescription());
                intent.putExtra("organizer", event.getOrganizer());
                intent.putExtra("clubId", assignedClubId);
                startActivity(intent);
            }

            @Override
            public void onDeleteEvent(Event event) {
                confirmDeleteEvent(event);
            }
        });

        btnCreateAnnouncement.setOnClickListener(v -> {
            Intent intent = new Intent(ClubMemberDashboardActivity.this, AddEditAnnouncementActivity.class);
            intent.putExtra("clubId", assignedClubId);
            intent.putExtra("clubName", clubName);
            startActivity(intent);
        });

        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(ClubMemberDashboardActivity.this, AddEditEventActivity.class);
            intent.putExtra("clubId", assignedClubId);
            intent.putExtra("organizer", clubName);
            startActivity(intent);
        });

        loadClubDetailsAndContent();
    }

    private void openEventDetails(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("date", event.getDate());
        intent.putExtra("time", event.getTime());
        intent.putExtra("venue", event.getVenue());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("organizer", event.getOrganizer());
        intent.putExtra("clubId", event.getClubId());
        startActivity(intent);
    }

    private void loadClubDetailsAndContent() {
        // Fetch club details
        FirebaseManager.getInstance().getClubsCollection().document(assignedClubId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Club club = documentSnapshot.toObject(Club.class);
                        if (club != null) {
                            clubName = club.getName();
                            tvClubName.setText("Assigned Club: " + club.getName());
                            tvClubDesc.setText(club.getDescription());
                            ivClubLogo.setImageResource(club.getIconResId());
                        }
                    }
                });

        loadAssignedAnnouncements();
        loadAssignedEvents();
    }

    private void loadAssignedAnnouncements() {
        FirebaseManager.getInstance().getAnnouncementsCollection()
                .whereEqualTo("clubId", assignedClubId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    announcementList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Announcement a = doc.toObject(Announcement.class);
                            if (a != null) {
                                a.setId(doc.getId());
                                announcementList.add(a);
                            }
                        }
                    }
                    announcementAdapter.notifyDataSetChanged();
                    tvNoAnnouncements.setVisibility(announcementList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> tvNoAnnouncements.setVisibility(View.VISIBLE));
    }

    private void loadAssignedEvents() {
        FirebaseManager.getInstance().getEventsCollection()
                .whereEqualTo("clubId", assignedClubId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Event e = doc.toObject(Event.class);
                            if (e != null) {
                                e.setId(doc.getId());
                                eventList.add(e);
                            }
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                    tvNoEvents.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> tvNoEvents.setVisibility(View.VISIBLE));
    }

    private void confirmDeleteAnnouncement(Announcement announcement) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage("Are you sure you want to delete '" + announcement.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (announcement.getId() != null) {
                        FirebaseManager.getInstance().getAnnouncementsCollection().document(announcement.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Notice deleted", Toast.LENGTH_SHORT).show();
                                    loadAssignedAnnouncements();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteEvent(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete '" + event.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (event.getId() != null) {
                        FirebaseManager.getInstance().getEventsCollection().document(event.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                                    loadAssignedEvents();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssignedAnnouncements();
        loadAssignedEvents();
    }
}
