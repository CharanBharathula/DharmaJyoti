package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dharmajyoti.Adapter.EventsAdapter;
import com.example.dharmajyoti.Adapter.PostAdapter;
import com.example.dharmajyoti.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostEventsHomeActivity extends AppCompatActivity {

    RecyclerView posts;
    List<String> postlist;
    EventsAdapter adapter;
    String ename;
    private List<Post> postDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_events_home);

        posts=findViewById(R.id.recycler_view_events);
        posts.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences pref=this.getSharedPreferences("PREF", Context.MODE_PRIVATE);
        ename=pref.getString("en","none");
        readPosts();
    }

    private void readPosts()
    {
        postlist=new ArrayList<>();
        postDetails=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Events").child(ename);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postDetails.clear();
                postlist.clear();
                for(DataSnapshot shot:dataSnapshot.getChildren())
                {
                    for(DataSnapshot snap:shot.getChildren())
                    {
                        if(snap.getKey().equals("pics"))
                        {
                            for(DataSnapshot last:snap.getChildren())
                            {
                                postlist.add(last.getValue().toString());
                            }
                        }
                        else if(snap.getKey().equals("postdetails"))
                        {
                            Post post=snap.getValue(Post.class);
                            postDetails.add(post);
                        }
                    }
                }

                adapter=new EventsAdapter(PostEventsHomeActivity.this,postlist,postDetails);
                posts.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}