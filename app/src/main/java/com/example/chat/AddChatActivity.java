package com.example.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddChatActivity extends AppCompatActivity implements FriendAdapter.OnFriendItemClickListener {

    SearchView searchView;
    Button btnCancel, btnNewGroup;
    RecyclerView rvAddChat;
    FriendAdapter friendAdapter;
    ArrayList<Friend> arrayList;
    String type = "0";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);
        arrayList = new ArrayList<>();
        btnNewGroup = findViewById(R.id.btnNewGroup);
        btnCancel = findViewById(R.id.btnCancel);
        searchView = findViewById(R.id.svChat);
        rvAddChat = findViewById(R.id.rvAddChat);
        friendAdapter = new FriendAdapter(arrayList, this, 1);
        rvAddChat.setAdapter(friendAdapter);
        rvAddChat.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvAddChat.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        btnNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddGroupActivity.class);
                startActivity(intent);
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

    }
    @Override
    protected void onResume() {
        super.onResume();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        reference.child("users").child(FirebaseAuth.getInstance().getUid()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Friend friend = snap.getValue(Friend.class);
                    arrayList.add(friend);
                }
                friendAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    int i = 0;

    @Override
    public void onFriendItemClick(Friend friend) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Conversation conversation = dataSnapshot.getValue(Conversation.class);
                    boolean cdn1 = conversation.getParticipants().get(0).getUID().equals(user.getUid()) && conversation.getParticipants().get(1).getUID().equals(friend.getUid());
                    boolean cdn2 = conversation.getParticipants().get(1).getUID().equals(user.getUid()) && conversation.getParticipants().get(0).getUID().equals(friend.getUid());
                    if(conversation.getType().equals("0") && (cdn1 || cdn2)){
                        i = 1;
                        Intent intent = new Intent(AddChatActivity.this, MessageActivity.class);
                        intent.putExtra("conversation", conversation);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
                if(i==0){
                    reference = FirebaseDatabase.getInstance().getReference();
                    String cid = UUID.randomUUID().toString();
                    ArrayList<Participant> participants = new ArrayList<>();
                    reference.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                User user1 = dataSnapshot.getValue(User.class);
                                String uid = dataSnapshot.getKey();
                                Log.d("ABC", dataSnapshot.getKey());
                                if(uid.equals(user.getUid())){
                                    participants.add(new Participant(user1.getFullName(), user.getUid(), false));
                                    participants.add(new Participant(friend.getFullName(), friend.getUid(), false));
//                                    Log.d("ABC", participants.size()+" "+participants.get(0).getNickname());
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("cid", cid);
                                    map.put("participants", participants);
                                    map.put("type", type);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("conversations").child(cid).setValue(map);
                                    databaseReference.child("conversations").child(cid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Conversation conversation = snapshot.getValue(Conversation.class);
                                            Intent intent = new Intent(AddChatActivity.this, MessageActivity.class);
                                            intent.putExtra("conversation", conversation);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    break;
                                }
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public void onLongItemClick(Friend friend) {

    }
}