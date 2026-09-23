package com.campusone.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public void updateNotifications(List<Notification> list) {
        this.notificationList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.category.setText(notification.getCategory());
        holder.title.setText(notification.getTitle());
        holder.desc.setText(notification.getDescription());
        holder.date.setText(notification.getDate());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView category, title, desc, date;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.notification_category);
            title = itemView.findViewById(R.id.notification_title);
            desc = itemView.findViewById(R.id.notification_desc);
            date = itemView.findViewById(R.id.notification_date);
        }
    }
}