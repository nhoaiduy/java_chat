package com.example.chat.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.AddChatActivity;
import com.example.chat.MessageActivity;
import com.example.chat.R;
import com.example.chat.adapter.ConversationAdapter;
import com.example.chat.model.Conversation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConversationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversationFragment extends Fragment implements ConversationAdapter.OnConversationItemClickListener, ConversationAdapter.OnConversationLongClick {
    RecyclerView rvConversation;
    ConversationAdapter conversationAdapter;
    ArrayList<Conversation> arrayList;
    SearchView searchView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConversationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversationFragment newInstance(String param1, String param2) {
        ConversationFragment fragment = new ConversationFragment();
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
        setHasOptionsMenu(true);
        arrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvConversation = view.findViewById(R.id.rvConversation);
        conversationAdapter = new ConversationAdapter(arrayList, this, this) ;
        rvConversation.setAdapter(conversationAdapter);
        searchView = view.findViewById(R.id.svConversation);
        rvConversation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvConversation.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                conversationAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.option_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuAdd) {
            Intent intent = new Intent(getContext(), AddChatActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Conversation conversation = snap.getValue(Conversation.class);
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    for(int i = 0; i< conversation.getParticipants().size();i++){
                        if(conversation.getParticipants().get(i)!=null){
                            if (userID.equals(conversation.getParticipants().get(i).getUID()) && conversation.getParticipants().get(i).getDelete()==false){
                                arrayList.add(conversation);
                                break;
                            }
                        }
                    }
                }
                conversationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onConversationItemClick(Conversation conversation) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("conversation", conversation);
        startActivity(intent);
    }

    @Override
    public void onConversationLongClick(Conversation conversation) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.remove_conversation, null);
        final TextView textView = (TextView) alertLayout.findViewById(R.id.txtRemoveConversation);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Xóa cuộc trò chuyện?");
        alert.setView(alertLayout);
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("conversations").child(conversation.getCid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Conversation conversation1 = snapshot.getValue(Conversation.class);
                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        for(int i=0; i<conversation1.getParticipants().size();i++){
                            if(userID.equals(conversation1.getParticipants().get(i).getUID())){
                                Map<String, Object> map = new HashMap<>();
                                map.put("delete", true);
                                reference.child("conversations").child(conversation.getCid()).child("participants").child(i+"").updateChildren(map);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}