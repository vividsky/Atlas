package com.example.atlas;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atlas.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String USER_PROFILE_SR = "Service Receiver";
    public static final String USER_PROFILE_SP = "Service Provider";

    TextView mProfiles;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String mCurrentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfiles = findViewById(R.id.tv_profiles);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // TODO: to ensure a user comes to this activity only if he has full modified profile and details saved on cloud.


        // get the current user profile
        Task<DocumentSnapshot> documentSnapshotTask = firebaseFirestore.collection("Users")
                .document(firebaseAuth.getCurrentUser().getUid()).get();
        documentSnapshotTask.addOnCompleteListener(task -> {
            Log.d(TAG, "task completed successfully");
            User userObj;
            if(task.isSuccessful()) {
                userObj = task.getResult().toObject(User.class);
                Log.d(TAG, String.valueOf(userObj));
                if(userObj != null && userObj.getProfile() != null) {
                     if(userObj.getProfile().equals(USER_PROFILE_SR)) {
                        // TODO: Show the data of SP
                        Log.d(TAG, "Show the data of SP");
                        mProfiles.setText("Show the data of SP");
                    }
                    else {
                        // TODO: Show the data of SR
                        Log.d(TAG, "Show the data of SR");
                        mProfiles.setText("Show the data of SR");
                    }
                }
                else {
                    // TODO it means a user whose profile was not set entered main activity
                    //  notify user his/her profile is not set and not valid user needs to set profile and come back
                    Log.d(TAG, "Your profile is not set, sign out and set profile");
                }
            }
        });

//        if(mCurrentUserProfile.equals(USER_PROFILE_SP)) {
//            // TODO: Show the data of SR
//            Log.d(TAG, "Show the data of SR");
//            mProfiles.setText("Show the data of SR");
//        } else if(mCurrentUserProfile.equals(USER_PROFILE_SR)) {
//            // TODO: Show the data of SP
//            Log.d(TAG, "Show the data of SP");
//            mProfiles.setText("Show the data of SP");
//        } else {
//            // TODO: notify user his/her profile is not set and not valid user, sign out, set profile and come back
//            Log.d(TAG, "Your profile is not set, sign out and set profile");
//        }



    }
}