package com.example.atlas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.atlas.authentication.AuthenticationActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

//    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        firebaseAuth = FirebaseAuth.getInstance();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.main_activity_container, new ServiceProviderDetailsFragment())
//                .commit();

//        if (firebaseAuth.getCurrentUser() == null) {
//            startActivity( new Intent(this, AuthenticationActivity.class));
//        }

    }
}