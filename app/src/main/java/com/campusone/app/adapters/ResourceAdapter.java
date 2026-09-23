package com.campusone.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.models.Resource;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {

    public interface OnResourceActionListener {
        void onEditResource(Resource resource);
        void onDeleteResource(Resource resource);
    }

    private List<Resource> resourceList;
    private OnResourceActionListener actionListener;
    private boolean isAdmin = false;
    private String currentUserId = null;

    public ResourceAdapter(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public void setUserPermissions(String currentUserId, boolean isAdmin, OnResourceActionListener actionListener) {
        this.currentUserId = currentUserId;
        this.isAdmin = isAdmin;
        this.actionListener = actionListener;
        notifyDataSetChanged();
    }

    public void setAdminPermissions(boolean isAdmin, OnResourceActionListener actionListener) {
        setUserPermissions(null, isAdmin, actionListener);
    }

    public void updateResources(List<Resource> resources) {
        this.resourceList = resources;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resource, parent, false);
        return new ResourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        Resource resource = resourceList.get(position);

        holder.categoryTextView.setText(resource.getCategory() != null ? resource.getCategory() : "Academic Resources");
        holder.dateTextView.setText(resource.getDate() != null ? resource.getDate() : "");
        holder.titleTextView.setText(resource.getTitle() != null ? resource.getTitle() : "");
        holder.descTextView.setText(resource.getDescription() != null ? resource.getDescription() : "");

        if (resource.getDepartment() != null && !resource.getDepartment().trim().isEmpty()) {
            holder.deptTextView.setText(resource.getDepartment().trim());
            holder.deptTextView.setVisibility(View.VISIBLE);
        } else {
            holder.deptTextView.setVisibility(View.GONE);
        }

        if (resource.getUploaderName() != null && !resource.getUploaderName().trim().isEmpty()) {
            holder.uploaderTextView.setText("Shared by: " + resource.getUploaderName().trim());
            holder.uploaderTextView.setVisibility(View.VISIBLE);
        } else {
            holder.uploaderTextView.setVisibility(View.GONE);
        }

        holder.openButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            String url = resource.getUrl();
            if (url != null && !url.trim().isEmpty()) {
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://" + url;
                    }
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No link provided for this resource", Toast.LENGTH_SHORT).show();
            }
        });

        boolean canManage = (isAdmin || (currentUserId != null && currentUserId.equals(resource.getUploaderUid())))
                && actionListener != null;

        if (canManage) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v -> actionListener.onEditResource(resource));
            holder.deleteButton.setOnClickListener(v -> actionListener.onDeleteResource(resource));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return resourceList != null ? resourceList.size() : 0;
    }

    static class ResourceViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, deptTextView, dateTextView, titleTextView, descTextView, uploaderTextView;
        MaterialButton openButton;
        ImageButton editButton, deleteButton;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.resource_category);
            deptTextView = itemView.findViewById(R.id.resource_department);
            dateTextView = itemView.findViewById(R.id.resource_date);
            titleTextView = itemView.findViewById(R.id.resource_title);
            descTextView = itemView.findViewById(R.id.resource_desc);
            uploaderTextView = itemView.findViewById(R.id.resource_uploader);
            openButton = itemView.findViewById(R.id.btn_open_resource);
            editButton = itemView.findViewById(R.id.btn_edit_resource);
            deleteButton = itemView.findViewById(R.id.btn_delete_resource);
        }
    }
}
