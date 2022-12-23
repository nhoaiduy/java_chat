package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chat.adapter.MediaAdapter;
import com.example.chat.adapter.MessageAdapter;
import com.example.chat.model.Conversation;
import com.example.chat.model.Media;
import com.example.chat.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MediaActivity extends AppCompatActivity implements MediaAdapter.OnMediaItemClickListener {

    Conversation conversation;
    RecyclerView rvMedia;
    MediaAdapter mediaAdapter;
    ArrayList<Media> medias = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        conversation = (Conversation) intent.getSerializableExtra("conversation");
        rvMedia = findViewById(R.id.rvMedia);
        readMedias();

        rvMedia.setLayoutManager(new GridLayoutManager(this, 3));
        rvMedia.addItemDecoration(new DividerItemDecoration(this,GridLayoutManager.VERTICAL));

    }

    public void readMedias(){
        databaseReference=FirebaseDatabase.getInstance().getReference("media");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medias.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Media media = snapshot.getValue(Media.class);
                    if(media.getCid().equals(conversation.getCid())){
                        medias.add(media);
                    }
                }
                mediaAdapter = new MediaAdapter(medias, MediaActivity.this, conversation);
                rvMedia.setAdapter(mediaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onMediaItemClick(Media media) {

    }
}