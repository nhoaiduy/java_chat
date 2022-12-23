package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.chat.adapter.FriendAdapter;
import com.example.chat.model.Conversation;
import com.example.chat.model.Friend;
import com.example.chat.model.Participant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMemberActivity extends AppCompatActivity implements FriendAdapter.OnFriendItemClickListener {

    RecyclerView rvFriend, rvMember;
    FriendAdapter friendAdapter, memberAdapter;
    ArrayList<Friend> friendList, memberList;
    Conversation conversation;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().setTitle("Add Member");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        rvFriend = findViewById(R.id.rvFriend);
        rvMember = findViewById(R.id.rvMember);

        Intent intent = getIntent();
        conversation = (Conversation) intent.getSerializableExtra("conversation");

        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(friendList, this,1);
        rvFriend.setAdapter(friendAdapter);
        rvFriend.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvFriend.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));

        memberList = new ArrayList<>();
        rvMember = findViewById(R.id.rvMember);
        memberAdapter = new FriendAdapter(memberList,0);
        rvMember.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.member_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_mem){
            for (Friend friend: memberList){
                conversation.getParticipants().add(new Participant(friend.getFullName(), friend.getUid(), false));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("participants", conversation.getParticipants());
            databaseReference.child("conversations").child(conversation.getCid()).updateChildren(map);
            Intent intent = new Intent(AddMemberActivity.this, MessageActivity.class);
            intent.putExtra("conversation", conversation);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onFriendItemClick(Friend friend) {
        Log.d("ABC", friend.getFullName()+" "+memberList.size());
        if (!memberList.contains(friend)){
            memberList.add(friend);
            rvMember.setAdapter(memberAdapter);
            memberAdapter.notifyDataSetChanged();
            friendAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLongItemClick(Friend friend) {

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onResume() {
        super.onResume();

        databaseReference.child("users").child(FirebaseAuth.getInstance().getUid()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    boolean flag =false;
                    for (Participant participant: conversation.getParticipants()){
                        if(participant.getUID().equals(friend.getUid())){
                            flag=true;
                            break;
                        }
                    }
                    if(!flag)friendList.add(friend);

                }
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}