package com.example.dharmajyoti.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>
{

    private static final int ONE = 0;
    private static final int TWO = 1;
    Context context;
    List<Post>postDetails;
    AlertDialog.Builder ad ;
    Map<Integer,List<String>> map;
    public PostAdapter(Context context, Map<Integer,List<String>> map, List<Post> postDetails) {
        this.context=context;
        this.map=map;
        this.postDetails=postDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.parent_recyclerview_item,parent,false);
        ad = new AlertDialog.Builder(context);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        RecyclerView.LayoutManager lm=new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false);
        holder.posts.setLayoutManager(lm);
        PicsAdapter adapter=new PicsAdapter(context,map.get(position));
        holder.posts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        final Post post=postDetails.get(position);
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
        return postDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        RecyclerView posts;
        public ImageView like,comment;
        TextView likes,description;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            likes=itemView.findViewById(R.id.likes);
            description=itemView.findViewById(R.id.description);
            posts=itemView.findViewById(R.id.recycle_view_posts);
            layout=itemView.findViewById(R.id.relative);
        }
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
}
