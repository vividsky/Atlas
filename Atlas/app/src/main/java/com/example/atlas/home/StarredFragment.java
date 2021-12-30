package com.example.atlas.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.Utils;
import com.example.atlas.adapters.ServiceProviderAdapter;
import com.example.atlas.adapters.ServiceReceiverAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class StarredFragment extends Fragment {

    private static final String TAG = StarredFragment.class.getSimpleName();

    private static ArrayList<ServiceReceiver> starredServiceReceiversArrayList;
    private static ArrayList<ServiceProvider> starredServiceProvidersArrayList;
    
    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    ServiceReceiverAdapter serviceReceiverAdapter;
    ServiceProviderAdapter serviceProviderAdapter;

    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // showing bottom navigation
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.VISIBLE);

        // set toolbar head to "Atlas"
        mToolbar = getActivity().findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);

        // showing side navigation
        mDrawerLayout = getActivity().findViewById(R.id.main_activity_drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        // set hamburger icon to open drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // hiding swipe refresh
        SwipeRefreshLayout swipeRefreshHome = getActivity().findViewById(R.id.swipe_refresh_home);
        swipeRefreshHome.setEnabled(false);

        // to get the options
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_starred, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.starred_fragment_progress_bar);
        mRecyclerView = view.findViewById(R.id.starred_fragment_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        
        starredServiceReceiversArrayList = new ArrayList<>();
        starredServiceProvidersArrayList = new ArrayList<>();
        mProgressBar.setVisibility(View.VISIBLE);
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference userDR = Utils.getCurrentUserDocumentReference();
        userDR.get().addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()) {
                User userObj = task1.getResult().toObject(User.class);
                ArrayList<String> starredUsersId = userObj.getStarredUsers();
                if (userObj.getProfile().equals(getString(R.string.service_receiver))) {
                    firebaseFirestore.collection(getString(R.string.service_provider)).get()
                            .addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()) {
                                    for (QueryDocumentSnapshot sp : task2.getResult()) {
                                        ServiceProvider spObj = sp.toObject(ServiceProvider.class);
                                        if (starredUsersId.contains(spObj.getId())) {
                                            starredServiceProvidersArrayList.add(spObj);
                                            serviceProviderAdapter = new ServiceProviderAdapter(starredServiceProvidersArrayList, getContext());
                                            mRecyclerView.setHasFixedSize(true);
                                            mRecyclerView.setAdapter(serviceProviderAdapter);
                                        }
                                    }
                                } else {
                                    Log.i(TAG, task2.getException().getMessage());
                                }
                            });
                } else if(userObj.getProfile().equals(getString(R.string.service_provider))) {
                    firebaseFirestore.collection(getString(R.string.service_receiver)).get()
                            .addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()) {
                                    for (QueryDocumentSnapshot sr : task2.getResult()) {
                                        ServiceReceiver srObj = sr.toObject(ServiceReceiver.class);
                                        if (starredUsersId.contains(srObj.getId())) {
                                            starredServiceReceiversArrayList.add(srObj);
                                            serviceReceiverAdapter = new ServiceReceiverAdapter(starredServiceReceiversArrayList, getContext());
                                            mRecyclerView.setHasFixedSize(true);
                                            mRecyclerView.setAdapter(serviceReceiverAdapter);
                                        }
                                    }
                                } else {
                                    Log.i(TAG, task2.getException().getMessage());
                                }
                            });
                }
                mProgressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "Some error has occurred", Toast.LENGTH_LONG).show();
                Log.i(TAG, task1.getException().getMessage());
            }
        });
    }

    // hide the menu item refresh
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_refresh).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}