package com.example.atlas.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.atlas.Models.Message;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.adapters.MessageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private static final String TAG = MessageFragment.class.getSimpleName();

    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    RecyclerView messageRecyclerView;
    MessageAdapter messageAdapter;
    List<Message> messageList;
    EditText textMessage;
    ImageView sendMessage;

    String chatroomId;
    String userId;
    String userName = "";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        chatroomId = this.getArguments().getString("chatId");
        userId = this.getArguments().getString("userId");

        mToolbar = getActivity().findViewById(R.id.toolbar);
        //set toolbar title
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(getString(R.string.user)).document(userId).get()
                .addOnCompleteListener(getUserName -> {
                    if(getUserName.isSuccessful()) {
                        User userObj = getUserName.getResult().toObject(User.class);
                        if(userObj != null) {
                            mToolbar.setTitle(userObj.getName());
                        }
                    } else {
                        mToolbar.setTitle(R.string.app_name);
                    }
                });
        // remove hamburger icon from toolbar
        mToolbar.setNavigationIcon(null);

        // hiding side navigation
        mDrawerLayout = getActivity().findViewById(R.id.main_activity_drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // hiding bottom navigation
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.GONE);

        // hiding swipe refresh
        SwipeRefreshLayout swipeRefresh = getActivity().findViewById(R.id.swipe_refresh_home);
        swipeRefresh.setEnabled(false);

        // to get the options and hide refresh menu item then
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        textMessage = view.findViewById(R.id.et_send_message);
        sendMessage = view.findViewById(R.id.iv_send_message);

        setUpRecyclerView(view);

        firebaseFirestore.collection(getString(R.string.user)).document(firebaseAuth.getCurrentUser().getUid())
                .collection(getString(R.string.chatrooms)).document(chatroomId)
                .collection(getString(R.string.messages)).orderBy("sentAt")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (!value.isEmpty()) {
                        List<DocumentChange> messagesDocumentChange =  value.getDocumentChanges();
                        for (DocumentChange messageDocumentChange: messagesDocumentChange) {
                            Message message = messageDocumentChange.getDocument().toObject(Message.class);
                            messageList.add(message);
                        }
                        messageAdapter.notifyItemInserted(messageList.size() - 1);
                        messageRecyclerView.scrollToPosition(messageRecyclerView.getAdapter().getItemCount() - 1);
                        Log.d(TAG, "Successfully listened messages");
                    } else {
                        Log.d(TAG, "message listened unsuccessful");
                    }
                });

        sendMessage.setOnClickListener(view1 -> {
            String text = textMessage.getText().toString();
            long sentAt = System.currentTimeMillis();
            String sentBy = firebaseAuth.getCurrentUser().getUid();

            if (!TextUtils.isEmpty(text)) {
                textMessage.setText("");
                firebaseFirestore.collection(getString(R.string.user)).document(firebaseAuth.getCurrentUser().getUid())
                        .collection(getString(R.string.chatrooms)).document(chatroomId)
                        .collection(getString(R.string.messages))
                        .document()
                        .set(new Message(sentBy, sentAt, text));
                firebaseFirestore.collection(getString(R.string.user)).document(userId)
                        .collection(getString(R.string.chatrooms)).document(chatroomId)
                        .collection(getString(R.string.messages))
                        .document()
                        .set(new Message(sentBy, sentAt, text));
            }
        });

    }

    private void setUpRecyclerView(View view) {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        messageRecyclerView = view.findViewById(R.id.message_recycler_view);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.scrollToPosition(messageRecyclerView.getAdapter().getItemCount() - 1);
    }

    // hide the menu item refresh
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_refresh).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}
