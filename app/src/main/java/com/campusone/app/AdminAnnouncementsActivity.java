package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.adapters.AnnouncementAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Announcement;
import com.campusone.app.utils.InsetsUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminAnnouncementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcementList;
    private ProgressBar progressBar;
    private TextView tvNoAnnouncements;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_announcements);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_admin_announcements));

        Toolbar toolbar = findViewById(R.id.toolbar_admin_announcements);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rv_admin_announcements);
        progressBar = findViewById(R.id.progress_admin_announcements);
        tvNoAnnouncements = findViewById(R.id.tv_no_admin_announcements);
        fabAdd = findViewById(R.id.fab_add_admin_announcement);

        announcementList = new ArrayList<>();
        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Enable admin management actions
        adapter.setManagementPermissions(true, "admin", new AnnouncementAdapter.OnAnnouncementActionListener() {
            @Override
            public void onEditAnnouncement(Announcement announcement) {
                Intent intent = new Intent(AdminAnnouncementsActivity.this, AddEditAnnouncementActivity.class);
                intent.putExtra("announcementId", announcement.getId());
                intent.putExtra("title", announcement.getTitle());
                intent.putExtra("description", announcement.getDescription());
                intent.putExtra("category", announcement.getCategory());
                intent.putExtra("date", announcement.getDate());
                intent.putExtra("clubId", announcement.getClubId());
                startActivity(intent);
            }

            @Override
            public void onDeleteAnnouncement(Announcement announcement) {
                confirmDelete(announcement);
            }
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AdminAnnouncementsActivity.this, AddEditAnnouncementActivity.class);
            startActivity(intent);
        });

        loadAnnouncements();
    }

    private void confirmDelete(Announcement announcement) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Announcement")
                .setMessage("Are you sure you want to delete '" + announcement.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (announcement.getId() != null) {
                        FirebaseManager.getInstance().getAnnouncementsCollection().document(announcement.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Announcement deleted", Toast.LENGTH_SHORT).show();
                                    loadAnnouncements();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadAnnouncements() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoAnnouncements.setVisibility(View.GONE);

        FirebaseManager.getInstance().getAnnouncementsCollection()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
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
                    adapter.notifyDataSetChanged();
                    tvNoAnnouncements.setVisibility(announcementList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading announcements: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvNoAnnouncements.setVisibility(announcementList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();
    }
}
