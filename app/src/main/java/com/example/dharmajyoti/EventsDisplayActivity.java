package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dharmajyoti.Adapter.HomeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsDisplayActivity extends AppCompatActivity  {

    RecyclerView recyclerView;
    List<String> postList;
    List<String>eventsList;
    HomeAdapter adapter;
    ArrayAdapter<String> array_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_display);

        recyclerView=findViewById(R.id.recyclerview_home);

        postList=new ArrayList<>();
        eventsList=new ArrayList<>();
        readEvents();
        /*array_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,R.id.eventname,postList);
        recyclerView.setAdapter(array_adapter);*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void readEvents()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Events");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                postList.clear();
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    eventsList.add(snap.getKey());
                    for(DataSnapshot shot:snap.getChildren())
                    {
                        if (shot.getKey().equals("pics"))
                        {
                            for(DataSnapshot snapShot:shot.getChildren())
                            {
                                postList.add(snapShot.getValue().toString());
                            }
                        }
                    }
                }
                adapter=new HomeAdapter(EventsDisplayActivity.this,postList,eventsList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}