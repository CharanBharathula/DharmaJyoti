package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dharmajyoti.Adapter.ChatsAdapter;
import com.example.dharmajyoti.Model.Customer;
import com.example.dharmajyoti.Model.Message;
import com.example.dharmajyoti.Model.User;
import com.example.dharmajyoti.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatsActivity extends AppCompatActivity {
    List<User> userslist=new ArrayList<>();
    RecyclerView users;
    ChatsAdapter adapter;
    List<String> list;
    List<String> keys=new ArrayList<>();
    Set<String> keyList;
    TextView header;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        users = findViewById(R.id.recyclerview_users);
        header=findViewById(R.id.chat_header);
        readUsers();
        users.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatsAdapter(userslist, keys, ChatsActivity.this);
        users.setAdapter(adapter);
    }
    private void readUsers()
    {
        list=new ArrayList<>();
        keyList=new HashSet<>();
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser.getUid().equals("tCavLXduTFPclGge9wrkJRa6NUq2"))
        {
            header.setText("Chats");
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Messages");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    keyList.clear();
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        for(DataSnapshot shot:snap.getChildren())
                        {
                            if(shot.getKey().equals("reciever") && shot.getValue().equals("tCavLXduTFPclGge9wrkJRa6NUq2"))
                            {
                                Message msg=snap.getValue(Message.class);
                                list.add(msg.getSender());
                                keyList.add(msg.getSender());
                            }
                            if(shot.getKey().equals("sender") && shot.getValue().equals("tCavLXduTFPclGge9wrkJRa6NUq2"))
                            {
                                Message msg=snap.getValue(Message.class);
                                list.add(msg.getReciever());
                                keyList.add(msg.getReciever());
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userslist.clear();
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        if(list.get(i).equals(snap.getKey()))
                        {
                            User user=snap.getValue(User.class);
                            userslist.add(user);
                            i++;
                        }
                    }
                    keys.addAll(keyList);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ChatsActivity.this, "size of keys="+ keys.size(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(ChatsActivity.this, "size of users="+userslist.size(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            header.setText("Contact Admin");
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child("tCavLXduTFPclGge9wrkJRa6NUq2");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user= dataSnapshot.getValue(User.class);
                    userslist.add(user);
                    keys.add("tCavLXduTFPclGge9wrkJRa6NUq2");
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        /*final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    keyList.clear();
                    String key = "";
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        list.clear();
                        key=snap.getKey();
                        if(!key.equals(fUser.getUid()))
                        {
                            User user=snap.getValue(User.class);
                            userslist.add(user);
                            keyList.add(key);

                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
    }
}