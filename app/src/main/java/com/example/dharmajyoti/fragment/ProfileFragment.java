package com.example.dharmajyoti.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dharmajyoti.MainActivity;
import com.example.dharmajyoti.Model.User;
import com.example.dharmajyoti.ProfileActivity;
import com.example.dharmajyoti.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView logout,editProfile;
    ImageView profilepic;
    EditText name,email,password,mobile,id;
    Map<String, String> map=new HashMap<>();
    ProgressDialog pd;
    String uid;
    AlertDialog.Builder ad ;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        ad = new AlertDialog.Builder(getContext());

        logout=view.findViewById(R.id.logout);
        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        mobile=view.findViewById(R.id.mobile);
        editProfile=view.findViewById(R.id.edit_profile);
        profilepic=view.findViewById(R.id.profle_pic);
        id=view.findViewById(R.id.id);
        SharedPreferences pref=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        uid=pref.getString("profileid","none");
        String userid;
        if(uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        else
        {
            id.setVisibility(View.GONE);
            userid=uid;
        }
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(userid);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pd=new ProgressDialog(getContext());
                    pd.setCancelable(false);
                    pd.setTitle("Loading Profile");
                    pd.setMessage("Please wait while loading profile");
                    pd.show();
                    for (DataSnapshot shot:dataSnapshot.getChildren())
                    {
                        map.put(shot.getKey(),shot.getValue().toString());
                    }
                    if(getContext()==null)
                    {
                        return;
                    }
                    Glide.with(getContext()).load(map.get("imageurl")).into(profilepic);
                    name.setText(map.get("username"));
                    email.setText(map.get("email"));
                    mobile.setText(map.get("mobile"));
                    password.setText(map.get("password"));
                    id.setText(map.get("userid"));
                    pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ProfileActivity.class));
            }
        });
        logout=view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.setCancelable(false);
                ad.setMessage("Do you want to Logout ??");
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(),MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        dialog.dismiss();
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert=ad.create();
                alert.show();

            }
        });
        return view;
    }



}
