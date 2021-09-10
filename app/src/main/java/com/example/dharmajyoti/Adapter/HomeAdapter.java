package com.example.dharmajyoti.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.dharmajyoti.MainActivity;
import com.example.dharmajyoti.Model.Post;
import com.example.dharmajyoti.PostEvents;
import com.example.dharmajyoti.PostEventsHomeActivity;
import com.example.dharmajyoti.PostHomeActivity;
import com.example.dharmajyoti.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>
{
    List<String>postList;
    List<String>eventsList;
    Context context;
    AlertDialog.Builder ad ;
    public HomeAdapter(Context context, List<String> postList, List<String> eventsList) {
        this.postList = postList;
        this.eventsList = eventsList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        ad = new AlertDialog.Builder(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //String eventName=eventsList.get(position);

        holder.eventname.setText(eventsList.get(position));

        /*final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("icon");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(context).load(dataSnapshot.getValue()).apply(new RequestOptions().placeholder(R.drawable.noimage)).into(holder.image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        holder.eventname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor=context.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit();
                editor.putString("en",eventsList.get(position));
                editor.apply();
                context.startActivity(new Intent(context, PostEventsHomeActivity.class));
            }
        });
        holder.eventname.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ad.setCancelable(false);
                ad.setMessage("Do you want to remove this events ??");
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Events").child(eventsList.get(position));
                        String temp=eventsList.get(position);
                        ref.removeValue();
                        Toast.makeText(context, temp+" Removed Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert=ad.create();
                alert.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView eventname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //image=itemView.findViewById(R.id.image);
            eventname=itemView.findViewById(R.id.imagename);
        }
    }
}
