package com.campusone.app.firebase;

import com.campusone.app.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseManager {

    private static FirebaseManager instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    private User currentUserProfile;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public StorageReference getStorageReference() {
        return storage.getReference();
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return auth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void signOut() {
        currentUserProfile = null;
        auth.signOut();
    }

    public User getCachedUserProfile() {
        return currentUserProfile;
    }

    public void setCachedUserProfile(User user) {
        this.currentUserProfile = user;
    }

    // Fetch user profile from Firestore
    public void fetchUserProfile(String uid, OnSuccessListener<User> successListener, OnFailureListener failureListener) {
        if (uid == null) {
            failureListener.onFailure(new Exception("UID cannot be null"));
            return;
        }

        getUsersCollection().document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setUid(documentSnapshot.getId());
                            currentUserProfile = user;
                            successListener.onSuccess(user);
                            return;
                        }
                    }
                    failureListener.onFailure(new Exception("User profile not found in database"));
                })
                .addOnFailureListener(failureListener);
    }

    // Collection References
    public CollectionReference getUsersCollection() {
        return firestore.collection("users");
    }

    public CollectionReference getAnnouncementsCollection() {
        return firestore.collection("announcements");
    }

    public CollectionReference getEventsCollection() {
        return firestore.collection("events");
    }

    public CollectionReference getClubsCollection() {
        return firestore.collection("clubs");
    }

    public CollectionReference getResourcesCollection() {
        return firestore.collection("resources");
    }

    public CollectionReference getLostFoundCollection() {
        return firestore.collection("lost_found");
    }

    public CollectionReference getNotificationsCollection() {
        return firestore.collection("notifications");
    }
}
