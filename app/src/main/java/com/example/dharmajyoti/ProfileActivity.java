package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dharmajyoti.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView close,save;
    ImageView profilepic;
    EditText name,email,password,mobile;
    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        close=findViewById(R.id.close);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        mobile=findViewById(R.id.mobile);
        save=findViewById(R.id.saveprofile);
        profilepic=findViewById(R.id.profle_pic);
        storageRef= FirebaseStorage.getInstance().getReference("uploads");

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(profilepic);
                    name.setText(user.getUsername());
                    email.setText(user.getEmail());
                    mobile.setText(user.getmobile());
                    password.setText(user.getPassword());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfileActivity.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri=result.getUri();
            uploadImage();
        }

    }
    private void updateProfile()
    {
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("username",name);
            hashMap.put("mobile",mobile);ref.updateChildren(hashMap);
            Toast.makeText(this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mt=MimeTypeMap.getSingleton();
        return mt.getExtensionFromMimeType(cr.getType(uri));
    }
    private void uploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.setCancelable(true);
        pd.show();
        if(mImageUri!=null)
        {
            final StorageReference fRef=storageRef.child(
                    System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            uploadTask=fRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloaduri=task.getResult();
                        String imagerl=downloaduri.toString();
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("imageurl",""+imagerl);
                            ref.updateChildren(hashMap);
                            pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(mContext, "Failed to update image", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(mContext, "No image is Selected !!!", Toast.LENGTH_SHORT).show();
        }
    }
}