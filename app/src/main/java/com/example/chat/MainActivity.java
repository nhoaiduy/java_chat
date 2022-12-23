package com.example.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chat.Fragment.ConversationFragment;
import com.example.chat.Fragment.FriendFragment;
import com.example.chat.Fragment.ProfileFragment;
import com.example.chat.Fragment.StatusFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btn_signOut, btn_info;
    FirebaseAuth fAuth;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        setFragment(R.id.mnuConversation);
        bottomNavigationView = findViewById(R.id.bn_main);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setFragment(item.getItemId());
                return true;
            }
        });
    }
    void setFragment(int id){
        Fragment fragment = null;
        switch (id){
            case R.id.mnuConversation:
                toolbar.setTitle("Conversation");
                fragment = new ConversationFragment();
                break;
            case R.id.mnuFriendRequest:
                toolbar.setTitle(("Friend"));
                fragment  =new FriendFragment();
                break;
            case R.id.mnuStatus:
                toolbar.setTitle(("Status"));
                fragment  =new StatusFragment();
                break;
            case R.id.mnuProfile:
                toolbar.setTitle(("Profile"));
                fragment = new ProfileFragment();
                break;

        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    public static void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());

        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }
}