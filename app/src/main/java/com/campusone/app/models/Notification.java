package com.campusone.app.models;

public class Notification {
    private String id;
    private String title;
    private String description;
    private String date;
    private String category; // "COLLEGE ANNOUNCEMENT", "CLUB ANNOUNCEMENT", "CAMPUS UPDATE", etc.
    private long timestamp;

    public Notification() {
        // Required for Firestore
    }

    public Notification(String id, String title, String description, String date, String category, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.timestamp = timestamp;
    }

    public Notification(String title, String description, String date, String category) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}