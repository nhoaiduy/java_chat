package com.example.chat.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Message;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MessageVH> {


    public interface OnMessageResultClick {
        void onMessageResultClick(Message message);
    }

    public List<Message> messages;
    public OnMessageResultClick messageResultClick;

    class MessageVH extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvMess;
        CircleImageView image;
        public MessageVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtName);
            tvMess = itemView.findViewById(R.id.txtMess);
            image = itemView.findViewById(R.id.imgAvatar);
        }
    }

    public  SearchAdapter(List<Message> messages, OnMessageResultClick messageResultClick){
        this.messages = messages;
        this.messageResultClick= messageResultClick;
    }

    @NonNull
    @Override
    public SearchAdapter.MessageVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_result_message,parent,false);
        return new MessageVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageVH messageVH, int i) {
        Message message = messages.get(i);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(message.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                StorageReference profileRef = storageReference.child("users/"+ user.getUid()+"/avatar");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(messageVH.image);
                    }
                });
                messageVH.tvName.setText(user.getFullName());
                messageVH.tvMess.setText(message.getMdes());;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageVH.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageResultClick.onMessageResultClick(message);
            }
        });
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
}
