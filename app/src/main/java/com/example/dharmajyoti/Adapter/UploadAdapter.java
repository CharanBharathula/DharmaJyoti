package com.example.dharmajyoti.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dharmajyoti.R;

import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder>{
    List<String>fileNames;
    List<String>filesDone;

    public UploadAdapter(List<String> fileNames, List<String> filesDone) {
        this.fileNames = fileNames;
        this.filesDone = filesDone;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName=fileNames.get(position);
        holder.imageName.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView imageName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageName=itemView.findViewById(R.id.imagename);
        }
    }
}
