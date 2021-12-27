package com.example.atlas.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.Chatroom;
import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.home.MessageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
// TODO to implement same as service provider but chatRoom id should be taken care of

public class ServiceReceiverAdapter extends RecyclerView.Adapter<ServiceReceiverAdapter.ContentViewHolder> {
    static final String TAG = ServiceReceiverAdapter.class.getSimpleName();

    Context context;
    List<ServiceReceiver> serviceReceiver;
    boolean isStarred;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public ServiceReceiverAdapter(List<ServiceReceiver> serviceReceiver, Context context) {
        this.serviceReceiver = serviceReceiver;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_receiver, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return serviceReceiver.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView gender;
        TextView address;
        ImageView starred;
        TextView requirements;
        ImageView sendMessage;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sr_tv_name);
            gender = itemView.findViewById(R.id.sr_tv_gender);
            address = itemView.findViewById(R.id.sr_tv_address);
            starred = itemView.findViewById(R.id.sr_iv_starred);
            requirements = itemView.findViewById(R.id.sr_tv_requirement);
            sendMessage = itemView.findViewById(R.id.sr_iv_sendMessage);
        }

        void bind(int listIndex) {
            ServiceReceiver serviceReceiverDetails = serviceReceiver.get(listIndex);
            User userDetails = serviceReceiverDetails.getUserDetails();
            name.setText(userDetails.getName());
            gender.setText(userDetails.getGender());
            address.setText(userDetails.getAddress());
            requirements.setText(serviceReceiverDetails.getRequirements().toString());

            sendMessage.setOnClickListener(view -> {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();

                DocumentReference currentUserDocumentReference =
                        firebaseFirestore.collection("User").document(firebaseAuth.getCurrentUser().getUid());

                currentUserDocumentReference.get()
                        .addOnCompleteListener(task -> {
                            String currentUserId = currentUserDocumentReference.getId();
                            String serviceReceiverId = serviceReceiverDetails.getId();
                            String uniqueChatroomId = serviceReceiverId + currentUserId;
                            User user;
                            if (task.isSuccessful()) {
                                DocumentReference drChatId = firebaseFirestore.collection("User").document(firebaseAuth.getCurrentUser().getUid())
                                        .collection("Chatroom").document(uniqueChatroomId);
                                drChatId.set(new Chatroom(userDetails.getName(), drChatId.getId(), serviceReceiverId));

                                user = task.getResult().toObject(User.class);
                                firebaseFirestore.collection("User").document(serviceReceiverId)
                                        .collection("Chatroom").document(drChatId.getId())
                                        .set(new Chatroom(user.getName(), drChatId.getId(), currentUserId));


                                Bundle bundle = new Bundle();
                                bundle.putString("userId", serviceReceiverId);
                                bundle.putString("chatId", drChatId.getId());

                                MessageFragment messageFragment = new MessageFragment();
                                messageFragment.setArguments(bundle);

                                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                                manager.beginTransaction()
                                        .replace(R.id.main_activity_container, messageFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
            });

            starred.setOnClickListener(view -> {
                if (!isStarred) {
                    starred.setImageResource(R.drawable.ic_starred);
                    isStarred = true;
                } else {
                    starred.setImageResource(R.drawable.ic_unstarred);
                    isStarred = false;
                }
            });
        }
    }
}
