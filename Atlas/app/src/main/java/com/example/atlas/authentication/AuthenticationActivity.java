package com.example.atlas.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atlas.MainActivity;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = AuthenticationActivity.class.getSimpleName();

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // TODO: 1. if user object created but details not saved and pressed back then go to user details fragment when app opens again.
        // TODO: 2. if user object created, details saved but profile is null and pressed back then go to user profile fragment when app opens again.
        // TODO: 3. if user object created, details saved but profile is also saved and pressed back
        //  then go to main activity(here on pressing back user profile should not open) when app opens again.

        if (firebaseAuth.getCurrentUser() == null) {
            Log.d(TAG, "new user it is!");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.auth_fragment_container, new LoginFragment())
                    .commit();
        } else {
            Log.d(TAG, "its not a new user");
            Log.d(TAG, "task starting to fetch user");
            DocumentReference user = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
            Log.d(TAG, "user's document fetched successfully");
            user.get().addOnCompleteListener(task -> {
                User userObject = null;
                if (task.isSuccessful()) {
                    Log.d(TAG, String.valueOf(userObject));
                    userObject = task.getResult().toObject(User.class);
                    Log.d(TAG, "task attempted successfully " + userObject);
                    if (userObject != null) {
                        if (userObject.getName() == null)
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.auth_fragment_container, new UserDetailsFragment())
                                    .commit();
                        else if(userObject.getProfile() == null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.auth_fragment_container, new UserProfileFragment())
                                    .commit();
                        }
                        else {
                            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                        }
                    }
                } else {
                    Log.v(TAG, "task failed");
                    Toast.makeText(AuthenticationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
