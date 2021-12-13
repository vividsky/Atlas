package com.example.atlas.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.R;

import java.util.List;

public class ServiceReceiverAdapter extends RecyclerView.Adapter<ServiceReceiverAdapter.ContentViewHolder> {
    static final String TAG = ServiceReceiverAdapter.class.getSimpleName();

    List<ServiceReceiver> serviceReceiver;
    boolean isStarred;

    public ServiceReceiverAdapter(List<ServiceReceiver> serviceReceiver) {
        this.serviceReceiver = serviceReceiver;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.service_receiver_item, parent, false);
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
        ImageView contact;
        ImageView starred;
        TextView requirements;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sr_tv_name);
            gender = itemView.findViewById(R.id.sr_tv_gender);
            address = itemView.findViewById(R.id.sr_tv_address);
            contact = itemView.findViewById(R.id.sr_iv_contact);
            starred = itemView.findViewById(R.id.sr_iv_starred);
            requirements = itemView.findViewById(R.id.sr_tv_requirement);
        }
        void bind(int listIndex) {
            ServiceReceiver serviceReceiverDetails = serviceReceiver.get(listIndex);
            User userDetails = serviceReceiverDetails.getUserDetails();
            name.setText(userDetails.getName());
            gender.setText(userDetails.getGender());
            address.setText(userDetails.getAddress());
            requirements.setText(serviceReceiverDetails.getRequirements().toString());
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
