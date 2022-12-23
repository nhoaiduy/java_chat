package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat.model.Conversation;

public class SearchMessageActivity extends AppCompatActivity {
    EditText edt_search;
    Button btn_cancel, btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);

        edt_search=findViewById(R.id.edt_search_mess);
        btn_cancel=findViewById(R.id.btn_cancel);
        btn_search=findViewById(R.id.btn_search);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        Conversation convarsation = (Conversation) intent.getSerializableExtra("conversation");

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edt_search.getText().toString().isEmpty()){
                    Intent intent1=new Intent(getApplicationContext(),SearchResultMessage.class);
                    intent1.putExtra("conversation", convarsation);
                    intent1.putExtra("searchString", edt_search.getText().toString());
                    startActivity(intent1);
                }

            }
        });


    }
}