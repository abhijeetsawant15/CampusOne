package com.campusone.app.models;

import com.campusone.app.R;

public class Club {
    private String clubId;
    private String name;
    private String description;
    private String category;
    private boolean active;
    private long createdAt;

    public Club() {
        // Required for Firestore
    }

    public Club(String clubId, String name, String description, String category, boolean active, long createdAt) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.active = active;
        this.createdAt = createdAt;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getIconResId() {
        if (clubId == null) return R.drawable.ic_clubs;
        switch (clubId.toLowerCase()) {
            case "nss":
                return R.drawable.ic_club_nss;
            case "csi":
                return R.drawable.ic_club_csi;
            case "gdg":
                return R.drawable.ic_club_gdg;
            case "tpc":
                return R.drawable.ic_club_tpc;
            case "tapas":
                return R.drawable.ic_club_tapas;
            case "student_council":
                return R.drawable.ic_club_student_council;
            case "ieee":
                return R.drawable.ic_club_ieee;
            case "satellite_club":
                return R.drawable.ic_club_satellite_club;
            case "spark_racing":
                return R.drawable.ic_club_spark_racing;
            case "hyperion_racing":
                return R.drawable.ic_club_hyperion_racing;
            case "vanguard_racing":
                return R.drawable.ic_club_vanguard_racing;
            default:
                return R.drawable.ic_clubs;
        }
    }
}