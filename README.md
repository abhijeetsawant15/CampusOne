# CampusOne — Centralized College Mobile Portal

**CampusOne** is a modern, reliable Android application engineered in **pure Java and XML** for the college community (Pillai University / Mahatma Education Society - MES ecosystem). It serves as a unified digital campus hub connecting **Students**, **College Administration**, and **11 Student Technical & Cultural Clubs**.

---

## 🏛️ Key Specifications & Tech Stack

- **Platform:** Android (Min SDK 24 / Android 7.0 Nougat, Target SDK 36)
- **Language:** Pure Java (100% Java + XML Layouts; No Kotlin, No Jetpack Compose)
- **Design System:** Material Design 3 with custom Pillai University Maroon Palette:
  - Primary Maroon: `#800000`
  - Dark Maroon: `#600000`
  - Accent / Surface Tint: `#F7E9EB`
- **Backend Services:**
  - **Firebase Authentication:** Secure email/password authentication with college email domain enforcement (`@student.mes.ac.in`).
  - **Cloud Firestore:** Real-time NoSQL cloud database storing users, clubs, announcements, events, resources, lost & found, and notifications.
  - **Firebase Storage:** Cloud storage for lost & found photos and club banners.
- **Architecture:** Clean Layered Architecture (Models, Adapters, Activities, Fragments, Helpers) designed for high performance, readability, and college viva presentation defense.

---

## 👥 Role-Based Access Control (RBAC)

The application enforces strict role segregation across 3 distinct roles:

| Role | Permissions & Capabilities |
| :--- | :--- |
| **`STUDENT`** | • Browse college & club announcements with category/club filtering.<br>• View upcoming events and register via official external links.<br>• Explore the 11 student clubs with full details, faculty coordinators, and leads.<br>• Browse & download official college resources (syllabus docs, forms, guidelines).<br>• Report lost or found items with photos, and delete own reported items.<br>• Edit personal profile details (contact number, department, year). |
| **`CLUB_MEMBER`** | • All Student permissions.<br>• Scoped Club Management: Can publish, edit, and delete announcements and events **strictly for their assigned club**.<br>• Cannot modify college-wide announcements or other clubs' listings. |
| **`ADMIN`** | • Full college-wide administrative control.<br>• Create, edit, and delete announcements across all categories (Academic, Placement, Sports, General, Club).<br>• Create, edit, and delete events for any organization.<br>• Upload and delete college resources & forms.<br>• User Management: View registered students, assign/revoke Admin roles, and designate Club Leads.<br>• Moderate Lost & Found listings. |

---

## 🚀 The 11 Defined Student Clubs

Every club has its own distinct custom vector drawable logo (`res/drawable/ic_club_*.xml`), official description, lead contacts, and Firestore document mapping:

1. **NSS (National Service Scheme)** — Community service, blood donation camps, and social awareness initiatives.
2. **CSI (Computer Society of India)** — Technical coding contests, hackathons, and software engineering workshops.
3. **GDG (Google Developer Groups on Campus)** — Google technologies, Android, Cloud, and web developer community.
4. **TPC (Training & Placement Cell)** — Aptitude training, mock interviews, resume reviews, and placement recruitment drives.
5. **TAPAS** — Cultural, arts, music, dance, and theater student club.
6. **Student Council** — Apex student body coordinating college fests, student grievance redressal, and governance.
7. **IEEE Student Branch** — Global technical association organizing international symposiums and research conferences.
8. **Satellite Club** — Aerospace, satellite telemetry, amateur radio, and space research team.
9. **Spark Racing Team** — Electric vehicle (EV) formula student racing design, engineering, and telemetry team.
10. **Hyperion Racing Team** — Combustion formula student racing car engineering, fabrication, and competition team.
11. **Vanguard Racing Team** — All-Terrain Vehicle (ATV) / BAJA racing design, powertrain, and chassis team.

---

## 🔒 Security & Firestore Rules (`firestore.rules`)

The system comes with production-ready security rules:
- **Authentication:** All database operations require a valid Firebase Auth token (`request.auth != null`).
- **Student Email Constraint:** Client-side registration strictly validates `^[A-Za-z0-9._%+-]+@student\.mes\.ac\.in$`.
- **Role Validation:** Roles are stored in the server-side Firestore `users/{userId}` document and verified on every read/write.
- **Club Scoping:** A `CLUB_MEMBER` writing to `announcements` or `events` must have their `clubId` match the document's `clubId`.

---

## 📂 Project Structure

```
CampusOne/
├── app/
│   ├── src/main/
│   │   ├── java/com/campusone/app/
│   │   │   ├── models/                # Data transfer objects
│   │   │   │   ├── User.java
│   │   │   │   ├── Club.java
│   │   │   │   ├── Announcement.java
│   │   │   │   ├── Event.java
│   │   │   │   ├── Resource.java
│   │   │   │   ├── LostFoundItem.java
│   │   │   │   └── Notification.java
│   │   │   ├── adapters/              # RecyclerView adapters
│   │   │   │   ├── ClubAdapter.java
│   │   │   │   ├── EventAdapter.java
│   │   │   │   ├── AnnouncementAdapter.java
│   │   │   │   ├── ResourceAdapter.java
│   │   │   │   ├── LostFoundAdapter.java
│   │   │   │   ├── UserAdapter.java
│   │   │   │   └── NotificationAdapter.java
│   │   │   ├── activities/            # Activity controllers
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── EventDetailActivity.java
│   │   │   │   ├── ClubDetailActivity.java
│   │   │   │   ├── ResourcesActivity.java
│   │   │   │   ├── LostFoundActivity.java
│   │   │   │   ├── ReportLostFoundActivity.java
│   │   │   │   ├── AdminDashboardActivity.java
│   │   │   │   ├── AdminAnnouncementsActivity.java
│   │   │   │   ├── AdminUsersActivity.java
│   │   │   │   ├── AddEditAnnouncementActivity.java
│   │   │   │   ├── AddEditEventActivity.java
│   │   │   │   └── AddEditResourceActivity.java
│   │   │   ├── fragments/             # Bottom navigation fragments
│   │   │   │   ├── HomeFragment.java
│   │   │   │   ├── EventsFragment.java
│   │   │   │   ├── NotificationsFragment.java
│   │   │   │   ├── ClubsFragment.java
│   │   │   │   └── ProfileFragment.java
│   │   │   └── utils/                 # Singletons & helpers
│   │   │       ├── FirebaseManager.java
│   │   │       ├── ValidationUtils.java
│   │   │       └── SampleDataSeeder.java
│   │   ├── res/                       # XML layouts, drawables, menus, colors
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── firestore.rules                    # Cloud Firestore security rules
├── build.gradle.kts
└── README.md
```

---

## 🛠️ Step-by-Step Firebase Setup

To connect your own genuine Firebase backend to CampusOne:

### Step 1: Create a Firebase Project
1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add project** and name it `CampusOne` (or any name you prefer).
3. Disable Google Analytics (optional for college projects) and click **Create project**.

### Step 2: Add Android App
1. In the Project Overview screen, click the **Android** icon to add an Android application.
2. Enter the Package Name: **`com.campusone.app`** *(Must match `applicationId` in `app/build.gradle.kts`)*.
3. App nickname: `CampusOne`.
4. Debug signing certificate SHA-1: *(Optional, leave blank for email/password auth)*.
5. Click **Register app**.

### Step 3: Download `google-services.json`
1. Download the `google-services.json` file provided by Firebase.
2. Place this file directly inside your `CampusOne/app/` folder:
   `C:\Users\Abhijeet\AndroidStudioProjects\CampusOne\app\google-services.json`

### Step 4: Enable the Google Services Gradle Plugin
1. Open `CampusOne/app/build.gradle.kts`.
2. In the `plugins` block at the top, uncomment the line:
   ```kotlin
   plugins {
       alias(libs.plugins.android.application)
       alias(libs.plugins.google.services) // <--- UNCOMMENT THIS LINE
   }
   ```
3. Click **Sync Project with Gradle Files** in Android Studio (or run `.\gradlew.bat assembleDebug`).

### Step 5: Enable Authentication
1. In Firebase Console, navigate to **Build > Authentication**.
2. Click **Get Started**.
3. Under the **Sign-in method** tab, click **Email/Password**.
4. Enable the first toggle (**Email/Password**) and click **Save**.

### Step 6: Set Up Cloud Firestore
1. In Firebase Console, navigate to **Build > Firestore Database**.
2. Click **Create database**.
3. Choose your nearest Cloud Firestore location (e.g. `asia-south1` for Mumbai).
4. Start in **Test mode** (or copy-paste the contents of `firestore.rules` into the **Rules** tab).
5. Click **Create**.

### Step 7: First Run & Admin Account Setup
1. Launch the app on an Android Emulator or physical phone.
2. Register a student account using a college email (e.g. `rahul.sharma@student.mes.ac.in`).
3. To promote this user (or any test account) to **`ADMIN`**:
   - Go to Firebase Console > Firestore Database > `users` collection.
   - Click on the user document.
   - Change the `role` field value from `"STUDENT"` to `"ADMIN"`.
   - In the app, pull to refresh or re-login. The app will immediately unlock the **Admin Dashboard**!
4. From the Admin Dashboard, you can tap **"Seed Sample Data"** to automatically populate all 11 clubs, sample events, college resources, and announcements into Firestore!

---

## 🎓 College Viva & Examiner Q&A Guide

### Q1: Why did you choose Java and XML instead of Jetpack Compose or Kotlin?
> **Answer:** Java and standard XML layouts are the foundational standard taught in university curricula. XML clearly separates the declarative UI layout from procedural business logic. Using Java demonstrates a deep understanding of core Android components (Activities, Fragments, ViewHolders, Async callbacks, and Android Lifecycles) without hiding underlying mechanics behind syntactic abstractions.

### Q2: How does the application handle Role-Based Access Control (RBAC)?
> **Answer:** RBAC is handled at two independent tiers:
> 1. **Client-side Presentation Tier:** Upon login, the user's profile document is fetched from the Firestore `users` collection. Based on the `role` field (`STUDENT`, `CLUB_MEMBER`, `ADMIN`), the UI selectively shows or hides administration menus, edit/delete floating action buttons, and moderation dashboards.
> 2. **Server-side Security Tier:** The `firestore.rules` file enforces database security. Even if a user attempts an unauthorized REST or SDK write, Firestore verifies their Firebase Auth token and role, rejecting unauthorized requests.

### Q3: How is data synchronized in real-time?
> **Answer:** We leverage Cloud Firestore's `addSnapshotListener` and `get()` queries. When an admin posts a new announcement or an event is modified, Firestore pushes real-time updates to all connected devices using WebSockets/gRPC under the hood, triggering `RecyclerView.Adapter.notifyDataSetChanged()` on the main UI thread.

### Q4: How is memory managed with large lists like Announcements and Events?
> **Answer:** We utilize Android's `RecyclerView` with custom `ViewHolder` implementations. Unlike legacy `ListView`, `RecyclerView` recycles and binds only the views visible in the viewport plus a small cache buffer, preventing Out-Of-Memory (OOM) errors even when displaying hundreds of college events or announcements.
