package com.example.atlas.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.Chatroom;
import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.home.MessageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ContentViewHolder> {
    static final String TAG = ServiceProviderAdapter.class.getSimpleName();

    List<ServiceProvider> serviceProvider;
    Context context;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public ServiceProviderAdapter(List<ServiceProvider> serviceProvider, Context context) {
        this.serviceProvider = serviceProvider;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_provider, parent, false);
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
        ImageView shareSPDetails;

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
            shareSPDetails = itemView.findViewById(R.id.sp_iv_share);
        }

        void bind(int listIndex) {
            ServiceProvider serviceProviderDetails = serviceProvider.get(listIndex);
            User userDetails = serviceProviderDetails.getUserDetails();
            name.setText(userDetails.getName());
            gender.setText(userDetails.getGender());
            address.setText(userDetails.getAddress());
            experience.setText("Exp : " + serviceProviderDetails.getExperience() + " Years");
            expectedWage.setText(serviceProviderDetails.getExpectedWage() + "");
            speciality.setText(serviceProviderDetails.getSpeciality());

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();

            sendMessage.setOnClickListener(view -> {

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

            String serviceProviderId = serviceProviderDetails.getId();
            firebaseFirestore.collection("User")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        User user;
                        if (task.isSuccessful()) {
                            user = task.getResult().toObject(User.class);
                            ArrayList<String> starredSet = user.getStarredUsers();
                            if (starredSet.contains(serviceProviderId)) {
                                starred.setImageResource(R.drawable.ic_starred);
                            } else {
                                starred.setImageResource(R.drawable.ic_unstarred);
                            }
                        }
                    });

            starred.setOnClickListener(view -> {
                DocumentReference currentUserDR = firebaseFirestore.collection("User")
                        .document(firebaseAuth.getCurrentUser().getUid());
                currentUserDR
                        .get()
                        .addOnCompleteListener(task -> {
                            User user;
                            if (task.isSuccessful()) {
                                user = task.getResult().toObject(User.class);
                                ArrayList<String> starredUsers = user.getStarredUsers();
                                if (starredUsers.contains(serviceProviderId)) {
                                    starred.setImageResource(R.drawable.ic_unstarred);
                                    starredUsers.remove(serviceProviderId);
                                    Toast.makeText(context, "User unstarred successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    starred.setImageResource(R.drawable.ic_starred);
                                    starredUsers.add(serviceProviderId);
                                    Toast.makeText(context, "User starred successfully.", Toast.LENGTH_SHORT).show();
                                }
                                currentUserDR.update("starredUsers", starredUsers);
                            }
                        });
            });

            shareSPDetails.setOnClickListener(view -> {
                Toast.makeText(context, "yet to be implemented in future.", Toast.LENGTH_LONG).show();
            });
        }
    }
}
