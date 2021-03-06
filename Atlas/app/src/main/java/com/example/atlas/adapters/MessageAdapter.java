package com.example.atlas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atlas.Models.Message;
import com.example.atlas.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    static final int SELF_MESSAGE_TEXT = 0;
    static final int OTHER_MESSAGE_TEXT = 1;
    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SELF_MESSAGE_TEXT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_self, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_other, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemViewType(int position) {

        if (messageList.get(position).getSentBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return SELF_MESSAGE_TEXT;
        } else {
            return OTHER_MESSAGE_TEXT;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView time;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.tv_message_text);
            time = itemView.findViewById(R.id.tv_message_time);
        }

        void bind(int listIndex) {
            Message message = messageList.get(listIndex);
            text.setText(message.getText());
            time.setText(millisToDate(message.getSentAt()));

        }
    }
    private String millisToDate(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date resultdate = new Date(milliseconds);
        return sdf.format(resultdate);
    }
}
