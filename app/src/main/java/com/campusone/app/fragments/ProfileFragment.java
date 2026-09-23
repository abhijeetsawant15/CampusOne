package com.campusone.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.campusone.app.AdminDashboardActivity;
import com.campusone.app.ClubMemberDashboardActivity;
import com.campusone.app.LoginActivity;
import com.campusone.app.R;
import com.campusone.app.firebase.FirebaseManager;
import com.campusone.app.models.User;
import com.campusone.app.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvRoleBadge, tvAssignedClub, tvEmail, tvDept, tvYear, tvDivision;
    private MaterialButton btnEditProfile, btnAdminPortal, btnClubPortal, btnLogout;
    private FirebaseManager firebaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseManager = FirebaseManager.getInstance();

        tvName = view.findViewById(R.id.profile_name);
        tvRoleBadge = view.findViewById(R.id.profile_role_badge);
        tvAssignedClub = view.findViewById(R.id.profile_assigned_club);
        tvEmail = view.findViewById(R.id.profile_email);
        tvDept = view.findViewById(R.id.profile_dept);
        tvYear = view.findViewById(R.id.profile_year);
        tvDivision = view.findViewById(R.id.profile_division);

        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnAdminPortal = view.findViewById(R.id.btn_admin_portal);
        btnClubPortal = view.findViewById(R.id.btn_club_portal);
        btnLogout = view.findViewById(R.id.logout_button);

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        btnAdminPortal.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AdminDashboardActivity.class));
        });

        btnClubPortal.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ClubMemberDashboardActivity.class));
        });

        btnLogout.setOnClickListener(v -> handleLogout());

        populateUserData();

        return view;
    }

    private void populateUserData() {
        User user = firebaseManager.getCachedUserProfile();
        if (user != null) {
            updateUIWithUser(user);
        } else {
            String uid = firebaseManager.getCurrentUserId();
            if (uid != null) {
                firebaseManager.fetchUserProfile(uid,
                        this::updateUIWithUser,
                        e -> {
                            // Fallback to basic Firebase user info
                            if (firebaseManager.getCurrentFirebaseUser() != null) {
                                tvEmail.setText(firebaseManager.getCurrentFirebaseUser().getEmail());
                            }
                        });
            }
        }
    }

    private void updateUIWithUser(User user) {
        if (!isAdded()) return;

        tvName.setText(user.getName() != null ? user.getName() : "Student");
        tvEmail.setText(user.getEmail());
        tvDept.setText(user.getDepartment() != null ? user.getDepartment() : "Not Specified");
        tvYear.setText(user.getYear() != null ? user.getYear() : "Not Specified");
        tvDivision.setText(user.getDivision() != null && !user.getDivision().isEmpty() ? "Div " + user.getDivision() : "General");

        String role = user.getRole();
        if ("admin".equalsIgnoreCase(role)) {
            tvRoleBadge.setText("ADMIN");
            tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_admin);
            tvRoleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_admin_text));
            btnAdminPortal.setVisibility(View.VISIBLE);
            btnClubPortal.setVisibility(View.GONE);
            tvAssignedClub.setVisibility(View.GONE);
        } else if ("club_member".equalsIgnoreCase(role)) {
            tvRoleBadge.setText("CLUB MEMBER");
            tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_club);
            tvRoleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_club_text));
            btnAdminPortal.setVisibility(View.GONE);
            btnClubPortal.setVisibility(View.VISIBLE);
            tvAssignedClub.setVisibility(View.VISIBLE);
            tvAssignedClub.setText("Assigned Club: " + (user.getClubId() != null ? user.getClubId().toUpperCase() : "None"));
        } else {
            tvRoleBadge.setText("STUDENT");
            tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_student);
            tvRoleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_student_text));
            btnAdminPortal.setVisibility(View.GONE);
            btnClubPortal.setVisibility(View.GONE);
            tvAssignedClub.setVisibility(View.GONE);
        }
    }

    private void showEditProfileDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etName = dialogView.findViewById(R.id.et_edit_name);
        TextInputEditText etDept = dialogView.findViewById(R.id.et_edit_dept);
        TextInputEditText etYear = dialogView.findViewById(R.id.et_edit_year);
        TextInputEditText etDivision = dialogView.findViewById(R.id.et_edit_division);

        User currentUser = firebaseManager.getCachedUserProfile();
        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etDept.setText(currentUser.getDepartment());
            etYear.setText(currentUser.getYear());
            etDivision.setText(currentUser.getDivision());
        }

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String newDept = etDept.getText() != null ? etDept.getText().toString().trim() : "";
                    String newYear = etYear.getText() != null ? etYear.getText().toString().trim() : "";
                    String newDiv = etDivision.getText() != null ? etDivision.getText().toString().trim() : "";

                    if (!ValidationUtils.isNotEmpty(newName)) {
                        Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = firebaseManager.getCurrentUserId();
                    if (uid != null) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", newName);
                        updates.put("department", newDept);
                        updates.put("year", newYear);
                        updates.put("division", newDiv);

                        firebaseManager.getUsersCollection().document(uid).update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                    if (currentUser != null) {
                                        currentUser.setName(newName);
                                        currentUser.setDepartment(newDept);
                                        currentUser.setYear(newYear);
                                        currentUser.setDivision(newDiv);
                                        updateUIWithUser(currentUser);
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleLogout() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out of CampusOne?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    firebaseManager.signOut();
                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateUserData();
    }
}