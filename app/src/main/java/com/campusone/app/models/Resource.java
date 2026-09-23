package com.campusone.app.models;

public class Resource {
    private String id;
    private String title;
    private String description;
    private String category; // e.g. "Student Notes", "Study Material", "Student Guide", "University Forms", "Academic Resources", "Other"
    private String department;
    private String url;
    private String uploaderUid;
    private String uploaderName;
    private String date; // or createdAt
    private String createdAt;
    private String status; // "APPROVED", "PENDING"
    private long timestamp;

    public Resource() {
        // Required for Firestore
    }

    public Resource(String id, String title, String description, String category, String url, String date, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
        this.date = date;
        this.createdAt = date;
        this.timestamp = timestamp;
        this.status = "APPROVED";
    }

    public Resource(String id, String title, String description, String category, String department, String url, String uploaderUid, String uploaderName, String date, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.department = department;
        this.url = url;
        this.uploaderUid = uploaderUid;
        this.uploaderName = uploaderName;
        this.date = date;
        this.createdAt = date;
        this.status = "APPROVED";
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUploaderUid() {
        return uploaderUid;
    }

    public void setUploaderUid(String uploaderUid) {
        this.uploaderUid = uploaderUid;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getDate() {
        return date != null ? date : createdAt;
    }

    public void setDate(Object date) {
        if (date instanceof String) {
            this.date = (String) date;
        } else if (date != null) {
            this.date = String.valueOf(date);
        } else {
            this.date = null;
        }
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : date;
    }

    public void setCreatedAt(Object createdAt) {
        if (createdAt instanceof String) {
            this.createdAt = (String) createdAt;
        } else if (createdAt != null) {
            this.createdAt = String.valueOf(createdAt);
        } else {
            this.createdAt = null;
        }
    }

    public String getStatus() {
        return status != null ? status : "APPROVED";
    }

    public void setStatus(String status) {
        this.status = status;
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
