package com.example.atlas.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.home.MainActivity;
import com.example.atlas.home.MessageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ContentViewHolder> {
    static final String TAG = ServiceProviderAdapter.class.getSimpleName();

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    List<ServiceProvider> serviceProvider;
    boolean isStarred;
    Context context;

    public ServiceProviderAdapter(List<ServiceProvider> serviceProvider, Context context) {
        this.serviceProvider = serviceProvider;
        this.context = context;
    }


    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.service_provider_item, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return serviceProvider.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView gender;
        TextView address;
        TextView experience;
        TextView expectedWage;
        TextView vehicleOwned;
        TextView speciality;
        ImageView starred;
        ImageView sendMessage;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sp_tv_name);
            gender = itemView.findViewById(R.id.sp_tv_gender);
            address = itemView.findViewById(R.id.sp_tv_address);
            experience = itemView.findViewById(R.id.sp_tv_experience);
            expectedWage = itemView.findViewById(R.id.sp_tv_expected_wage);
            vehicleOwned = itemView.findViewById(R.id.sp_tv_vehicle_owned);
            speciality = itemView.findViewById(R.id.tv_sp_speciality);
            starred = itemView.findViewById(R.id.sp_iv_starred);
            sendMessage = itemView.findViewById(R.id.sp_iv_sendMessage);

        }
        void bind(int listIndex) {
            ServiceProvider serviceProviderDetails = serviceProvider.get(listIndex);
            User userDetails = serviceProviderDetails.getUserDetails();
            name.setText(userDetails.getName());
            gender.setText(userDetails.getGender());
            address.setText(userDetails.getAddress());
            experience.setText("Exp : " + serviceProviderDetails.experience + " Years");
            expectedWage.setText(serviceProviderDetails.getExpectedWage());
            speciality.setText(serviceProviderDetails.getSpeciality());

            sendMessage.setOnClickListener(view -> {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();

                DocumentReference currentUserDocumentReference =
                        firebaseFirestore.collection("User").document(firebaseAuth.getCurrentUser().getUid());

                currentUserDocumentReference.get()
                .addOnCompleteListener(task -> {
                    String currentUserId = currentUserDocumentReference.getId();
                    String serviceProviderId = serviceProviderDetails.getId();
                    String uniqueChatroomId = currentUserId + serviceProviderId;
                    User user;
                    if (task.isSuccessful()) {
                        DocumentReference drChatId = firebaseFirestore.collection("User").document(firebaseAuth.getCurrentUser().getUid())
                                .collection("Chatroom").document(uniqueChatroomId);
                        drChatId.set(new Chatroom(userDetails.getName(), drChatId.getId(), serviceProviderId));

                        user = task.getResult().toObject(User.class);
                        firebaseFirestore.collection("User").document(serviceProviderId)
                                .collection("Chatroom").document(drChatId.getId())
                                .set(new Chatroom(user.getName(), drChatId.getId(), currentUserId));


                        Bundle bundle = new Bundle();
                        bundle.putString("userId", serviceProviderId);
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
