package com.campusone.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class ValidationUtils {

    // Strictly enforces @student.mes.ac.in for college students
    private static final String STUDENT_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@student\\.mes\\.ac\\.in$";
    private static final Pattern STUDENT_EMAIL_PATTERN = Pattern.compile(STUDENT_EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    // Standard email pattern for admin / club-members
    private static final String GENERAL_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern GENERAL_EMAIL_PATTERN = Pattern.compile(GENERAL_EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    public static boolean isValidStudentEmail(String email) {
        if (email == null) return false;
        return STUDENT_EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return GENERAL_EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.trim().length() >= 6;
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidUrl(String url) {
        if (url == null) return false;
        String trimmed = url.trim().toLowerCase(Locale.ROOT);
        return trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }

    public static boolean isGoogleDriveUrl(String url) {
        if (!isValidUrl(url)) return false;
        String trimmed = url.trim().toLowerCase(Locale.ROOT);
        return trimmed.contains("drive.google.com") || trimmed.contains("docs.google.com");
    }

    public static String getCurrentFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
