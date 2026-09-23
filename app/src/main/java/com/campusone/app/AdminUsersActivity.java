package com.campusone.app;

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

import com.campusone.app.adapters.UserAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private ProgressBar progressBar;
    private TextView tvNoUsers;

    private final String[] ROLES = {"Student", "Admin", "Club Member"};
    private final String[] CLUB_NAMES = {
            "NSS", "CSI", "GDG", "TPC", "TAPAS", "Student Council",
            "IEEE", "Satellite Club", "Spark Racing Team", "Hyperion Racing Team", "Vanguard Racing Team"
    };
    private final String[] CLUB_IDS = {
            "nss", "csi", "gdg", "tpc", "tapas", "student_council",
            "ieee", "satellite_club", "spark_racing", "hyperion_racing", "vanguard_racing"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_admin_users));

        Toolbar toolbar = findViewById(R.id.toolbar_admin_users);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rv_admin_users);
        progressBar = findViewById(R.id.progress_admin_users);
        tvNoUsers = findViewById(R.id.tv_no_users);

        userList = new ArrayList<>();
        adapter = new UserAdapter(userList, this::promptChangeRole);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoUsers.setVisibility(View.GONE);

        FirebaseManager.getInstance().getUsersCollection()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    userList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            User u = doc.toObject(User.class);
                            if (u != null) {
                                u.setUid(doc.getId());
                                userList.add(u);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvNoUsers.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvNoUsers.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void promptChangeRole(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Select Role for " + user.getName())
                .setItems(ROLES, (dialog, which) -> {
                    if (which == 0) {
                        // Student
                        updateUserRole(user.getUid(), "student", null);
                    } else if (which == 1) {
                        // Admin
                        updateUserRole(user.getUid(), "admin", null);
                    } else if (which == 2) {
                        // Club Member -> prompt to choose assigned club
                        promptAssignClub(user);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void promptAssignClub(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Assign Club to " + user.getName())
                .setItems(CLUB_NAMES, (dialog, which) -> {
                    String selectedClubId = CLUB_IDS[which];
                    updateUserRole(user.getUid(), "club_member", selectedClubId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUserRole(String uid, String newRole, String newClubId) {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<>();
        updates.put("role", newRole);
        updates.put("clubId", newClubId);

        FirebaseManager.getInstance().getUsersCollection().document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Role updated successfully!", Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to update role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
