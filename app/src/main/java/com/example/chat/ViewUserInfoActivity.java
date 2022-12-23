package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat.model.Participant;
import com.example.chat.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewUserInfoActivity extends AppCompatActivity {
    ImageView imgAva;
    TextView tvFullName, tvPhone, tvDOB, tvGender, tvMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgAva=findViewById(R.id.img_avt);
        tvFullName=findViewById(R.id.tv_fullname);
        tvDOB=findViewById(R.id.tv_dob);
        tvGender=findViewById(R.id.tv_gender);
        tvMail=findViewById(R.id.tv_mail);
        tvPhone=findViewById(R.id.tv_phone);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        Intent intent = getIntent();
        ArrayList<Participant> participants = (ArrayList<Participant>) intent.getSerializableExtra("participants");

        String uid = "";
        for (Participant participant: participants){
            if(!participant.getUID().equals(FirebaseAuth.getInstance().getUid())){
                uid = participant.getUID();
            }
        }
        final User[] user = new User[] {new User()};


        databaseReference.child("users").child(uid)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user[0] = snapshot.getValue(User.class);
                    if(user[0]!=null){
                        tvFullName.setText(user[0].getFullName());
                        tvPhone.setText(user[0].getPhone());
                        tvDOB.setText(user[0].getDob());
                        tvMail.setText(user[0].getEmail());
                        tvGender.setText(user[0].getGender());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+uid+"/avatar");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgAva);
            }
        });
    }
}