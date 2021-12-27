package com.example.atlas.home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.authentication.AuthenticationActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    static ArrayList<ServiceProvider> serviceProvidersArrayList;
    static ArrayList<ServiceReceiver> serviceReceiversArrayList;
    static ArrayList<ServiceReceiver> starredServiceReceiversArrayList;
    static ArrayList<ServiceProvider> starredServiceProvidersArrayList;

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;
    FrameLayout homeLayout;

    private DrawerLayout drawerLayout;
    private TextView mUserName;
    private TextView mUserEmail;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ServiceProvider serviceProvider;

    private User user;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.main_activity_drawer_layout);
        homeLayout = findViewById(R.id.auth_fragment_container);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navigationHeaderView = navigationView.getHeaderView(0);
        mUserName = navigationHeaderView.findViewById(R.id.tv_username_in_nav_view);
        mUserEmail = navigationHeaderView.findViewById(R.id.tv_user_email_in_nav_view);
        swipeRefresh = findViewById(R.id.swipe_refresh_home);
        progressBar = findViewById(R.id.main_activity_progress_bar);
        serviceProvidersArrayList = new ArrayList<>();
        serviceReceiversArrayList = new ArrayList<>();
        starredServiceProvidersArrayList = new ArrayList<>();
        starredServiceReceiversArrayList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);

        // side navigation view
        navigationView.setNavigationItemSelectedListener(this);
        // set hamburger icon to open drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        swipeRefresh.setOnRefreshListener( () -> {
            serviceReceiversArrayList.clear();
            serviceProvidersArrayList.clear();
            starredServiceReceiversArrayList.clear();
            starredServiceProvidersArrayList.clear();
            passDataToFragments();
            swipeRefresh.setRefreshing(false);
        });

        passDataToFragments();

        // set bottom navigation bar
        bottomNavigationView = findViewById(R.id.nv_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_home:
                    switchToHomeFragment();
                    break;
                case R.id.menu_starred:
                    switchToStarredFragment();
                    break;
                case R.id.menu_chat:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_activity_container, new ChatRoomFragment())
                            .commit();
                    break;
                default:
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

    }

    private void passDataToFragments() {
        // instead of fetching all users and SP's and SR's in each fragment we fetch them here and send it to fragments

        // get the current user to send it to fragments
        firebaseFirestore.collection(getString(R.string.user))
                .document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
            User userObj;
            if (task.isSuccessful()) {
                userObj = task.getResult().toObject(User.class);
                if (userObj != null) {

                    user = userObj;
                    mUserName.setText(userObj.getName());
                    mUserEmail.setText(userObj.getEmail());
                    ArrayList<String> starredUsers = userObj.getStarredUsers();

                    if(userObj.getProfile().equals(getString(R.string.service_provider))) {
                        // get the service provider to send it to fragments
                        firebaseFirestore.collection(getString(R.string.service_provider))
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .get().addOnCompleteListener(task1 -> {
                            ServiceProvider serviceProviderObj;
                            if (task1.isSuccessful()) {
                                serviceProviderObj = task1.getResult().toObject(ServiceProvider.class);
                                if (serviceProviderObj != null) {
                                    serviceProvider = serviceProviderObj;
                                    // Fetching all serviceReceivers to show content on home fragment
                                    firebaseFirestore.collection(getString(R.string.service_receiver)).get()
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot sp : task2.getResult()) {
                                                        ServiceReceiver srObj = sp.toObject(ServiceReceiver.class);

                                                        ArrayList<String> requirements = new ArrayList<>(srObj.getRequirements());
                                                        if (starredUsers.contains(srObj.getId())) {
                                                            starredServiceReceiversArrayList.add(srObj);
                                                        }
                                                        String speciality = serviceProviderObj.getSpeciality();

                                                        // Store the comparison output
                                                        // in ArrayList requirements
                                                        for (String requirement: requirements) {
                                                            if (requirement.equals(speciality)) {
                                                                serviceReceiversArrayList.add(srObj);
                                                            }
                                                        }
                                                    }
                                                    switchToHomeFragment();

                                                } else {
                                                    Log.i(TAG, task.getException().getMessage());
                                                }
                                            });
                                } else {
                                    Log.w(TAG, "serviceProviderObj is null");
                                }
                            } else {
                                Log.w(TAG, task1.getException().getMessage());
                            }
                        });

                    } else if(userObj.getProfile().equals(getString(R.string.service_receiver))) {
                        // get the service receiver to send it to fragments
                        firebaseFirestore.collection(getString(R.string.service_receiver))
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .get().addOnCompleteListener(task2 -> {
                            ServiceReceiver serviceReceiver;
                            if (task2.isSuccessful()) {
                                serviceReceiver = task2.getResult().toObject(ServiceReceiver.class);
                                if (serviceReceiver != null) {
                                    // Fetching all serviceProviders to show content on home fragment
                                    firebaseFirestore.collection(getString(R.string.service_provider)).get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    for (QueryDocumentSnapshot sp : task1.getResult()) {
                                                        ServiceProvider spObj = sp.toObject(ServiceProvider.class);

                                                        String speciality = spObj.getSpeciality();
                                                        ArrayList<String> requirements = new ArrayList<>(serviceReceiver.getRequirements());

                                                        if (starredUsers.contains(spObj.getId())) {
                                                            starredServiceProvidersArrayList.add(spObj);
                                                        }

                                                        // Store the comparison output
                                                        // in ArrayList requirements
                                                        for (String requirement: requirements) {
                                                            if (requirement.equals(speciality)) {
                                                                serviceProvidersArrayList.add(spObj);
                                                            }
                                                        }
                                                    }
                                                    switchToHomeFragment();
                                                } else {
                                                    Log.i(TAG, task.getException().getMessage());
                                                }
                                            });
                                } else {
                                    Log.w(TAG, "serviceReceiverObj is null");
                                }
                            } else {
                                Log.w(TAG, task2.getException().getMessage());
                            }
                        });
                    }
                } else {
                    Log.w(TAG, "userObj is null");
                }
            } else {
                Log.w(TAG, task.getException().getMessage());
            }
            progressBar.setVisibility(View.GONE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_refresh);
        item.setOnMenuItemClickListener(menuItem -> {
            progressBar.setVisibility(View.VISIBLE);
            serviceReceiversArrayList.clear();
            serviceProvidersArrayList.clear();
            passDataToFragments();
            return true;
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_update_profile:

                // put in bundle and set FragmentClass Arguments
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.user), (Serializable) user);
                if(user.getProfile().equals(getString(R.string.service_provider))) {
                    bundle.putSerializable(getString(R.string.service_provider), (Serializable) serviceProvider);
                }
                EditUserDetailsFragment fragObj = new EditUserDetailsFragment();
                fragObj.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_container, fragObj)
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

    private void switchToHomeFragment() {
        Bundle homeBundle = new Bundle();
        homeBundle.putSerializable(getString(R.string.user), user);
        homeBundle.putSerializable(getString(R.string.service_provider), serviceProvidersArrayList);
        homeBundle.putSerializable(getString(R.string.service_receiver), serviceReceiversArrayList);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(homeBundle);
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_activity_container, homeFragment)
                .commit();

    }
    private void switchToStarredFragment() {
        Bundle starredBundle = new Bundle();
        starredBundle.putSerializable(getString(R.string.user), user);
        starredBundle.putSerializable(getString(R.string.starred_service_provider), starredServiceProvidersArrayList);
        starredBundle.putSerializable(getString(R.string.starred_service_receiver), starredServiceReceiversArrayList);
        StarredFragment starredFragment = new StarredFragment();
        starredFragment.setArguments(starredBundle);
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_activity_container, starredFragment)
                .commit();
    }
}
