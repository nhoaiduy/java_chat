package com.example.chat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Conversation;
import com.example.chat.model.Media;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnMediaItemClickListener{
        void onMediaItemClick(Media media);
    }

    class MediaVH extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MediaVH(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgMedia);
        }
    }

    List<Media> medias;
    OnMediaItemClickListener onMediaItemClickListener;
    Conversation conversation;

    public MediaAdapter(List<Media> media, OnMediaItemClickListener onMediaItemClickListener, Conversation conversation){
        this.medias = media;
        this.onMediaItemClickListener = onMediaItemClickListener;
        this.conversation = conversation;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_media, viewGroup, false);
        return new MediaVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Media media = medias.get(i) ;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        MediaVH mediaVH = (MediaVH) viewHolder;
        StorageReference profileRef = storageReference.child("conversations/"+conversation.getCid()+"/"+media.getSrc());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mediaVH.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }
}
