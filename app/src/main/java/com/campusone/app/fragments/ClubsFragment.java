package com.campusone.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.campusone.app.ClubDetailActivity;
import com.campusone.app.R;
import com.campusone.app.adapters.ClubAdapter;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.firebase.SampleDataSeeder;
import com.campusone.app.models.Club;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClubsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClubAdapter adapter;
    private List<Club> allClubsList;
    private List<Club> displayedClubsList;
    private ProgressBar progressBar;
    private View emptyState;
    private TextInputEditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        recyclerView = view.findViewById(R.id.clubs_recycler_view);
        progressBar = view.findViewById(R.id.clubs_progress);
        emptyState = view.findViewById(R.id.clubs_empty_state);
        etSearch = view.findViewById(R.id.et_search_clubs);

        allClubsList = new ArrayList<>();
        displayedClubsList = new ArrayList<>();

        adapter = new ClubAdapter(displayedClubsList, this::openClubDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClubs(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadClubsFromFirestore();

        return view;
    }

    private void openClubDetails(Club club) {
        Intent intent = new Intent(getActivity(), ClubDetailActivity.class);
        intent.putExtra("clubId", club.getClubId());
        intent.putExtra("name", club.getName());
        intent.putExtra("description", club.getDescription());
        intent.putExtra("category", club.getCategory());
        startActivity(intent);
    }

    private void loadClubsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        FirebaseManager.getInstance().getClubsCollection()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    allClubsList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Club club = doc.toObject(Club.class);
                            if (club != null) {
                                club.setClubId(doc.getId());
                                allClubsList.add(club);
                            }
                        }
                    } else {
                        // If empty, auto-seed and reload
                        SampleDataSeeder.seedInitialDataIfEmpty(FirebaseManager.getInstance().getFirestore());
                        loadFallbackClubs();
                    }
                    filterClubs(etSearch.getText() != null ? etSearch.getText().toString() : "");
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Loading offline club directory...", Toast.LENGTH_SHORT).show();
                    loadFallbackClubs();
                    filterClubs(etSearch.getText() != null ? etSearch.getText().toString() : "");
                });
    }

    private void loadFallbackClubs() {
        allClubsList.clear();
        allClubsList.add(new Club("nss", "NSS", "Community service, volunteering and social responsibility activities across college.", "Social Service", true, 0));
        allClubsList.add(new Club("csi", "CSI", "Technology, programming, computing activities and technical learning for computer engineering.", "Technical", true, 0));
        allClubsList.add(new Club("gdg", "GDG", "Developer-focused learning, technology workshops, open-source and modern developer ecosystem activities.", "Technical", true, 0));
        allClubsList.add(new Club("tpc", "TPC", "Training, placement preparation, resume building, career development and employability activities.", "Career", true, 0));
        allClubsList.add(new Club("tapas", "TAPAS", "Student personality development, aptitude training, soft skills and holistic student growth.", "Development", true, 0));
        allClubsList.add(new Club("student_council", "Student Council", "Student representation, campus governance coordination and college festival leadership.", "Leadership", true, 0));
        allClubsList.add(new Club("ieee", "IEEE", "Engineering, technology research, professional networking and technical learning.", "Technical", true, 0));
        allClubsList.add(new Club("satellite_club", "Satellite Club", "Space technology, small satellite research, aerospace and telemetry technical projects.", "Aerospace", true, 0));
        allClubsList.add(new Club("spark_racing", "Spark Racing Team", "Student formula electric vehicle development, powertrain and technical racing projects.", "Automotive", true, 0));
        allClubsList.add(new Club("hyperion_racing", "Hyperion Racing Team", "Student automotive engineering, combustion engine vehicle design and racing competitions.", "Automotive", true, 0));
        allClubsList.add(new Club("vanguard_racing", "Vanguard Racing Team", "All-terrain vehicle design, mechanical engineering, racing and technical innovation.", "Automotive", true, 0));
    }

    private void filterClubs(String query) {
        displayedClubsList.clear();
        String lowerQuery = query.toLowerCase().trim();
        for (Club club : allClubsList) {
            if (lowerQuery.isEmpty() ||
                    (club.getName() != null && club.getName().toLowerCase().contains(lowerQuery)) ||
                    (club.getDescription() != null && club.getDescription().toLowerCase().contains(lowerQuery))) {
                displayedClubsList.add(club);
            }
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(displayedClubsList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClubsFromFirestore();
    }
}