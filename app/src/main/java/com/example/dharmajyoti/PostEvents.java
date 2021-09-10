package com.example.dharmajyoti;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dharmajyoti.Adapter.UploadAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostEvents extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_events);

        Initialize();
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
                        description.setError("Please Enter Description");
                    }
                    else if(eventname.getText().toString().equals(""))
                    {
                        eventname.setError("Please enter event name");
                    }
                    else
                    {
                        final ProgressDialog pd = new ProgressDialog(PostEvents.this);
                        pd.setTitle("Uploading");
                        pd.setCancelable(false);
                        pd.show();
                        pd.setMessage("Please Wait we are uploading your images");
                        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Events").child(eventname.getText().toString());
                        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Events").child(eventname.getText().toString());
                        postid=reference.push().getKey();

                        hashMap=new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("description",description.getText().toString());
                        hashMap.put("owner","ధ ర్మ జ్యో తి  సేవా సం ఘం");
                        ref.child(postid).child("postdetails").setValue(hashMap);
                        if(fileNames.size()>1)
                        {
                            for (int i = 0; i < fileNames.size(); i++)
                            {
                                final int c=i+1;
                                final StorageReference fileUpload = mStoorageRef.child("admin").child("events").child(eventname.getText().toString()).child(fileNames.get(i));
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
                                                Toast.makeText(PostEvents.this, "failed to Post", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PostEvents.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(PostEvents.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                        else
                        {
                            final StorageReference fileUpload = mStoorageRef.child("admin").child("events").child(eventname.getText().toString()).child(fileNames.get(0));
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
                                        reference.child("pics").child("pic"+1).setValue(myurl);
                                    } else {
                                        Toast.makeText(PostEvents.this, "failed to Post", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PostEvents.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        pd.dismiss();
                        Toast.makeText(PostEvents.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PostEvents.this,AdminHomeActivity.class));
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(PostEvents.this, "Please choose atleast one pic", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences.Editor editor=PostEvents.this.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("en",eventname.getText().toString());
                editor.apply();
            }
        });

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
        eventname=findViewById(R.id.eventname);
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
                        eventname.setVisibility(View.VISIBLE);
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
                    eventname.setVisibility(View.VISIBLE);
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
}