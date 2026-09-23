package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class ClubDetailActivity extends AppCompatActivity {

    private String clubId, clubName, clubDesc, clubCategory;
    private ImageView ivLogo;
    private TextView tvName, tvCategory, tvDesc;
    private TextView tvNoAnnouncements, tvNoEvents;
    private RecyclerView rvAnnouncements, rvEvents;
    private LinearLayout layoutActions;
    private MaterialButton btnAddAnnouncement, btnAddEvent;

    private AnnouncementAdapter announcementAdapter;
    private List<Announcement> announcementList;

    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_club_detail));

        Toolbar toolbar = findViewById(R.id.toolbar_club_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivLogo = findViewById(R.id.club_detail_logo);
        tvName = findViewById(R.id.club_detail_name);
        tvCategory = findViewById(R.id.club_detail_category);
        tvDesc = findViewById(R.id.club_detail_desc);
        tvNoAnnouncements = findViewById(R.id.tv_club_no_announcements);
        tvNoEvents = findViewById(R.id.tv_club_no_events);
        rvAnnouncements = findViewById(R.id.recycler_club_announcements);
        rvEvents = findViewById(R.id.recycler_club_events);
        layoutActions = findViewById(R.id.layout_club_member_actions);
        btnAddAnnouncement = findViewById(R.id.btn_club_add_announcement);
        btnAddEvent = findViewById(R.id.btn_club_add_event);

        Intent intent = getIntent();
        clubId = intent.getStringExtra("clubId");
        clubName = intent.getStringExtra("name");
        clubDesc = intent.getStringExtra("description");
        clubCategory = intent.getStringExtra("category");

        tvName.setText(clubName != null ? clubName : "Student Club");
        tvCategory.setText(clubCategory != null ? clubCategory : "Student Organization");
        tvDesc.setText(clubDesc != null ? clubDesc : "");

        Club tempClub = new Club();
        tempClub.setClubId(clubId);
        ivLogo.setImageResource(tempClub.getIconResId());

        // Setup Announcements Recycler
        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(announcementList);
        rvAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        rvAnnouncements.setAdapter(announcementAdapter);

        // Setup Events Recycler
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this::openEventDetails);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);

        checkPermissions();

        btnAddAnnouncement.setOnClickListener(v -> {
            Intent addIntent = new Intent(ClubDetailActivity.this, AddEditAnnouncementActivity.class);
            addIntent.putExtra("clubId", clubId);
            addIntent.putExtra("clubName", clubName);
            startActivity(addIntent);
        });

        btnAddEvent.setOnClickListener(v -> {
            Intent addIntent = new Intent(ClubDetailActivity.this, AddEditEventActivity.class);
            addIntent.putExtra("clubId", clubId);
            addIntent.putExtra("organizer", clubName);
            startActivity(addIntent);
        });

        loadClubContent();
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

    private void checkPermissions() {
        FirebaseManager fm = FirebaseManager.getInstance();
        User user = fm.getCachedUserProfile();
        if (user != null) {
            String role = user.getRole();
            boolean isAuthorized = false;

            if ("admin".equalsIgnoreCase(role)) {
                isAuthorized = true;
            } else if ("club_member".equalsIgnoreCase(role)) {
                if (user.getClubId() != null && user.getClubId().equalsIgnoreCase(clubId)) {
                    isAuthorized = true;
                }
            }

            layoutActions.setVisibility(isAuthorized ? View.VISIBLE : View.GONE);
        }
    }

    private void loadClubContent() {
        if (clubId == null) return;

        // Load Announcements for this club
        FirebaseManager.getInstance().getAnnouncementsCollection()
                .whereEqualTo("clubId", clubId)
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

        // Load Events for this club
        FirebaseManager.getInstance().getEventsCollection()
                .whereEqualTo("clubId", clubId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Event event = doc.toObject(Event.class);
                            if (event != null) {
                                event.setId(doc.getId());
                                eventList.add(event);
                            }
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                    tvNoEvents.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> tvNoEvents.setVisibility(View.VISIBLE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
        loadClubContent();
    }
}
