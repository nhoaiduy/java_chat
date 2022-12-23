package com.example.chat.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Conversation;
import com.example.chat.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int IMG_TYPE_LEFT = 2;
    public static final int IMG_TYPE_RIGHT = 3;
    public static final int VID_TYPE_LEFT = 4;
    public static final int VID_TYPE_RIGHT = 5;

    private Context mContext;
    private ArrayList<Message> mMessage;
    private ArrayList<Conversation> mConversation;
    private String imgurl;
    public interface OnMessageLongClick{
        void onMessageLongClick(Message message);
    }

    FirebaseUser fuser;
    public OnMessageLongClick mListener;

    public MessageAdapter(Context mContext, ArrayList<Message> mMessage, String imgurl, OnMessageLongClick mListener) {
        this.mContext = mContext;
        this.mMessage = mMessage;
        this.imgurl = imgurl;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent,false);
            return new ViewHolder(view);
        } else if(viewType==MSG_TYPE_LEFT) {
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent,false);
            return new ViewHolder(view);
        } else if(viewType==IMG_TYPE_LEFT){
            View view= LayoutInflater.from(mContext).inflate(R.layout.img_item_left, parent,false);
            return new ViewHolder(view);
        } else if (viewType==IMG_TYPE_RIGHT){
            View view= LayoutInflater.from(mContext).inflate(R.layout.img_item_right, parent,false);
            return new ViewHolder(view);
        }else {
            View view= LayoutInflater.from(mContext).inflate(R.layout.img_item_right, parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message=mMessage.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+message.getUid()+"/avatar");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.img_profile);
            }
        });
        if(message.getMtype().equals("img")){
            holder.show_img.setImageResource(R.drawable.attachment);
            profileRef = storageReference.child("conversations/"+message.getCid()+"/"+message.getMdes());
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.show_img);
                }
            });
        }else {
            holder.show_mess.setText(message.getMdes());
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onMessageLongClick(message);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_mess;
        public CircleImageView img_profile_mess, img_profile;
        public ImageView show_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_mess=itemView.findViewById(R.id.show_mess);
            img_profile_mess=itemView.findViewById(R.id.img_profile_mess);
            show_img=itemView.findViewById(R.id.show_img);
            img_profile = itemView.findViewById(R.id.img_profile);
        }

    }
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
//        final String[] userid = {null};
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("conversations");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
//                    Conversation conversation = snapshot.getValue(Conversation.class);
//
//                    for (int i = 0; i < conversation.getParticipants().size(); i++) {
//                        if (!conversation.getParticipants().get(i).getUID().equals(fuser.getUid())) {
//                            userid[0] =conversation.getParticipants().get(i).getUID();
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        if(mMessage.get(position).getMtype().equals("txt")){
            if (mMessage.get(position).getUid().equals(fuser.getUid())){
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }else {
            if (mMessage.get(position).getUid().equals(fuser.getUid())){
                return IMG_TYPE_RIGHT;
            } else {
                return IMG_TYPE_LEFT;
            }
        }

    }
}
