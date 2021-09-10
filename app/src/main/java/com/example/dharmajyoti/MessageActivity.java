package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dharmajyoti.Adapter.MessageAdapter;
import com.example.dharmajyoti.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    ImageView send_message,profile_pic,imageView;
    EditText text;
    String url,imageurl;

    RecyclerView messages;
    List<Message> msgList;
    MessageAdapter adapter;

    TextView name,mobile;
    String username,userid,usermobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        name=findViewById(R.id.uname);
        mobile=findViewById(R.id.umob);
        profile_pic=findViewById(R.id.profile_image);
        imageView=findViewById(R.id.image_profile);
        text=findViewById(R.id.text);
        send_message=findViewById(R.id.send_msg);
        messages=findViewById(R.id.recyclerview_messages);

        LinearLayoutManager lm=new LinearLayoutManager(this);
        messages.setLayoutManager(new LinearLayoutManager(this));
        lm.setStackFromEnd(true);
        messages.setLayoutManager(lm);
        readMessage();

        SharedPreferences pref=this.getSharedPreferences("PREF", Context.MODE_PRIVATE);
        userid=pref.getString("userid","none");
        username=pref.getString("name","none");
        usermobile=pref.getString("mobile","none");
        imageurl=pref.getString("imageurl","none");

        name.setText(username);
        mobile.setText(usermobile);
        Glide.with(getApplicationContext()).load(imageurl).into(profile_pic);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageurl");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(getApplicationContext()).load(dataSnapshot.getValue()).into(imageView);
                url=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getText().toString().equals(""))
                {
                    Toast.makeText(MessageActivity.this, "Please enter message", Toast.LENGTH_SHORT).show();
                    text.setText("");
                }
                else
                {
                    sendMessage();
                    text.setText("");
                }
            }
        });
    }

    private void readMessage()
    {
        msgList=new ArrayList<>();
        final FirebaseUser fUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Messages");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msgList.clear();
                for(DataSnapshot shot:dataSnapshot.getChildren())
                {
                    Message msg=shot.getValue(Message.class);
                    if(msg.getReciever().equals(fUser.getUid()) && msg.getSender().equals(userid) ||
                            msg.getReciever().equals(userid) && msg.getSender().equals(fUser.getUid()))
                    {
                        msgList.add(msg);
                    }
                }
                adapter=new MessageAdapter(MessageActivity.this,msgList,url,imageurl);
                messages.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void sendMessage()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Messages");
        HashMap<String,Object> map=new HashMap<>();
        map.put("sender",FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("reciever",userid);
        map.put("message",text.getText().toString());
        ref.push().setValue(map);
    }
}