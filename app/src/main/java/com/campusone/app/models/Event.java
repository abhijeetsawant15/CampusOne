package com.campusone.app.models;

public class Event {
    private String id;
    private String title;
    private String date;
    private String time;
    private String venue;
    private String description;
    private String organizer;
    private String clubId;
    private long timestamp;

    public Event() {
        // Required for Firestore
    }

    public Event(String id, String title, String date, String time, String venue, String description, String organizer, String clubId, long timestamp) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.description = description;
        this.organizer = organizer;
        this.clubId = clubId;
        this.timestamp = timestamp;
    }

    // Constructor for backward compatibility if needed
    public Event(String title, String date, String time, String venue, String description, String organizer) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.description = description;
        this.organizer = organizer;
        this.clubId = "general";
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}