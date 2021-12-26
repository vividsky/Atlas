package com.example.atlas.home;

import android.app.Fragment;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.atlas.Models.Message;
import com.example.atlas.R;
import com.example.atlas.adapters.MessageAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private static final String TAG = MessageFragment.class.getSimpleName();
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String chatroomId;
    String userId;

    RecyclerView messageRecyclerView;
    MessageAdapter messageAdapter;
    List<Message> messageList;
    EditText textMessage;
    ImageView sendMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO hide bottom navigation and side navigation and swipe refresh and refresh
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Here chatroomId is coming from chatroom fragment)
        chatroomId = this.getArguments().getString("chatId");
        // Here userId is coming from serviceProviderAdapter/serviceReceiverAdapter(Home fragment)
        userId = this.getArguments().getString("userId");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        textMessage = view.findViewById(R.id.et_send_message);
        sendMessage = view.findViewById(R.id.iv_send_message);
        setUpRecyclerView(view);

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

                setUpRecyclerView(view);
            }
        });

    }

    private void setUpRecyclerView(View view) {

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        messageRecyclerView = view.findViewById(R.id.message_recycler_view);

        firebaseFirestore.collection(getString(R.string.user)).document(firebaseAuth.getCurrentUser().getUid())
                .collection(getString(R.string.chatrooms)).document(chatroomId)
                .collection(getString(R.string.messages)).orderBy("sentAt").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Message message;
                        for (QueryDocumentSnapshot messageQuery: task.getResult()) {
                            message = messageQuery.toObject(Message.class);
                            messageList.add(message);
                        }
                        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        messageRecyclerView.setHasFixedSize(true);
                        messageRecyclerView.setAdapter(messageAdapter);
                    } else {
                        Log.i(TAG, task.getException().getMessage());
                    }
                });
    }
}