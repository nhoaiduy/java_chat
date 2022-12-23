package com.example.chat.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationVH> implements Filterable {


    public interface OnConversationItemClickListener{
        void onConversationItemClick(Conversation conversation);
    }
    public interface OnConversationLongClick{
        void onConversationLongClick(Conversation conversation);
    }

    class ConversationVH extends RecyclerView.ViewHolder{
        TextView tvName;
        CircleImageView image;
        public ConversationVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.imgAvatar);
        }
    }
    public List<Conversation> mConversations;
    public List<Conversation> fConversations;
    public OnConversationItemClickListener mListener;
    public OnConversationLongClick longListener;

    public ConversationAdapter(List<Conversation> mConversations, OnConversationItemClickListener mListener, OnConversationLongClick longListener){
        this.mConversations = mConversations;
        this.mListener = mListener;
        this.longListener = longListener;
        this.fConversations = mConversations;
    }
    @NonNull
    @Override
    public ConversationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_conversation,parent,false);
        return new ConversationVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationVH holder, int position) {
        Conversation conversation = fConversations.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        if (conversation.getType().equals("1")) {
            StorageReference profileRef = storageReference.child("conversations/"+ conversation.getCid()+"/avatar");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.image);
                }
            });
        }else {
            String uid = " ";
            for (Participant participant: conversation.getParticipants()){
                if(!participant.getUID().equals(FirebaseAuth.getInstance().getUid())){
                    uid = participant.getUID();
                    break;
                }
            }
            StorageReference profileRef = storageReference.child("users/"+ uid+"/avatar");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.image);
                }
            });
        }

        if (conversation.getType().equals("1")){
            holder.tvName.setText(conversation.getCName());
        }
        else {
            holder.tvName.setText(conversation.getParticipants().get(1).getNickname());
            if (!conversation.getParticipants().get(0).getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                holder.tvName.setText(conversation.getParticipants().get(0).getNickname());
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onConversationItemClick(conversation);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longListener.onConversationLongClick(conversation);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return fConversations.size();
    }

    @Override
    public Filter getFilter() {
        return new ConversationFilter();
    }

    class ConversationFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString();
            if(charString.isEmpty()){
                fConversations = mConversations;
            }else {
                ArrayList<Conversation> filteredList = new ArrayList<>();
                for (Conversation conversation : mConversations){
                    if(conversation.getType().equals("1")){
                        if(conversation.getCName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(conversation);
                            Log.d("ABC", conversation.getType());
                        }
                    }
                    else{
                        for (Participant participant: conversation.getParticipants()){
                            if(participant.getNickname().toLowerCase().contains(charString.toLowerCase())){
                                filteredList.add(conversation);
                                Log.d("ABC", conversation.getType());
                                break;
                            }
                        }
                    }
                }
                fConversations = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values= fConversations;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            fConversations = (ArrayList<Conversation>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
