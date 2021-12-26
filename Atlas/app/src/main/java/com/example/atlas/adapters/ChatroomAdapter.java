package com.example.atlas.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.Chatroom;
import com.example.atlas.R;
import com.example.atlas.home.MainActivity;
import com.example.atlas.home.MessageFragment;

import java.util.List;

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ContentViewHolder> {

    List<Chatroom> chatroomList;
    Context context;

    public ChatroomAdapter(List<Chatroom> chatroomList, Context context) {
        this.chatroomList = chatroomList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_chathead, parent, false);
        return new ChatroomAdapter.ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return chatroomList.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_chat_head_name);
        }

        void bind(int index) {
            Chatroom chatroom = chatroomList.get(index);
            name.setText(chatroom.getName());

            name.setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putString("chatId", chatroom.getChatId());
                bundle.putString("userId", chatroom.getUserId());
                MessageFragment messageFragment = new MessageFragment();
                messageFragment.setArguments(bundle);

                Activity activity = (MainActivity) context;
                activity.getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_container, messageFragment)
                        .addToBackStack(null)
                        .commit();
                    });
        }
    }
}
