package com.example.dharmajyoti.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dharmajyoti.ChatsActivity;
import com.example.dharmajyoti.EventsDisplayActivity;
import com.example.dharmajyoti.Model.Post;
import com.example.dharmajyoti.PostActivity;
import com.example.dharmajyoti.PostEvents;
import com.example.dharmajyoti.PostHomeActivity;
import com.example.dharmajyoti.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    Button b1,b2,b3,b4,b5,b6,b7,b8;
    ImageView open_chat;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        b1=view.findViewById(R.id.saamajika);
        b2=view.findViewById(R.id.maatrumandali);
        b3=view.findViewById(R.id.baalakutira);
        b4=view.findViewById(R.id.deva);
        b5=view.findViewById(R.id.dharmaprachaaram);
        b6=view.findViewById(R.id.about);
        b7=view.findViewById(R.id.donations);
        b8=view.findViewById(R.id.upcoming);
        open_chat=view.findViewById(R.id.open_chat);
        open_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ChatsActivity.class));
            }
        });

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        SharedPreferences.Editor editor;
        switch(v.getId())
        {
            case R.id.saamajika:
                editor=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("cn","సామాజిక సేవ");
                editor.apply();
                startActivity(new Intent(getActivity(), PostHomeActivity.class));
                break;
            case R.id.maatrumandali:
                editor=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("cn","మాతృ మండలి");
                editor.apply();
                startActivity(new Intent(getActivity(), PostHomeActivity.class));
                break;
            case R.id.baalakutira:
                editor=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("cn","బాల కుటీర");
                editor.apply();
                startActivity(new Intent(getActivity(), PostHomeActivity.class));
                break;
            case R.id.dharmaprachaaram:
                editor=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("cn","ధర్మ ప్రచారం");
                editor.apply();
                startActivity(new Intent(getActivity(), PostHomeActivity.class));
                break;
            case R.id.deva:
                editor=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                editor.putString("cn","దేవాలయ పరిరక్షణ");
                editor.apply();
                startActivity(new Intent(getActivity(), PostHomeActivity.class));
                break;
            case R.id.about:
                View view=getLayoutInflater().inflate(R.layout.dialog_layout,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setView(view);
                builder.setTitle("సంస్ట గురించి");
                final AlertDialog alert=builder.create();
                alert.show();
                break;
            case R.id.donations:
                View view2=getLayoutInflater().inflate(R.layout.donations,null);
                AlertDialog.Builder builder2=new AlertDialog.Builder(getActivity());
                builder2.setView(view2);
                builder2.setTitle("సంస్ట గురించి");
                final AlertDialog alert2=builder2.create();
                alert2.show();
                break;
            case R.id.upcoming:
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals("tCavLXduTFPclGge9wrkJRa6NUq2"))
                {
                    View view3=getLayoutInflater().inflate(R.layout.upcoming_alert,null);
                    AlertDialog.Builder builder3=new AlertDialog.Builder(getActivity());
                    builder3.setView(view3);
                    builder3.setTitle("Choose Options");
                    builder3.setIcon(R.drawable.choose);
                    final TextView post=view3.findViewById(R.id.post);
                    final TextView see=view3.findViewById(R.id.view_events);
                    final AlertDialog alert3=builder3.create();
                    alert3.show();
                    post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), PostEvents.class));
                            alert3.dismiss();
                        }
                    });
                    see.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), EventsDisplayActivity.class));
                            alert3.dismiss();
                        }
                    });
                }
                else
                {
                    startActivity(new Intent(getActivity(), EventsDisplayActivity.class));
                }
                break;
        }
    }

}
