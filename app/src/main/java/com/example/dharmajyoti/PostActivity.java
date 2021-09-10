package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dharmajyoti.Adapter.UploadAdapter;
import com.example.dharmajyoti.Model.Post;
import com.example.dharmajyoti.fragment.HomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int RESULT_IMAGE_LOAD = 1;
    List<String> fileNames;
    List<String> filesDone;
    RecyclerView photosList;
    TextView choose,post,close;
    UploadAdapter adapter;
    EditText eventname,description;
    public StorageReference mStoorageRef;
    Uri uri;
    public String fileName;
    public StorageTask uploadtask;
    public List<Uri>fileUri=new ArrayList<>();
    String postid;
    public String myurl;
    HashMap<String,Object> hashMap;
    String category;

    Spinner catogeries;
    String catogeries_arry[]={"choose category","సామాజిక సేవ","మాతృ మండలి","బాల కుటీర","ధర్మ ప్రచారం","దేవాలయ పరిరక్షణ"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Initialize();
        catogeries.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,catogeries_arry);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catogeries.setAdapter(aa);
        adapter=new UploadAdapter(fileNames,filesDone);
        photosList.setLayoutManager(new GridLayoutManager(this,3));
        photosList.setAdapter(adapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent i=new Intent();
                    i.setType("image/*");
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i,"Select Picture"),RESULT_IMAGE_LOAD);
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uri!=null)
                {
                    if(description.getText().toString().equals(""))
                    {
                        Toast.makeText(PostActivity.this, "Please enter where to save your photos", Toast.LENGTH_SHORT).show();
                    }
                    else if(catogeries.getSelectedItem().equals("choose category"))
                    {
                        Toast.makeText(PostActivity.this, "Enter a valid category", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        final ProgressDialog pd = new ProgressDialog(PostActivity.this);
                        pd.setTitle("Uploading");
                        pd.setCancelable(false);
                        pd.show();
                        pd.setMessage("Please Wait we are uploading your images");
                        final DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child("Admin").child(category);
                        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child("Admin").child(category);
                        postid=reference.push().getKey();

                        hashMap=new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("publisherid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("description",description.getText().toString());
                        hashMap.put("owner","ధ ర్మ జ్యో తి  సేవా సం ఘం");
                        ref.child(postid).child("postsdetails").setValue(hashMap);
                        if(fileNames.size()>1)
                        {
                            for (int i = 0; i < fileNames.size(); i++)
                            {
                                final int c=i+1;
                                final StorageReference fileUpload = mStoorageRef.child("admin").child(category).child(fileNames.get(i));
                                uploadtask = fileUpload.putFile(fileUri.get(i));
                                if (fileUri.get(i) != null)
                                {
                                    uploadtask.continueWithTask(new Continuation() {
                                        @Override
                                        public Object then(@NonNull Task task) throws Exception {
                                            if (!task.isComplete()) {
                                                throw task.getException();
                                            }
                                            return fileUpload.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Uri downloadUri = task.getResult();
                                                myurl=downloadUri.toString();
                                                reference.child(postid).child("pics").child("pic"+c).setValue(myurl);
                                                pd.dismiss();
                                            } else {
                                                Toast.makeText(PostActivity.this, "failed to Post", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(PostActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                        else
                        {
                            final StorageReference fileUpload = mStoorageRef.child("admin").child(category).child(fileNames.get(0));
                            uploadtask = fileUpload.putFile(uri);
                            uploadtask.continueWithTask(new Continuation() {
                                @Override
                                public Object then(@NonNull Task task) throws Exception {
                                    if (!task.isComplete()) {
                                        throw task.getException();
                                    }
                                    return fileUpload.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        myurl=downloadUri.toString();
                                        reference.child(postid).child("pics").child("pic"+1).setValue(myurl);
                                        pd.dismiss();
                                    } else {
                                        Toast.makeText(PostActivity.this, "failed to Post", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        pd.dismiss();
                        Toast.makeText(PostActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PostActivity.this,AdminHomeActivity.class));
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(PostActivity.this, "Please choose atleast one pic", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("postid",postid);
                editor.apply();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_IMAGE_LOAD && resultCode==RESULT_OK)
        {
            if(data.getClipData()!=null)
            {
                //multiples pics are selected
                int itemCount=data.getClipData().getItemCount();
                for(int i=0;i<itemCount;i++)
                {
                    uri=data.getClipData().getItemAt(i).getUri();
                    fileName=getFileName(uri);
                    fileUri.add(uri);
                    fileNames.add(fileName);
                    if(adapter.getItemCount()>0)
                    {
                        description.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            else if(data.getData()!=null)
            {
                //only one image is selected
                Uri uri=data.getData();
                this.uri=data.getData();
                String fileName=getFileName(uri);
                fileNames.add(fileName);
                fileUri.add(uri);
                if(adapter.getItemCount()>0)
                {
                    description.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void Initialize()
    {
        fileNames=new ArrayList<>();
        filesDone=new ArrayList<>();
        photosList=findViewById(R.id.recyclerview_post_item);
        choose=findViewById(R.id.choosepics);
        post=findViewById(R.id.post);
        close=findViewById(R.id.close);
        description=findViewById(R.id.description);
        mStoorageRef= FirebaseStorage.getInstance().getReference("posts");
        catogeries=findViewById(R.id.spinner_addpage);
        eventname=findViewById(R.id.eventname);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category=catogeries_arry[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}