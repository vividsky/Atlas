package com.example.atlas.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.atlas.Models.Chatroom;
import com.example.atlas.R;
import com.example.atlas.adapters.ChatroomAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment {
    public static final String TAG = ChatRoomFragment.class.getSimpleName();

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    RecyclerView recyclerView;
    ChatroomAdapter chatroomAdapter;
    List<Chatroom> chatRoomsArrayList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // set toolbar head to "Atlas"
        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        // showing side navigation
        mDrawerLayout = getActivity().findViewById(R.id.main_activity_drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        // set hamburger icon to open drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // showing bottom navigation
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.VISIBLE);

        // hiding swipe refresh
        SwipeRefreshLayout swipeRefresh = getActivity().findViewById(R.id.swipe_refresh_home);
        swipeRefresh.setEnabled(false);

        // to get the options and hide it then
        setHasOptionsMenu(true);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatroom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.chatroom_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        chatRoomsArrayList = new ArrayList<>();
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(getString(R.string.user))
                .document(firebaseAuth.getCurrentUser().getUid()).collection(getString(R.string.chatrooms))
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Chatroom chatroom;
                        for(QueryDocumentSnapshot chatroomQuery: task.getResult()) {
                            chatroom = chatroomQuery.toObject(Chatroom.class);
                            chatRoomsArrayList.add(chatroom);
                        }
                        chatroomAdapter = new ChatroomAdapter(chatRoomsArrayList, getContext());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(chatroomAdapter);
                    } else {
                        Log.i(TAG, task.getException().getMessage());
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