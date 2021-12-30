package com.example.atlas.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.atlas.adapters.ServiceProviderAdapter;
import com.example.atlas.adapters.ServiceReceiverAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();

    private static ArrayList<ServiceProvider> serviceProvidersArrayList;
    private static ArrayList<ServiceReceiver> serviceReceiversArrayList;

    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    RecyclerView mRecyclerView;
    ServiceProviderAdapter serviceProviderAdapter;
    ServiceReceiverAdapter serviceReceiverAdapter;

    User user;

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
        SwipeRefreshLayout swipeRefresh = getActivity().findViewById(R.id.swipe_refresh_home);
        swipeRefresh.setEnabled(true);

        user = (User) getArguments().getSerializable(getString(R.string.user));
        serviceProvidersArrayList = (ArrayList<ServiceProvider>) getArguments().getSerializable(getString(R.string.service_provider));
        serviceReceiversArrayList = (ArrayList<ServiceReceiver>) getArguments().getSerializable(getString(R.string.service_receiver));

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.main_activity_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        String profile = user.getProfile();

        if (profile.equals(getString(R.string.service_receiver))) {

            serviceProviderAdapter = new ServiceProviderAdapter(serviceProvidersArrayList, getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(serviceProviderAdapter);

        } else if (profile.equals(getString(R.string.service_provider))) {

            serviceReceiverAdapter = new ServiceReceiverAdapter(serviceReceiversArrayList, getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(serviceReceiverAdapter);

        } else {
            Toast.makeText(getContext(), "Some error has occurred", Toast.LENGTH_LONG).show();
        }
    }
}