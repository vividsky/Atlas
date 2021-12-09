package com.example.atlas.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.atlas.R;
import com.example.atlas.ServiceProviderDetailsFragment;
import com.example.atlas.UserDetailsFragment;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.auth_fragment_container, new LoginFragment())
                .commit();
    }
}
