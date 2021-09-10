package com.example.dharmajyoti.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dharmajyoti.R;

import java.util.List;

public class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.ViewHolder>
{
    Context context;
    List<String> pics;
    public PicsAdapter(Context context, List<String> pics)
    {
        this.context=context;
        this.pics=pics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.postitem,parent,false);
        return new PicsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.top.setVisibility(View.GONE);
        holder.middle.setVisibility(View.GONE);
        Glide.with(context).load(pics.get(position)).apply(new RequestOptions().placeholder(R.drawable.noimage)).into(holder.post_image);
    }

    @Override
    public int getItemCount() {
        return pics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView post_image;
        RelativeLayout top,middle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image=itemView.findViewById(R.id.post_image);
            top=itemView.findViewById(R.id.relative_top);
            middle=itemView.findViewById(R.id.relative);
        }
    }
}
