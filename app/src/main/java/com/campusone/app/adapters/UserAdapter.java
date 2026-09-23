package com.campusone.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.models.User;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnChangeRoleListener {
        void onChangeRole(User user);
    }

    private List<User> userList;
    private OnChangeRoleListener changeRoleListener;

    public UserAdapter(List<User> userList, OnChangeRoleListener changeRoleListener) {
        this.userList = userList;
        this.changeRoleListener = changeRoleListener;
    }

    public void updateUsers(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        Context context = holder.itemView.getContext();

        holder.nameTextView.setText(user.getName() != null ? user.getName() : "Student");
        holder.emailTextView.setText(user.getEmail());

        String academicInfo = (user.getDepartment() != null ? user.getDepartment() : "General")
                + " | " + (user.getYear() != null ? user.getYear() : "FY")
                + (user.getDivision() != null && !user.getDivision().isEmpty() ? " - Div " + user.getDivision() : "");
        holder.academicInfoTextView.setText(academicInfo);

        String role = user.getRole();
        if ("admin".equalsIgnoreCase(role)) {
            holder.roleBadge.setText("ADMIN");
            holder.roleBadge.setBackgroundResource(R.drawable.bg_badge_admin);
            holder.roleBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_admin_text));
            holder.assignedClubTextView.setVisibility(View.GONE);
        } else if ("club_member".equalsIgnoreCase(role)) {
            holder.roleBadge.setText("CLUB MEMBER");
            holder.roleBadge.setBackgroundResource(R.drawable.bg_badge_club);
            holder.roleBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_club_text));
            holder.assignedClubTextView.setVisibility(View.VISIBLE);
            holder.assignedClubTextView.setText("Assigned Club: " + (user.getClubId() != null ? user.getClubId().toUpperCase() : "None"));
        } else {
            holder.roleBadge.setText("STUDENT");
            holder.roleBadge.setBackgroundResource(R.drawable.bg_badge_student);
            holder.roleBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_student_text));
            holder.assignedClubTextView.setVisibility(View.GONE);
        }

        holder.changeRoleButton.setOnClickListener(v -> {
            if (changeRoleListener != null) {
                changeRoleListener.onChangeRole(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, academicInfoTextView, roleBadge, assignedClubTextView;
        MaterialButton changeRoleButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.user_name);
            emailTextView = itemView.findViewById(R.id.user_email);
            academicInfoTextView = itemView.findViewById(R.id.user_academic_info);
            roleBadge = itemView.findViewById(R.id.user_role_badge);
            assignedClubTextView = itemView.findViewById(R.id.user_assigned_club);
            changeRoleButton = itemView.findViewById(R.id.btn_change_role);
        }
    }
}
