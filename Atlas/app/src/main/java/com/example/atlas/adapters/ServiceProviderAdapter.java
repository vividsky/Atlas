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
import com.example.atlas.Models.User;
import com.example.atlas.R;

import java.util.List;

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ContentViewHolder> {
    static final String TAG = ServiceProviderAdapter.class.getSimpleName();

    List<ServiceProvider> serviceProvider;

    public ServiceProviderAdapter(List<ServiceProvider> serviceProvider) {
        this.serviceProvider = serviceProvider;
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
        return serviceProvider.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView gender;
        TextView address;
        TextView contact;
        TextView experience;
        TextView expectedWage;
        TextView vehicleOwned;


        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sp_tv_name);
            gender = itemView.findViewById(R.id.sp_tv_gender);
            address = itemView.findViewById(R.id.sp_tv_address);
            contact = itemView.findViewById(R.id.sp_tv_contact);
            experience = itemView.findViewById(R.id.sp_tv_experience);
            expectedWage = itemView.findViewById(R.id.sp_tv_expected_wage);
            vehicleOwned = itemView.findViewById(R.id.sp_tv_vehicle_owned);

        }
        void bind(int listIndex) {
            ServiceProvider serviceProviderDetails = serviceProvider.get(listIndex);
            User userDetails = serviceProviderDetails.getUserDetails();
            name.setText(userDetails.getName());
            gender.setText(userDetails.getGender());
            address.setText(userDetails.getAddress());
            contact.setText(userDetails.getContact());
            experience.setText(serviceProviderDetails.experience);
            expectedWage.setText(serviceProviderDetails.getExpectedWage());
        }
    }
}
