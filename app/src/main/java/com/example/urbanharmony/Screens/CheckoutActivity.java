package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.AddToCartModel;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    ImageView locationEditBtn, shippingEditBtn;
    TextView totalAmount, shippingAmount, grandTotalAmount, locationName, locationAddress, shippingName, shippingDetail, shippingPrice;
    LinearLayout orderList, btnOrder, chooseAddressBtn, locationContainer, chooseShippingBtn, shippingContainer;
    static int grandTotal = 0;
    static int grandTotalShipping = 0;
    static int grandOverallTotal = 0;
    static boolean shippingAddress = false;
    static boolean shippingType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        locationContainer = findViewById(R.id.locationContainer);
        chooseAddressBtn = findViewById(R.id.chooseAddressBtn);
        locationName = findViewById(R.id.locationName);
        locationAddress = findViewById(R.id.locationAddress);
        locationEditBtn = findViewById(R.id.locationEditBtn);
        totalAmount = findViewById(R.id.totalAmount);
        shippingAmount = findViewById(R.id.shippingAmount);
        grandTotalAmount = findViewById(R.id.grandTotalAmount);
        orderList = findViewById(R.id.orderList);
        btnOrder = findViewById(R.id.btnOrder);
        chooseShippingBtn = findViewById(R.id.chooseShippingBtn);
        shippingContainer = findViewById(R.id.shippingContainer);
        shippingName = findViewById(R.id.shippingName);
        shippingDetail = findViewById(R.id.shippingDetail);
        shippingPrice = findViewById(R.id.shippingPrice);
        shippingEditBtn = findViewById(R.id.shippingEditBtn);

        locationEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckoutActivity.this, SelectAddressActivity.class));
            }
        });

        chooseAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckoutActivity.this, SelectAddressActivity.class));
            }
        });

        chooseShippingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckoutActivity.this, SelectShippingActivity.class));
            }
        });

        shippingEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckoutActivity.this, SelectShippingActivity.class));
            }
        });

        MainActivity.db.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String AddressIdFromUser = snapshot.child("address").getValue().toString().trim();
                    if(AddressIdFromUser.equals("")){
                        MainActivity.db.child("Address").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    int addressCount = 0;
                                    for(DataSnapshot ds: snapshot.getChildren()){
                                        if(ds.child("UID").getValue().toString().trim().equals(UID)){
                                            if(addressCount == 0){
                                                locationContainer.setVisibility(View.VISIBLE);
                                                chooseAddressBtn.setVisibility(View.GONE);
                                                shippingAddress = true;
                                                locationName.setText(ds.child("name").getValue().toString().trim());
                                                locationAddress.setText(ds.child("address").getValue().toString().trim());
                                            }
                                            addressCount++;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        MainActivity.db.child("Address").child(AddressIdFromUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    locationContainer.setVisibility(View.VISIBLE);
                                    chooseAddressBtn.setVisibility(View.GONE);
                                    shippingAddress = true;
                                    locationName.setText(datasnapshot.child("name").getValue().toString().trim());
                                    locationAddress.setText(datasnapshot.child("address").getValue().toString().trim());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if(!snapshot.child("shipping").getValue().toString().trim().equals("")){
                        MainActivity.db.child("Shippings").child(snapshot.child("shipping").getValue().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    shippingContainer.setVisibility(View.VISIBLE);
                                    chooseShippingBtn.setVisibility(View.GONE);
                                    shippingType = true;
                                    shippingName.setText(datasnapshot.child("name").getValue().toString().trim());
                                    shippingDetail.setText(datasnapshot.child("detail").getValue().toString().trim());
                                    shippingPrice.setText("$"+datasnapshot.child("price").getValue().toString().trim());
                                    grandTotalShipping += Integer.parseInt(datasnapshot.child("price").getValue().toString().trim());
                                    shippingAmount.setText("$"+grandTotalShipping);
                                }
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

        MainActivity.db.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setGrandTotal(0,false);
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(UID.equals(ds.child("UID").getValue().toString().trim())){
                        MainActivity.db.child("Products").child(ds.child("PID").getValue().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    int pQty = Integer.parseInt(ds.child("qty").getValue().toString().trim());
                                    int pPrice = Integer.parseInt(datasnapshot.child("pPrice").getValue().toString().trim());
                                    int pStock = Integer.parseInt(datasnapshot.child("pStock").getValue().toString().trim());
                                    int pDiscount = Integer.parseInt(datasnapshot.child("pDiscount").getValue().toString().trim());

                                    double discount = Double.parseDouble(datasnapshot.child("pDiscount").getValue().toString())/100;
                                    double calcDiscount = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) * discount;
                                    double totalPrice = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;

                                    View itemView = getLayoutInflater().inflate(R.layout.checkout_custom_listview,null);
                                    ImageView pImage;
                                    TextView pName, pQtyTextView, pPriceTextView, pPriceOffTextView, pDiscountTextView;
                                    pImage = itemView.findViewById(R.id.pImage);
                                    pName = itemView.findViewById(R.id.pName);
                                    pPriceTextView = itemView.findViewById(R.id.pPrice);
                                    pPriceOffTextView = itemView.findViewById(R.id.pPriceOff);
                                    pQtyTextView = itemView.findViewById(R.id.pQty);
                                    pDiscountTextView = itemView.findViewById(R.id.pDiscount);

                                    pQtyTextView.setText(""+pQty);
                                    Glide.with(CheckoutActivity.this).load(datasnapshot.child("pImage").getValue().toString().trim()).into(pImage);
                                    pName.setText(datasnapshot.child("pName").getValue().toString().trim());
                                    pPriceOffTextView.setText("$"+(pPrice*pQty));
                                    if(pDiscount > 0){
                                        pPriceOffTextView.setVisibility(View.VISIBLE);
                                        pDiscountTextView.setVisibility(View.VISIBLE);
                                        pDiscountTextView.setText(pDiscount+"% OFF");
                                    }
                                    pPriceTextView.setText("$"+(Math.round(totalPrice)*pQty));
                                    setGrandTotal((int) Math.round(totalPrice)*pQty,true);
                                    orderList.addView(itemView);
                                }
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

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forwardToPayment();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutActivity.super.onBackPressed();
            }
        });
    }

    public void setGrandTotal(int itemPrice,boolean status){
        if(status == true){
            grandTotal += itemPrice;
            totalAmount.setText("$"+grandTotal);
        } else {
            grandTotal = 0;
        }
        setOverallGrandTotal();
    }

    public void setOverallGrandTotal(){
        grandOverallTotal = grandTotal+grandTotalShipping;
        grandTotalAmount.setText("$"+grandOverallTotal);
    }

    public static String generateOrderID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "").substring(0, 10);
    }

    public void forwardToPayment(){
        if(shippingAddress == false && shippingType == false){
            Dialog dialog = new Dialog(CheckoutActivity.this);
            dialog.setContentView(R.layout.dialog_error);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView msg = dialog.findViewById(R.id.msgDialog);
            msg.setText("Please Select Your Shipping Address & Shipping Type!!!");
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            },3000);
        } else {
            if(shippingAddress == false){
                Dialog dialog = new Dialog(CheckoutActivity.this);
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
            } else if(shippingType == false){
                Dialog dialog = new Dialog(CheckoutActivity.this);
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
            } else if(shippingAddress == true && shippingType == true) {
                Intent intent = new Intent(CheckoutActivity.this,SelectPaymentMethodActivity.class);
                intent.putExtra("totalItemAmount",""+grandTotal);
                intent.putExtra("totalShippingAmount",""+grandTotalShipping);
                intent.putExtra("totalOverallAmount",""+grandOverallTotal);
                intent.putExtra("shippingName",""+shippingName.getText().toString().trim());
                intent.putExtra("shippingDetail",""+shippingDetail.getText().toString().trim());
                intent.putExtra("locationName",""+locationName.getText().toString().trim());
                intent.putExtra("locationAddress",""+locationAddress.getText().toString().trim());
                startActivity(intent);
            }
        }
    }
}