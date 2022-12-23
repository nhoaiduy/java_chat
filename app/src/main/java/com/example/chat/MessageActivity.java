package com.example.chat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.adapter.MessageAdapter;
import com.example.chat.model.Conversation;
import com.example.chat.model.Media;
import com.example.chat.model.Message;
import com.example.chat.model.Participant;
import com.example.chat.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.OnMessageLongClick {
    CircleImageView img_profile;
    TextView tv_username;

    FirebaseUser fuser;
    DatabaseReference reference;
    ImageView imgChoose;
    ImageButton btn_send;
    ImageButton btn_ivideo;
    EditText edt_send;

    Intent intent;
    String TYPE_CONVERSATION;
    String CID_CON, CID_MESS;
    Toolbar toolbar;

    ArrayList<Message> mMessage;

    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    Conversation conversation;

    FirebaseStorage storage;
    StorageReference storageReference;

    public static long order=0;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    View view;

    String mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        view = findViewById(R.id.pathRelative);
        img_profile=findViewById(R.id.img_profile_mess);
        tv_username=findViewById(R.id.tv_username_mess);

        imgChoose = findViewById(R.id.imgChoose);
        btn_send=findViewById(R.id.btn_send);
        btn_ivideo=findViewById(R.id.btn_ivideo);
        edt_send=findViewById(R.id.edt_send);

        recyclerView=findViewById(R.id.recycler_view_mess);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mid = "";

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        intent=getIntent();

        mid = intent.getStringExtra("mid");

        mMessage=new ArrayList<>();


        conversation = (Conversation) intent.getSerializableExtra("conversation");

        TYPE_CONVERSATION = conversation.getType();
        if(mid != null){
            Log.d("ABC", mid);
            scrollToItem(Integer.parseInt(mid));
        }

        imgChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePath=null;
                view.setVisibility(View.GONE);
            }
        });

        btn_ivideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        renderMessage();
    }

    private void scrollToItem(int flag) {
        recyclerView.getLayoutManager().scrollToPosition(flag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chatbox, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuMore){
            Intent intent = new Intent(this, ConversationInfoActivity.class);
            intent.putExtra("conversation", conversation);
            finish();
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                view.setVisibility(View.VISIBLE);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgChoose.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() {
        if(filePath != null)
        {
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("message");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            if (!TextUtils.isEmpty(edt_send.getText().toString())){
                String mid = UUID.randomUUID().toString();
                order=mMessage.size();
                Map<String, Object> map = new HashMap<>();
                map.put("cid", conversation.getCid());
                map.put("order", order);
                map.put("mid", mid);
                map.put("uid", fuser.getUid());
                map.put("mdes", edt_send.getText().toString());
                map.put("mdate", currentDate);
                map.put("mtype", "txt");
                reference.child(mid).setValue(map);
                edt_send.setText("");
            }
            view.setVisibility(View.GONE);
            String url = UUID.randomUUID().toString();
            String mid = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("conversations/"+conversation.getCid()+"/"+ url);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            order=mMessage.size();
                            Map<String, Object> map = new HashMap<>();
                            map.put("cid", conversation.getCid());
                            map.put("order", order);
                            map.put("mid", mid);
                            map.put("uid", fuser.getUid());
                            map.put("mdes", url);
                            map.put("mdate", currentDate);
                            map.put("mtype", "img");
                            mReference.child(mid).setValue(map);

                            DatabaseReference mdReference = FirebaseDatabase.getInstance().getReference("media");
                            Map<String, Object> meMap = new HashMap<>();
                            meMap.put("cid", conversation.getCid());
                            meMap.put("uid", fuser.getUid());
                            meMap.put("mid", mid);
                            meMap.put("src", url);
                            meMap.put("type", "img");
                            mdReference.child(mid).setValue(meMap);
                            filePath=null;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MessageActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }else if(!TextUtils.isEmpty(edt_send.getText().toString())){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("message");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            String mid = UUID.randomUUID().toString();
            order=mMessage.size();
            Map<String, Object> map = new HashMap<>();
            map.put("cid", conversation.getCid());
            map.put("order", order);
            map.put("mid", mid);
            map.put("uid", fuser.getUid());
            map.put("mdes", edt_send.getText().toString());
            map.put("mdate", currentDate);
            map.put("mtype", "txt");
            reference.child(mid).setValue(map);
            edt_send.setText("");
        }else {
            filePath=null;
        }

    }



    private void renderMessage(){
        if(TYPE_CONVERSATION.equals("1")){
            tv_username.setText(conversation.getCName());

            fuser= FirebaseAuth.getInstance().getCurrentUser();

            reference=FirebaseDatabase.getInstance().getReference("conversations").child(conversation.getCid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Conversation conversation1 = dataSnapshot.getValue(Conversation.class);
                    if(conversation1.getImage().equals("ic_android+_group.png")){
                        img_profile.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        StorageReference ref = FirebaseStorage.getInstance().getReference();
                        ref.child("conversations/"+conversation1.getCid()+"/avatar").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(img_profile);
                            }
                        });

                    }
                    readMessage(conversation.getCid(), conversation1.getImage());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            fuser= FirebaseAuth.getInstance().getCurrentUser();
            reference=FirebaseDatabase.getInstance().getReference("conversations").child(conversation.getCid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Conversation conversation1 = dataSnapshot.getValue(Conversation.class);
                    for (Participant participant:conversation1.getParticipants()){
                        if(!participant.getUID().equals(fuser.getUid())){
                            tv_username.setText(participant.getNickname());
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            StorageReference profileRef = storageReference.child("users/"+participant.getUID()+"/avatar");
                            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(img_profile);
                                }
                            });
                            String uid = participant.getUID();
                            Log.d("ABC", uid);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    User user = snapshot1.getValue(User.class);
                                    readMessage(conversation.getCid(), user.getImage());
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

    private void readMessage(String conid, String imgurl){
        mMessage=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("message");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessage.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message=snapshot.getValue(Message.class);
                    if(message.getCid().equals(conid)){
                        mMessage.add(message);
                    }
                }
                Collections.sort(mMessage, Comparator.comparing(Message::getOrder));
                messageAdapter=new MessageAdapter(MessageActivity.this, mMessage, imgurl, MessageActivity.this);
                recyclerView.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onMessageLongClick(Message message) {
        if(message.getUid().equals(FirebaseAuth.getInstance().getUid())){
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.remove_message, null);
            final TextView textView = (TextView) alertLayout.findViewById(R.id.textView5);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Thu hồi tin nhắn");
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
                    StorageReference ref = FirebaseStorage.getInstance().getReference();
                    reference = FirebaseDatabase.getInstance().getReference();
                    if(message.getMtype().equals("img")){
                        ref.child("conversations").child(message.getCid()).child(message.getMdes()).delete();
                        reference.child("media").child(message.getMid()).removeValue();
                    }


                    reference.child("message").child(message.getMid()).removeValue();
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

    }
}