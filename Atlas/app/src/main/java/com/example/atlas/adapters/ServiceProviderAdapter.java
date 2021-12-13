package com.example.atlas.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.home.MainActivity;

import java.util.List;

public class ServiceProviderAdapter extends RecyclerView.Adapter<ServiceProviderAdapter.ContentViewHolder> {
    static final String TAG = ServiceProviderAdapter.class.getSimpleName();

    List<ServiceProvider> serviceProvider;
    boolean isStarred;

    public ServiceProviderAdapter(List<ServiceProvider> serviceProvider) {
        this.serviceProvider = serviceProvider;
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
        ImageView contact;
        TextView experience;
        TextView expectedWage;
        TextView vehicleOwned;
        TextView specialities;
        ImageView starred;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sp_tv_name);
            gender = itemView.findViewById(R.id.sp_tv_gender);
            address = itemView.findViewById(R.id.sp_tv_address);
            contact = itemView.findViewById(R.id.sp_tv_contact);
            experience = itemView.findViewById(R.id.sp_tv_experience);
            expectedWage = itemView.findViewById(R.id.sp_tv_expected_wage);
            vehicleOwned = itemView.findViewById(R.id.sp_tv_vehicle_owned);
            specialities = itemView.findViewById(R.id.tv_sp_speciality);
            starred = itemView.findViewById(R.id.ib_sp_starred);

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
        void bind(int listIndex) {
            ServiceProvider serviceProviderDetails = serviceProvider.get(listIndex);
            User userDetails = serviceProviderDetails.getUserDetails();
            name.setText(userDetails.getName());
            gender.setText(userDetails.getGender());
            address.setText(userDetails.getAddress());
            experience.setText("Exp : " + serviceProviderDetails.experience + " Years");
            expectedWage.setText(serviceProviderDetails.getExpectedWage());
            specialities.setText(serviceProviderDetails.getSpecialities().toString());
        }
    }
}
