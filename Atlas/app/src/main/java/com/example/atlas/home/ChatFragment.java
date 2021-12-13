package com.example.atlas.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.atlas.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // showing bottom navigation
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.VISIBLE);

        // hiding swipe refresh
        SwipeRefreshLayout swipeRefresh = getActivity().findViewById(R.id.swipe_refresh);
        swipeRefresh.setEnabled(false);

        // to get the options and hide it then
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    // hide the menu item refresh
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_refresh).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}