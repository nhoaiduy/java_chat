package com.example.chat.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chat.R;
import com.example.chat.model.Conversation;
import com.example.chat.model.Friend;
import com.example.chat.model.Participant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {



    public interface OnFriendItemClickListener{
        void onFriendItemClick(Friend friend);
        void onLongItemClick(Friend friend);
    }

    public class ViewHolderFriend extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView image;
        public ViewHolderFriend(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.imgAvatar);
        }
    }
    public class ViewHolderMember extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView image;
        public ViewHolderMember(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.imgAvatar);
        }
    }

    public List<Friend> mFriends;
    public List<Friend> fFriends;
    public OnFriendItemClickListener mListener;
    public int type = 1;

    public FriendAdapter(List<Friend> mFriends, OnFriendItemClickListener mListener, int type){
        this.mFriends = mFriends;
        this.mListener = mListener;
        this.type = type;
        this.fFriends = mFriends;
    }

    public FriendAdapter(List<Friend> mFriends, int type){
        this.mFriends = mFriends;
        this.type = type;
        this.fFriends = mFriends;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(type == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_conversation, parent, false);
            return new ViewHolderFriend(view);
        }else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_create, parent, false);
            return new ViewHolderMember(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Friend friend = fFriends.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        if (type==1){
            ViewHolderFriend viewHolderFriend = (ViewHolderFriend) holder;
            StorageReference profileRef = storageReference.child("users/"+friend.getUid()+"/"+ friend.getImage());
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(viewHolderFriend.image);
                }
            });
            viewHolderFriend.tvName.setText(friend.getFullName());
            viewHolderFriend.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFriendItemClick(friend);
                }

            });
            viewHolderFriend.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onLongItemClick(friend);
                    return true;
                }
            });
        }else {
            ViewHolderMember viewHolderMember = (ViewHolderMember) holder;
            StorageReference profileRef = storageReference.child("users/"+friend.getUid()+"/"+ friend.getImage());
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(viewHolderMember.image);
                }
            });
            viewHolderMember.tvName.setText(friend.getFullName());
            viewHolderMember.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFriendItemClick(friend);
                }
            });
            viewHolderMember.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onLongItemClick(friend);
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return fFriends.size();
    }

    @Override
    public Filter getFilter() {
        return new FriendFilter();
    }

    class FriendFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString();
            if(charString.isEmpty()){
                fFriends = mFriends;
            }else {
                ArrayList<Friend> filteredList = new ArrayList<>();
                for (Friend friend : mFriends){
                    if(friend.getFullName().toLowerCase().contains(charString.toLowerCase())){
                        filteredList.add(friend);
                    }
                }
                fFriends = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values= fFriends;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            fFriends = (ArrayList<Friend>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
