package com.example.chat.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat.MediaActivity;
import com.example.chat.R;
import com.example.chat.SearchMessageActivity;
import com.example.chat.ViewUserInfoActivity;
import com.example.chat.ViewUserInfoDialog;
import com.example.chat.model.Conversation;
import com.example.chat.model.Participant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConversationInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversationInfoFragment extends Fragment {

    TextView tvName, tvUserInfo, tvMedia, tvMessageSearch;
    ImageView imgAva;
    Conversation conversation;
    String uid;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConversationInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversationInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversationInfoFragment newInstance(String param1, String param2) {
        ConversationInfoFragment fragment = new ConversationInfoFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.conName);
        imgAva = view.findViewById(R.id.conImage);
        tvUserInfo = view.findViewById(R.id.user_info);
        tvMedia = view.findViewById(R.id.mediaList);
        tvMessageSearch = view.findViewById(R.id.messageSearch);

        conversation = (Conversation) getActivity().getIntent().getSerializableExtra("conversation");
        boolean flag = true;
        if (conversation.getParticipants().get(0).getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            flag = false;
        }
        if (flag==false){
            Participant participant = conversation.getParticipants().get(1);
            tvName.setText(participant.getNickname());
            uid=conversation.getParticipants().get(1).getUID();
        }
        else{
            Participant participant = conversation.getParticipants().get(0);
            tvName.setText(participant.getNickname());
            uid=conversation.getParticipants().get(0).getUID();
        }


        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+uid+"/avatar");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgAva);
            }
        });

        tvMessageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchMessageActivity.class);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }
        });

        tvUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ViewUserInfoActivity.class);
                intent.putExtra("participants", conversation.getParticipants());
                startActivity(intent);
            }
        });
        tvMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MediaActivity.class);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }
        });

    }


}