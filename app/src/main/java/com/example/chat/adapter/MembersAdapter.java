package com.example.chat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Conversation;
import com.example.chat.model.Participant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private Context mContext;
    List<Participant> mParticipant;
    List<Conversation> mConversation;
    Listener listener;

    public interface Listener {
        void onclick(Participant participant);
        void onLongClick (Participant participant);
    }

    public MembersAdapter(List<Participant> mParticipant, Listener listener) {
        this.mParticipant = mParticipant;
        this.listener=listener;
    }

    public MembersAdapter(List<Participant> mParticipant, List<Conversation> mConversation, Listener listener) {
        this.mParticipant = mParticipant;
        this.mConversation = mConversation;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.view_members_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant participant=mParticipant.get(position);
        holder.tvName.setText(participant.getNickname());
        if (!participant.getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.tvName.setText(participant.getNickname());
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+participant.getUID()+"/avatar");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.imgAva);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onclick(participant);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(participant);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mParticipant.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgAva;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAva=itemView.findViewById(R.id.img_member);
            tvName=itemView.findViewById(R.id.tv_member);
        }
    }
}
