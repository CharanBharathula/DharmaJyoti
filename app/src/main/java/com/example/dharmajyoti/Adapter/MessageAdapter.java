package com.example.dharmajyoti.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dharmajyoti.Model.Message;
import com.example.dharmajyoti.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
    final static int MSG_RIGHT=0;
    final static int MSG_LEFT=1;
    Context context;
    String url,imageurl;
    List<Message> messageDetails;

    public MessageAdapter(Context context, List<Message> messageDetails, String url, String imageurl) {
        this.context = context;
        this.messageDetails = messageDetails;
        this.url=url;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_LEFT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_left_item,parent,false);
            return new ViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_right__item,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg=messageDetails.get(position);
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        holder.msg.setText(msg.getMessage());
        if(fUser.getUid().equals(msg.getSender()))
        {
            Glide.with(context).load(url).into(holder.profile_pic);
        }
        else
        {
            Glide.with(context).load(imageurl).into(holder.profile_pic);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(fUser.getUid().equals(messageDetails.get(position).getSender()))
        {
            return MSG_RIGHT;
        }
        else
        {
            return MSG_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return messageDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profile_pic;
        TextView msg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic=itemView.findViewById(R.id.profile_image);
            msg=itemView.findViewById(R.id.msg);
        }
    }
}
