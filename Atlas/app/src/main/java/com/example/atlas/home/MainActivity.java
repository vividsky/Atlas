package com.example.atlas.home;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.PreferenceActivity;
import com.example.atlas.R;
import com.example.atlas.Utils;
import com.example.atlas.authentication.AuthenticationActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    View navigationHeaderView;
    FrameLayout homeLayout;
    Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView mUserName;
    private TextView mUserEmail;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ServiceProvider serviceProvider;
    private User user;
    private String sortBy;
    private Set<String> gender;
    private Set<String> specialities;

    private static ArrayList<ServiceProvider> serviceProvidersArrayList;
    private static ArrayList<ServiceReceiver> serviceReceiversArrayList;
    private static ArrayList<ServiceReceiver> starredServiceReceiversArrayList;
    private static ArrayList<ServiceProvider> starredServiceProvidersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        serviceProvidersArrayList = new ArrayList<>();
        serviceReceiversArrayList = new ArrayList<>();
        starredServiceProvidersArrayList = new ArrayList<>();
        starredServiceReceiversArrayList = new ArrayList<>();

        drawerLayout = findViewById(R.id.main_activity_drawer_layout);
        homeLayout = findViewById(R.id.auth_fragment_container);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.nv_bottom);
        navigationHeaderView = navigationView.getHeaderView(0);
        mUserName = navigationHeaderView.findViewById(R.id.tv_username_in_nav_view);
        mUserEmail = navigationHeaderView.findViewById(R.id.tv_user_email_in_nav_view);
        swipeRefresh = findViewById(R.id.swipe_refresh_home);
        progressBar = findViewById(R.id.main_activity_progress_bar);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        progressBar.setVisibility(View.VISIBLE);

        // side navigation view
        navigationView.setNavigationItemSelectedListener(this);

//        DocumentReference userDR = Utils.getCurrentUserDocumentReference().get()
//                .addOnCompleteListener(task -> {
//                    User userObj;
//                    if(task.isSuccessful()) {
//
//                    }
//                })

        // set hamburger icon to open drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //swipe refresh feature
        swipeRefresh.setOnRefreshListener( () -> {
            serviceReceiversArrayList.clear();
            serviceProvidersArrayList.clear();
            starredServiceReceiversArrayList.clear();
            starredServiceProvidersArrayList.clear();
            passDataToFragments();
            swipeRefresh.setRefreshing(false);
        });

        // set bottom navigation bar
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sortBy = sharedPreferences.getString(getString(R.string.sort_by), getString(R.string.experience));


        Set<String> defaultGenders = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.preference_filter_by_gender_values)));
        gender = sharedPreferences.getStringSet(getString(R.string.filter_by_gender), defaultGenders);

        Set<String> defaultSpecialities = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.speciality_values)));
        specialities = sharedPreferences.getStringSet(getString(R.string.filter_by_speciality), defaultSpecialities);

        passDataToFragments();

    }

    private void passDataToFragments() {
        // get the current user to send it to fragments
        Utils.getCurrentUserDocumentReference().get().addOnCompleteListener(task -> {
            User userObj;
            if (task.isSuccessful()) {
                userObj = task.getResult().toObject(User.class);
                if (userObj != null) {
                    user = userObj;
                    mUserName.setText(userObj.getName());
                    mUserEmail.setText(userObj.getEmail());
                    ArrayList<String> starredUsers = userObj.getStarredUsers();

                    if(userObj.getProfile().equals(getString(R.string.service_provider))) {
                        // hide preference menu item for ServiceProvider
                        navigationView.getMenu().findItem(R.id.nav_preferences).setVisible(false);

                        // get the service provider to send it to fragments
                        Utils.getCurrentServiceProviderDocumentReference().get().addOnCompleteListener(task1 -> {
                            ServiceProvider serviceProviderObj;
                            if (task1.isSuccessful()) {
                                serviceProviderObj = task1.getResult().toObject(ServiceProvider.class);
                                if (serviceProviderObj != null) {
                                    serviceProvider = serviceProviderObj;
                                    // Fetching all serviceReceivers to show content on home fragment
                                    firebaseFirestore.collection(getString(R.string.service_receiver))
                                            .get()
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
                        Utils.getCurrentServiceReceiverDocumentReference()
                                .get().addOnCompleteListener(task2 -> {
                            ServiceReceiver serviceReceiver;
                            if (task2.isSuccessful()) {
                                serviceReceiver = task2.getResult().toObject(ServiceReceiver.class);
                                if (serviceReceiver != null) {
                                    // Fetching all serviceProviders to show content on home fragment
                                    firebaseFirestore.collection(getString(R.string.service_provider))
                                            .orderBy(sortBy)
                                            .get()
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
                                                            if (requirement.equals(speciality) && specialities.contains(requirement) && gender.contains(spObj.getUserDetails().getGender())) {
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


    @Override
    public void onBackPressed() {
        // if drawer is open, on back press drawer gets closed else we override super
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

            case R.id.nav_preferences:
                // TODO if user is of type serviceProvider, then hide it
                startActivity(new Intent(MainActivity.this, PreferenceActivity.class));
                finish();
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
                new AlertDialog.Builder(this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to Delete your account?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Continue with delete operation
                            deleteCurrentUser();
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_baseline_logout_24)
                        .show();
                break;

            default:
                Toast.makeText(this, "Yet to be implemented", Toast.LENGTH_LONG).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteCurrentUser() {

        DocumentReference currentUserDR = Utils.getCurrentUserDocumentReference();

        currentUserDR.get().addOnCompleteListener(fetchCurrentUser -> {
            String profile;
            // To delete ServiceProvider/ ServiceReceiver
            if (fetchCurrentUser.isSuccessful()) {

                User user = fetchCurrentUser.getResult().toObject(User.class);
                profile = user.getProfile();

                if (profile.equals(getString(R.string.service_provider))) {
                    Utils.getCurrentServiceProviderDocumentReference().delete();
                } else {
                    Utils.getCurrentServiceReceiverDocumentReference().delete();
                }

                // To delete CurrentUser
                currentUserDR
                        .delete()
                        .addOnCompleteListener(deleteCurrentUserFromDatabase -> {
                            if (deleteCurrentUserFromDatabase.isSuccessful()) {
                                Log.d(TAG, "User data deleted successfully");
                                FirebaseUser firebaseUser = Utils.getCurrentFirebaseUser();
                                firebaseUser.delete()
                                        .addOnCompleteListener(deleteUserAuthentication -> {
                                            if (deleteUserAuthentication.isSuccessful()) {
                                                Log.d(TAG, "User account deleted.");
                                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                                                finish();
                                            } else {
                                                Log.i(TAG, deleteUserAuthentication.getException().getMessage());
                                            }
                                        });
                            } else {
                                Log.d(TAG, deleteCurrentUserFromDatabase.getException().getMessage());
                            }
                        });

            } else {
                Log.d(TAG, fetchCurrentUser.getException().getMessage());
            }
        });
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
