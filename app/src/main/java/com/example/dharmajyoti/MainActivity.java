package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dharmajyoti.Model.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button register,login;
    FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(fUser!=null)
        {
            if(fUser.getUid().equals("tCavLXduTFPclGge9wrkJRa6NUq2"))
            {
                startActivity(new Intent(MainActivity.this,AdminHomeActivity.class));
                finish();
            }
            else
            {
                startActivity(new Intent(MainActivity.this,UserHomeActivity.class));
                finish();
            }
        }
        Initialize();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Registration.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }

    private void Initialize()
    {
        register=findViewById(R.id.register);
        login=findViewById(R.id.login);
    }
}
