package com.campusone.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.adapters.NotificationAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Notification;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> allNotificationsList;
    private List<Notification> displayedNotificationsList;
    private ProgressBar progressBar;
    private View emptyState;
    private ChipGroup chipGroup;
    private int currentFilter = 0; // 0: All, 1: College, 2: Clubs, 3: Campus

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        progressBar = view.findViewById(R.id.notif_progress);
        emptyState = view.findViewById(R.id.notif_empty_state);
        chipGroup = view.findViewById(R.id.notif_chip_group);

        allNotificationsList = new ArrayList<>();
        displayedNotificationsList = new ArrayList<>();

        adapter = new NotificationAdapter(displayedNotificationsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_notif_college) {
                currentFilter = 1;
            } else if (checkedId == R.id.chip_notif_clubs) {
                currentFilter = 2;
            } else if (checkedId == R.id.chip_notif_campus) {
                currentFilter = 3;
            } else {
                currentFilter = 0;
            }
            applyFilter();
        });

        loadNotificationsFromFirestore();

        return view;
    }

    private void loadNotificationsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        FirebaseManager.getInstance().getNotificationsCollection()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    allNotificationsList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Notification n = doc.toObject(Notification.class);
                            if (n != null) {
                                n.setId(doc.getId());
                                allNotificationsList.add(n);
                            }
                        }
                    }
                    applyFilter();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Unable to load notifications. Please check connection.", Toast.LENGTH_SHORT).show();
                    applyFilter();
                });
    }

    private void applyFilter() {
        displayedNotificationsList.clear();
        for (Notification n : allNotificationsList) {
            String cat = n.getCategory() != null ? n.getCategory().toUpperCase() : "";
            if (currentFilter == 0) {
                displayedNotificationsList.add(n);
            } else if (currentFilter == 1 && cat.contains("COLLEGE")) {
                displayedNotificationsList.add(n);
            } else if (currentFilter == 2 && cat.contains("CLUB")) {
                displayedNotificationsList.add(n);
            } else if (currentFilter == 3 && (cat.contains("CAMPUS") || cat.contains("UPDATE"))) {
                displayedNotificationsList.add(n);
            }
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(displayedNotificationsList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotificationsFromFirestore();
    }
}