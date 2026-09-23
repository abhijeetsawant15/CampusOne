package com.campusone.app.firebase;

import com.campusone.app.models.Announcement;
import com.campusone.app.models.Club;
import com.campusone.app.models.Event;
import com.campusone.app.models.LostFoundItem;
import com.campusone.app.models.Notification;
import com.campusone.app.models.Resource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SampleDataSeeder {

    public static void seedInitialDataIfEmpty(FirebaseFirestore db) {
        db.collection("clubs").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                seedAllData(db);
            }
        });
    }

    private static void seedAllData(FirebaseFirestore db) {
        WriteBatch batch = db.batch();

        // 1. Seed the 11 Defined Clubs
        List<Club> clubs = new ArrayList<>();
        clubs.add(new Club("nss", "NSS", "Community service, volunteering and social responsibility activities across the college.", "Social Service", true, System.currentTimeMillis()));
        clubs.add(new Club("csi", "CSI", "Technology, programming, computing activities and technical learning for computer engineering.", "Technical", true, System.currentTimeMillis()));
        clubs.add(new Club("gdg", "GDG", "Developer-focused learning, technology workshops, open-source and modern developer ecosystem activities.", "Technical", true, System.currentTimeMillis()));
        clubs.add(new Club("tpc", "TPC", "Training, placement preparation, resume building, career development and employability activities.", "Career", true, System.currentTimeMillis()));
        clubs.add(new Club("tapas", "TAPAS", "Student personality development, aptitude training, soft skills and holistic student growth.", "Development", true, System.currentTimeMillis()));
        clubs.add(new Club("student_council", "Student Council", "Student representation, campus governance coordination and college festival leadership.", "Leadership", true, System.currentTimeMillis()));
        clubs.add(new Club("ieee", "IEEE", "Engineering, technology research, professional networking and technical learning.", "Technical", true, System.currentTimeMillis()));
        clubs.add(new Club("satellite_club", "Satellite Club", "Space technology, small satellite research, aerospace and telemetry technical projects.", "Aerospace", true, System.currentTimeMillis()));
        clubs.add(new Club("spark_racing", "Spark Racing Team", "Student formula electric vehicle development, powertrain and technical racing projects.", "Automotive", true, System.currentTimeMillis()));
        clubs.add(new Club("hyperion_racing", "Hyperion Racing Team", "Student automotive engineering, combustion engine vehicle design and racing competitions.", "Automotive", true, System.currentTimeMillis()));
        clubs.add(new Club("vanguard_racing", "Vanguard Racing Team", "All-terrain vehicle design, mechanical engineering, racing and technical innovation.", "Automotive", true, System.currentTimeMillis()));

        for (Club club : clubs) {
            batch.set(db.collection("clubs").document(club.getClubId()), club);
        }

        // 2. Seed Announcements (College & Club)
        List<Announcement> announcements = new ArrayList<>();
        announcements.add(new Announcement("ann_1", "Tech Fest 2026 Registration Open",
                "Registrations are officially open for all competitive events, project exhibitions, and coding hackathons in Tech Fest 2026.",
                "15 Sep 2026", "COLLEGE ANNOUNCEMENT", "college", "College Administration", "admin", System.currentTimeMillis() - 86400000));

        announcements.add(new Announcement("ann_2", "Placement Drive Phase-1",
                "Final year students must submit their updated verified resumes on the TPC portal by this Friday.",
                "14 Sep 2026", "COLLEGE ANNOUNCEMENT", "college", "Training & Placement Cell", "admin", System.currentTimeMillis() - 172800000));

        announcements.add(new Announcement("ann_3", "Blood Donation Drive",
                "NSS is organizing an annual campus blood donation camp in collaboration with the City Red Cross on Tuesday.",
                "12 Sep 2026", "CLUB ANNOUNCEMENT", "nss", "NSS Lead", "club_member", System.currentTimeMillis() - 259200000));

        announcements.add(new Announcement("ann_4", "CodeX Coding Contest",
                "CSI presents CodeX, a 3-hour competitive programming contest covering Data Structures and Algorithms.",
                "10 Sep 2026", "CLUB ANNOUNCEMENT", "csi", "CSI Technical Team", "club_member", System.currentTimeMillis() - 345600000));

        announcements.add(new Announcement("ann_5", "Formula EV Powertrain Workshop",
                "Spark Racing is conducting an open workshop on electric motor controllers and battery management systems.",
                "08 Sep 2026", "CLUB ANNOUNCEMENT", "spark_racing", "Spark Racing Crew", "club_member", System.currentTimeMillis() - 432000000));

        for (Announcement a : announcements) {
            batch.set(db.collection("announcements").document(a.getId()), a);
        }

        // 3. Seed Events
        List<Event> events = new ArrayList<>();
        events.add(new Event("evt_1", "TECH FEST 2026", "15 Oct 2026", "10:00 AM", "College Auditorium",
                "The flagship annual national technology festival featuring project exhibitions, robotics, paper presentations and hackathons.",
                "College Administration", "college", System.currentTimeMillis() + 864000000));

        events.add(new Event("evt_2", "Cultural Night 2026", "20 Oct 2026", "05:30 PM", "Open Air Theater",
                "An evening of music, drama, folk dances and student band performances celebrating campus culture.",
                "Student Council", "student_council", System.currentTimeMillis() + 1296000000));

        events.add(new Event("evt_3", "Sports Meet 2026", "28 Oct 2026", "08:30 AM", "College Ground",
                "Inter-department track and field events, football, basketball and cricket tournaments.",
                "Sports Committee", "general", System.currentTimeMillis() + 1728000000));

        events.add(new Event("evt_4", "Cloud & Android Bootcamp", "05 Nov 2026", "11:00 AM", "Computer Lab 301",
                "Hands-on session with Google Cloud technologies and modern Android application architecture.",
                "GDG", "gdg", System.currentTimeMillis() + 2160000000L));

        events.add(new Event("evt_5", "ATV Vehicle Testing Demo", "12 Nov 2026", "02:00 PM", "Mechanical Workshop Track",
                "Live demonstration and test run of the newly fabricated all-terrain vehicle by Vanguard Racing.",
                "Vanguard Racing Team", "vanguard_racing", System.currentTimeMillis() + 2592000000L));

        for (Event e : events) {
            batch.set(db.collection("events").document(e.getId()), e);
        }

        // 4. Seed Resources (NO CALENDARS / TIMETABLES)
        List<Resource> resources = new ArrayList<>();
        resources.add(new Resource("res_1", "Student Handbook 2026",
                "Official handbook containing code of conduct, campus facilities, academic regulations and department guides.",
                "Student Handbook", "https://www.mes.ac.in", "10 Sep 2026", System.currentTimeMillis()));

        resources.add(new Resource("res_2", "Bonafide Certificate Form",
                "Application form required for educational loan, passport verification, and regional scholarship applications.",
                "University Forms", "https://www.mes.ac.in", "08 Sep 2026", System.currentTimeMillis()));

        resources.add(new Resource("res_3", "Digital Library Portal Guide",
                "Instructions for accessing IEEE Xplore, ScienceDirect, and international e-journal subscriptions off-campus.",
                "Student Guide", "https://www.mes.ac.in", "05 Sep 2026", System.currentTimeMillis()));

        resources.add(new Resource("res_4", "TPC Placement Brochure & Resume Guide",
                "Official guidelines, formatting standards, and interview preparation resources curated by TPC.",
                "Important Documents", "https://www.mes.ac.in", "01 Sep 2026", System.currentTimeMillis()));

        resources.add(new Resource("res_5", "Engineering Laboratories Safety Manual",
                "Mandatory safety guidelines, equipment handling instructions, and protocols for engineering labs.",
                "Academic Resources", "https://www.mes.ac.in", "25 Aug 2026", System.currentTimeMillis()));

        for (Resource r : resources) {
            batch.set(db.collection("resources").document(r.getId()), r);
        }

        // 5. Seed Lost & Found
        List<LostFoundItem> lostFoundItems = new ArrayList<>();
        lostFoundItems.add(new LostFoundItem("lf_1", "Casio Scientific Calculator FX-991EX",
                "Black scientific calculator in gray sliding cover. Has a small blue sticker on the back.",
                "College Canteen Table 4", "18 Sep 2026", "LOST", "Rohit Patil", "student@student.mes.ac.in", "", "sample_user_1", System.currentTimeMillis() - 86400000));

        lostFoundItems.add(new LostFoundItem("lf_2", "Navy Blue Insulated Water Bottle",
                "Stainless steel navy blue Milton bottle found on the third row bench after morning lecture.",
                "Room 402 (CS Dept)", "19 Sep 2026", "FOUND", "Sneha Sharma", "sneha@student.mes.ac.in", "", "sample_user_2", System.currentTimeMillis() - 43200000));

        lostFoundItems.add(new LostFoundItem("lf_3", "Student Identity Card",
                "Found a college ID card near the main staircase. Name on card: Abhijeet.",
                "Main Building Staircase", "19 Sep 2026", "FOUND", "Security Office", "admin@mes.ac.in", "", "admin_id", System.currentTimeMillis() - 21600000));

        for (LostFoundItem item : lostFoundItems) {
            batch.set(db.collection("lost_found").document(item.getId()), item);
        }

        // 6. Seed Notifications
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("notif_1", "Tech Fest 2026 Registration Open",
                "Registrations for events and workshops are now open on the portal.", "15 Sep 2026", "COLLEGE ANNOUNCEMENT", System.currentTimeMillis() - 86400000));

        notifications.add(new Notification("notif_2", "Placement Drive Phase-1",
                "Submit your verified resume to the TPC portal by Friday.", "14 Sep 2026", "COLLEGE ANNOUNCEMENT", System.currentTimeMillis() - 172800000));

        notifications.add(new Notification("notif_3", "Blood Donation Drive",
                "NSS Camp on Tuesday at the campus auditorium.", "12 Sep 2026", "CLUB ANNOUNCEMENT", System.currentTimeMillis() - 259200000));

        notifications.add(new Notification("notif_4", "Library Digital Access Updated",
                "Remote access to e-journals is now enabled for all enrolled students.", "10 Sep 2026", "CAMPUS UPDATE", System.currentTimeMillis() - 345600000));

        for (Notification n : notifications) {
            batch.set(db.collection("notifications").document(n.getId()), n);
        }

        batch.commit();
    }
}
