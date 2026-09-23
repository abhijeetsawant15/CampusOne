package com.campusone.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.models.Event;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public interface OnEventActionListener {
        void onEditEvent(Event event);
        void onDeleteEvent(Event event);
    }

    private List<Event> eventList;
    private OnEventClickListener clickListener;
    private OnEventActionListener actionListener;
    private boolean canManage = false;
    private String currentClubId = null; // if club_member, can only manage events for this club

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    public EventAdapter(List<Event> eventList, OnEventClickListener clickListener) {
        this.eventList = eventList;
        this.clickListener = clickListener;
    }

    public void setManagementPermissions(boolean canManage, String currentClubId, OnEventActionListener actionListener) {
        this.canManage = canManage;
        this.currentClubId = currentClubId;
        this.actionListener = actionListener;
        notifyDataSetChanged();
    }

    public void updateEvents(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.titleTextView.setText(event.getTitle());
        holder.dateTextView.setText(event.getDate());
        holder.timeTextView.setText(event.getTime());
        holder.venueTextView.setText(event.getVenue());
        holder.organizerTextView.setText(event.getOrganizer());
        holder.descPreviewTextView.setText(event.getDescription());

        // Manage actions visibility
        boolean allowEdit = false;
        if (canManage) {
            if (currentClubId == null || currentClubId.isEmpty() || "admin".equalsIgnoreCase(currentClubId)) {
                // Admin can edit/delete all
                allowEdit = true;
            } else if (currentClubId.equalsIgnoreCase(event.getClubId())) {
                // Club member can only edit/delete their own club's events
                allowEdit = true;
            }
        }

        if (allowEdit && actionListener != null) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v -> actionListener.onEditEvent(event));
            holder.deleteButton.setOnClickListener(v -> actionListener.onDeleteEvent(event));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        View.OnClickListener openDetails = v -> {
            if (clickListener != null) {
                clickListener.onEventClick(event);
            }
        };

        holder.itemView.setOnClickListener(openDetails);
        holder.detailsButton.setOnClickListener(openDetails);
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView organizerTextView, dateTextView, titleTextView, timeTextView, venueTextView, descPreviewTextView;
        MaterialButton detailsButton;
        ImageButton editButton, deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            organizerTextView = itemView.findViewById(R.id.event_organizer);
            dateTextView = itemView.findViewById(R.id.event_date);
            titleTextView = itemView.findViewById(R.id.event_title);
            timeTextView = itemView.findViewById(R.id.event_time);
            venueTextView = itemView.findViewById(R.id.event_venue);
            descPreviewTextView = itemView.findViewById(R.id.event_desc_preview);
            detailsButton = itemView.findViewById(R.id.btn_event_details);
            editButton = itemView.findViewById(R.id.btn_edit_event);
            deleteButton = itemView.findViewById(R.id.btn_delete_event);
        }
    }
}