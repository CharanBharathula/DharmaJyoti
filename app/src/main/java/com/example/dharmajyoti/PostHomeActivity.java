package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dharmajyoti.Adapter.PostAdapter;
import com.example.dharmajyoti.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostHomeActivity extends AppCompatActivity {

    RecyclerView posts;
    List<String> postlist;
    PostAdapter adapter;
    String cname;
    TextView title;
    Map<Integer,List<String>> map=new HashMap<>();
    private List<Post> postDetails=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_home);
        posts=findViewById(R.id.recycler_view);
        posts.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences pref=this.getSharedPreferences("PREF", Context.MODE_PRIVATE);
        cname=pref.getString("cn","none");
        title=findViewById(R.id.title_text);
        title.setText(cname);
        readPosts();

    }

    private void readPosts()
    {
        postDetails=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child("Admin").child(cname);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postDetails.clear();
                Integer count=0;
                for(DataSnapshot shot:dataSnapshot.getChildren())
                {
                    for(DataSnapshot snap:shot.getChildren())
                    {
                        if(snap.getKey().equals("pics"))
                        {
                            postlist=new ArrayList<>();
                            postlist.clear();
                            for(DataSnapshot last:snap.getChildren())
                            {
                                postlist.add(last.getValue().toString());
                            }
                            map.put(count,postlist);
                            count++;
                        }
                        else if(snap.getKey().equals("postsdetails"))
                        {
                            Post post=snap.getValue(Post.class);
                            postDetails.add(post);
                        }
                    }
                }
                adapter=new PostAdapter(PostHomeActivity.this,map,postDetails);
                posts.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}