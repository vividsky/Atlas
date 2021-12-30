package com.example.atlas.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.Chatroom;
import com.example.atlas.R;
import com.example.atlas.Utils;
import com.example.atlas.authentication.AuthenticationActivity;
import com.example.atlas.home.ChatRoomFragment;
import com.example.atlas.home.MainActivity;
import com.example.atlas.home.MessageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.main_activity_container, messageFragment)
                        .addToBackStack(null)
                        .commit();
            });

            name.setOnLongClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to Delete this chat?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Continue with delete operation
                            DocumentReference chatroomDR = Utils.getCurrentUserDocumentReference()
                                    .collection(context.getString(R.string.chatrooms))
                                    .document(chatroom.getChatId());
                            chatroomDR.collection(context.getString(R.string.messages))
                                    .get().addOnCompleteListener(deleteMessages -> {
                                        if(deleteMessages.isSuccessful()) {
                                            for (QueryDocumentSnapshot message : deleteMessages.getResult()) {
                                                message.getReference().delete();
                                            }
                                            chatroomDR.delete().addOnCompleteListener(deleteChatHead -> {
                                                if(deleteChatHead.isSuccessful()) {
                                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();

                                                    FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                                                    manager.beginTransaction()
                                                            .replace(R.id.main_activity_container, new ChatRoomFragment())
                                                            .commit();
                                                } else {
                                                    Toast.makeText(context, "Sorry cannot delete this chat.Please try again.", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            Log.w("DeleteMessage", deleteMessages.getException().getMessage());
                                        }
                            });
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .show();
                return true;
            });

        }
    }
}
