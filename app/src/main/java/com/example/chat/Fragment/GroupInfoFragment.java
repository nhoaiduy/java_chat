package com.example.chat.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.MediaActivity;
import com.example.chat.R;
import com.example.chat.SearchMessageActivity;
import com.example.chat.ViewMembersActivity;
import com.example.chat.model.Conversation;
import com.example.chat.model.Participant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupInfoFragment extends Fragment {

    TextView tvName, tvMember, tvMedia, tvMessageSearch, tvLeave, tvDelete;;
    ImageView imgAva;
    Conversation conversation;
    DatabaseReference reference;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupInfoFragment newInstance(String param1, String param2) {
        GroupInfoFragment fragment = new GroupInfoFragment();
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
        return inflater.inflate(R.layout.fragment_group_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.groupName);
        imgAva = view.findViewById(R.id.groupImage);
        tvMedia = view.findViewById(R.id.mediaList);
        tvMessageSearch = view.findViewById(R.id.messageSearch);
        tvMember = view.findViewById(R.id.members);
        tvLeave = view.findViewById(R.id.leaveGroup);
        tvDelete = view.findViewById(R.id.groupDelete);
        conversation = (Conversation) getActivity().getIntent().getSerializableExtra("conversation");
        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(conversation.getCAdmin())){
            tvDelete.setVisibility(View.INVISIBLE);
        }
        tvName.setText(conversation.getCName());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("conversations/"+conversation.getCid()+"/"+ conversation.getImage());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgAva);
            }
        });
        imgAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();

            }
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRenameDialog();
            }
        });

        tvLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveGroup();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteGroup();
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
        tvMessageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchMessageActivity.class);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }
        });
        tvMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ViewMembersActivity.class);
                intent.putExtra("participants", conversation.getParticipants());
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }
        });
    }

    public void displayRenameDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.rename_dialog, null);
        final EditText etname = (EditText) alertLayout.findViewById(R.id.tvName);
        etname.setText(tvName.getText().toString());
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Rename");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference().child("conversations").child(conversation.getCid());
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(etname.getText().toString())){
                    Map<String, Object> map = new HashMap();
                    map.put("cname", etname.getText().toString());
                    reference.updateChildren(map);
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    tvName.setText(etname.getText().toString());
                }else {
                    Toast.makeText(getContext(), "Điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void leaveGroup(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.remove_message, null);
        final TextView textView = (TextView) alertLayout.findViewById(R.id.textView5);
        textView.setText("Bạn muốn rời nhóm?");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Rời nhóm?");
        alertDialogBuilder.setView(alertLayout);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        reference = FirebaseDatabase.getInstance().getReference().child("conversations").child(conversation.getCid());
                        ArrayList<Participant> participants = conversation.getParticipants();
                        for(int i = 0;i< participants.size();i++){
                            if (participants.get(i).getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                if (conversation.getCAdmin().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    conversation.setCAdmin(participants.get(0).getUID());
                                }
                                participants.remove(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("participants", participants);
                                map.put("cadmin", conversation.getCAdmin());
                                reference.updateChildren(map);
                                Intent intent = new Intent(getContext(), ConversationFragment.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();



    }

    public  void deleteGroup(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.remove_message, null);
        final TextView textView = (TextView) alertLayout.findViewById(R.id.textView5);
        textView.setText("Bạn muốn xóa nhóm?");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Xóa nhóm");
        alertDialogBuilder.setView(alertLayout);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        reference = FirebaseDatabase.getInstance().getReference().child("conversations").child(conversation.getCid());
                        reference.removeValue();
                        getActivity().finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imgAva.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        update();

    }
    public void update(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.remove_message, null);
        final TextView textView = (TextView) alertLayout.findViewById(R.id.textView5);
        textView.setText("Bạn muốn thay đổi ảnh nhóm?");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Đổi ảnh nhóm");
        alertDialogBuilder.setView(alertLayout);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String cid=conversation.getCid();
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("conversations/"+cid+"/avatar");

                        final String[] img = {"ic_android+_group.png"};
                        if(filePath!=null){
                            ref.putFile(filePath);
                            img[0] = "avatar";
                            Map<String,Object> con = new HashMap<>();
                            con.put("image", img[0]);
                            reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("conversations").child(cid).updateChildren(con);
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}