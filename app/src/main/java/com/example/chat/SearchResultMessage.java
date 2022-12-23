package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chat.adapter.SearchAdapter;
import com.example.chat.model.Conversation;
import com.example.chat.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultMessage extends AppCompatActivity implements SearchAdapter.OnMessageResultClick {

    RecyclerView recyclerView;
    SearchAdapter adapter;
    ArrayList<Message> messages;
    String  searchString;
    Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        conversation = (Conversation) intent.getSerializableExtra("conversation");
        searchString = intent.getStringExtra("searchString");
        messages = new ArrayList<>();
        recyclerView = findViewById(R.id.rvResultMess);
        adapter = new SearchAdapter(messages, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    if(message.getCid().equals(conversation.getCid()) && message.getMdes().toLowerCase().contains(searchString.toLowerCase())){
                        messages.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMessageResultClick(Message message) {
        Intent intent = new Intent(SearchResultMessage.this, MessageActivity.class);
        intent.putExtra("conversation", conversation);
        ArrayList<Message> sMessage= new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message1=snapshot.getValue(Message.class);
                    if(message1.getCid().equals(conversation.getCid())){
                        sMessage.add(message1);
                    }
                }
                String mid = null;
                Log.d("ABC", sMessage.size()+"");
                for(int i = 0; i< sMessage.size(); i++){
                    if(message.getMid().equals(sMessage.get(i).getMid())){
                        mid = i+"";
                    }
                }

                Log.d("ABC", mid);
                intent.putExtra("mid", mid);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}