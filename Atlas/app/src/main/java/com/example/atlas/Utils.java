package com.example.atlas;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Utils {

    static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static DocumentReference getCurrentUserDocumentReference() {
        return firebaseFirestore.collection("User")
                .document(firebaseAuth.getCurrentUser().getUid());
    }
    public static DocumentReference getCurrentServiceProviderDocumentReference() {
        return firebaseFirestore.collection("ServiceProvider")
                .document(firebaseAuth.getCurrentUser().getUid());
    }
    public static DocumentReference getCurrentServiceReceiverDocumentReference() {
        return firebaseFirestore.collection("ServiceReceiver")
                .document(firebaseAuth.getCurrentUser().getUid());
    }

    public static FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
