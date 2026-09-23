package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.firebase.SampleDataSeeder;
import com.campusone.app.fragments.ClubsFragment;
import com.campusone.app.fragments.EventsFragment;
import com.campusone.app.fragments.HomeFragment;
import com.campusone.app.fragments.NotificationsFragment;
import com.campusone.app.fragments.ProfileFragment;
import com.campusone.app.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;
    private MaterialToolbar toolbar;
    private MenuItem itemAdminDash;
    private MenuItem itemClubDash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseManager = FirebaseManager.getInstance();

        // If user is not logged in, route to LoginActivity
        if (!firebaseManager.isUserLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_admin_dashboard) {
                startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
                return true;
            } else if (id == R.id.action_club_dashboard) {
                startActivity(new Intent(MainActivity.this, ClubMemberDashboardActivity.class));
                return true;
            }
            return false;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_events) {
                selectedFragment = new EventsFragment();
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (itemId == R.id.nav_clubs) {
                selectedFragment = new ClubsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        loadUserProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_main_menu, menu);
        itemAdminDash = menu.findItem(R.id.action_admin_dashboard);
        itemClubDash = menu.findItem(R.id.action_club_dashboard);
        updateMenuVisibility();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin_dashboard) {
            startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
            return true;
        } else if (id == R.id.action_club_dashboard) {
            startActivity(new Intent(MainActivity.this, ClubMemberDashboardActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfile() {
        String uid = firebaseManager.getCurrentUserId();
        if (uid != null) {
            firebaseManager.fetchUserProfile(uid,
                    user -> {
                        if (user != null) {
                            String roleDisplay = user.getRole().toUpperCase();
                            toolbar.setSubtitle(user.getName() + " (" + roleDisplay + ")");
                            updateMenuVisibility();
                        }
                    },
                    e -> {
                        // Keep default subtitle if profile not found
                        toolbar.setSubtitle("Pillai College Ecosystem");
                    });
        }
    }

    private void updateMenuVisibility() {
        User user = firebaseManager.getCachedUserProfile();
        if (user != null) {
            String role = user.getRole();
            if (itemAdminDash != null) {
                itemAdminDash.setVisible("admin".equalsIgnoreCase(role));
            }
            if (itemClubDash != null) {
                itemClubDash.setVisible("club_member".equalsIgnoreCase(role));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firebaseManager.isUserLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }
        loadUserProfile();
    }
}