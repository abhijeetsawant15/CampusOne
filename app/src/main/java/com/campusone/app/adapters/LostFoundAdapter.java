package com.campusone.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.models.LostFoundItem;
import com.campusone.app.utils.ImageLoader;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LostFoundAdapter extends RecyclerView.Adapter<LostFoundAdapter.LostFoundViewHolder> {

    public interface OnLostFoundActionListener {
        void onDeleteItem(LostFoundItem item);
    }

    private List<LostFoundItem> itemList;
    private OnLostFoundActionListener actionListener;
    private String currentUserId;
    private boolean isAdmin = false;

    public LostFoundAdapter(List<LostFoundItem> itemList) {
        this.itemList = itemList;
    }

    public void setUserContext(String currentUserId, boolean isAdmin, OnLostFoundActionListener actionListener) {
        this.currentUserId = currentUserId;
        this.isAdmin = isAdmin;
        this.actionListener = actionListener;
        notifyDataSetChanged();
    }

    public void updateItems(List<LostFoundItem> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        LostFoundItem item = itemList.get(position);
        Context context = holder.itemView.getContext();

        holder.titleTextView.setText(item.getTitle());
        holder.locationTextView.setText(item.getLocation());
        holder.dateTextView.setText(item.getDate());
        holder.descriptionTextView.setText(item.getDescription());
        holder.reporterTextView.setText("Reported by: " + (item.getReporterName() != null ? item.getReporterName() : "Student"));
        holder.contactTextView.setText(item.getContact());

        if ("FOUND".equalsIgnoreCase(item.getStatus())) {
            holder.statusBadge.setText("FOUND");
            holder.statusBadge.setBackgroundResource(R.drawable.bg_status_found);
            holder.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_found_text));
        } else {
            holder.statusBadge.setText("LOST");
            holder.statusBadge.setBackgroundResource(R.drawable.bg_status_lost);
            holder.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_lost_text));
        }

        // Display photo if available
        if (item.getImageUrl() != null && !item.getImageUrl().trim().isEmpty()) {
            holder.imageContainerView.setVisibility(View.VISIBLE);
            ImageLoader.load(item.getImageUrl(), holder.itemImageView);
        } else {
            holder.imageContainerView.setVisibility(View.GONE);
        }

        // Contact button action
        holder.contactButton.setOnClickListener(v -> {
            String contact = item.getContact();
            if (contact != null && !contact.trim().isEmpty()) {
                if (contact.contains("@")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + contact.trim()));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding CampusOne Lost & Found: " + item.getTitle());
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
                } else {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + contact.trim()));
                    context.startActivity(dialIntent);
                }
            } else {
                Toast.makeText(context, "No contact details provided", Toast.LENGTH_SHORT).show();
            }
        });

        // Allow delete if user is admin or the author
        boolean canDelete = isAdmin || (currentUserId != null && currentUserId.equals(item.getUserId()));
        if (canDelete && actionListener != null) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> actionListener.onDeleteItem(item));
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    static class LostFoundViewHolder extends RecyclerView.ViewHolder {
        TextView statusBadge, dateTextView, titleTextView, locationTextView, descriptionTextView, reporterTextView, contactTextView;
        MaterialButton contactButton;
        ImageButton deleteButton;
        ImageView itemImageView;
        View imageContainerView;

        public LostFoundViewHolder(@NonNull View itemView) {
            super(itemView);
            statusBadge = itemView.findViewById(R.id.lf_status_badge);
            dateTextView = itemView.findViewById(R.id.lf_date);
            titleTextView = itemView.findViewById(R.id.lf_title);
            locationTextView = itemView.findViewById(R.id.lf_location);
            descriptionTextView = itemView.findViewById(R.id.lf_description);
            reporterTextView = itemView.findViewById(R.id.lf_reporter);
            contactTextView = itemView.findViewById(R.id.lf_contact);
            contactButton = itemView.findViewById(R.id.btn_contact_reporter);
            deleteButton = itemView.findViewById(R.id.btn_delete_lf);
            itemImageView = itemView.findViewById(R.id.lf_item_image);
            imageContainerView = itemView.findViewById(R.id.card_lf_image_container);
        }
    }
}
