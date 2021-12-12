package com.example.atlas.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atlas.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StarredUsersFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.VISIBLE);

        return inflater.inflate(R.layout.fragment_starred_users, container, false);
    }
}