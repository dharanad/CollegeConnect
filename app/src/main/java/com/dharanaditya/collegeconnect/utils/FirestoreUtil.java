/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dharanaditya.collegeconnect.models.AssignmentFeed;
import com.dharanaditya.collegeconnect.models.NotificationFeed;
import com.dharanaditya.collegeconnect.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

/**
 * Created by dharanaditya on 31/01/18.
 */

public class FirestoreUtil {
    private static final String TAG = FirestoreUtil.class.getSimpleName();

    private static final String TEST_DATA = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    public static void assignmentStubTest() {
        CollectionReference collectionReference = FirebaseFirestore
                .getInstance()
                .collection("feed")
                .document("assignment")
                .collection("year-1");

        AssignmentFeed assignmentFeed = new AssignmentFeed(
                "BME Assignment 3", TEST_DATA, "Hui Hui", "ksjdflksjadglksdg", "ECE", "1", "A", "DSP", new Date(2018, 2, 14)
        );
        collectionReference.add(assignmentFeed)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        showSnackBar("Data posted success");
                        Log.d(TAG, "Data posted successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                showSnackBar("Unable to post data. Try again");
                Log.d(TAG, "Unable to post data. Try again");
            }
        });
    }

    public static void notificationStubTest() {
        String uid = FirebaseAuth.getInstance().getUid();
        CollectionReference collectionReference = FirebaseFirestore
                .getInstance()
                .collection("feed")
                .document("notification")
                .collection("general");

        NotificationFeed notificationFeed = new NotificationFeed("Notification", TEST_DATA, "Developer", uid);

        collectionReference.add(notificationFeed)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        showSnackBar("Data posted success");
                        Log.d(TAG, "Data posted successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                showSnackBar("Unable to post data. Try again");
                Log.d(TAG, "Unable to post data. Try again");
            }
        });
    }

    public static void stubUserData() {
        User[] users = new User[3];
        users[0] = new User("Dharan", "Aditya", "15A31A04L8", "student", "dharan.aditya@gmail.com", "7731907477");
        users[1] = new User("Krishna", "Mahidhar", "15A31B0540", "student", "chkmaheedhar@gmail.com", "8790273315");
        users[2] = new User("Uma", "Shankar", "15A31A04M8", "student", "umashd7@gmail.com", "9885690799");
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("user");
        collectionReference.document("kyVEGatV92Y81OGC6qMLC51RoZg1").set(users[0]);
        collectionReference.document("vIEEPKA4cBcrHEuUdeoEKrl1Y4w1").set(users[1]);
        collectionReference.document("wSkGoCN3NhQLknTwl4B6Jbq7cSK2").set(users[2]);
        Log.d(TAG, "stubUserData: Done");
    }
}
