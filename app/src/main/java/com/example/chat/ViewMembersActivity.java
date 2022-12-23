package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.chat.adapter.MembersAdapter;
import com.example.chat.model.Conversation;
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

public class ViewMembersActivity extends AppCompatActivity implements MembersAdapter.Listener {

    RecyclerView rvMember;
    MembersAdapter membersAdapter;
    ArrayList<Conversation> arrayList;

    ArrayList<Participant> participants;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rvMember = findViewById(R.id.rvMember);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        rvMember.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        participants = new ArrayList<>();
        conversation = (Conversation) intent.getSerializableExtra("conversation");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        membersAdapter=new MembersAdapter(participants,this);
        rvMember.setAdapter(membersAdapter);
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
            Intent intent = new Intent(ViewMembersActivity.this, AddMemberActivity.class);
            intent.putExtra("conversation", conversation);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.child("conversations").child(conversation.getCid()).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                participants.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Participant participant = dataSnapshot.getValue(Participant.class);
                    participants.add(participant);
                }
                membersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onclick(Participant participant) {
        Intent intent = getIntent();
        participants = (ArrayList<Participant>) intent.getSerializableExtra("participants");
        Intent i=new Intent(this,ViewUserInfoDialog.class);
        i.putExtra("participants", participant);
        startActivity(i);
    }

    @Override
    public void onLongClick(Participant participant) {
        if(conversation.getCAdmin().equals(FirebaseAuth.getInstance().getUid())){
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.remove_message, null);
            final TextView textView = (TextView) alertLayout.findViewById(R.id.textView5);
            textView.setText("Bạn có muốn xóa thành viên này?");
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Xóa thành viên");
            alert.setView(alertLayout);
            alert.setCancelable(true);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    databaseReference.child("conversations").child(conversation.getCid());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    ArrayList<Participant> participants = new ArrayList<>();
                    for(Participant participant1: conversation.getParticipants()){
                        if(!participant1.getUID().equals(participant.getUID())){
                            participants.add(participant1);
                        }
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("participants", participants);
                    reference.child("conversations").child(conversation.getCid()).child("participants").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                Participant participant1 = dataSnapshot.getValue(Participant.class);
                                if(participant1.getUID().equals(participant.getUID())){
                                    reference.child("conversations").child(conversation.getCid()).child("participants").child(dataSnapshot.getKey()).removeValue();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    reference.child("conversations").child(conversation.getCid()).updateChildren(map);
                    membersAdapter.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

    }
}