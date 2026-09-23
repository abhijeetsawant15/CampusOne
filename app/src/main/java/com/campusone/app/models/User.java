package com.campusone.app.models;

public class User {
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_CLUB_MEMBER = "club_member";

    private String uid;
    private String name;
    private String email;
    private String department;
    private String year;
    private String division;
    private String role; // "student", "admin", "club_member"
    private String clubId; // Assigned club id if role is club_member
    private long createdAt;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String name, String email, String department, String year, String division, String role, String clubId, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
        this.division = division;
        this.role = role;
        this.clubId = clubId;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getRole() {
        return role != null ? role : "student";
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
