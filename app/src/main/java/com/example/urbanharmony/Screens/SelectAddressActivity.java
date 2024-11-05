package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.urbanharmony.Models.AddressModel;
import com.example.urbanharmony.Models.ShippingModel;
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SelectAddressActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notfoundContainer;
    ArrayList<AddressModel> datalist = new ArrayList<>();
    static String selectedAddressId;
    LinearLayout applyBtn;
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

        newAddressBtn = findViewById(R.id.newAddressBtn);
        applyBtn = findViewById(R.id.applyBtn);
        listView = findViewById(R.id.listView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);

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
                selectAddress();
            }
        });

        fetchData("");
    }

    public void fetchData(String data){
        MainActivity.db.child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(UID.equals(ds.child("UID").getValue().toString().trim())){
                            AddressModel model = new AddressModel(ds.getKey(),
                                    ds.child("UID").getValue().toString().trim(),
                                    ds.child("name").getValue().toString().trim(),
                                    ds.child("address").getValue().toString().trim(),
                                    ds.child("defaultStatus").getValue().toString().trim()
                            );
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        MyAdapter adapter = new MyAdapter(SelectAddressActivity.this,datalist);
                        listView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    loader.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setRadioBtn(RadioButton radioBtn, String addressIds){
        selectedRadioButton = radioBtn;
        selectedAddressId = addressIds;
    }

    public void selectAddress(){
        if(selectedRadioButton == null){
            Dialog dialog = new Dialog(SelectAddressActivity.this);
            dialog.setContentView(R.layout.dialog_error);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView msg = dialog.findViewById(R.id.msgDialog);
            msg.setText("Please Select Your Shipping Address!!!");
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            },3000);
        } else {
            if(MainActivity.connectionCheck(SelectAddressActivity.this)){
                MainActivity.db.child("Users").child(UID).child("address").setValue(selectedAddressId);
                SelectAddressActivity.super.onBackPressed();
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<AddressModel> data;

        public MyAdapter(Context context, ArrayList<AddressModel> data) {
            this.context = context;
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View customListItem = LayoutInflater.from(context).inflate(R.layout.selected_address_custom_listview,null);
            TextView addressName, addressDetail, defaultStatus;
            RadioButton radioBtn;
            LinearLayout itemContainer;

            itemContainer = customListItem.findViewById(R.id.itemContainer);
            addressName = customListItem.findViewById(R.id.addressName);
            addressDetail = customListItem.findViewById(R.id.addressDetail);
            defaultStatus = customListItem.findViewById(R.id.defaultStatus);
            radioBtn = customListItem.findViewById(R.id.radioBtn);

            addressName.setText(data.get(i).getName());
            addressDetail.setText(data.get(i).getAddress());
            if(data.get(i).getDefaultStatus().equals("true")){
                defaultStatus.setVisibility(View.VISIBLE);
            }

            MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                    if(dsnapshot.exists()){
                        if(!dsnapshot.child("address").getValue().toString().trim().equals("")){
                            if(data.get(i).getId().equals(dsnapshot.child("address").getValue().toString().trim())){
                                if (selectedRadioButton != null) {
                                    selectedRadioButton.setChecked(false);
                                }
                                radioBtn.setChecked(true);
                                setRadioBtn(radioBtn, data.get(i).getId());
                            }
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
                    setRadioBtn(radioBtn, data.get(i).getId());
                }
            });
            radioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedRadioButton != null) {
                        selectedRadioButton.setChecked(false);
                    }
                    radioBtn.setChecked(true);
                    setRadioBtn(radioBtn, data.get(i).getId());
                }
            });


            if(i==data.size()-1){
                customListItem.setPadding(customListItem.getPaddingLeft(), customListItem.getPaddingTop(),customListItem.getPaddingRight(), 30);
            }
            if(i==0){
                customListItem.setPadding(customListItem.getPaddingLeft(), 0,customListItem.getPaddingRight(), 0);
            }
            customListItem.setAlpha(0f);
            customListItem.animate().alpha(1f).setDuration(200).setStartDelay(i * 2).start();
            return customListItem;
        }
    }

}