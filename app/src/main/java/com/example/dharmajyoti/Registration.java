package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dharmajyoti.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity {

    EditText username,email,password,mobile,fullname,cpwd;
    Button signup;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Initialize();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String values[]={
                        username.getText().toString(),
                        email.getText().toString().trim(),
                        mobile.getText().toString(),
                        password.getText().toString(),
                        cpwd.getText().toString()};
                saveData(values[0],values[1],values[2],values[3],values[4]);
            }
        });
    }

    private void Initialize()
    {
        username=findViewById(R.id.name);

        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        password=findViewById(R.id.pwd);
        cpwd=findViewById(R.id.cpwd);
        signup=findViewById(R.id.signup);
        auth=FirebaseAuth.getInstance();
    }
    private void saveData(final String username, final String email, final String mobile, final String password, String cpwd) {
        if (username.equals(""))
            this.username.setError("Enter username please");
        else if (email.equals(""))
            this.email.setError("Enter email please");
        else if (password.equals(""))
            this.password.setError("Enter password please");
        else if (mobile.equals(""))
            this.mobile.setError("Enter mobile number please");
        else if (!password.equals(cpwd))
            this.cpwd.setError("Passwords donot match");
        else if (mobile.length() < 10)
        {
            this.mobile.setError("Enter valid mobile number");
        }
        else
        {
            progressDialog=new ProgressDialog(Registration.this);
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Signing Up please wait a while :)");
            progressDialog.setCancelable(false);
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(Registration.this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                            FirebaseUser cuser=auth.getCurrentUser();
                            String userid=cuser.getUid();
                            ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String,Object> userDetails=new HashMap<>();
                            userDetails.put("userid",userid);
                            userDetails.put("username",username);
                            userDetails.put("email",email);
                            userDetails.put("password",password);
                            userDetails.put("mobile",mobile);
                            userDetails.put("imageurl","https://firebasestorage.googleapis.com/v0/b/dharmajyoti-8dc9a.appspot.com/o/pro.png?alt=media&token=1b18c92e-b4ab-4522-b6e4-7bb13a298eae");

                            ref.setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        if (email.equals("dharmajyothiseva@gmail.com"))
                                        {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(Registration.this, AdminHomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(Registration.this, UserHomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });

                    }
                    else
                    {
                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
                        Toast.makeText(Registration.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }
}
