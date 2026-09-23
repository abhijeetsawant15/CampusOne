package com.campusone.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.models.Announcement;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    public interface OnAnnouncementActionListener {
        void onEditAnnouncement(Announcement announcement);
        void onDeleteAnnouncement(Announcement announcement);
    }

    private List<Announcement> announcementList;
    private OnAnnouncementActionListener actionListener;
    private boolean canManage = false;
    private String currentClubId = null;

    public AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }

    public void setManagementPermissions(boolean canManage, String currentClubId, OnAnnouncementActionListener actionListener) {
        this.canManage = canManage;
        this.currentClubId = currentClubId;
        this.actionListener = actionListener;
        notifyDataSetChanged();
    }

    public void updateAnnouncements(List<Announcement> announcements) {
        this.announcementList = announcements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);

        holder.categoryTextView.setText(announcement.getCategory());
        holder.dateTextView.setText(announcement.getDate());
        holder.titleTextView.setText(announcement.getTitle());
        holder.descTextView.setText(announcement.getDescription());
        holder.authorTextView.setText("By " + (announcement.getAuthorName() != null ? announcement.getAuthorName() : "Administration"));

        // Manage actions visibility
        boolean allowEdit = false;
        if (canManage) {
            if (currentClubId == null || currentClubId.isEmpty() || "admin".equalsIgnoreCase(currentClubId)) {
                // Admin can edit/delete all
                allowEdit = true;
            } else if (currentClubId.equalsIgnoreCase(announcement.getClubId())) {
                // Club member can only edit/delete their own club's announcements
                allowEdit = true;
            }
        }

        if (allowEdit && actionListener != null) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v -> actionListener.onEditAnnouncement(announcement));
            holder.deleteButton.setOnClickListener(v -> actionListener.onDeleteAnnouncement(announcement));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return announcementList != null ? announcementList.size() : 0;
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, dateTextView, titleTextView, descTextView, authorTextView;
        ImageButton editButton, deleteButton;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.announcement_category);
            dateTextView = itemView.findViewById(R.id.announcement_date);
            titleTextView = itemView.findViewById(R.id.announcement_title);
            descTextView = itemView.findViewById(R.id.announcement_desc);
            authorTextView = itemView.findViewById(R.id.announcement_author);
            editButton = itemView.findViewById(R.id.btn_edit_announcement);
            deleteButton = itemView.findViewById(R.id.btn_delete_announcement);
        }
    }
}
