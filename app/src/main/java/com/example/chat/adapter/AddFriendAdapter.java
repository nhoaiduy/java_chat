package com.example.chat.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Conversation;
import com.example.chat.model.Participant;
import com.example.chat.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.AddFriendVH> implements Filterable {

    public List<User> mAFQ;
    public OnAddFriendItemClickListener mListener;
    public List<User> fAFQ;


    public interface OnAddFriendItemClickListener{
        void onAddFriendItemClickListener(User user);
    }

    public AddFriendAdapter(List<User> mAFQ, OnAddFriendItemClickListener mListener){
        this.mAFQ = mAFQ;
        this.mListener = mListener;
        this.fAFQ = mAFQ;
    }
    @NonNull
    @Override
    public AddFriendVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_add_friend,parent,false);
        return new AddFriendVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendVH holder, int position) {
        User user = fAFQ.get(position);


        final String[] name = new String[1];
        final String[] image = new String[1];
        holder.imageAFQ.setImageResource(R.drawable.ava);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+ user.getUid()+"/"+ user.getImage());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.imageAFQ);
            }
        });
        holder.tvNameAFQ.setText(user.getFullName());
        holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.getValue(User.class);
                        name[0] = user1.getFullName();
                        image[0] = user1.getImage();
                        Map<String, Object> friend = new HashMap();
                        friend.put("uid", userID);
                        friend.put("fullName", name[0]);
                        friend.put("image",image[0]);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("users").child(user.getUid()).child("requests").child(userID).setValue(friend);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAddFriendItemClickListener(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fAFQ.size();
    }

    public class AddFriendVH extends RecyclerView.ViewHolder{
        TextView tvNameAFQ;
        ImageView imageAFQ;
        Button btnAddFriend, btnDelete;
        public AddFriendVH(@NonNull View itemView) {
            super(itemView);
            tvNameAFQ = itemView.findViewById(R.id.txtNameAFQ);
            imageAFQ = itemView.findViewById(R.id.imgAvatarAFQ);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public Filter getFilter() {
        return new AddFriendFilter();
    }

    class AddFriendFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString();
            if(charString.isEmpty()){
                fAFQ = mAFQ;
            }else {
                ArrayList<User> filteredList = new ArrayList<>();
                for (User user : mAFQ){
                    if(user.getFullName().toLowerCase().contains(charString.toLowerCase())){
                        filteredList.add(user);
                    }
                }
                fAFQ = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values= fAFQ;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            fAFQ = (ArrayList<User>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
