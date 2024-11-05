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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.ShippingModel;
import com.example.urbanharmony.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SelectShippingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "asc";
    ListView listView;
    LinearLayout loader, notfoundContainer;
    ArrayList<ShippingModel> datalist = new ArrayList<>();
    static String selectedShippingId;
    LinearLayout applyBtn;
    RadioButton selectedRadioButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_shipping);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        applyBtn = findViewById(R.id.applyBtn);
        listView = findViewById(R.id.listView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectShippingActivity.super.onBackPressed();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShipping();
            }
        });

        fetchData("");
    }

    public void fetchData(String data){
        MainActivity.db.child("Shippings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        ShippingModel model = new ShippingModel(ds.getKey(),
                                ds.child("name").getValue().toString(),
                                ds.child("detail").getValue().toString(),
                                ds.child("price").getValue().toString()
                        );
                        datalist.add(model);
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        MyAdapter adapter = new MyAdapter(SelectShippingActivity.this,datalist);
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

    public void setRadioBtn(RadioButton radioBtn, String shippingIds){
        selectedRadioButton = radioBtn;
        selectedShippingId = shippingIds;
    }

    public void selectShipping(){
        if(selectedRadioButton == null){
            Dialog dialog = new Dialog(SelectShippingActivity.this);
            dialog.setContentView(R.layout.dialog_error);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView msg = dialog.findViewById(R.id.msgDialog);
            msg.setText("Please Select Your Shipping Type!!!");
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            },3000);
        } else {
            if(MainActivity.connectionCheck(SelectShippingActivity.this)){
                MainActivity.db.child("Users").child(UID).child("shipping").setValue(selectedShippingId);
                SelectShippingActivity.super.onBackPressed();
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<ShippingModel> data;

        public MyAdapter(Context context, ArrayList<ShippingModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.selected_shipping_custom_listview,null);
            TextView shippingName, shippingDetail, shippingPrice;
            RadioButton radioBtn;
            LinearLayout itemContainer;

            itemContainer = customListItem.findViewById(R.id.itemContainer);
            shippingName = customListItem.findViewById(R.id.shippingName);
            shippingDetail = customListItem.findViewById(R.id.shippingDetail);
            shippingPrice = customListItem.findViewById(R.id.shippingPrice);
            radioBtn = customListItem.findViewById(R.id.radioBtn);

            shippingName.setText(data.get(i).getName());
            shippingDetail.setText(data.get(i).getDetail());
            shippingPrice.setText("$"+data.get(i).getPrice());

            MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                    if(dsnapshot.exists()){
                        if(!dsnapshot.child("shipping").getValue().toString().trim().equals("")){
                            if(data.get(i).getId().equals(dsnapshot.child("shipping").getValue().toString().trim())){
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