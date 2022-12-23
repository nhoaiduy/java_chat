package com.example.chat.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chat.MainActivity;
import com.example.chat.R;
import com.example.chat.SignInActivity;
import com.example.chat.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    ImageView imgAva;
    EditText edtFullName, edtPhone, edtDOB, edtMail;
    RadioGroup rgp_gender;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth fAuth;
    DatePickerDialog picker;

    Button btnUpdate, btnChangePassword, btnLogOut;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgAva=view.findViewById(R.id.img_avatar);

        edtFullName=view.findViewById(R.id.edt_fullname);
        edtDOB=view.findViewById(R.id.edt_dob);
        edtMail=view.findViewById(R.id.edt_mail);
        edtPhone=view.findViewById(R.id.edt_phone);

        rgp_gender = view.findViewById(R.id.rgp_gender);

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogOut=view.findViewById(R.id.btnLogOut);

        imgAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                edtDOB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                edtFullName.setText(user.getFullName());
                edtDOB.setText(user.getDob());
                edtMail.setText(user.getEmail());
                edtPhone.setText(user.getPhone());

                if(user.getGender().equals("Male")){
                    rgp_gender.check(R.id.rdb_male);
                }else {
                    rgp_gender.check(R.id.rdb_female);
                }
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child("users/"+FirebaseAuth.getInstance().getUid()+"/avatar");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgAva);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.change_password, null);
                final EditText etPassword = (EditText) alertLayout.findViewById(R.id.tvPassword);
                final EditText etConfirmPassword = (EditText) alertLayout.findViewById(R.id.tvConfirmPassword);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Change Password");
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
                        if(etConfirmPassword.getText().toString().equals(etPassword.getText().toString())){{
                            firebaseUser.updatePassword(etPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }}else {
                            Toast.makeText(getContext(), "Vui lòng nhâp lại khóp mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });

        fAuth = FirebaseAuth.getInstance();
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.status("offline");
                Intent intent = new Intent(getContext(), SignInActivity.class);
                fAuth.signOut();
                startActivity(intent);

            }
        });
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
    }
    public void update() {
        String name, phone, dob;
        String gender;

        name = edtFullName.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();
        dob = edtDOB.getText().toString().trim();

        if(rgp_gender.getCheckedRadioButtonId()== R.id.rdb_male){
            gender = "Male";
        }else {
            gender = "Female";
        }

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
            Map<String, Object> user = new HashMap<>();
            user.put("fullName", name);
            user.put("phone", phone);
            user.put("dob",dob);
            user.put("gender",gender);
            reference.updateChildren(user);
            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        String userID = fAuth.getCurrentUser().getUid();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("users/"+userID+"/avatar");

        final String[] img = {"ic_android+_group.png"};
        if(filePath!=null){
            ref.putFile(filePath);
            img[0] = "avatar";
        }

        Map<String,Object> user = new HashMap<>();
        user.put("image", img[0]);
        reference.updateChildren(user);
        Picasso picasso=Picasso.get();
        picasso.invalidate(img[0]);

    }
}