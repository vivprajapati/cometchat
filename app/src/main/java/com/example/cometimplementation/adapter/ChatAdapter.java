package com.example.cometimplementation.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cometimplementation.AppConfig;
import com.example.cometimplementation.R;
import com.example.cometimplementation.models.ChatMessageModel;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.myViewHolder> {
    Context context;
    List<ChatMessageModel> chatMessageModels;

    public ChatAdapter(Context context, List<ChatMessageModel> chatMessageModels) {
        this.context = context;
        this.chatMessageModels = chatMessageModels;
    }

    @Override
    public int getItemViewType(int position) {
//        return position % 2;
        if (chatMessageModels.get(position).getUid().equals(AppConfig.UID))
            return 1;
        else
            return 0;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_reciver_layout, null, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sender_layout, null, false);
        }
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
//        holder.message.setGravity(Gravity.START);
        holder.message.setText(chatMessageModels.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
        }
    }
}
