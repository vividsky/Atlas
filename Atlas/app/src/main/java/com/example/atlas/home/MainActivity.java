package com.example.atlas.home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.authentication.AuthenticationActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    private DrawerLayout drawerLayout;
    private TextView mUserName;
    private TextView mUserEmail;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

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
                case R.id.menu_home:
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_activity_container, new HomeFragment())
                            .commit();
                    break;
                case R.id.menu_starred:
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_activity_container, new StarredUsersFragment())
                            .commit();
                    break;
                case R.id.menu_chat:
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


        // set custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.main_activity_drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navigationHeaderView = navigationView.getHeaderView(0);
        mUserName = navigationHeaderView.findViewById(R.id.tv_username_in_nav_view);
        mUserEmail = navigationHeaderView.findViewById(R.id.tv_user_email_in_nav_view);

        navigationView.setNavigationItemSelectedListener(this);


        // set hamburger icon to open drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // get the current user profile
        Task<DocumentSnapshot> documentSnapshotTask = firebaseFirestore.collection(getString(R.string.user))
                .document(firebaseAuth.getCurrentUser().getUid()).get();
        documentSnapshotTask.addOnCompleteListener(task -> {
            User userObj;
            if (task.isSuccessful()) {
                userObj = task.getResult().toObject(User.class);
                if (userObj != null) {
                    mUserName.setText(userObj.getName());
                    mUserEmail.setText(userObj.getEmail());
                } else {
                    Log.w(TAG, "userObj is null");
                }
            } else {
                Log.w(TAG, task.getException().getMessage());
            }
        });
    }

    // if drawer is open, on back press drawer gets closed else we override super
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_update_profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_container, new EditUserProfileFragment())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_logout:
                new AlertDialog.Builder(this)
                        .setTitle("Log out")
                        .setMessage("Are you sure you want to Log out?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Continue with delete operation
                            firebaseAuth.signOut();
                            Toast.makeText(this, "Sign out successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                            finish();
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_baseline_logout_24)
                        .show();
                break;

            case R.id.nav_delete_account:
//                deleteUser(mUserEmail.getText().toString());
                Toast.makeText(this, "Sorry, We love our users and cannot delete your account :)", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(this, "Yet to be implemented", Toast.LENGTH_LONG).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void deleteUser(String email) {
        String password = "Shradha123@";


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: authentication complete");
                            user.delete()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                            startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                                            finish();
                                            Toast.makeText(MainActivity.this, "Deleted User Successfully,", Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.d(TAG, "User account deleted failed due to task1.");
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Authentication failed");
                        }

                    });
        }
    }
}