package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat.model.Participant;
import com.example.chat.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewUserInfoDialog extends AppCompatActivity {
    Participant participants;
    ImageView imgAva;
    TextView tvFullName, tvPhone, tvDOB, tvGender, tvMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_user_info_dialog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgAva=findViewById(R.id.img_avt_dialog);
        tvFullName=findViewById(R.id.tv_fullname_dialog);
        tvDOB=findViewById(R.id.tv_dob_dialog);
        tvGender=findViewById(R.id.tv_gender_dialog);
        tvMail=findViewById(R.id.tv_mail_dialog);
        tvPhone=findViewById(R.id.tv_phone_dialog);

        Intent intent = getIntent();
        participants = (Participant) intent.getSerializableExtra("participants");



        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        String uid = "";

        final User[] user = {new User()};

        databaseReference.child("users").child(participants.getUID())
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
        StorageReference profileRef = storageReference.child("users/"+participants.getUID()+"/avatar");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgAva);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.mnu_close){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}