package com.example.chat.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.Fragment.FriendFragment;
import com.example.chat.R;
import com.example.chat.model.Friend;
import com.example.chat.model.FriendRequest;
import com.example.chat.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestVH> {
    public List<User> mFQ;
    public OnFriendRequestItemClickListener mListener;

    public interface OnFriendRequestItemClickListener{
        void onFriendRequestItemClickListener(User user);

    }
    public class FriendRequestVH extends RecyclerView.ViewHolder{
        TextView tvName;
        ImageView image;
        Button btnAccept, btnDeny;
        public FriendRequestVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtNameFQ);
            image = itemView.findViewById(R.id.imgAvatarFQ);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDeny = itemView.findViewById(R.id.btnDeny);
        }
    }
    public FriendRequestAdapter(List<User> mFQ, OnFriendRequestItemClickListener mListener){
        this.mFQ = mFQ;
        this.mListener = mListener;
    }
    @NonNull
    @Override
    public FriendRequestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_friend_request,parent,false);
        return new FriendRequestVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestVH holder, int position) {
        User user = mFQ.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+user.getUid()+"/avatar");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.image);
            }
        });
        holder.tvName.setText(user.getFullName());
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] name = new String[1];
                String[] image = new String[1];
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                reference.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.getValue(User.class);

                        name[0] = user1.getFullName();
                        image[0] = user1.getImage();
                        ArrayList<Friend> friends = new ArrayList<>();
                        int order = 0;
                        if(user.getFriends()!=null){
                            friends.addAll(user.getFriends());
                            order = Integer.parseInt(friends.get(friends.size()-1).getOrder())+1;
                        }

                        Map<String, Object> map = new HashMap();
                        map.put("fullName", user1.getFullName());
                        map.put("uid", user1.getUid());
                        map.put("image", user1.getImage());
                        map.put("order", order+"");
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                        reference1.child("users").child(user.getUid()).child("friends").child(order+"").setValue(map);

                        ArrayList<Friend> friends1 = new ArrayList<>();
                        order = 0;
                        if(user1.getFriends()!=null){
                            friends1.addAll(user1.getFriends());
                            order = Integer.parseInt(friends1.get(friends1.size()-1).getOrder())+1;
                        }
                        boolean flag = false;
                        map.clear();
                        Log.e("ABC", friends1.size()+"");
                        for(Friend friend: friends1){
                            if(friend != null && friend.getUid().equals(user.getUid())){
                                flag = true;
                                break;
                            }
                        }
                        if(!flag){
                            map.put("fullName", user.getFullName());
                            map.put("uid", user.getUid());
                            map.put("image", user.getImage());
                            map.put("order", order+"");
                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                            reference2.child("users").child(userID).child("friends").child(order+"").setValue(map);
                            map.clear();
                        }
                        reference.child("users").child(userID).child("requests").child(user.getUid()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });
        holder.btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users").child(userID).child("requests").child(user.getUid()).removeValue();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFriendRequestItemClickListener(user);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFQ.size();
    }



}