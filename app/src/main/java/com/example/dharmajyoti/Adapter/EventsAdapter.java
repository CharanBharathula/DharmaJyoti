package com.example.dharmajyoti.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dharmajyoti.CommentActivity;
import com.example.dharmajyoti.Model.Post;
import com.example.dharmajyoti.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder>
{
    Context context;
    List<String> postList;
    List<Post> postDetails;

    public EventsAdapter(Context context, List<String> postList, List<Post> postDetails) {
        this.postList = postList;
        this.postDetails = postDetails;
        this.context=context;
    }
    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.postitem,parent,false);
        return new EventsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsAdapter.ViewHolder holder, int position) {
        final Post post=postDetails.get(0);
        Glide.with(context).load(postList.get(position)).apply(new RequestOptions().placeholder(R.drawable.noimage)).into(holder.post_image);
        if(post.getDescription().equals(""))
        {
            holder.description.setVisibility(View.GONE);
        }
        else
        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        isLikes(post.getPostid(),holder.like);
        nolikes(holder.likes,post.getPostid());
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, CommentActivity.class);
                i.putExtra("postid",post.getPostid());
                i.putExtra("publisher",post.getPublisher());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void isLikes(String postid, final ImageView image)
    {
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists())
                {
                    image.setImageResource(R.drawable.ic_like_blue);
                    image.setTag("Liked");
                }
                else
                {
                    image.setImageResource(R.drawable.ic_like);
                    image.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void nolikes(final TextView likes, String postid)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView like,comment,post_image;
        TextView likes,description;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            likes=itemView.findViewById(R.id.likes);
            description=itemView.findViewById(R.id.description);
            post_image=itemView.findViewById(R.id.post_image);
            layout=itemView.findViewById(R.id.relative);
        }
    }
}
