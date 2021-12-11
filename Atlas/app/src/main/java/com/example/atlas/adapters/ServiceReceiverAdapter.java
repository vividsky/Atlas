package com.example.atlas.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceReceiverAdapter extends RecyclerView.Adapter<ServiceReceiverAdapter.ContentViewHolder> {
    static final String TAG = ServiceReceiverAdapter.class.getSimpleName();

    List<ServiceReceiver> serviceReceiver;

    public ServiceReceiverAdapter(List<ServiceReceiver> serviceReceiver) {
        this.serviceReceiver = serviceReceiver;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
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
        TextView contact;
        TextView experience;
        TextView expectedWage;


        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sp_tv_name);
            gender = itemView.findViewById(R.id.sp_tv_gender);
            address = itemView.findViewById(R.id.sp_tv_address);
            contact = itemView.findViewById(R.id.sp_tv_contact);
            experience = itemView.findViewById(R.id.sp_tv_experience);
            expectedWage = itemView.findViewById(R.id.sp_tv_expected_wage);


        }
        void bind(int listIndex) {
            ServiceReceiver serviceReceiverDetails = serviceReceiver.get(listIndex);
            User userDetails = serviceReceiverDetails.getUserDetails();
            name.setText(userDetails.getName());
            gender.setText(userDetails.getGender());
            address.setText(userDetails.getAddress());
            experience.setVisibility(View.GONE);
            expectedWage.setVisibility(View.GONE);
        }
    }
}
