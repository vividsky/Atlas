package com.example.atlas.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.atlas.adapters.ServiceProviderAdapter;
import com.example.atlas.adapters.ServiceReceiverAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class StarredFragment extends Fragment {

    private static final String TAG = StarredFragment.class.getSimpleName();
    static ArrayList<ServiceReceiver> starredServiceReceiversArrayList;
    static ArrayList<ServiceProvider> starredServiceProvidersArrayList;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    SwipeRefreshLayout swipeRefreshStarred;
    RecyclerView recyclerView;
    ServiceReceiverAdapter serviceReceiverAdapter;
    ServiceProviderAdapter serviceProviderAdapter;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // showing bottom navigation
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.VISIBLE);

        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        mDrawerLayout = getActivity().findViewById(R.id.main_activity_drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        // set hamburger icon to open drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // hiding swipe refresh
//        SwipeRefreshLayout swipeRefreshHome = getActivity().findViewById(R.id.swipe_refresh_home);
//        swipeRefreshHome.setEnabled(false);
//        swipeRefreshStarred = getActivity().findViewById(R.id.swipe_refresh_starred);
//        swipeRefreshStarred.setOnRefreshListener( () -> {
//            starredServiceReceiversArrayList.clear();
//            starredServiceProvidersArrayList.clear();
//            swipeRefreshStarred.setRefreshing(false);
//        });

        // to get the options and hide it then
//        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_starred, container, false);
    }

//    // hide the menu item refresh
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.menu_item_refresh).setVisible(false);
//        super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        user = (User) bundle.get(getString(R.string.user));
        Log.i(TAG, user.toString());

        recyclerView = view.findViewById(R.id.starred_fragment_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        if (user.getProfile().equals(getString(R.string.service_receiver))) {

            starredServiceProvidersArrayList = (ArrayList<ServiceProvider>) bundle.get(getString(R.string.starred_service_provider));
            serviceProviderAdapter = new ServiceProviderAdapter(starredServiceProvidersArrayList, getContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(serviceProviderAdapter);

        } else {

            starredServiceReceiversArrayList = (ArrayList<ServiceReceiver>) bundle.get(getString(R.string.starred_service_receiver));
            serviceReceiverAdapter = new ServiceReceiverAdapter(starredServiceReceiversArrayList, getContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(serviceReceiverAdapter);

        }

    }
}