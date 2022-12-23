package com.example.chat.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.MessageActivity;
import com.example.chat.R;
import com.example.chat.adapter.FriendAdapter;
import com.example.chat.model.Conversation;
import com.example.chat.model.Friend;
import com.example.chat.model.Participant;
import com.example.chat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFriendFragment extends Fragment implements FriendAdapter.OnFriendItemClickListener {
    RecyclerView rvFriend;
    FriendAdapter friendAdapter;
    ArrayList<Friend> arrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String type = "0";
    FirebaseUser user;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListFriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFriendFragment newInstance(String param1, String param2) {
        ListFriendFragment fragment = new ListFriendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        arrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rvFriend = view.findViewById(R.id.rvFriend);
        friendAdapter = new FriendAdapter(arrayList, this, 1);
        rvFriend.setAdapter(friendAdapter);
        rvFriend.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvFriend.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
    }
    @Override
    public void onResume() {
        super.onResume();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        reference.child("users").child(user.getUid()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Friend friend = snap.getValue(Friend.class);
                    arrayList.add(friend);
                }
                friendAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    int i=0;
    @Override
    public void onFriendItemClick(Friend friend) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Conversation conversation = dataSnapshot.getValue(Conversation.class);
                    boolean cdn1 = conversation.getParticipants().get(0).getUID().equals(user.getUid()) && conversation.getParticipants().get(1).getUID().equals(friend.getUid());
                    boolean cdn2 = conversation.getParticipants().get(1).getUID().equals(user.getUid()) && conversation.getParticipants().get(0).getUID().equals(friend.getUid());
                    if(conversation.getType().equals("0") && (cdn1 || cdn2)){
                        i = 1;
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                        reference1.child("conversations").child(conversation.getCid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Conversation conversation1 = snapshot.getValue(Conversation.class);
                                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                for(int i=0; i<conversation1.getParticipants().size();i++){
                                    if(userID.equals(conversation1.getParticipants().get(i).getUID())){
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("delete", false);
                                        reference1.child("conversations").child(conversation.getCid()).child("participants").child(i+"").updateChildren(map);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("conversation", conversation);
                        startActivity(intent);
                        //finish();
                    }
                }
                if(i==0){
                    reference = FirebaseDatabase.getInstance().getReference();
                    String cid = UUID.randomUUID().toString();
                    ArrayList<Participant> participants = new ArrayList<>();
                    reference.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                User user1 = dataSnapshot.getValue(User.class);
                                String uid = dataSnapshot.getKey();
                                if(uid.equals(user.getUid())){
                                    participants.add(new Participant(user1.getFullName(), user.getUid(), false));
                                    participants.add(new Participant(friend.getFullName(), friend.getUid(), false));
                                    Log.d("ABC", participants.size()+" "+participants.get(0).getNickname());
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("cid", cid);
                                    map.put("participants", participants);
                                    map.put("type", type);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("conversations").child(cid).setValue(map);
                                    databaseReference.child("conversations").child(cid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Conversation conversation = snapshot.getValue(Conversation.class);
                                            Intent intent = new Intent(getContext(), MessageActivity.class);
                                            intent.putExtra("conversation", conversation);
                                            startActivity(intent);
                                            //finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onLongItemClick(Friend friend) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.remove_message, null);
        final TextView textView = (TextView) alertLayout.findViewById(R.id.textView5);
        textView.setText("Bạn có muốn hủy kết bạn với người dùng này?");
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Hủy kết bạn");
        alert.setView(alertLayout);
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(user.getUid()).child("friends").child(friend.getOrder()).removeValue();
                reference.child("users").child(friend.getUid()).child("friends").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Friend friend1 = dataSnapshot.getValue(Friend.class);
                            if(friend1.getUid().equals(user.getUid())){
                                reference.child("users").child(friend.getUid()).child("friends").child(friend1.getOrder()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                friendAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}