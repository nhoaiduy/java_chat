package com.example.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.adapter.FriendAdapter;
import com.example.chat.model.Conversation;
import com.example.chat.model.Friend;
import com.example.chat.model.Participant;
import com.example.chat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddGroupActivity extends AppCompatActivity implements  FriendAdapter.OnFriendItemClickListener {

    TextView txtName;
    SearchView searchView;
    Button btnCancel, btnCreate;
    RecyclerView rvAddGroup, rvMember;
    FriendAdapter friendAdapter, memberAdapter;
    ArrayList<Friend> friendList, memberList;
    ArrayList<Conversation> conversations;
    String type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        txtName = findViewById(R.id.edtNameGroup);
        searchView = findViewById(R.id.svGroup);
        friendList = new ArrayList<>();
        conversations = new ArrayList<>();
        btnCreate = findViewById(R.id.btnCreateGr);
        btnCancel = findViewById(R.id.btnCancelGr);
        rvAddGroup = findViewById(R.id.rvAddGroup);
        friendAdapter = new FriendAdapter(friendList, this,1);
        rvAddGroup.setAdapter(friendAdapter);
        rvAddGroup.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvAddGroup.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendAdapter.getFilter().filter(newText);
                return false;
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddGroupActivity.this, MessageActivity.class);
                Conversation conversation = new Conversation();
                String cid = UUID.randomUUID().toString();
                if(TextUtils.isEmpty(txtName.getText().toString()))
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập tên nhóm!", Toast.LENGTH_SHORT).show();
                else{
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference();
                    ArrayList<Participant> participants = new ArrayList<>();
                    for (int i =0; i< memberList.size();i++){
                        participants.add(new Participant(memberList.get(i).getFullName(), memberList.get(i).getUid(), false));
                    }
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    databaseReference.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            participants.add(new Participant(user.getFullName(), userID, false));
                            Map<String, Object> map = new HashMap();
                            map.put("cid", cid);
                            map.put("cadmin", FirebaseAuth.getInstance().getUid());
                            map.put("cname", txtName.getText().toString());
                            map.put("image", "ic_android+_group.png");
                            map.put("type", "1");

                            map.put("participants", participants);
                            databaseReference.child("conversations").child(cid).setValue(map);
                            databaseReference.child("conversations").child(cid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Conversation conversation = snapshot.getValue(Conversation.class);
                                    Intent intent = new Intent(AddGroupActivity.this, MessageActivity.class);
                                    intent.putExtra("conversation", conversation);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });





                }
            }
        });

        memberList = new ArrayList<>();
        rvMember = findViewById(R.id.rvMember);
        memberAdapter = new FriendAdapter(memberList,0);
        rvMember.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMember.setAdapter(memberAdapter);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onFriendItemClick(Friend friend) {
        Log.d("ABC", friend.getFullName()+" "+memberList.size());
        if (!memberList.contains(friend)){
            memberList.add(friend);
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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users").child(FirebaseAuth.getInstance().getUid()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    friendList.add(friend);
                }
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
