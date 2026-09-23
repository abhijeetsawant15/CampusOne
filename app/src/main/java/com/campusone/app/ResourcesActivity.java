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

import com.campusone.app.adapters.ResourceAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Resource;
import com.campusone.app.models.User;
import com.campusone.app.utils.InsetsUtils;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResourceAdapter adapter;
    private List<Resource> allResourcesList;
    private List<Resource> displayedResourcesList;
    private ProgressBar progressBar;
    private View emptyState;
    private FloatingActionButton fabAddResource;
    private ChipGroup chipGroup;
    private String selectedCategory = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        InsetsUtils.applySystemBarInsets(findViewById(R.id.root_resources));

        Toolbar toolbar = findViewById(R.id.toolbar_resources);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.resources_recycler_view);
        progressBar = findViewById(R.id.resources_progress);
        emptyState = findViewById(R.id.resources_empty_state);
        fabAddResource = findViewById(R.id.fab_add_resource);
        chipGroup = findViewById(R.id.resources_chip_group);

        allResourcesList = new ArrayList<>();
        displayedResourcesList = new ArrayList<>();

        adapter = new ResourceAdapter(displayedResourcesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupUserControls();

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_res_notes) {
                selectedCategory = "Notes & Study Material";
            } else if (checkedId == R.id.chip_res_guide) {
                selectedCategory = "Student Guide";
            } else if (checkedId == R.id.chip_res_forms) {
                selectedCategory = "University Forms";
            } else if (checkedId == R.id.chip_res_academic) {
                selectedCategory = "Academic Resources";
            } else if (checkedId == R.id.chip_res_other) {
                selectedCategory = "Other";
            } else {
                selectedCategory = "ALL";
            }
            applyCategoryFilter();
        });

        fabAddResource.setOnClickListener(v -> {
            Intent intent = new Intent(ResourcesActivity.this, AddEditResourceActivity.class);
            startActivity(intent);
        });

        loadResources();
    }

    private void setupUserControls() {
        FirebaseManager fm = FirebaseManager.getInstance();
        User user = fm.getCachedUserProfile();
        String currentUid = fm.getCurrentUserId();
        boolean isAdmin = user != null && "admin".equalsIgnoreCase(user.getRole());

        // Students and admins can share resources
        fabAddResource.setVisibility(fm.isUserLoggedIn() ? View.VISIBLE : View.GONE);

        adapter.setUserPermissions(currentUid, isAdmin, new ResourceAdapter.OnResourceActionListener() {
            @Override
            public void onEditResource(Resource resource) {
                Intent intent = new Intent(ResourcesActivity.this, AddEditResourceActivity.class);
                intent.putExtra("resourceId", resource.getId());
                intent.putExtra("title", resource.getTitle());
                intent.putExtra("description", resource.getDescription());
                intent.putExtra("category", resource.getCategory());
                intent.putExtra("department", resource.getDepartment());
                intent.putExtra("url", resource.getUrl());
                startActivity(intent);
            }

            @Override
            public void onDeleteResource(Resource resource) {
                confirmDeleteResource(resource);
            }
        });
    }

    private void confirmDeleteResource(Resource resource) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Resource")
                .setMessage("Are you sure you want to delete '" + resource.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (resource.getId() != null) {
                        FirebaseManager.getInstance().getResourcesCollection().document(resource.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Resource deleted", Toast.LENGTH_SHORT).show();
                                    loadResources();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadResources() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        FirebaseManager.getInstance().getResourcesCollection()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    allResourcesList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Resource r = doc.toObject(Resource.class);
                            if (r != null) {
                                r.setId(doc.getId());
                                allResourcesList.add(r);
                            }
                        }
                    }
                    applyCategoryFilter();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Unable to load resources. Please check connection.", Toast.LENGTH_SHORT).show();
                    applyCategoryFilter();
                });
    }

    private void applyCategoryFilter() {
        displayedResourcesList.clear();
        for (Resource r : allResourcesList) {
            if ("ALL".equalsIgnoreCase(selectedCategory) ||
                    (r.getCategory() != null && r.getCategory().equalsIgnoreCase(selectedCategory))) {
                displayedResourcesList.add(r);
            }
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(displayedResourcesList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUserControls();
        loadResources();
    }
}