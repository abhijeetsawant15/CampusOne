# CampusOne — Technical System Documentation
**Final Current Implementation Document**

---

## 1. PROJECT TITLE AND OVERVIEW

### 1.1 Project Title
**CampusOne: Integrated College Community & Campus Management Android Application**

### 1.2 Overview & Problem Statement
In traditional higher education institutions, academic and campus life communications are heavily fragmented. Important notices are scattered across physical bulletin boards, informal instant messaging groups, disparate departmental websites, and unstructured email threads. This fragmentation leads to:
1. **Missed Critical Deadlines:** Students frequently miss examination forms, placement drives, scholarship notices, and campus events.
2. **Unverified Information & Rumors:** Unofficial messaging channels foster misinformation with no administrative provenance.
3. **Ineffective Lost & Found Recovery:** Physical lost-and-found desks are localized and rarely checked, resulting in low recovery rates for misplaced items such as calculators, student ID cards, and books.
4. **Disorganized Academic Resource Sharing:** Previous years' question papers (PYQs), laboratory manuals, and lecture notes are hoarded in private drives or lost across graduating batches.
5. **Low Visibility for Student Organizations:** College clubs and technical societies struggle to reach students outside their immediate circles.

### 1.3 Target Audience & User Groups
CampusOne connects three distinct campus stakeholders:
- **Students:** The primary campus body seeking centralized notices, club events, verified academic resources, and lost property recovery.
- **College Administration:** Authorized administrative officials responsible for publishing verified institutional announcements, moderating academic content, scheduling major campus festivals, and managing user roles.
- **Student Clubs & Societies:** Recognized student bodies (e.g., CSI, GDG, NSS, Formula/Baja racing teams) requiring an official platform to publish workshops, technical competitions, recruitment notices, and event schedules.

### 1.4 Main Purpose & Core Philosophy
CampusOne provides an authentic, single-pane-of-glass mobile interface for the **Pillai / Mahatma Education Society (MES)** college community. It replaces disparate communication silos with a structured, verified, role-based platform designed with native Android components and backed by Google Firebase cloud infrastructure.

### 1.5 Overall Functionality
- **Centralized Announcements & Notifications:** Institutional and club notices classified by category with real-time Firestore updates.
- **Campus Events Calendar:** Comprehensive event tracking featuring dates, venues, organizers, and detailed schedules.
- **Directory of 11 Student Clubs:** Complete coverage of campus organizations with dedicated branding, descriptions, and club-specific noticeboards.
- **Academic Resources Repository:** Peer-to-peer sharing of study notes, syllabus copies, and lab manuals using cloud-hosted Google Drive links.
- **Campus Lost & Found:** Interactive reporting system supporting photo uploads, description tagging, owner-controlled deletion, and one-tap contact intents (Email/Phone) with resilient offline fallback.
- **Role-Based Access Control (RBAC):** Strict permissions separating Student, Admin, and Club Member operations across both the Android client and Cloud Firestore security rules.

---

## 2. EXECUTIVE SUMMARY

The CampusOne application has been engineered as a robust, native Android application built using **Java 11**, **Material Design Components 3**, and **Google Firebase**. 

The architecture strictly follows a decoupled, layered approach:
- **Presentation Layer:** 14 Activities and 5 Fragments handling Android lifecycles, user inputs, and view bindings.
- **Data Adapters:** 7 custom `RecyclerView.Adapter` implementations for smooth, memory-efficient data rendering.
- **Domain Models:** 7 Plain Old Java Object (POJO) models featuring safe getters/setters, default constructors, and timestamp serialization handling.
- **Firebase Infrastructure:** Unified `FirebaseManager` singleton orchestrating Firebase Authentication, Cloud Firestore (7 collections), and Firebase Storage.
- **Design & Layout System:** A clean, high-contrast university palette dominated by Pillai Primary Maroon (`#800000`), white card surfaces (`#FFFFFF`), light maroon containers (`#FFF7F7`), and edge-to-edge window inset management (`InsetsUtils`).

The application connects directly to the production Firebase project `campusone-79e84`. All authentication strictly enforces institutional domain validation (`@student.mes.ac.in`). Academic resource distribution is achieved via lightweight Google Drive metadata sharing, eliminating server storage overhead. The Lost & Found module features full image lifecycle management with an in-memory `LruCache` bitmap loader and graceful degradation when offline or unprovisioned.

Automated unit tests (`ValidationUtilsTest`) pass with 100% success, and end-to-end user workflows have been verified on Android Virtual Devices running API levels 34, 35, and 36.

---

## 3. PROJECT OBJECTIVES

1. **Institutional Communication Centralization:** Deliver an administrative portal for publishing official, tamper-proof college notices.
2. **Student Organization Empowerment:** Provide dedicated dashboards for 11 recognized student clubs to publish events and workshops strictly scoped to their respective organizations.
3. **Streamlined Academic Resource Exchange:** Facilitate collaborative sharing of notes, syllabus copies, and previous exam question papers via verified external Google Drive links.
4. **Efficient Campus Property Recovery:** Enable rapid lost-and-found reporting with photo evidence and immediate communication channels (direct email and dialer intents).
5. **Strict Domain-Restricted Security:** Enforce authentication restricted to genuine college email addresses (`@student.mes.ac.in`) with automated role resolution directly from Cloud Firestore.
6. **Viva-Ready, Maintainable Engineering:** Adhere to clean Java code, standard Android architectural patterns, zero redundant third-party dependencies (Glide-free custom image cache), and passing unit tests.

---

## 4. TARGET USERS & ROLE-BASED PERMISSIONS

CampusOne implements three discrete user roles stored in the Firestore `users` collection:

```mermaid
flowchart TD
    User([Authenticated User]) --> RoleCheck{User Role in Firestore}
    RoleCheck -->|student| StudentRole[Role: STUDENT]
    RoleCheck -->|admin| AdminRole[Role: ADMIN]
    RoleCheck -->|club_member| ClubRole[Role: CLUB_MEMBER]

    StudentRole --> S1[View Announcements & Events]
    StudentRole --> S2[View Clubs & Directory]
    StudentRole --> S3[Submit & Manage Own Resources]
    StudentRole --> S4[Report & Delete Own Lost/Found]
    StudentRole --> S5[Edit Own Profile]

    AdminRole --> A1[Full Student Privileges]
    AdminRole --> A2[Access Admin Dashboard]
    AdminRole --> A3[Create/Edit/Delete Any Announcement]
    AdminRole --> A4[Create/Edit/Delete Any Event]
    AdminRole --> A5[Moderate & Delete Any Resource]
    AdminRole --> A6[Manage Users & Promote Roles]
    AdminRole --> A7[Seed Database Bootstrapping]

    ClubRole --> C1[Full Student Privileges]
    ClubRole --> C2[Access Club Member Dashboard]
    ClubRole --> C3[Create/Edit/Delete Assigned Club Announcements]
    ClubRole --> C4[Create/Edit/Delete Assigned Club Events]
    ClubRole --> C5[Strictly Blocked from Other Clubs]
```

### 4.1 STUDENT
- **Purpose:** Enrolled undergraduate and postgraduate students.
- **Permissions:**
  - View all approved college and club announcements.
  - View upcoming campus events and detailed event information.
  - Browse all 11 student clubs and read club descriptions and activities.
  - Submit academic resources with Google Drive links.
  - Edit or delete **only their own** submitted resources.
  - Report lost or found items with photo attachments.
  - Delete **only their own** reported lost/found items.
  - Contact lost/found reporters via Gmail or Phone dialer.
  - Update their personal student profile (Name, Department, Year, Division).
- **Restrictions:** Cannot post official announcements; cannot create campus events; cannot moderate peers' content; cannot access administrative dashboards.

### 4.2 ADMIN
- **Purpose:** College management, heads of departments, and institutional administrators.
- **Permissions:**
  - Full access to all Student features.
  - Dedicated access to `AdminDashboardActivity`.
  - Full CRUD on all college announcements (`COLLEGE ANNOUNCEMENT`).
  - Full CRUD on all events (College and Club).
  - Unrestricted moderation and deletion of any academic resource.
  - User and role management (`AdminUsersActivity`): ability to inspect registered students, promote students to club leads with assigned club IDs, or grant administrator status.
  - Database seeding utility via `SampleDataSeeder`.
- **Restrictions:** Must possess verified admin role in Firestore document `users/{uid}`.

### 4.3 CLUB_MEMBER
- **Purpose:** Elected leaders, technical heads, and designated coordinators of recognized college clubs.
- **Permissions:**
  - Full access to all Student features.
  - Access to `ClubMemberDashboardActivity`.
  - Publish, edit, and delete announcements tagged under their specific `clubId`.
  - Create, update, and remove events scheduled under their specific `clubId`.
- **Restrictions:**
  - Strictly forbidden from modifying or publishing content for clubs other than their assigned `clubId`.
  - Cannot publish official institutional notices under the college administration banner.
  - Cannot access the global Admin Dashboard or manage user roles.

---

## 5. COMPLETE TECHNOLOGY STACK

All versions, plugins, and dependencies are derived directly from `build.gradle.kts`, `app/build.gradle.kts`, and `gradle/libs.versions.toml`:

### 5.1 Platform & Toolchain
| Parameter | Value |
|---|---|
| Platform | Android |
| Programming Language | Java 11 (`JavaVersion.VERSION_11`) |
| Gradle DSL | Kotlin DSL (`build.gradle.kts`) |
| Gradle Version | 8.13 |
| Android Gradle Plugin (AGP) | `9.2.1` |
| Google Services Plugin | `4.4.2` |
| Compile SDK | `36` (minorApiLevel = 1) |
| Target SDK | `36` |
| Minimum SDK | `24` (Android 7.0 Nougat) |
| Package Name | `com.campusone.app` |
| Application ID | `com.campusone.app` |
| Version Code | `1` |
| Version Name | `1.0` |

### 5.2 UI & AndroidX Libraries
| Dependency | Version Reference | Purpose |
|---|---|---|
| `androidx.appcompat:appcompat` | `1.6.1` | Backward-compatible base activities and ActionBar support |
| `com.google.android.material:material` | `1.10.0` | Material Design 3 components, BottomNavigationView, Cards, Chips, TextInputs |
| `androidx.activity:activity-ktx` | `1.8.0` | Modern activity contracts, modern photo picker integration |
| `androidx.constraintlayout:constraintlayout` | `2.1.4` | Responsive constraint-based view layouts |

### 5.3 Backend & Cloud Services (Firebase BoM `33.7.0`)
| Dependency | Purpose |
|---|---|
| `com.google.firebase:firebase-bom:33.7.0` | Centralized Firebase version alignment |
| `com.google.firebase:firebase-auth` | User authentication, session management, token handling |
| `com.google.firebase:firebase-firestore` | Cloud NoSQL real-time document database |
| `com.google.firebase:firebase-storage` | Binary blob storage for Lost & Found item photos |

### 5.4 Testing Frameworks
| Dependency | Version Reference | Purpose |
|---|---|---|
| `junit:junit` | `4.13.2` | Unit testing engine |
| `androidx.test.ext:junit` | `1.1.5` | AndroidX test runner extensions |
| `androidx.test.espresso:espresso-core` | `3.5.1` | UI automation testing framework |

### 5.5 External Dependencies Policy
- **No Third-Party Image Libraries:** Glide, Picasso, and Coil are intentionally omitted. Image loading and memory caching are managed via a custom, lightweight `ImageLoader` leveraging Android's native `LruCache` and `HttpURLConnection`.
- **No Unnecessary Architectures:** No Dagger/Hilt, No RxJava, No Jetpack Compose. Standard native Android Java architecture for maximum clarity and viva comprehensibility.

---

## 6. PROJECT ARCHITECTURE

CampusOne utilizes a structured **Model-View-Controller / Component-Based Layered Architecture**:

```mermaid
graph TD
    subgraph UI_Layer [Presentation Layer: Activities & Fragments]
        MA[MainActivity]
        HF[HomeFragment]
        EF[EventsFragment]
        CF[ClubsFragment]
        NF[NotificationsFragment]
        PF[ProfileFragment]
        RA[ResourcesActivity]
        LFA[LostFoundActivity]
        ADA[AdminDashboardActivity]
        CMDA[ClubMemberDashboardActivity]
    end

    subgraph Adapter_Layer [Adapter Layer]
        AA[AnnouncementAdapter]
        EA[EventAdapter]
        CA[ClubAdapter]
        RLA[ResourceAdapter]
        LFa[LostFoundAdapter]
        UA[UserAdapter]
        NA[NotificationAdapter]
    end

    subgraph Service_Layer [Service & Utilities Layer]
        FM[FirebaseManager Singleton]
        SDS[SampleDataSeeder]
        VU[ValidationUtils]
        IU[InsetsUtils]
        IL[ImageLoader LruCache]
    end

    subgraph Model_Layer [Data Models: POJOs]
        MUser[User]
        MAnn[Announcement]
        MEvt[Event]
        MClub[Club]
        MRes[Resource]
        MLF[LostFoundItem]
        MNotif[Notification]
    end

    subgraph Backend_Layer [Google Firebase Cloud Backend]
        FAuth[(Firebase Authentication)]
        CFS[(Cloud Firestore: 7 Collections)]
        FStor[(Firebase Storage: lost_found/)]
        GDrive[(External Google Drive Storage)]
    end

    UI_Layer --> Adapter_Layer
    UI_Layer --> Service_Layer
    Adapter_Layer --> Model_Layer
    Service_Layer --> Model_Layer
    Service_Layer --> Backend_Layer
    RA -.->|ACTION_VIEW| GDrive
```

### Architectural Responsibilities
1. **Presentation Layer:**
   - Activities manage window insets (`InsetsUtils`), toolbars, navigation events, and dialogs.
   - Fragments represent top-level tabs housed within `MainActivity` via `BottomNavigationView`.
2. **Adapter Layer:**
   - Binds Firestore models to custom XML item cards (`item_*.xml`).
   - Handles item click callbacks, author action menus, and dynamic status badge color rendering.
3. **Domain Model Layer:**
   - POJOs with parameterless constructors required by Firestore's `toObject()` deserializer.
   - Defensive setters handling both numeric Long and String timestamps.
4. **Service & Helper Layer:**
   - `FirebaseManager` encapsulates Firebase singletons, provides direct collection references, and maintains in-memory session profile caching (`currentUserProfile`).
   - `SampleDataSeeder` bootstraps initial collections using atomic Firestore `WriteBatch`.
   - `ValidationUtils` centralizes regex validation rules.
   - `InsetsUtils` applies top status bar insets across secondary activities.
   - `ImageLoader` executes asynchronous network downloads with a 15MB memory cache.

---

## 7. PROJECT FOLDER & FILE STRUCTURE

```
CampusOne/
├── app/
│   ├── build.gradle.kts                 # Module build configuration, SDK 36, dependencies
│   ├── google-services.json             # Live Firebase credentials for campusone-79e84
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml      # Manifest: 14 Activities, Internet permissions
│       │   ├── java/com/campusone/app/
│       │   │   ├── MainActivity.java                 # Host activity with BottomNavigationView
│       │   │   ├── LoginActivity.java                # Firebase Auth login screen
│       │   │   ├── RegisterActivity.java             # Registration with @student.mes.ac.in check
│       │   │   ├── ResourcesActivity.java            # Academic resources list with Drive links
│       │   │   ├── AddEditResourceActivity.java      # Submit / Edit resource form
│       │   │   ├── LostFoundActivity.java            # Campus Lost & Found item feed
│       │   │   ├── ReportLostFoundActivity.java      # Report item with photo picker & fallback
│       │   │   ├── EventDetailActivity.java          # Full screen event view
│       │   │   ├── AddEditEventActivity.java         # Admin / Club event creation form
│       │   │   ├── ClubDetailActivity.java           # Detailed view of selected club
│       │   │   ├── AdminDashboardActivity.java       # Central administrative management portal
│       │   │   ├── AdminAnnouncementsActivity.java   # Admin notices manager
│       │   │   ├── AdminUsersActivity.java           # User role promotion / demotion manager
│       │   │   ├── AddEditAnnouncementActivity.java  # Notice creation form
│       │   │   ├── ClubMemberDashboardActivity.java  # Scoped club representative portal
│       │   │   │
│       │   │   ├── adapters/
│       │   │   │   ├── AnnouncementAdapter.java      # Binds announcements to item_announcement
│       │   │   │   ├── ClubAdapter.java              # Binds clubs to item_club
│       │   │   │   ├── EventAdapter.java             # Binds events to item_event
│       │   │   │   ├── LostFoundAdapter.java         # Binds lost/found items to item_lost_found
│       │   │   │   ├── NotificationAdapter.java      # Binds notices to item_notification
│       │   │   │   ├── ResourceAdapter.java          # Binds resources to item_resource
│       │   │   │   └── UserAdapter.java              # Binds user roles to item_user
│       │   │   │
│       │   │   ├── firebase/
│       │   │   │   ├── FirebaseManager.java          # Centralized Firebase singleton helper
│       │   │   │   └── SampleDataSeeder.java         # Firestore data bootstrap batch writer
│       │   │   │
│       │   │   ├── fragments/
│       │   │   │   ├── HomeFragment.java             # Feed, quick access, banners, dynamic badges
│       │   │   │   ├── EventsFragment.java           # College and club events browser
│       │   │   │   ├── ClubsFragment.java            # 11 Campus clubs directory
│       │   │   │   ├── NotificationsFragment.java    # Official notifications timeline
│       │   │   │   └── ProfileFragment.java          # Student profile, edit dialog, logout
│       │   │   │
│       │   │   ├── models/
│       │   │   │   ├── Announcement.java             # Announcement document model
│       │   │   │   ├── Club.java                     # Club document model with icon resolver
│       │   │   │   ├── Event.java                    # Event document model
│       │   │   │   ├── LostFoundItem.java            # Lost & Found document model
│       │   │   │   ├── Notification.java             # Notification document model
│       │   │   │   ├── Resource.java                 # Academic resource document model
│       │   │   │   └── User.java                     # User profile and role model
│       │   │   │
│       │   │   └── utils/
│       │   │       ├── ImageLoader.java              # Glide-free LruCache bitmap loader
│       │   │       ├── InsetsUtils.java              # Window insets status-bar padding helper
│       │   │       └── ValidationUtils.java          # Email, password, URL regex validators
│       │   │
│       │   └── res/
│       │       ├── drawable/                         # Vectors: ic_club_*, ic_nav_*, shapes
│       │       ├── layout/                           # 28 XML layouts (screens, dialogs, items)
│       │       ├── values/
│       │       │   ├── colors.xml                    # Maroon (#800000), white cards, badges
│       │       │   ├── strings.xml                   # Application string constants
│       │       │   └── themes.xml                    # Theme.CampusOne Material3 configuration
│       │       └── xml/                              # Backup rules and data extraction rules
│       │
│       └── test/java/com/campusone/app/
│           ├── ExampleUnitTest.java                  # Boilerplate unit test
│           └── ValidationUtilsTest.java              # 8 rigorous unit tests (100% passing)
│
├── gradle/
│   ├── libs.versions.toml                            # Version catalog
│   └── wrapper/                                      # Gradle 8.13 wrapper
├── build.gradle.kts                                  # Root build file
├── settings.gradle.kts                               # Project settings
├── firestore.rules                                   # Production security rules (7 collections)
└── storage.rules                                     # Production storage rules (lost_found/)
```

---

## 8. APPLICATION NAVIGATION

```mermaid
stateDiagram-v2
    [*] --> SplashCheck : App Launch
    SplashCheck --> LoginActivity : No active session
    SplashCheck --> MainActivity : Active FirebaseUser exists

    state LoginActivity {
        EnterCredentials --> Authenticate
        GoRegister --> RegisterActivity
    }

    state RegisterActivity {
        ValidateDomain --> CreateFirebaseUser
        CreateFirebaseUser --> InitializeFirestoreDoc
        InitializeFirestoreDoc --> MainActivity
    }

    state MainActivity {
        [*] --> HomeFragment
        HomeFragment --> EventsFragment : Bottom Nav
        HomeFragment --> ClubsFragment : Bottom Nav
        HomeFragment --> NotificationsFragment : Bottom Nav
        HomeFragment --> ProfileFragment : Bottom Nav
        
        HomeFragment --> ResourcesActivity : Quick Access Tap
        HomeFragment --> LostFoundActivity : Quick Access Tap
        HomeFragment --> AdminDashboardActivity : Admin Card Tap (Role=admin)
        HomeFragment --> ClubMemberDashboardActivity : Club Card Tap (Role=club_member)
    }

    state ResourcesActivity {
        ViewList --> OpenDriveUrl : Click Card (ACTION_VIEW)
        ViewList --> AddEditResourceActivity : Tap Add FAB
        ViewList --> AuthorEditDeleteDialog : Long Press / Action (Author/Admin)
    }

    state LostFoundActivity {
        ViewLostFound --> OpenContactModal : Click Contact
        ViewLostFound --> ReportLostFoundActivity : Tap Report FAB
        ViewLostFound --> OwnerDeleteDialog : Click Delete (Author/Admin)
    }

    state AdminDashboardActivity {
        AdminAnnouncementsActivity
        AddEditEventActivity
        AdminUsersActivity
        ResourcesActivity_Mod
    }

    state ProfileFragment {
        EditProfileDialog
        SignOut --> LoginActivity
    }
```

### Detailed Navigation Transitions
1. **Entry Point (`MainActivity`):** Configured as `MAIN` / `LAUNCHER` in `AndroidManifest.xml`. In `onCreate()`, it verifies `FirebaseManager.getInstance().isUserLoggedIn()`. If false, it redirects immediately to `LoginActivity`.
2. **Authentication Transition:** Upon successful login or registration, the activity starts `MainActivity` with `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK` to purge authentication activities from the backstack.
3. **Role-Driven Dashboards:**
   - If `currentUserProfile.getRole().equals("admin")`, a prominent "Admin Control Panel" card appears on `HomeFragment` routing to `AdminDashboardActivity`.
   - If `currentUserProfile.getRole().equals("club_member")`, a "Club Management Dashboard" card appears routing to `ClubMemberDashboardActivity`.
4. **Feature Activities:**
   - `ResourcesActivity` and `LostFoundActivity` are launched from either the Home quick-access grid or relevant fragment cards.
   - All secondary activities invoke `InsetsUtils.applySystemBarInsets(rootView)` during `onCreate()` to ensure zero overlap with system bars.

---

## 9. AUTHENTICATION SYSTEM

### 9.1 Technical Overview
Authentication is powered by **Firebase Authentication** using email and password credentials, strictly enforced on the client and in database security rules.

### 9.2 Institutional Domain Restriction
Registration strictly rejects non-institutional email addresses:
```java
// ValidationUtils.java
private static final String STUDENT_EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@student\\.mes\\.ac\\.in$";
private static final Pattern STUDENT_EMAIL_PATTERN = Pattern.compile(STUDENT_EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

public static boolean isValidStudentEmail(String email) {
    if (email == null) return false;
    return STUDENT_EMAIL_PATTERN.matcher(email.trim()).matches();
}
```
- Valid: `rahul.sharma@student.mes.ac.in`, `COMP.2024.01@student.mes.ac.in`.
- Rejected: `student@gmail.com`, `student@yahoo.com`, `student@pillai.edu`, `@student.mes.ac.in`.

### 9.3 Registration Pipeline (`RegisterActivity.java`)
1. User enters Full Name, Email, Password, Department, Academic Year, and Division.
2. `ValidationUtils.isValidStudentEmail(email)` verifies institutional ownership.
3. `ValidationUtils.isValidPassword(password)` confirms $\ge 6$ characters.
4. Invokes `FirebaseAuth.createUserWithEmailAndPassword(email, password)`.
5. Upon successful creation, constructs a `User` model:
   - `uid`: Set to `firebaseUser.getUid()`.
   - `role`: Defaulted strictly to `"student"`.
   - `clubId`: Empty string `""`.
   - `createdAt`: `System.currentTimeMillis()`.
6. Writes document to Firestore collection `users/{uid}`.
7. Populates `FirebaseManager.getInstance().setCachedUserProfile(user)` and navigates to `MainActivity`.

### 9.4 Login Pipeline (`LoginActivity.java`)
1. User enters email and password.
2. Invokes `FirebaseAuth.signInWithEmailAndPassword(email, password)`.
3. Fetches the user profile from `users/{uid}` via `FirebaseManager.fetchUserProfile()`.
4. Evaluates role:
   - `student` $\rightarrow$ Standard student privileges.
   - `admin` $\rightarrow$ Full administrative capability enabled.
   - `club_member` $\rightarrow$ Extracts `assignedClubId` for club scoping.
5. Saves the loaded `User` instance in memory and transitions to `MainActivity`.

### 9.5 Session Management & Sign-Out
- Firebase maintains token persistence in private app storage.
- Sign out is executed in `ProfileFragment.java`:
  ```java
  FirebaseManager.getInstance().signOut();
  Intent intent = new Intent(getActivity(), LoginActivity.class);
  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
  startActivity(intent);
  ```

---

## 10. ROLE-BASED ACCESS CONTROL (RBAC)

### 10.1 Role Determination Architecture
The application does not use hardcoded role bypasses or insecure local switches. On every session start:
1. `FirebaseManager.getInstance().getCurrentUserId()` extracts the active UID.
2. `FirebaseManager.fetchUserProfile()` queries `users/{uid}`.
3. The returned document contains the authoritative `role` string (`"student"`, `"admin"`, or `"club_member"`).
4. Cached in `FirebaseManager.getInstance().setCachedUserProfile(user)`.

### 10.2 Role Permissions Matrix
| Capability | Student | Club Member | Admin | Enforced By |
|---|:---:|:---:|:---:|---|
| Read Notices & Events | Yes | Yes | Yes | Client & `firestore.rules` |
| Browse Clubs Directory | Yes | Yes | Yes | Client & `firestore.rules` |
| Submit Google Drive Resource | Yes | Yes | Yes | Client & `firestore.rules` |
| Edit/Delete Own Resource | Yes | Yes | Yes | Client & `firestore.rules` |
| Edit/Delete Others' Resources | No | No | Yes | Client & `firestore.rules` |
| Report Lost & Found Item | Yes | Yes | Yes | Client & `firestore.rules` |
| Upload Lost/Found Photo | Yes | Yes | Yes | Client & `storage.rules` |
| Delete Own Lost/Found Item | Yes | Yes | Yes | Client & `firestore.rules` |
| Delete Others' Lost/Found Item | No | No | Yes | Client & `firestore.rules` |
| Post College Announcement | No | No | Yes | Client & `firestore.rules` |
| Post Club Announcement | No | Yes (Own Club Only) | Yes | Client & `firestore.rules` |
| Create / Edit Events | No | Yes (Own Club Only) | Yes | Client & `firestore.rules` |
| Access Admin Dashboard | No | No | Yes | Client Activity Check |
| Access Club Dashboard | No | Yes | No | Client Activity Check |
| Promote / Demote User Roles | No | No | Yes | Client & `firestore.rules` |

---

## 11. HOME SCREEN

**Files:** `HomeFragment.java`, `fragment_home.xml`

### 11.1 Visual Elements & Layout
- **Institutional Header:** Pillai / MES College Community banner with real-time greeting: `"Welcome back, [Student Name]"`.
- **Dynamic Role Badge:** Pill-shaped Material Chip displaying `STUDENT` (maroon badge), `ADMIN` (dark maroon badge), or `CLUB LEAD` (green badge).
- **Conditional Management Cards:**
  - If `role.equals("admin")`: Displays an "Admin Control Panel" card with action button opening `AdminDashboardActivity`.
  - If `role.equals("club_member")`: Displays a "Club Management" card opening `ClubMemberDashboardActivity`.
- **Quick Access Navigation Grid:** High-contrast Material cards with maroon icons:
  1. *Academic Resources* $\rightarrow$ Launches `ResourcesActivity`.
  2. *Lost & Found* $\rightarrow$ Launches `LostFoundActivity`.
  3. *Student Clubs* $\rightarrow$ Selects `ClubsFragment` tab.
  4. *College Events* $\rightarrow$ Selects `EventsFragment` tab.
- **Featured Upcoming Event:** A spotlight MaterialCard displaying the next upcoming campus event (Title, Date, Time, Venue) fetched dynamically from Firestore.
- **Recent Announcements Stream:** A vertical `RecyclerView` showing the latest official notices with category tags and publication dates.

---

## 12. EVENTS MODULE

**Files:** `EventsFragment.java`, `EventDetailActivity.java`, `AddEditEventActivity.java`, `EventAdapter.java`, `Event.java`

### 12.1 Functional Overview
The Events module manages the college calendar, technical competitions, workshops, and annual festivals.

### 12.2 Event Entity Schema
- `id` (String): Firestore document identifier.
- `title` (String): Official event title.
- `date` (String): Formatted date string (e.g., `"15 Oct 2026"`).
- `time` (String): Scheduled time (e.g., `"10:00 AM"`).
- `venue` (String): Physical campus location (e.g., `"College Auditorium"`).
- `description` (String): Comprehensive agenda and details.
- `organizer` (String): Entity organizing the event (e.g., `"College Administration"`, `"CSI"`).
- `clubId` (String): `"college"`, `"general"`, or specific club ID (`"nss"`, `"csi"`, etc.).
- `timestamp` (long): Milliseconds for temporal ordering.

### 12.3 Operations & Permissions
- **Listing:** All authenticated users view events sorted chronologically by timestamp in `EventsFragment`.
- **Details:** Tapping an event opens `EventDetailActivity`, displaying full description, venue, time, organizer, and an action button.
- **Creation & Modification:**
  - Admins can create or modify any event via `AddEditEventActivity`.
  - Club members can create or modify events tagged with their `assignedClubId`.
  - Normal students cannot create or edit events (FAB and edit icons are hidden).

---

## 13. ANNOUNCEMENTS & NOTIFICATIONS MODULE

**Files:** `NotificationsFragment.java`, `AdminAnnouncementsActivity.java`, `AddEditAnnouncementActivity.java`, `AnnouncementAdapter.java`, `NotificationAdapter.java`

### 13.1 Categorization
Announcements are grouped into two primary tiers:
1. `COLLEGE ANNOUNCEMENT`: Institutional notices published by administration (placement drives, academic deadlines, administrative closures).
2. `CLUB ANNOUNCEMENT`: Technical events, workshops, team selections, and community drives published by student clubs.

### 13.2 Real-Time Data Flow
- `NotificationsFragment` registers a query on Firestore `announcements` ordered by `timestamp` descending.
- Changes in Firestore trigger real-time updates via `addSnapshotListener`, immediately refreshing `NotificationAdapter`.
- Tapping an announcement displays the full text and author provenance.
- In the Admin Announcements manager (`AdminAnnouncementsActivity`), admins can filter notices, edit existing text, or delete outdated broadcasts.

---

## 14. STUDENT CLUBS MODULE

**Files:** `ClubsFragment.java`, `ClubDetailActivity.java`, `ClubAdapter.java`, `Club.java`

### 14.1 The 11 Campus Organizations
CampusOne implements full, dedicated representation for all 11 recognized student bodies. Each organization has an internal identifier, formal title, category, description, and custom vector drawable:

| # | Club Identifier (`clubId`) | Official Club Name | Category | Vector Asset (`ic_club_*`) | Primary Activity Scope |
|---|---|---|---|---|---|
| 1 | `nss` | NSS | Social Service | `R.drawable.ic_club_nss` | Community service, blood donation camps, environmental initiatives |
| 2 | `csi` | CSI | Technical | `R.drawable.ic_club_csi` | Computer Society of India: coding contests, DSA workshops, tech talks |
| 3 | `gdg` | GDG | Technical | `R.drawable.ic_club_gdg` | Google Developer Groups: Android, Cloud, open-source tech events |
| 4 | `tpc` | TPC | Career | `R.drawable.ic_club_tpc` | Training & Placement Cell: resumes, mock interviews, corporate drives |
| 5 | `tapas` | TAPAS | Development | `R.drawable.ic_club_tapas` | Personality development, soft skills, aptitude training |
| 6 | `student_council` | Student Council | Leadership | `R.drawable.ic_club_student_council` | Student governance, campus festival leadership, inter-department coordination |
| 7 | `ieee` | IEEE | Technical | `R.drawable.ic_club_ieee` | Technical research, paper writing, international engineering standards |
| 8 | `satellite_club` | Satellite Club | Aerospace | `R.drawable.ic_club_satellite_club` | CanSat development, high-altitude balloons, space telecommunications |
| 9 | `spark_racing` | Spark Racing Team | Automotive | `R.drawable.ic_club_spark_racing` | Student Formula electric vehicle design, powertrain engineering |
| 10 | `hyperion_racing` | Hyperion Racing Team | Automotive | `R.drawable.ic_club_hyperion_racing` | Combustion engine formula vehicle design, chassis dynamics |
| 11 | `vanguard_racing` | Vanguard Racing Team | Automotive | `R.drawable.ic_club_vanguard_racing` | All-terrain vehicle (ATV) fabrication, Baja SAE racing competitions |

### 14.2 Vector Icon Resolution
In `Club.java`, dynamic vector mapping is handled through a switch expression on `clubId.toLowerCase()`:
```java
public int getIconResId() {
    if (clubId == null) return R.drawable.ic_clubs;
    switch (clubId.toLowerCase()) {
        case "nss": return R.drawable.ic_club_nss;
        case "csi": return R.drawable.ic_club_csi;
        case "gdg": return R.drawable.ic_club_gdg;
        case "tpc": return R.drawable.ic_club_tpc;
        case "tapas": return R.drawable.ic_club_tapas;
        case "student_council": return R.drawable.ic_club_student_council;
        case "ieee": return R.drawable.ic_club_ieee;
        case "satellite_club": return R.drawable.ic_club_satellite_club;
        case "spark_racing": return R.drawable.ic_club_spark_racing;
        case "hyperion_racing": return R.drawable.ic_club_hyperion_racing;
        case "vanguard_racing": return R.drawable.ic_club_vanguard_racing;
        default: return R.drawable.ic_clubs;
    }
}
```

### 14.3 Club Detail View (`ClubDetailActivity.java`)
When a student selects a club card in `ClubsFragment`:
1. Passes `clubId` via Intent extras.
2. Queries Firestore `clubs/{clubId}` to display title, category, description, and large banner icon.
3. Automatically executes a secondary query on `announcements` where `clubId == selectedClubId` to display that club's active feed.

---

## 15. ADMIN DASHBOARD

**Files:** `AdminDashboardActivity.java`, `AdminAnnouncementsActivity.java`, `AdminUsersActivity.java`, `AddEditAnnouncementActivity.java`, `AddEditEventActivity.java`

### 15.1 Administrative Capabilities
The Admin Dashboard is the central control station for college management:
1. **Announcements Management:** Open `AdminAnnouncementsActivity` to review, add, edit, or delete institutional broadcasts.
2. **Events Management:** Create new campus-wide events or edit schedules via `AddEditEventActivity`.
3. **Resource Moderation:** Navigate to `ResourcesActivity` with elevated privileges allowing deletion of any inappropriate, incorrect, or duplicate student study resources.
4. **User & Role Management (`AdminUsersActivity.java`):**
   - Displays a live list of registered students from Firestore `users`.
   - Admin can select a user and open a role modification dialog.
   - Permits promoting a `student` to `club_member` (with required selection of their assigned `clubId` from the 11 recognized clubs).
   - Permits promoting a user to full `admin` status, or demoting back to `student`.
5. **Initial Data Seeding:** A verification utility invoking `SampleDataSeeder.seedInitialDataIfEmpty()` to populate demo documents if Firestore is brand new.

---

## 16. CLUB MEMBER DASHBOARD

**Files:** `ClubMemberDashboardActivity.java`, `AddEditAnnouncementActivity.java`, `AddEditEventActivity.java`

### 16.1 Scoped Representation
- When a user with `role = "club_member"` signs in, `FirebaseManager` extracts their `assignedClubId` (e.g., `"csi"` or `"nss"`).
- The dashboard title dynamically displays: `"[Club Name] Dashboard"`.
- It lists announcements and events filtered strictly by `clubId == user.getClubId()`.

### 16.2 Strict Scoping Enforcement
- When tapping "Post New Announcement", `AddEditAnnouncementActivity` is initialized with `EXTRA_CLUB_ID` locked to the user's club. The club selection dropdown is disabled.
- Attempting to modify documents belonging to another club is rejected both client-side and by Cloud Firestore security rules (`request.resource.data.clubId == getUserData().clubId`).

---

## 17. PROFILE MODULE

**Files:** `ProfileFragment.java`, `dialog_edit_profile.xml`

### 17.1 Displayed Attributes
The profile screen provides transparency on student academic records:
- **Full Name:** Retrieved from Firestore `users/{uid}.name`.
- **Institutional Email:** Read-only verification of `@student.mes.ac.in`.
- **Department:** Engineering discipline (e.g., Computer Engineering, Information Technology, Mechanical).
- **Academic Year:** Current year (FE, SE, TE, BE).
- **Division:** Class section (e.g., Division A, B, C).
- **Role Chip:** Visual badge representing user privileges (`STUDENT`, `ADMIN`, `CLUB LEAD`).

### 17.2 Profile Modification
- Tapping "Edit Profile" opens `dialog_edit_profile.xml` in an AlertDialog.
- Users can modify Name, Department, Year, and Division.
- Email and Role fields are immutable from this dialog to prevent privilege escalation.
- Updates are saved to `users/{uid}` with immediate local UI update.

---

## 18. RESOURCES MODULE (GOOGLE DRIVE LINK SYSTEM)

**Files:** `ResourcesActivity.java`, `AddEditResourceActivity.java`, `ResourceAdapter.java`, `Resource.java`

```mermaid
sequenceDiagram
    autonumber
    actor Student as Student / Uploader
    participant App as CampusOne Android App
    participant FS as Cloud Firestore (resources)
    actor Peer as Peer Student
    participant Browser as Android System Browser / Drive

    Student->>Student: Uploads study document to personal Google Drive
    Student->>Student: Sets permission to "Anyone with link can view"
    Student->>App: Opens Resources -> Taps Add FAB
    Student->>App: Fills Title, Category, Department, Google Drive URL
    App->>App: ValidationUtils.isGoogleDriveUrl(url)
    App->>FS: Saves Resource metadata + Drive URL
    Note over FS: File binary is NOT in Firebase Storage
    Peer->>App: Opens ResourcesActivity
    FS-->>App: Loads Resource list
    Peer->>App: Taps Resource Card
    App->>Browser: Intent.ACTION_VIEW (Drive URL)
    Browser-->>Peer: Opens document in Google Drive App or Chrome
```

### 18.1 Architectural Decision: Cloud Link Sharing
To prevent excessive cloud storage bandwidth consumption, storage billing, and document parsing failures on mobile devices, CampusOne implements a **Google Drive Link Sharing Architecture**:
- Students upload study materials, notes, syllabus PDFs, and previous exam question papers directly to their institutional Google Drive accounts.
- Students copy the shareable link and submit it within CampusOne.
- **The actual file is NEVER stored in Firebase Storage.**
- Firestore stores only the structured metadata and URL pointer.

### 18.2 Resource Form & Metadata Fields
1. `title` (String): e.g., `"Data Structures & Algorithms - Module 3 Notes"`.
2. `description` (String): Concise summary of the contents.
3. `category` (String): Selected from `Notes`, `PYQs`, `Lab Manuals`, `Syllabus`, `Forms`, `Guides`.
4. `department` (String): Academic department.
5. `url` (String): The verified HTTP/HTTPS link.
6. `uploaderUid` (String): UID of the publishing student.
7. `uploaderName` (String): Display name of the student.
8. `date` / `createdAt` (String): Formatted date string (`"23 Sep 2026"`).
9. `status` (String): `"APPROVED"`.
10. `timestamp` (long): Epoch milliseconds.

### 18.3 URL Validation Logic
```java
// ValidationUtils.java
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
```

### 18.4 Document Consumption & Launch Intent
When a student taps a resource item card:
```java
String url = resource.getUrl();
if (ValidationUtils.isValidUrl(url)) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    context.startActivity(browserIntent);
} else {
    Toast.makeText(context, "Invalid resource URL", Toast.LENGTH_SHORT).show();
}
```
This triggers Android's intent resolver to launch Google Drive, Google Docs, or Google Chrome.

### 18.5 Author Ownership & Moderation
- Students can edit or delete **only their own** resources (`resource.getUploaderUid().equals(currentUserId)`).
- Administrators have global edit and delete authority over all resources for moderation.

---

## 19. LOST & FOUND MODULE

**Files:** `LostFoundActivity.java`, `ReportLostFoundActivity.java`, `LostFoundAdapter.java`, `LostFoundItem.java`

```mermaid
flowchart TD
    Start([User Reports Item]) --> PickPhoto{Select Photo?}
    PickPhoto -->|Yes| LaunchPicker[ActivityResultContracts.GetContent]
    LaunchPicker --> Preview[Display Preview in ImageView]
    Preview --> UploadStorage[Upload to Firebase Storage]
    
    UploadStorage -->|Success| SaveURL[Obtain downloadUrl]
    UploadStorage -->|Failure / 404| StorageFallback[Viva Safety Fallback: Set imageUrl = '']
    
    PickPhoto -->|No| SetEmpty[Set imageUrl = '']
    
    SaveURL --> WriteFirestore[Write item to Firestore: lost_found collection]
    StorageFallback --> WriteFirestore
    SetEmpty --> WriteFirestore
    
    WriteFirestore --> RefreshFeed[Feed Refreshed in LostFoundActivity]
```

### 19.1 Two-Tier Architecture
Unlike academic resources, Lost & Found items use a dual-layer Firebase model:
1. **Media Layer (Firebase Storage):** Stores the photographic evidence of the lost/found object.
   - Storage Path: `lost_found/{userId}/{itemId}/image.jpg`
   - Scoped strictly to the uploading user's UID in `storage.rules`.
2. **Metadata Layer (Cloud Firestore):** Stores document attributes in collection `lost_found`:
   - `id`: Unique document ID.
   - `title`: Item name (e.g., `"Casio Scientific Calculator FX-991EX"`).
   - `description`: Detailed visual marks, stickers, color.
   - `location`: Specific campus spot (e.g., `"Canteen Table 4"`, `"Lab 301"`).
   - `date`: Date found or lost.
   - `status`: String flag: `"LOST"` (red badge) or `"FOUND"` (green badge).
   - `reporterName`: Full name of reporter.
   - `contact`: Reporter email or phone number.
   - `imageUrl`: Public download URL pointing to Firebase Storage.
   - `userId`: Author UID.
   - `imageStoragePath`: Storage reference string.
   - `timestamp`: Epoch milliseconds.

### 19.2 Photo Picker & Preview Management
`ReportLostFoundActivity` utilizes Android's modern `ActivityResultContracts.GetContent()`:
- User taps "Select Image" $\rightarrow$ opens native system photo picker.
- Selected image renders in `imgPreview` with a "Remove Image" icon.
- User can change or remove the photo before submission.

### 19.3 Viva Safety Fallback (Storage 404 Resiliency)
In student viva or offline evaluation scenarios where Firebase Cloud Storage may be unprovisioned, firewalled, or encountering quota errors (HTTP 404), CampusOne includes an automatic fallback:
- If `storageRef.putFile()` throws an exception, the failure callback intercepts the error.
- Rather than crashing or aborting, it logs the warning, sets `imageUrl = ""`, proceeds to write the metadata into Firestore, and notifies the user with a friendly Toast: `"Image upload failed, saved report details without photo."`

### 19.4 Direct Contact Intents (Gmail & Phone)
Tapping "Contact Reporter" on any lost/found card displays a dialog offering direct communication:
1. **Email Reporter:**
   ```java
   Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
   emailIntent.setData(Uri.parse("mailto:" + contactEmail));
   emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry: " + item.getTitle() + " (CampusOne)");
   startActivity(Intent.createChooser(emailIntent, "Send Email"));
   ```
2. **Call / SMS Reporter:**
   ```java
   Intent callIntent = new Intent(Intent.ACTION_DIAL);
   callIntent.setData(Uri.parse("tel:" + contactPhone));
   startActivity(callIntent);
   ```

### 19.5 Owner-Only Deletion
- A delete button is visible **only** if `item.getUserId().equals(currentUserId)` or `currentUserProfile.getRole().equals("admin")`.
- Tapping delete presents an AlertDialog confirmation. Upon confirmation, the document is deleted from Firestore and its associated photo is deleted from Storage.

---

## 20. FIREBASE ARCHITECTURE

CampusOne integrates three core Firebase cloud primitives:

```mermaid
graph LR
    subgraph Firebase_Project [Firebase Project: campusone-79e84]
        subgraph F_Auth [Firebase Authentication]
            A1[Student Accounts]
            A2[Admin Accounts]
            A3[Club Lead Accounts]
        end
        
        subgraph F_Store [Cloud Firestore NoSQL]
            C1[(users)]
            C2[(clubs)]
            C3[(announcements)]
            C4[(events)]
            C5[(resources)]
            C6[(lost_found)]
            C7[(notifications)]
        end
        
        subgraph F_Storage [Firebase Storage]
            S1[lost_found/{userId}/{itemId}/image.jpg]
        end
    end

    App[CampusOne Android App] --> F_Auth
    App --> F_Store
    App --> F_Storage
```

1. **Firebase Authentication:**
   - Provider: Email/Password.
   - Purpose: Secure credential storage, token generation, user UID assignment, session recovery.
2. **Cloud Firestore:**
   - Architecture: Document-oriented NoSQL database.
   - Purpose: Stores structured application records across 7 collections with real-time snapshot listeners.
3. **Firebase Cloud Storage:**
   - Purpose: Object storage for Lost & Found photographs uploaded by students.

---

## 21. FIRESTORE DATABASE STRUCTURE

CampusOne utilizes 7 top-level collections in Cloud Firestore:

### 21.1 Collection: `users`
- **Document ID:** Firebase User UID (`request.auth.uid`).
- **Purpose:** User profiles, academic records, and role authorization.
- **Fields:**
  | Field | Type | Description |
  |---|---|---|
  | `uid` | String | Unique Firebase user ID |
  | `name` | String | Student / Administrator full name |
  | `email` | String | Institutional email (`@student.mes.ac.in`) |
  | `department` | String | e.g. `"Computer Engineering"` |
  | `year` | String | e.g. `"TE"`, `"BE"` |
  | `division` | String | e.g. `"A"`, `"B"` |
  | `role` | String | `"student"`, `"admin"`, or `"club_member"` |
  | `clubId` | String | Assigned club identifier (for club members) |
  | `createdAt` | Number (Long) | Epoch timestamp |
- **Access:** Read: Authenticated; Create/Update: Owner or Admin; Delete: Admin only.

### 21.2 Collection: `clubs`
- **Document ID:** Club identifier (e.g., `nss`, `csi`, `gdg`, `tpc`).
- **Purpose:** Registry of all 11 recognized student bodies.
- **Fields:**
  | Field | Type | Description |
  |---|---|---|
  | `clubId` | String | Lowercase identifier |
  | `name` | String | Official display title |
  | `description` | String | Full overview of activities |
  | `category` | String | `"Technical"`, `"Social Service"`, `"Automotive"`, etc. |
  | `active` | Boolean | Operational status flag |
  | `createdAt` | Number (Long) | Creation timestamp |
- **Access:** Read: Authenticated; Write: Admin only.

### 21.3 Collection: `announcements`
- **Document ID:** Unique auto-generated ID or `ann_*`.
- **Purpose:** Broadcast notices for campus and clubs.
- **Fields:**
  | Field | Type | Description |
  |---|---|---|
  | `id` | String | Announcement ID |
  | `title` | String | Notice heading |
  | `description` | String | Body text |
  | `date` | String | Display date (e.g. `"15 Sep 2026"`) |
  | `category` | String | `"COLLEGE ANNOUNCEMENT"` or `"CLUB ANNOUNCEMENT"` |
  | `clubId` | String | `"college"` or specific club ID |
  | `authorName` | String | Creator name |
  | `authorId` | String | Author UID |
  | `timestamp` | Number (Long) | Sort order timestamp |
- **Access:** Read: Authenticated; Create/Update/Delete: Admin or Club Member matching `clubId`.

### 21.4 Collection: `events`
- **Document ID:** Unique auto-generated ID or `evt_*`.
- **Purpose:** Campus calendar and event schedules.
- **Fields:**
  | Field | Type | Description |
  |---|---|---|
  | `id` | String | Event ID |
  | `title` | String | Event name |
  | `date` | String | Scheduled date |
  | `time` | String | Scheduled time |
  | `venue` | String | Campus venue |
  | `description` | String | Event agenda |
  | `organizer` | String | Display name of organizer |
  | `clubId` | String | `"college"`, `"general"`, or club ID |
  | `timestamp` | Number (Long) | Milliseconds for sorting |
- **Access:** Read: Authenticated; Create/Update/Delete: Admin or Club Member matching `clubId`.

### 21.5 Collection: `resources`
- **Document ID:** Unique auto-generated ID or `res_*`.
- **Purpose:** Academic resource metadata pointing to Google Drive files.
- **Fields:**
  | Field | Type | Description |
  |---|---|---|
  | `id` | String | Resource ID |
  | `title` | String | Document title |
  | `description` | String | Content summary |
  | `category` | String | Notes, PYQs, Lab Manuals, Syllabus, Forms, Guides |
  | `department` | String | Academic department |
  | `url` | String | Google Drive link |
  | `uploaderUid` | String | Student UID |
  | `uploaderName` | String | Student name |
  | `date` / `createdAt` | String | Creation date |
  | `status` | String | `"APPROVED"` |
  | `timestamp` | Number (Long) | Sort order timestamp |
- **Access:** Read: Authenticated; Create: Authenticated; Update/Delete: Owner or Admin.

### 21.6 Collection: `lost_found`
- **Document ID:** Unique auto-generated ID or `lf_*`.
- **Purpose:** Lost and found item listings.
- **Fields:**
  | Field | Type | Description |
  |---|---|---|
  | `id` | String | Item ID |
  | `title` | String | Item title |
  | `description` | String | Detailed description |
  | `location` | String | Campus location |
  | `date` | String | Date lost or found |
  | `status` | String | `"LOST"` or `"FOUND"` |
  | `reporterName` | String | Student name |
  | `contact` | String | Phone number or email |
  | `imageUrl` | String | Public download URL from Storage |
  | `imageStoragePath` | String | Storage path |
  | `userId` | String | Student UID |
  | `timestamp` | Number (Long) | Sort order timestamp |
- **Access:** Read: Authenticated; Create: Authenticated; Update/Delete: Owner or Admin.

### 21.7 Collection: `notifications`
- **Document ID:** Unique auto-generated ID or `notif_*`.
- **Purpose:** Historical notification log.
- **Fields:** `id`, `title`, `description`, `date`, `category`, `timestamp`.
- **Access:** Read: Authenticated; Write: Admin only.

---

## 22. FIREBASE STORAGE STRUCTURE

### 22.1 Bucket Path Structure
```
gs://campusone-79e84.firebasestorage.app/
└── lost_found/
    └── {userId}/
        └── {itemId}/
            └── image.jpg
```
- **File Type:** JPEG (`image/jpeg`).
- **File Size:** Compressed before upload to conserve mobile bandwidth.

### 22.2 Security Rules (`storage.rules`)
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /lost_found/{userId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```
- **Read:** Any logged-in user can download item photos.
- **Write:** Strictly locked to the user whose UID matches the `{userId}` directory path. Users cannot overwrite other students' uploaded images.

---

## 23. FIRESTORE SECURITY RULES

The production `firestore.rules` file enforces zero-trust security:

### 23.1 Helper Functions
```javascript
function isAuthenticated() {
  return request.auth != null;
}

function getUserData() {
  return get(/databases/$(database)/documents/users/$(request.auth.uid)).data;
}

function isAdmin() {
  return isAuthenticated() && (getUserData().role == 'admin' || getUserData().role == 'ADMIN');
}

function isClubMember() {
  return isAuthenticated() && (getUserData().role == 'club_member' || getUserData().role == 'CLUB_MEMBER');
}

function isClubLeadFor(clubId) {
  return isClubMember() && getUserData().clubId == clubId;
}
```

### 23.2 Rules Evaluation Summary
1. **Unauthenticated Access Blocked:** All collections unconditionally require `isAuthenticated()`.
2. **User Collection Protection:** Users can read profiles, update only their own profile, but cannot delete users or change other users' roles (only `isAdmin()` can delete or modify roles).
3. **Clubs Protection:** Clubs are read-only for students and club leads; writes require `isAdmin()`.
4. **Announcements & Events Scoping:** Admins have global write access. Club members can write **only** if `request.resource.data.clubId == getUserData().clubId`.
5. **Resources & Lost/Found Ownership:** Any student can create records. Deletions and updates strictly require `resource.data.uploaderUid == request.auth.uid` (or `userId == request.auth.uid`) or `isAdmin()`.

---

## 24. IMPORTANT JAVA CLASSES

| Class | Package | Responsibility |
|---|---|---|
| `MainActivity` | `com.campusone.app` | Main container activity hosting `BottomNavigationView` and 5 tabs; handles session validation |
| `LoginActivity` | `com.campusone.app` | Handles user authentication via Firebase Auth and routes based on role |
| `RegisterActivity` | `com.campusone.app` | Registers new students with `@student.mes.ac.in` domain verification and creates Firestore profile |
| `ResourcesActivity` | `com.campusone.app` | Displays academic study resources with category filtering and Drive links |
| `AddEditResourceActivity` | `com.campusone.app` | Form for submitting or updating Google Drive resource links |
| `LostFoundActivity` | `com.campusone.app` | Feed of lost and found items with status badges and contact dialogs |
| `ReportLostFoundActivity` | `com.campusone.app` | Form to report lost/found items with image picker, preview, and storage upload |
| `EventDetailActivity` | `com.campusone.app` | Detailed event screen showing agenda, venue, time, and organizer |
| `AddEditEventActivity` | `com.campusone.app` | Event creation and modification form for admins and club leads |
| `ClubDetailActivity` | `com.campusone.app` | Displays detailed club information and filtered club announcements |
| `AdminDashboardActivity` | `com.campusone.app` | Central administrative command center for announcements, events, resources, users |
| `AdminAnnouncementsActivity` | `com.campusone.app` | Dedicated announcement moderation screen for administrators |
| `AdminUsersActivity` | `com.campusone.app` | User list interface for promoting students to club leads or admins |
| `AddEditAnnouncementActivity` | `com.campusone.app` | Form for drafting official notices with category selection |
| `ClubMemberDashboardActivity` | `com.campusone.app` | Dedicated dashboard for club leaders scoped to their assigned club ID |
| `HomeFragment` | `com.campusone.app.fragments` | Home tab with header, role badge, quick access grid, featured event, recent notices |
| `EventsFragment` | `com.campusone.app.fragments` | Events tab displaying campus calendar |
| `ClubsFragment` | `com.campusone.app.fragments` | Directory tab presenting all 11 recognized student clubs |
| `NotificationsFragment` | `com.campusone.app.fragments` | Timeline tab showing official broadcasts |
| `ProfileFragment` | `com.campusone.app.fragments` | Profile tab showing student data, edit dialog, and sign out |
| `FirebaseManager` | `com.campusone.app.firebase` | Singleton managing Firebase instances, cached profile, and collection references |
| `SampleDataSeeder` | `com.campusone.app.firebase` | Batch seeder populating demo clubs, notices, events, resources, and items |
| `ValidationUtils` | `com.campusone.app.utils` | Static validation methods for emails, passwords, URLs, and dates |
| `InsetsUtils` | `com.campusone.app.utils` | Window insets utility applying top system bar padding to root views |
| `ImageLoader` | `com.campusone.app.utils` | In-memory `LruCache` asynchronous bitmap downloader without third-party libraries |

---

## 25. IMPORTANT XML LAYOUTS

| Layout File | Screen / Component | Purpose |
|---|---|---|
| `activity_main.xml` | `MainActivity` | Container for `FragmentContainerView` and `BottomNavigationView` |
| `activity_login.xml` | `LoginActivity` | Login screen with institutional branding, inputs, and register link |
| `activity_register.xml` | `RegisterActivity` | Registration form with name, email, department, year, division fields |
| `activity_resources.xml` | `ResourcesActivity` | Toolbar, category chips, `RecyclerView`, and Add FAB |
| `activity_add_edit_resource.xml` | `AddEditResourceActivity` | Form for Title, Category, Department, Google Drive URL |
| `activity_lost_found.xml` | `LostFoundActivity` | Toolbar, filter tabs, item `RecyclerView`, and Report FAB |
| `activity_report_lost_found.xml` | `ReportLostFoundActivity` | Form with photo selection, preview, status toggle, contact info |
| `activity_event_detail.xml` | `EventDetailActivity` | Header banner, date/venue chips, description card, action button |
| `activity_add_edit_event.xml` | `AddEditEventActivity` | Form for Title, Date, Time, Venue, Organizer, Description |
| `activity_club_detail.xml` | `ClubDetailActivity` | Club banner, category badge, description, and club notices feed |
| `activity_admin_dashboard.xml` | `AdminDashboardActivity` | Grid of administrative tools (Announcements, Events, Users, Seed) |
| `activity_admin_announcements.xml` | `AdminAnnouncementsActivity` | Admin list of announcements with edit and delete controls |
| `activity_admin_users.xml` | `AdminUsersActivity` | User list with role badges and role modification triggers |
| `activity_add_edit_announcement.xml` | `AddEditAnnouncementActivity` | Form with Title, Category selector, Body text, Club ID |
| `activity_club_member_dashboard.xml` | `ClubMemberDashboardActivity` | Scoped club header, quick post actions, and club feed |
| `fragment_home.xml` | `HomeFragment` | Header, role chip, role-specific action cards, quick grid, event card |
| `fragment_events.xml` | `EventsFragment` | Event list with swipe refresh and filter options |
| `fragment_clubs.xml` | `ClubsFragment` | 11-club directory list with custom icons |
| `fragment_notifications.xml` | `NotificationsFragment` | Chronological feed of official announcements |
| `fragment_profile.xml` | `ProfileFragment` | Student ID card layout, department details, edit button, logout |
| `dialog_edit_profile.xml` | `ProfileFragment` Dialog | Modal form for editing student name, department, year, division |
| `item_announcement.xml` | `AnnouncementAdapter` | Card with category chip, title, snippet, author, and date |
| `item_club.xml` | `ClubAdapter` | Card with custom club vector icon, title, category, and chevron |
| `item_event.xml` | `EventAdapter` | Card with date box, event title, time, venue, and organizer |
| `item_lost_found.xml` | `LostFoundAdapter` | Card with image thumbnail, status badge, title, location, contact btn |
| `item_notification.xml` | `NotificationAdapter` | Notification row with timestamp and category tag |
| `item_resource.xml` | `ResourceAdapter` | Card with title, category chip, department, author, and link icon |
| `item_user.xml` | `UserAdapter` | User row with name, email, department, role chip, and edit role btn |

---

## 26. IMPORTANT METHODS & UTILITIES

### 26.1 `ValidationUtils.java`
- `isValidStudentEmail(String email)`: Tests against `^[A-Za-z0-9._%+-]+@student\.mes\.ac\.in$`.
- `isValidEmail(String email)`: General email validator for staff and administrators.
- `isValidPassword(String password)`: Validates `password != null && password.trim().length() >= 6`.
- `isValidUrl(String url)`: Validates that string begins with `http://` or `https://`.
- `isGoogleDriveUrl(String url)`: Validates that URL contains `drive.google.com` or `docs.google.com`.
- `isNotEmpty(String text)`: Ensures string is non-null and contains non-whitespace characters.
- `getCurrentFormattedDate()`: Formats current time into `"dd MMM yyyy"` (e.g. `"23 Sep 2026"`).

### 26.2 `InsetsUtils.java`
- `applySystemBarInsets(View view)`:
  ```java
  public static void applySystemBarInsets(View view) {
      if (view == null) return;
      ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
          return insets;
      });
  }
  ```
  Attaches an insets listener to dynamically consume `systemBars.top`, eliminating status bar overlap without hardcoded margins.

### 26.3 `ImageLoader.java`
- In-memory `LruCache<String, Bitmap>` sized to 15MB.
- `load(String url, ImageView imageView)`:
  - If cached, immediately sets bitmap on the UI thread.
  - If uncached, tags view with URL to prevent RecyclerView view recycling mismatches, executes `HttpURLConnection` in a 3-thread fixed threadpool, decodes `BitmapFactory.decodeStream()`, stores in `memoryCache`, and posts back to the main thread via `Handler(Looper.getMainLooper())`.

### 26.4 `FirebaseManager.java`
- Thread-safe singleton `getInstance()`.
- Accessors: `getAuth()`, `getFirestore()`, `getStorage()`, `getStorageReference()`.
- Session helpers: `getCurrentUserId()`, `isUserLoggedIn()`, `signOut()`.
- Profile cache: `getCachedUserProfile()`, `setCachedUserProfile(User user)`.
- `fetchUserProfile(String uid, OnSuccessListener, OnFailureListener)`: Pulls live Firestore user document and converts to `User` POJO.

---

## 27. UI & DESIGN SYSTEM

CampusOne incorporates an official university design palette tailored for readability, contrast, and visual hierarchy:

```mermaid
graph LR
    subgraph Colors [University Color Palette]
        P[Primary Maroon #800000]
        DM[Dark Maroon #650000]
        W[Background White #FFFFFF]
        LMS[Light Maroon Surface #FFF7F7]
        LMA[Light Maroon Accent #F8E8EA]
        B[Primary Text #212121]
        G[Secondary Text #666666]
        BR[Border Maroon #E8C8CC]
    end
```

### 27.1 Color Tokens (`colors.xml`)
- **Primary Maroon (`#800000`):** Core brand color; applied to Top Toolbars, Primary Action Buttons, Active Bottom Navigation Tabs, and Focus Rings.
- **Dark Maroon (`#650000`):** Accent shade for Admin headers, pressed states, and high-emphasis elements.
- **Main Background (`#FFFFFF`):** Pure white background for all activity roots.
- **Card Background (`#FFFFFF`):** Crisp white surface eliminating dark/charcoal cards.
- **Light Maroon Surface (`#FFF7F7`):** Subtle tinted background for input containers, read-only fields, and section headers.
- **Light Maroon Accent (`#F8E8EA`):** Fill color for Student and Admin role chips.
- **Primary Text (`#212121`):** Near-black typography for maximum contrast against white card surfaces.
- **Secondary Text (`#666666`):** Slate gray for metadata, dates, authors, and captions.
- **Border / Stroke (`#E8C8CC`):** Subtle 1dp border applied to MaterialCards to define card boundaries cleanly.
- **Lost Status Badge:** `#C62828` text on `#FDE8E8` container.
- **Found Status Badge:** `#2E7D32` text on `#E8F5E9` container.

### 27.2 Components & Typography
- **Cards:** `com.google.android.material.card.MaterialCardView` with `app:cardCornerRadius="12dp"`, `app:strokeColor="@color/border_maroon"`, and `app:strokeWidth="1dp"`.
- **Buttons:** Pill-shaped Material Buttons with 8dp corner radius and `#800000` background.
- **Bottom Navigation:** `BottomNavigationView` with pill indicator `#F3DDE1` and active icon tint `#800000`.
- **Inputs:** `TextInputLayout` in OutlinedBox mode with `#800000` stroke focus.

---

## 28. WINDOW INSETS & ANDROID SYSTEM UI HANDLING

### 28.1 The Edge-to-Edge Problem
On modern Android devices (Android 14/15/16, API 34+), the default window configuration extends layouts edge-to-edge behind the transparent system status bar and display camera cutouts. Without manual insets handling, toolbars, back buttons, and titles render directly under status bar icons (battery, clock, Wi-Fi), causing visual collisions and untappable navigation arrows.

### 28.2 The CampusOne Insets Solution
CampusOne resolves this systematically using `InsetsUtils.applySystemBarInsets(rootView)`:
1. In every secondary Activity (`ResourcesActivity`, `LostFoundActivity`, `AdminDashboardActivity`, `ClubDetailActivity`, `EventDetailActivity`, etc.), the root layout is retrieved immediately following `setContentView()`.
2. `InsetsUtils.applySystemBarInsets(rootView)` attaches a `ViewCompat.setOnApplyWindowInsetsListener`.
3. When the window dispatches system insets, it extracts `insets.getInsets(WindowInsetsCompat.Type.systemBars()).top`.
4. It sets the top padding of the root view to match the exact system bar height.
5. This dynamically accommodates standard status bars, punch-hole cameras, and notch cutouts without hardcoding fragile pixel margins.

---

## 29. SYSTEM DATA FLOWS

### 29.1 Registration Data Flow
1. User enters profile fields in `RegisterActivity`.
2. Client checks regex: `email.endsWith("@student.mes.ac.in")` and `password.length >= 6`.
3. `FirebaseAuth.createUserWithEmailAndPassword()` registers account in Firebase Auth.
4. Generates unique `uid`.
5. Writes new document to `users/{uid}` with role `"student"`.
6. Sets cached profile in `FirebaseManager`.
7. Navigates to `MainActivity`.

### 29.2 Academic Resource Submission Flow
1. Student obtains shareable link from personal Google Drive.
2. In `AddEditResourceActivity`, student enters Title, Category, Department, and URL.
3. `ValidationUtils.isGoogleDriveUrl()` confirms URL validity.
4. `FirebaseFirestore.collection("resources").add(resource)` writes document.
5. `ResourcesActivity` receives update via snapshot listener or query refresh.
6. Card displays in feed; clicking it triggers `ACTION_VIEW` intent opening external browser.

### 29.3 Lost & Found Reporting Flow
1. User enters item details in `ReportLostFoundActivity`.
2. Optional photo selected via `ActivityResultContracts.GetContent()`.
3. If photo selected:
   - File stream uploads to Firebase Storage at `lost_found/{userId}/{itemId}/image.jpg`.
   - On success, retrieves `downloadUrl`.
   - On failure (404/network), executes fallback: sets `imageUrl = ""` and proceeds.
4. Item metadata and `imageUrl` written to Firestore collection `lost_found`.
5. Item displays immediately on `LostFoundActivity`.

### 29.4 User Role Elevation Flow
1. Administrator navigates to `AdminDashboardActivity` $\rightarrow$ `AdminUsersActivity`.
2. App queries `users` collection.
3. Admin selects a student and chooses new role:
   - If promoting to `club_member`, admin selects target club from the 11-club dialog.
   - If promoting to `admin`, role is set to `"admin"`.
4. Document `users/{selectedUid}` updated with new `role` and `clubId`.
5. Upon the user's next login or profile refresh, their UI adapts with elevated permissions.

---

## 30. SECURITY IMPLEMENTATION

1. **Zero Client Bypass:** All demo logins, mock role switches, and bypass backdoors were permanently removed. Authentication relies entirely on Firebase Auth.
2. **Domain Whitelisting:** Client-side regex strictly limits student registration to `@student.mes.ac.in`.
3. **Database Rules Enforcement:** Firestore security rules validate every read, write, update, and delete operation against the user's authenticated UID and server-side document in `users/{request.auth.uid}`.
4. **Club Isolation:** Club members can only write documents matching their `assignedClubId`.
5. **Storage Isolation:** Photo upload permissions in Firebase Storage are partitioned strictly by user UID (`/lost_found/{userId}/...`), preventing cross-user file overwriting.
6. **Credential Privacy:** Zero passwords, API keys, or raw Firebase configuration secrets are hardcoded in source repositories.

---

## 31. ERROR HANDLING & RESILIENCE

| Failure Scenario | Interception Mechanism | User Experience |
|---|---|---|
| Invalid College Domain | `ValidationUtils.isValidStudentEmail()` in `RegisterActivity` | Immediate error text on input field: *"Only @student.mes.ac.in allowed"* |
| Weak Password (< 6 chars) | `ValidationUtils.isValidPassword()` in `RegisterActivity` | Field error: *"Password must be at least 6 characters"* |
| Invalid / Malformed URL | `ValidationUtils.isValidUrl()` in `AddEditResourceActivity` | Field error: *"Please enter a valid HTTP/HTTPS URL"* |
| Non-Drive Link in Resources | `ValidationUtils.isGoogleDriveUrl()` | Warning prompt advising student to provide a Google Drive shareable link |
| Firebase Storage Unprovisioned (404) | Failure listener in `ReportLostFoundActivity` | Graceful fallback: saves metadata without image; toast notifies student |
| Network Timeout / Offline | `addOnFailureListener` across all Firestore queries | Toast notification: *"Failed to load data. Please check connection."* |
| Broken / Dead Image Link | `try-catch` inside `ImageLoader` background thread | Silently hides image or maintains placeholder; no application crash |
| Unauthorized Moderation Attempt | Hidden UI controls + Firestore rule denial | Regular students cannot see Edit/Delete buttons for others' items |

---

## 32. VERIFICATION & TESTING MATRIX

All test suites were executed on the active codebase. Unit tests passed with 100% compliance. Live flows were verified on Android Virtual Device `ExpiryAI_API35` (API 35, Android 15).

### 32.1 Automated Unit Tests (`ValidationUtilsTest.java`)
| Test Method | Purpose | Expected Result | Actual Result | Status |
|---|---|---|---|:---:|
| `studentEmail_validatesCorrectCollegeDomain` | Verify valid `@student.mes.ac.in` addresses | All return `true` | Returned `true` | **PASS** |
| `studentEmail_rejectsUnauthorizedDomains` | Test `@gmail.com`, `@yahoo.com`, empty string | All return `false` | Returned `false` | **PASS** |
| `generalEmail_validatesStandardEmails` | Verify standard email format for staff/admins | Validates standard emails | Validated correctly | **PASS** |
| `password_enforcesMinimumLength` | Verify $\ge 6$ character password threshold | $\ge 6$ passes, $< 6$ fails | Enforced correctly | **PASS** |
| `isNotEmpty_validatesProperly` | Test non-empty, whitespace-only, null strings | Whitespace/null fail | Validated correctly | **PASS** |
| `url_validatesProperly` | Verify HTTP/HTTPS URL prefixes | http/https passes, ftp fails | Validated correctly | **PASS** |
| `googleDriveUrl_identifiesDriveLinksCorrectly` | Identify `drive.google.com` links | Identifies Drive domains | Validated correctly | **PASS** |
| `clubs_resolvesAllElevenDefinedClubsToDistinctIcons` | Verify all 11 clubs return non-zero vector drawables | All 11 return valid IDs | All 11 resolved | **PASS** |

### 32.2 Live System & Functional Verification
| Test ID | Feature Under Test | Test Scenario | Expected Result | Actual Result | Status |
|---|---|---|---|---|:---:|
| TC-01 | Build Verification | `./gradlew.bat assembleDebug` | Build succeeds with zero compilation errors | `BUILD SUCCESSFUL in 12s` | **PASS** |
| TC-02 | Unit Test Execution | `./gradlew.bat testDebugUnitTest` | 8/8 tests in `ValidationUtilsTest` pass | `8 tests completed, 0 failed` | **PASS** |
| TC-03 | Student Registration | Register with `test.student@student.mes.ac.in` | Account created in Auth and Firestore `users` | Account created, routed to Home | **PASS** |
| TC-04 | Domain Enforcement | Attempt register with `test@gmail.com` | Registration rejected client-side | Form blocked with error toast | **PASS** |
| TC-05 | Role Resolution | Login with Admin credentials | Role resolved to `"admin"`; admin card visible | Admin Control Panel displayed | **PASS** |
| TC-06 | Resource Submission | Submit notes with valid Google Drive link | Document saved in Firestore `resources` | Appears in Resources list | **PASS** |
| TC-07 | Resource Consumption | Tap resource item in `ResourcesActivity` | Fires `ACTION_VIEW` intent to open Drive/Browser | Browser launches with target URL | **PASS** |
| TC-08 | Resource Ownership | Non-author views student's resource | Long-press does not show Edit/Delete options | Non-authors cannot modify | **PASS** |
| TC-09 | Lost & Found Photo Picker | Select photo in `ReportLostFoundActivity` | Photo selected and preview displayed | Preview rendered in `ImageView` | **PASS** |
| TC-10 | Storage 404 Resiliency | Report item when Storage is unavailable | Graceful fallback saves metadata to Firestore | Item saved, toast displayed | **PASS** |
| TC-11 | Contact Reporter Intent | Tap "Contact Reporter" on Lost item | Opens chooser for Email and Phone intents | Dialer/Gmail opened with data | **PASS** |
| TC-12 | Owner Item Deletion | Author taps delete on their Lost item | Confirmation dialog appears; item deleted | Item removed from list | **PASS** |
| TC-13 | 11 Clubs Rendering | Open Clubs tab in Bottom Navigation | Displays 11 clubs with unique vector drawables | All 11 rendered correctly | **PASS** |
| TC-14 | Window Insets Check | Open secondary activities on API 35 | Top toolbar does not collide with status bar | Proper padding applied via `InsetsUtils` | **PASS** |
| TC-15 | University Color Palette | Inspect background and card contrast | White cards, maroon accents, dark text | Palette applied cleanly | **PASS** |

---

## 33. BUILD & DEPLOYMENT SPECIFICATIONS

- **Build Tool:** Gradle 8.13 via Gradle Wrapper (`gradlew.bat`).
- **Compilation Command:**
  ```powershell
  ./gradlew.bat assembleDebug
  ```
- **Test Command:**
  ```powershell
  ./gradlew.bat testDebugUnitTest
  ```
- **Build Status:** `BUILD SUCCESSFUL` (0 errors, 0 warnings).
- **Output Artifact:**
  - Path: `app/build/outputs/apk/debug/app-debug.apk`
  - Size: **11,936,809 bytes (~11.38 MB)**
  - Timestamp: `23-09-2026 23:21:47`
- **Application Package:** `com.campusone.app`
- **Target Architecture:** Universal APK supporting `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`.
- **Verified Virtual Devices:** Android Virtual Device `ExpiryAI_API35` (Pixel, Android 15.0, API Level 35, x86_64).

---

## 34. CURRENT IMPLEMENTATION LIMITATIONS

To maintain documentation honesty and academic integrity, the following genuine architectural boundaries of the current release are noted:
1. **External Resource Hosting:** Academic files are not stored on local device flash or in Firebase Storage; they rely entirely on Google Drive links. If an external student revokes Drive permissions, the document cannot be opened.
2. **Offline Data Persistence:** While Cloud Firestore caches recently fetched documents in local SQLite storage, full offline creation queues depend on standard Firestore background sync without an explicit Room database layer.
3. **In-App Messaging Omission:** CampusOne intentionally does not implement a peer-to-peer real-time chat service; contact for Lost & Found items is delegated to system email (`mailto:`) and telephone (`tel:`) intents.
4. **Push Notifications:** The notification tab reflects Firestore `notifications` and `announcements` documents via live snapshot listeners. Background push notifications via Firebase Cloud Messaging (FCM) server workers are not currently provisioned.

---

## 35. FUTURE SCOPE (PLANNED / NOT IMPLEMENTED)

> [!NOTE]
> The following features are explicitly **NOT IMPLEMENTED** in the current version of CampusOne and represent theoretical future enhancements for subsequent releases:

1. **Firebase Cloud Messaging (FCM) Background Worker:** Provisioning cloud functions to trigger remote push notifications when emergency announcements are published.
2. **In-App PDF Viewer Integration:** Embedding Android PDFium or WebView rendering to preview study notes directly inside the application without launching external browsers.
3. **Automated Optical Character Recognition (OCR):** Integrating Google ML Kit to automatically scan student identity cards in Lost & Found photos and auto-fill student roll numbers.
4. **Multi-Campus Institutional Federation:** Extending the database schema to support multiple colleges under the Mahatma Education Society umbrella through campus tenant identifiers.

---

## 36. CONCLUSION

CampusOne represents a fully realized, end-to-end engineered Android college application developed with native Java and modern Android toolchains. By directly integrating Google Firebase Authentication, Cloud Firestore, and Firebase Storage, the application solves real-world campus communication fragmentation across students, administration, and 11 distinct student organizations.

The architecture emphasizes reliability, clean separation of concerns, high visual accessibility through an institutional `#800000` maroon and white palette, and strict security rules governing access control. With zero extraneous dependencies, an in-memory `LruCache` image loader, edge-to-edge system insets handling, and comprehensive passing unit tests, CampusOne is fully verified, stable, and ready for viva examination and academic reporting.

---

## 37. APPENDIX

### 37.1 Key Configuration Files
- **App Build Script:** `app/build.gradle.kts`
- **Dependency Catalog:** `gradle/libs.versions.toml`
- **Manifest:** `app/src/main/AndroidManifest.xml`
- **Firestore Security Rules:** `firestore.rules`
- **Storage Security Rules:** `storage.rules`

### 37.2 Firebase Environment Details
- **Firebase Project ID:** `campusone-79e84`
- **Package Name:** `com.campusone.app`
- **Primary Auth Provider:** Email / Password

### 37.3 Verified Test Roles (Non-Exposing)
| Role Category | Example Account Format | Permissions Scope |
|---|---|---|
| **Student** | `*.student@student.mes.ac.in` | Read-only feeds, own resource sharing, own lost/found reporting |
| **Club Member** | `csi.lead@mes.ac.in` (Assigned: `csi`) | Student privileges + scoped announcement/event posting for assigned club |
| **Admin** | `admin@mes.ac.in` | Global administrative CRUD, user role management, data seeding |

---
*End of Technical Documentation.*
