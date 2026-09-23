package com.campusone.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campusone.app.R;
import com.campusone.app.models.Club;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    public interface OnClubClickListener {
        void onClubClick(Club club);
    }

    private List<Club> clubList;
    private OnClubClickListener listener;

    public ClubAdapter(List<Club> clubList) {
        this.clubList = clubList;
    }

    public ClubAdapter(List<Club> clubList, OnClubClickListener listener) {
        this.clubList = clubList;
        this.listener = listener;
    }

    public void updateClubs(List<Club> clubs) {
        this.clubList = clubs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = clubList.get(position);
        holder.nameTextView.setText(club.getName());
        holder.descTextView.setText(club.getDescription());
        holder.iconImageView.setImageResource(club.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClubClick(club);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubList != null ? clubList.size() : 0;
    }

    static class ClubViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;
        TextView descTextView;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.club_icon);
            nameTextView = itemView.findViewById(R.id.club_name);
            descTextView = itemView.findViewById(R.id.club_desc);
        }
    }
}