package com.campusone.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.EventDetailActivity;
import com.campusone.app.LostFoundActivity;
import com.campusone.app.R;
import com.campusone.app.ResourcesActivity;
import com.campusone.app.adapters.AnnouncementAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.Announcement;
import com.campusone.app.models.Event;
import com.campusone.app.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvWelcomeTitle, tvUserSubtitle, tvRoleBadge;
    private RecyclerView rvRecentAnnouncements;
    private AnnouncementAdapter announcementAdapter;
    private List<Announcement> announcementList;
    private Event featuredEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcomeTitle = view.findViewById(R.id.home_welcome_title);
        tvUserSubtitle = view.findViewById(R.id.home_user_subtitle);
        tvRoleBadge = view.findViewById(R.id.home_role_badge);
        rvRecentAnnouncements = view.findViewById(R.id.home_announcements_recycler);

        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(announcementList);
        rvRecentAnnouncements.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentAnnouncements.setAdapter(announcementAdapter);

        // Featured event default
        featuredEvent = new Event("evt_1", "TECH FEST 2026", "15 Oct 2026", "10:00 AM", "College Auditorium",
                "The flagship annual national technology festival featuring project exhibitions, robotics, paper presentations and hackathons.",
                "College Administration", "college", System.currentTimeMillis());

        view.findViewById(R.id.btn_home_highlight_details).setOnClickListener(v -> openEventDetails(featuredEvent));
        view.findViewById(R.id.highlight_event_card).setOnClickListener(v -> openEventDetails(featuredEvent));

        // Quick Access Cards
        view.findViewById(R.id.card_resources).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResourcesActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.card_lost_found).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LostFoundActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.card_quick_clubs).setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView nav = getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) {
                    nav.setSelectedItemId(R.id.nav_clubs);
                }
            }
        });

        view.findViewById(R.id.card_quick_events).setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView nav = getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) {
                    nav.setSelectedItemId(R.id.nav_events);
                }
            }
        });

        view.findViewById(R.id.tv_see_all_announcements).setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView nav = getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) {
                    nav.setSelectedItemId(R.id.nav_notifications);
                }
            }
        });

        displayUserInfo();
        loadRecentAnnouncements();

        return view;
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

    private void displayUserInfo() {
        FirebaseManager fm = FirebaseManager.getInstance();
        User user = fm.getCachedUserProfile();
        if (user != null) {
            tvWelcomeTitle.setText("Welcome, " + user.getName());
            String dept = user.getDepartment() != null ? user.getDepartment() : "MES Student";
            String year = user.getYear() != null ? " • " + user.getYear() : "";
            tvUserSubtitle.setText(dept + year);

            String role = user.getRole();
            if ("admin".equalsIgnoreCase(role)) {
                tvRoleBadge.setText("ADMIN");
                tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_admin);
                tvRoleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_admin_text));
            } else if ("club_member".equalsIgnoreCase(role)) {
                tvRoleBadge.setText("CLUB MEMBER");
                tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_club);
                tvRoleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_club_text));
            } else {
                tvRoleBadge.setText("STUDENT");
                tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_student);
                tvRoleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_student_text));
            }
        }
    }

    private void loadRecentAnnouncements() {
        FirebaseManager.getInstance().getAnnouncementsCollection()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        announcementList.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Announcement a = doc.toObject(Announcement.class);
                            if (a != null) {
                                a.setId(doc.getId());
                                announcementList.add(a);
                            }
                        }
                        announcementAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Fallback to empty/offline gracefully
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserInfo();
        loadRecentAnnouncements();
    }
}