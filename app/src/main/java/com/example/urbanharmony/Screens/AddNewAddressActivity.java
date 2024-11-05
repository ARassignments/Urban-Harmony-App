package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewAddressActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    TextInputLayout nameLayout, addressLayout;
    TextInputEditText nameInput, addressInput;
    CheckBox defaultAddressCheckBox;
    Button addBtn;
    CircleImageView profileimage;
    TextView navTitle;
    String forEdit, addressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_address);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if(!extra.isEmpty()){
            forEdit = extra.getString("edit");
            addressId = extra.getString("addressId");
        }

        nameLayout = findViewById(R.id.nameLayout);
        nameInput = findViewById(R.id.nameInput);
        addressLayout = findViewById(R.id.addressLayout);
        addressInput = findViewById(R.id.addressInput);
        defaultAddressCheckBox = findViewById(R.id.defaultAddressCheckBox);
        addBtn = findViewById(R.id.addBtn);
        profileimage = findViewById(R.id.profileimage);
        navTitle = findViewById(R.id.navTitle);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addressValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
        MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!snapshot.child("image").getValue().toString().equals("")){
                        Glide.with(AddNewAddressActivity.this).load(snapshot.child("image").getValue().toString()).into(profileimage);
//                        profileimage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(forEdit.equals("true")) {
            navTitle.setText("Edit Address");
            addBtn.setText("Edit");
            MainActivity.db.child("Address").child(addressId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nameInput.setText(snapshot.child("name").getValue().toString().trim());
                    addressInput.setText(snapshot.child("address").getValue().toString().trim());
                    if (snapshot.child("defaultStatus").getValue().toString().trim().equals("true")) {
                        defaultAddressCheckBox.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewAddressActivity.super.onBackPressed();
            }
        });
    }

    public boolean nameValidation(){
        String input = nameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            nameLayout.setError("Address Name is Required!!!");
            return false;
        } else if(input.length() < 3){
            nameLayout.setError("Address Name at least 3 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            nameLayout.setError("Only text allowed!!!");
            return false;
        } else {
            nameLayout.setError(null);
            return true;
        }
    }

    public boolean addressValidation(){
        String input = addressInput.getText().toString().trim();
        String regex = "^[a-zA-Z0-9.,'/\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            addressLayout.setError("Address Detail is Required!!!");
            return false;
        } else if(input.length() < 20){
            addressLayout.setError("Address Detail at least 20 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            addressLayout.setError("Only text allowed!!!");
            return false;
        } else {
            addressLayout.setError(null);
            return true;
        }
    }

    private void validation() {
        boolean nameErr = false, addressErr = false;
        nameErr = nameValidation();
        addressErr = addressValidation();

        if((nameErr && addressErr) == true){
            if(forEdit.equals("true")){
                MainActivity.db.child("Address").child(addressId).child("name").setValue(nameInput.getText().toString().trim());
                MainActivity.db.child("Address").child(addressId).child("address").setValue(addressInput.getText().toString().trim());
                if(defaultAddressCheckBox.isChecked()){
                    MainActivity.db.child("Address").child(addressId).child("defaultStatus").setValue("true");
                    MainActivity.db.child("Users").child(UID).child("address").setValue(addressId);
                } else {
                    MainActivity.db.child("Address").child(addressId).child("defaultStatus").setValue("");
                    MainActivity.db.child("Users").child(UID).child("address").setValue("");
                }
                Dialog loaddialog = new Dialog(AddNewAddressActivity.this);
                loaddialog.setContentView(R.layout.dialog_success);
                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                loaddialog.getWindow().setGravity(Gravity.CENTER);
                loaddialog.setCancelable(false);
                loaddialog.setCanceledOnTouchOutside(false);
                TextView message = loaddialog.findViewById(R.id.msgDialog);
                message.setText("Address Edited Successfully!!!");
                loaddialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loaddialog.dismiss();
                        AddNewAddressActivity.super.onBackPressed();
                    }
                },2000);
            } else {
                String uploadId = MainActivity.db.child("Address").push().getKey();
                HashMap<String,String> Obj = new HashMap<String,String>();
                Obj.put("name",nameInput.getText().toString().trim());
                Obj.put("address",addressInput.getText().toString().trim());
                Obj.put("UID",UID);
                Obj.put("defaultStatus","");
                if(defaultAddressCheckBox.isChecked()){
                    Obj.put("defaultStatus","true");
                    MainActivity.db.child("Users").child(UID).child("address").setValue(uploadId);
                }
                MainActivity.db.child("Address").child(uploadId).setValue(Obj);
                Dialog loaddialog = new Dialog(AddNewAddressActivity.this);
                loaddialog.setContentView(R.layout.dialog_success);
                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                loaddialog.getWindow().setGravity(Gravity.CENTER);
                loaddialog.setCancelable(false);
                loaddialog.setCanceledOnTouchOutside(false);
                TextView message = loaddialog.findViewById(R.id.msgDialog);
                message.setText("Address Added Successfully!!!");
                loaddialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loaddialog.dismiss();
                        AddNewAddressActivity.super.onBackPressed();
                    }
                },2000);
            }
        }
    }
}