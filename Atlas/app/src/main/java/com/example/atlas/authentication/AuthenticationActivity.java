package com.example.atlas.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atlas.home.MainActivity;
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

        if (firebaseAuth.getCurrentUser() == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.auth_fragment_container, new LoginFragment())
                    .commit();
        } else {
            DocumentReference user = firebaseFirestore.collection(getString(R.string.user)).document(firebaseAuth.getCurrentUser().getUid());

            user.get().addOnCompleteListener(task -> {
                User userObj;
                if (task.isSuccessful()) {
                    userObj = task.getResult().toObject(User.class);
                    if (userObj != null) {
                        if (userObj.getName() == null)
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.auth_fragment_container, new UserDetailsFragment())
                                    .commit();
                        else if(userObj.getProfile() == null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.auth_fragment_container, new UserProfileFragment())
                                    .commit();
                        }
                        else {
                            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(AuthenticationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
