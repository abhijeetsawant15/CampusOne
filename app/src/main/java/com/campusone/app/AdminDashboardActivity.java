package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.campusone.app.utils.InsetsUtils;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_admin_dashboard));

        Toolbar toolbar = findViewById(R.id.toolbar_admin_dash);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 1. Manage Announcements
        findViewById(R.id.card_admin_announcements).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminAnnouncementsActivity.class));
        });

        // 2. Manage Events
        findViewById(R.id.card_admin_events).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddEditEventActivity.class);
            startActivity(intent);
        });

        // 3. Manage Resources
        findViewById(R.id.card_admin_resources).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ResourcesActivity.class));
        });

        // 4. Manage Lost & Found
        findViewById(R.id.card_admin_lost_found).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, LostFoundActivity.class));
        });

        // 5. Manage Clubs
        findViewById(R.id.card_admin_clubs).setOnClickListener(v -> {
            // Take user to the clubs tab or open main activity with clubs tab
            finish();
        });

        // 6. Manage Users
        findViewById(R.id.card_admin_users).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class));
        });
    }
}
