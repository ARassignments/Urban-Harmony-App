package com.example.urbanharmony.Screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SelectAddressActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    String selectedAddressId;
    LinearLayout addressContainer, applyBtn;
    Button newAddressBtn;
    RadioButton selectedRadioButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_address);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        addressContainer = findViewById(R.id.addressContainer);
        newAddressBtn = findViewById(R.id.newAddressBtn);
        applyBtn = findViewById(R.id.applyBtn);

        MainActivity.db.child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int addressCount = 0;
                View addressItemLayout = LayoutInflater.from(SelectAddressActivity.this).inflate(R.layout.selected_address_custom_listview,null);
                addressContainer.removeAllViews();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("UID").getValue().toString().trim().equals(UID)){
                        TextView addressName, addressDetail, defaultStatus;
                        RadioButton radioBtn;
                        LinearLayout itemContainer;
                        addressName = addressItemLayout.findViewById(R.id.addressName);
                        addressDetail = addressItemLayout.findViewById(R.id.addressDetail);
                        defaultStatus = addressItemLayout.findViewById(R.id.defaultStatus);
                        radioBtn = addressItemLayout.findViewById(R.id.radioBtn);
                        itemContainer = addressItemLayout.findViewById(R.id.itemContainer);
                        addressName.setText(ds.child("name").getValue().toString().trim());
                        addressDetail.setText(ds.child("address").getValue().toString().trim());
                        if(ds.child("defaultStatus").getValue().toString().trim().equals("true")){
                            defaultStatus.setVisibility(View.VISIBLE);
                        }
                        radioBtn.setVisibility(View.VISIBLE);
                        if(addressCount == 0){
                            MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                    if(dsnapshot.child("address").getValue().toString().trim().equals("")){
                                        radioBtn.setChecked(true);
                                        setRadioBtn(radioBtn, ds.getKey());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        addressCount++;
                        MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                if(!dsnapshot.child("address").getValue().toString().trim().equals("")){
                                    if(dsnapshot.child("address").getValue().toString().trim().equals(ds.getKey())){
                                        if (selectedRadioButton != null) {
                                            selectedRadioButton.setChecked(false);
                                        }
                                        radioBtn.setChecked(true);
                                        setRadioBtn(radioBtn, ds.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        itemContainer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedRadioButton != null) {
                                    selectedRadioButton.setChecked(false);
                                }
                                radioBtn.setChecked(true);
                                setRadioBtn(radioBtn, ds.getKey());
                            }
                        });
                        radioBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedRadioButton != null) {
                                    selectedRadioButton.setChecked(false);
                                }
                                radioBtn.setChecked(true);
                                setRadioBtn(radioBtn, ds.getKey());
                            }
                        });
                        addressContainer.addView(addressItemLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectAddressActivity.this, AddressActivity.class));
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectAddressActivity.super.onBackPressed();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.connectionCheck(SelectAddressActivity.this)){
                    MainActivity.db.child("Users").child(UID).child("address").setValue(selectedAddressId);
                    SelectAddressActivity.super.onBackPressed();
                }
            }
        });
    }
    public void setRadioBtn(RadioButton radioBtn, String addressIds){
        selectedRadioButton = radioBtn;
        selectedAddressId = addressIds;
    }
}