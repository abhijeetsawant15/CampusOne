package com.campusone.app.models;

public class Announcement {
    private String id;
    private String title;
    private String description;
    private String date;
    private String category; // "COLLEGE ANNOUNCEMENT" or "CLUB ANNOUNCEMENT"
    private String clubId; // "college" for admin announcements, or clubId for club announcements
    private String authorName;
    private String authorId;
    private long timestamp;

    public Announcement() {
        // Required for Firestore
    }

    public Announcement(String id, String title, String description, String date, String category, String clubId, String authorName, String authorId, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.clubId = clubId;
        this.authorName = authorName;
        this.authorId = authorId;
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

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
