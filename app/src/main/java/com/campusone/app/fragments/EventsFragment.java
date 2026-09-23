package com.campusone.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.AddEditEventActivity;
import com.campusone.app.EventDetailActivity;
import com.campusone.app.R;
import com.campusone.app.adapters.EventAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Event;
import com.campusone.app.models.User;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> allEventsList;
    private List<Event> displayedEventsList;
    private ProgressBar progressBar;
    private View emptyState;
    private FloatingActionButton fabAddEvent;
    private ChipGroup chipGroup;
    private int currentFilter = 0; // 0: All, 1: College, 2: Clubs

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = view.findViewById(R.id.events_recycler_view);
        progressBar = view.findViewById(R.id.events_progress);
        emptyState = view.findViewById(R.id.events_empty_state);
        fabAddEvent = view.findViewById(R.id.fab_add_event);
        chipGroup = view.findViewById(R.id.events_chip_group);

        allEventsList = new ArrayList<>();
        displayedEventsList = new ArrayList<>();

        adapter = new EventAdapter(displayedEventsList, this::openEventDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setupPermissions();

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_events_college) {
                currentFilter = 1;
            } else if (checkedId == R.id.chip_events_clubs) {
                currentFilter = 2;
            } else {
                currentFilter = 0;
            }
            applyFilter();
        });

        fabAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditEventActivity.class);
            startActivity(intent);
        });

        loadEventsFromFirestore();

        return view;
    }

    private void setupPermissions() {
        FirebaseManager fm = FirebaseManager.getInstance();
        User user = fm.getCachedUserProfile();
        if (user != null) {
            String role = user.getRole();
            boolean canAdd = "admin".equalsIgnoreCase(role) || "club_member".equalsIgnoreCase(role);
            fabAddEvent.setVisibility(canAdd ? View.VISIBLE : View.GONE);

            adapter.setManagementPermissions(canAdd, user.getClubId(), new EventAdapter.OnEventActionListener() {
                @Override
                public void onEditEvent(Event event) {
                    Intent intent = new Intent(getActivity(), AddEditEventActivity.class);
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

                @Override
                public void onDeleteEvent(Event event) {
                    confirmDeleteEvent(event);
                }
            });
        }
    }

    private void confirmDeleteEvent(Event event) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete '" + event.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (event.getId() != null) {
                        FirebaseManager.getInstance().getEventsCollection().document(event.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                                    loadEventsFromFirestore();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openEventDetails(Event event) {
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
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

    private void loadEventsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        FirebaseManager.getInstance().getEventsCollection()
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    allEventsList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Event event = doc.toObject(Event.class);
                            if (event != null) {
                                event.setId(doc.getId());
                                allEventsList.add(event);
                            }
                        }
                    }
                    applyFilter();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Unable to load events. Please check connection.", Toast.LENGTH_SHORT).show();
                    applyFilter();
                });
    }

    private void applyFilter() {
        displayedEventsList.clear();
        for (Event event : allEventsList) {
            String clubId = event.getClubId() != null ? event.getClubId().toLowerCase() : "";
            boolean isCollege = "college".equals(clubId) || "general".equals(clubId) || clubId.isEmpty();

            if (currentFilter == 0) {
                displayedEventsList.add(event);
            } else if (currentFilter == 1 && isCollege) {
                displayedEventsList.add(event);
            } else if (currentFilter == 2 && !isCollege) {
                displayedEventsList.add(event);
            }
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(displayedEventsList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPermissions();
        loadEventsFromFirestore();
    }
}