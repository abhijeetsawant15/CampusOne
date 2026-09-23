package com.campusone.app.models;

public class LostFoundItem {
    private String id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String status; // "LOST" or "FOUND"
    private String reporterName;
    private String contact;
    private String imageUrl;
    private String userId;
    private long timestamp;

    public LostFoundItem() {
        // Required for Firestore
    }

    public LostFoundItem(String id, String title, String description, String location, String date, String status, String reporterName, String contact, String imageUrl, String userId, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.status = status;
        this.reporterName = reporterName;
        this.contact = contact;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status != null ? status : "LOST";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String imageStoragePath;

    public String getImageStoragePath() {
        return imageStoragePath;
    }

    public void setImageStoragePath(String imageStoragePath) {
        this.imageStoragePath = imageStoragePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        if (timestamp instanceof Number) {
            this.timestamp = ((Number) timestamp).longValue();
        } else if (timestamp != null) {
            try {
                this.timestamp = Long.parseLong(timestamp.toString());
            } catch (Exception e) {
                this.timestamp = System.currentTimeMillis();
            }
        }
    }
}
