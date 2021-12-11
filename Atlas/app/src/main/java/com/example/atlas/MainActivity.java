package com.example.atlas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.nv_bottom);
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_activity_container, new HomeFragment())
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_home :
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_activity_container, new HomeFragment())
                            .commit();
                    break;
                case R.id.menu_starred :
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_activity_container, new StarredUsersFragment())
                            .commit();
                    break;
                case R.id.menu_chat :
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_activity_container, new ChatFragment())
                            .commit();
                    break;
                default:
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }
}