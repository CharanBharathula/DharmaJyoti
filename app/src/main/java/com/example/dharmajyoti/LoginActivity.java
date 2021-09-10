package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button login,verify;
    FirebaseAuth auth;
    TextView signup,forget,login_phone;
    private String verificationCode;
    String phoneNumber,otp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intialize();
        verifyOtp();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login.getText().equals("SendOTP"))
                {
                    if(email.getText().toString().trim().equals("") )
                        Toast.makeText(LoginActivity.this, "Enter mobile please ", Toast.LENGTH_SHORT).show();
                    else
                    {
                        SendOtp();
                    }
                }
                else
                {
                    if(email.getText().toString().trim().equals("") )
                        Toast.makeText(LoginActivity.this, "Enter email please ", Toast.LENGTH_SHORT).show();
                    else if(password.getText().toString().trim().equals(""))
                        Toast.makeText(LoginActivity.this, "Enter password please ", Toast.LENGTH_SHORT).show();
                    else
                        loginUser(email.getText().toString().trim(),password.getText().toString().trim());
                }
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=getLayoutInflater().inflate(R.layout.forget_password,null);

                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                builder.setView(view);
                builder.setTitle("Password Reset");
                final AlertDialog alert=builder.create();
                alert.show();
                final EditText email=view.findViewById(R.id.email);
                Button submit=view.findViewById(R.id.submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(email.getText().toString().equals(""))
                        {
                            Toast.makeText(LoginActivity.this, "Enter Email please", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FirebaseAuth fAuth=FirebaseAuth.getInstance();
                            fAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(LoginActivity.this, "Please Check your email for password reset link", Toast.LENGTH_SHORT).show();
                                        alert.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        alert.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Registration.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        login_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setHint("Enter Mobile Number");
                password.setHint("Enter OTP");
                login.setText("SendOTP");
                verify.setVisibility(View.VISIBLE);
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=password.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                SigninWithPhone(credential);

            }
        });
    }

    private void SigninWithPhone(PhoneAuthCredential credential)
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            if(auth.getCurrentUser().getUid().equals("tCavLXduTFPclGge9wrkJRa6NUq2"))
                            {
                                pd.dismiss();
                                verify.setVisibility(View.GONE);
                                email.setHint("Enter your email");
                                password.setHint("Enter your passord");
                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(LoginActivity.this, "id="+FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "id="+FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verifyOtp()
    {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this,"verification fialed "+e,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(LoginActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void SendOtp()
    {
        phoneNumber=email.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                LoginActivity.this,        // Activity (for callback binding)
                mCallback);                      // OnVerificationStateChangedCallbacks
    }


    private void loginUser(final String email, final String password)
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                pd.dismiss();
                                if(auth.getCurrentUser().getUid().equals("tCavLXduTFPclGge9wrkJRa6NUq2"))
                                {
                                    Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
                    Toast.makeText(LoginActivity.this, "Login Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }
    private void Intialize()
    {
        email=findViewById(R.id.email);
        password=findViewById(R.id.pwd);
        login=findViewById(R.id.login);
        signup=findViewById(R.id.txt_signup);
        forget=findViewById(R.id.forgt_pwd);
        auth=FirebaseAuth.getInstance();
        verify=findViewById(R.id.verify_otp);
        login_phone=findViewById(R.id.login_with_phone);
    }

}
