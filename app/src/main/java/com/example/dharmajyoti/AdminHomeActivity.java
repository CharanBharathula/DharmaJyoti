package com.example.dharmajyoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;

import com.example.dharmajyoti.fragment.AddFragment;
import com.example.dharmajyoti.fragment.HomeFragment;
import com.example.dharmajyoti.fragment.NotificationsFragment;
import com.example.dharmajyoti.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {
    BottomNavigationView bottom_nav;
    Fragment selectedFragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        bottom_nav=findViewById(R.id.bottom_navigation);
        bottom_nav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch(item.getItemId())
            {
                case R.id.nav_admin_add:
                    selectedFragment=null;
                    startActivity(new Intent(AdminHomeActivity.this,PostActivity.class));
                    break;
                case R.id.nav_admin_home:
                    selectedFragment=new HomeFragment();
                    break;
                case R.id.nav_admin_profile:
                    SharedPreferences.Editor editor=AdminHomeActivity.this.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment=new ProfileFragment();
                    break;
                case R.id.nav_admin_search:
                    selectedFragment=new NotificationsFragment();
                    break;
            }
            if(selectedFragment!=null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }
            return true;
        }
    };
}
