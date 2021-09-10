package com.example.dharmajyoti.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dharmajyoti.Adapter.UploadAdapter;
import com.example.dharmajyoti.AdminHomeActivity;
import com.example.dharmajyoti.PostActivity;
import com.example.dharmajyoti.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {
    private static final int RESULT_IMAGE_LOAD = 1;
    List<String> fileNames;
    List<String> filesDone;
    RecyclerView photosList;
    TextView choose,post,close;
    UploadAdapter adapter;
    EditText destinaion,description;
    public StorageReference mStoorageRef;
    Uri uri;
    public String fileName;
    public StorageTask uploadtask;
    public List<Uri>fileUri=new ArrayList<>();
    String postid;
    public String myurl;
    HashMap<String,Object> hashMap;
    public List<String>imageDownloadUrls;


    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_add, container, false);
        Initialize(view);
        adapter=new UploadAdapter(fileNames,filesDone);
        photosList.setLayoutManager(new GridLayoutManager(getContext(),3));
        photosList.setAdapter(adapter);

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
                    if(destinaion.getText().toString().equals("") && description.getText().toString().equals(""))
                    {
                        Toast.makeText(getActivity(), "Please enter where to save your photos", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        final ProgressDialog pd = new ProgressDialog(getActivity());
                        pd.setTitle("Uploading");
                        pd.setCancelable(false);
                        pd.show();
                        pd.setMessage("Please Wait we are uploading your images");
                        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Admin").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("posts").child(destinaion.getText().toString().trim());
                        postid=reference.push().getKey();
                        hashMap=new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("description",description.getText().toString());
                        hashMap.put("owner",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.setValue(hashMap);
                        imageDownloadUrls=new ArrayList<>();
                        for (int i = 0; i < fileNames.size(); i++)
                        {
                            final int c=i+1;
                            final StorageReference fileUpload = mStoorageRef.child("admin").child(destinaion.getText().toString().trim()).child(fileNames.get(i));
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
                                            reference.child("pics").child("pic"+c).setValue(myurl);
                                        } else {
                                            Toast.makeText(getActivity(), "failed to Post", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();
                            }

                        }

                        pd.dismiss();
                        Toast.makeText(getActivity(), "uploaded successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), AdminHomeActivity.class));
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Please choose atleast one pic", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_IMAGE_LOAD && resultCode==1) {
            if (data.getClipData() != null) {
                //multiples pics are selected
                int itemCount = data.getClipData().getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    uri = data.getClipData().getItemAt(i).getUri();
                    //fileName=getFileName(uri);
                    fileUri.add(uri);
                    fileNames.add(fileName);
                    if (adapter.getItemCount() > 0) {
                        destinaion.setVisibility(View.VISIBLE);
                        description.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else if (data.getData() != null) {
                //only one image is selected
                Uri uri = data.getData();
                //String fileName=getFileName(uri);
                fileNames.add(fileName);
                fileUri.add(uri);
                if (adapter.getItemCount() > 0) {
                    destinaion.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    /*public String getFileName(Uri uri) {
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
    }*/
    private void Initialize(View view)
    {
        fileNames=new ArrayList<>();
        filesDone=new ArrayList<>();
        photosList=view.findViewById(R.id.recyclerview_post_item);
        choose=view.findViewById(R.id.choosepics);
        post=view.findViewById(R.id.post);
        close=view.findViewById(R.id.close);
        description=view.findViewById(R.id.description);
        mStoorageRef= FirebaseStorage.getInstance().getReference("posts");
    }
}
