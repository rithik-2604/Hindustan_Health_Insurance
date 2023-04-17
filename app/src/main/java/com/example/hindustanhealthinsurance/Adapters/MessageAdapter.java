package com.example.hindustanhealthinsurance.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hindustanhealthinsurance.Models.AllMethods;
import com.example.hindustanhealthinsurance.Models.Message;
import com.example.hindustanhealthinsurance.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {

    Context context;
    List<Message> messageList;
    DatabaseReference messageDb;

    public MessageAdapter(Context context, List<Message> messageList, DatabaseReference messageDb) {
        this.context = context;
        this.messageList = messageList;
        this.messageDb = messageDb;
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getName().equals(AllMethods.name)) {
            holder.tvTitle.setText("You: " + message.getMessage());
            holder.tvTitle.setGravity(Gravity.START);
        }
        else {
            holder.tvTitle.setText(message.getName() + ": " + message.getMessage());
            holder.ibDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageButton ibDelete;
        LinearLayout ll;

        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ibDelete = itemView.findViewById(R.id.ibDelete);
            ll = itemView.findViewById(R.id.message_1);

            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    messageDb.child(messageList.get(getAdapterPosition()).getKey()).removeValue();
                }
            });
        }
    }
}