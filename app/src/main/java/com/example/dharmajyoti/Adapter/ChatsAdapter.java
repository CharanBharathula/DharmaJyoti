package com.example.dharmajyoti.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dharmajyoti.ChatsActivity;
import com.example.dharmajyoti.MessageActivity;
import com.example.dharmajyoti.Model.Customer;
import com.example.dharmajyoti.Model.User;
import com.example.dharmajyoti.R;

import java.util.List;
import java.util.Map;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>
{
    Map<String,List<String>> map;
    Context context;
    List<String> keys;
    List<User> users;
    public ChatsAdapter(List<User> users, List<String> keyList, Context context) {
        this.users=users;
        this.context = context;
        keys=keyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final User u=users.get(position);
        holder.username.setText(u.getUsername());
        Glide.with(context).load(u.getImageurl()).into(holder.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("name",u.getUsername());
                editor.putString("userid",keys.get(position));
                editor.putString("mobile",u.getmobile());
                editor.putString("imageurl",u.getImageurl());
                editor.apply();
                context.startActivity(new Intent(context, MessageActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        ImageView profile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_name);
            profile=itemView.findViewById(R.id.profile_image);
        }
    }
}
