package com.example.chat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    FirebaseAuth fAuth;
    ImageView img_back, img_ava;
    TextInputEditText tvFullName, tvPhone, tvDoB, tvEmail, tvPassword, tvConfirmPassword;
    RadioGroup rdg_gender;
    Button btn_signUp;
    DatePickerDialog picker;
    FirebaseDatabase fDatabase;
    String userID;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fAuth = FirebaseAuth.getInstance();

        img_back = findViewById(R.id.img_back);
        img_ava = findViewById(R.id.img_ava);

        tvFullName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);
        tvDoB = findViewById(R.id.tvDoB);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        tvConfirmPassword = findViewById(R.id.tvConfirmPassword);
        rdg_gender = findViewById(R.id.rdg_gender);

        btn_signUp = findViewById(R.id.btn_signUp);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        img_ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        tvDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                tvDoB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                Uri selectedImageUri = data.getData();
//                img_ava.setImageURI(selectedImageUri);
//            }
//        }
//    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img_ava.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void signUp(){
        fDatabase = FirebaseDatabase.getInstance();
        String fullName = tvFullName.getText().toString();
        String phone = tvPhone.getText().toString();
        String dob = tvDoB.getText().toString();
        String gender;
        if(rdg_gender.getCheckedRadioButtonId()== R.id.rd_male){
            gender = "Male";
        }else {
            gender = "Female";
        }

        final String[] img = {"ic_android+_group.png"};



        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();
        String confirmPassword = tvConfirmPassword.getText().toString();

        if(!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)  ){
            if(confirmPassword.equals(password)) {
                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    userID = fAuth.getCurrentUser().getUid();
                                    DatabaseReference databaseReference = fDatabase.getReference();
                                    StorageReference ref = FirebaseStorage.getInstance().getReference().child("users/"+userID+"/avatar");
                                    if(filePath!=null){
                                        ref.putFile(filePath);
                                    }

                                    img[0] = "avatar";
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("fullName",fullName);
                                    user.put("phone",phone);
                                    user.put("dob",dob);
                                    user.put("email",email);
                                    user.put("status", "offline");
                                    user.put("gender", gender);
                                    user.put("image", img[0]);
                                    user.put("uid", userID);
                                    databaseReference.child("users").child(userID).setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "OKE", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                    intent.putExtra("email", email);
                                                    setResult(Activity.RESULT_OK, intent);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(this, "Không khớp mật khẩu. Vui lòng nhập lại", Toast.LENGTH_SHORT).show();;
            }
        }else {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}