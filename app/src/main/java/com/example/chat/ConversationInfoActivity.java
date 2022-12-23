package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chat.Fragment.ConversationInfoFragment;
import com.example.chat.Fragment.GroupInfoFragment;
import com.example.chat.model.Conversation;

public class ConversationInfoActivity extends AppCompatActivity {


    String TYPE_CONVERSATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Fragment fragment = null;
        Intent intent = getIntent();
        Conversation conversation = (Conversation) intent.getSerializableExtra("conversation");
        TYPE_CONVERSATION = conversation.getType();
        if(TYPE_CONVERSATION.equals("1")){
            fragment = new GroupInfoFragment();
        }else if(TYPE_CONVERSATION.equals("0")) {
            fragment = new ConversationInfoFragment();
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( ConversationInfoActivity.this, MessageActivity.class);
                intent.putExtra("conversation", conversation);
                finish();
                startActivity(intent);
            }
        });


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}