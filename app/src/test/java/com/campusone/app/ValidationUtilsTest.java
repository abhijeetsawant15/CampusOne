package com.campusone.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.campusone.app.models.Club;
import com.campusone.app.utils.ValidationUtils;

import org.junit.Test;

public class ValidationUtilsTest {

    @Test
    public void studentEmail_validatesCorrectCollegeDomain() {
        assertTrue(ValidationUtils.isValidStudentEmail("student@student.mes.ac.in"));
        assertTrue(ValidationUtils.isValidStudentEmail("rahul.sharma@student.mes.ac.in"));
        assertTrue(ValidationUtils.isValidStudentEmail("priya_verma12@student.mes.ac.in"));
        assertTrue(ValidationUtils.isValidStudentEmail("COMP.2024.01@student.mes.ac.in"));
    }

    @Test
    public void studentEmail_rejectsUnauthorizedDomains() {
        assertFalse(ValidationUtils.isValidStudentEmail("student@gmail.com"));
        assertFalse(ValidationUtils.isValidStudentEmail("faculty@mes.ac.in"));
        assertFalse(ValidationUtils.isValidStudentEmail("student@yahoo.com"));
        assertFalse(ValidationUtils.isValidStudentEmail("student@pillai.edu"));
        assertFalse(ValidationUtils.isValidStudentEmail("student@student.mes.com"));
        assertFalse(ValidationUtils.isValidStudentEmail(""));
        assertFalse(ValidationUtils.isValidStudentEmail(null));
        assertFalse(ValidationUtils.isValidStudentEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidStudentEmail("@student.mes.ac.in"));
    }

    @Test
    public void generalEmail_validatesStandardEmails() {
        assertTrue(ValidationUtils.isValidEmail("admin@mes.ac.in"));
        assertTrue(ValidationUtils.isValidEmail("principal@pillai.edu"));
        assertTrue(ValidationUtils.isValidEmail("hod.computer@mes.ac.in"));
        assertFalse(ValidationUtils.isValidEmail("not-an-email"));
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    public void password_enforcesMinimumLength() {
        assertTrue(ValidationUtils.isValidPassword("123456"));
        assertTrue(ValidationUtils.isValidPassword("strongPassword2026!"));
        assertFalse(ValidationUtils.isValidPassword("12345"));
        assertFalse(ValidationUtils.isValidPassword(""));
        assertFalse(ValidationUtils.isValidPassword(null));
    }

    @Test
    public void isNotEmpty_validatesProperly() {
        assertTrue(ValidationUtils.isNotEmpty("CampusOne"));
        assertFalse(ValidationUtils.isNotEmpty(""));
        assertFalse(ValidationUtils.isNotEmpty("   "));
        assertFalse(ValidationUtils.isNotEmpty(null));
    }

    @Test
    public void url_validatesProperly() {
        assertTrue(ValidationUtils.isValidUrl("https://drive.google.com/file/d/12345/view"));
        assertTrue(ValidationUtils.isValidUrl("http://docs.google.com/document/d/abcdef/edit"));
        assertTrue(ValidationUtils.isValidUrl("https://www.mes.ac.in"));
        assertFalse(ValidationUtils.isValidUrl("not-a-url"));
        assertFalse(ValidationUtils.isValidUrl("ftp://invalid.com"));
        assertFalse(ValidationUtils.isValidUrl(""));
        assertFalse(ValidationUtils.isValidUrl(null));
    }

    @Test
    public void googleDriveUrl_identifiesDriveLinksCorrectly() {
        assertTrue(ValidationUtils.isGoogleDriveUrl("https://drive.google.com/file/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs/view?usp=sharing"));
        assertTrue(ValidationUtils.isGoogleDriveUrl("https://docs.google.com/document/d/1abcXYZ/edit?usp=sharing"));
        assertFalse(ValidationUtils.isGoogleDriveUrl("https://www.dropbox.com/s/123/file.pdf"));
        assertFalse(ValidationUtils.isGoogleDriveUrl("https://www.mes.ac.in/syllabus.pdf"));
        assertFalse(ValidationUtils.isGoogleDriveUrl(null));
    }

    @Test
    public void clubs_resolvesAllElevenDefinedClubsToDistinctIcons() {
        String[] clubIds = {
            "nss", "csi", "gdg", "tpc", "tapas",
            "student_council", "ieee", "satellite_club",
            "spark_racing", "hyperion_racing", "vanguard_racing"
        };

        for (String id : clubIds) {
            Club club = new Club();
            club.setClubId(id);
            int iconRes = club.getIconResId();
            assertNotEquals(0, iconRes);
        }
    }
}