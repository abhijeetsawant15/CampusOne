package com.campusone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.adapters.LostFoundAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.LostFoundItem;
import com.campusone.app.models.User;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import com.campusone.app.utils.InsetsUtils;
import com.google.firebase.storage.FirebaseStorage;

public class LostFoundActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostFoundAdapter adapter;
    private List<LostFoundItem> allItemsList;
    private List<LostFoundItem> displayedItemsList;
    private ProgressBar progressBar;
    private View emptyState;
    private FloatingActionButton fabReportItem;
    private ChipGroup chipGroup;
    private int currentFilter = 0; // 0: All, 1: Lost, 2: Found

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_lost_found));

        Toolbar toolbar = findViewById(R.id.toolbar_lost_found);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.lf_recycler_view);
        progressBar = findViewById(R.id.lf_progress);
        emptyState = findViewById(R.id.lf_empty_state);
        fabReportItem = findViewById(R.id.fab_report_item);
        chipGroup = findViewById(R.id.lf_chip_group);

        allItemsList = new ArrayList<>();
        displayedItemsList = new ArrayList<>();

        adapter = new LostFoundAdapter(displayedItemsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupUserContext();

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_lf_lost) {
                currentFilter = 1;
            } else if (checkedId == R.id.chip_lf_found) {
                currentFilter = 2;
            } else {
                currentFilter = 0;
            }
            applyFilter();
        });

        fabReportItem.setOnClickListener(v -> {
            Intent intent = new Intent(LostFoundActivity.this, ReportLostFoundActivity.class);
            startActivity(intent);
        });

        loadItems();
    }

    private void setupUserContext() {
        FirebaseManager fm = FirebaseManager.getInstance();
        User user = fm.getCachedUserProfile();
        String currentUid = fm.getCurrentUserId();
        boolean isAdmin = user != null && "admin".equalsIgnoreCase(user.getRole());

        adapter.setUserContext(currentUid, isAdmin, this::confirmDeleteItem);
    }

    private void confirmDeleteItem(LostFoundItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to remove '" + item.getTitle() + "' from Lost & Found?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (item.getId() != null) {
                        FirebaseManager.getInstance().getLostFoundCollection().document(item.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    if (item.getImageStoragePath() != null && !item.getImageStoragePath().isEmpty()) {
                                        FirebaseStorage.getInstance().getReference().child(item.getImageStoragePath()).delete()
                                                .addOnFailureListener(e -> {/* ignore deletion failure for orphaned image */});
                                    }
                                    Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
                                    loadItems();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadItems() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        FirebaseManager.getInstance().getLostFoundCollection()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    allItemsList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            LostFoundItem item = doc.toObject(LostFoundItem.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                allItemsList.add(item);
                            }
                        }
                    }
                    applyFilter();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Unable to load items. Please check connection.", Toast.LENGTH_SHORT).show();
                    applyFilter();
                });
    }

    private void applyFilter() {
        displayedItemsList.clear();
        for (LostFoundItem item : allItemsList) {
            String status = item.getStatus() != null ? item.getStatus().toUpperCase() : "";
            if (currentFilter == 0) {
                displayedItemsList.add(item);
            } else if (currentFilter == 1 && "LOST".equals(status)) {
                displayedItemsList.add(item);
            } else if (currentFilter == 2 && "FOUND".equals(status)) {
                displayedItemsList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(displayedItemsList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUserContext();
        loadItems();
    }
}