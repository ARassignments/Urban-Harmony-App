package com.example.urbanharmony.Screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class OrderDetailActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String OID, OrderId, OItems, OTotalItemAmount, OTotalShippingAmount, OTotalOverallAmount, OCreatedOn, OLocationAddress, OPaymentMethod;
    LinearLayout orderList;
    TextView totalAmount, shippingAmount, grandTotalAmount, orderIdHead, paymentMethod, orderDate, orderId, shippingAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            OID = extra.getString("OID");
            OrderId = extra.getString("OrderId");
            OItems = extra.getString("OItems");
            OTotalItemAmount = extra.getString("OTotalItemAmount");
            OTotalShippingAmount = extra.getString("OTotalShippingAmount");
            OTotalOverallAmount = extra.getString("OTotalOverallAmount");
            OCreatedOn = extra.getString("OCreatedOn");
            OLocationAddress = extra.getString("OLocationAddress");
            OPaymentMethod = extra.getString("OPaymentMethod");
        }

        orderList = findViewById(R.id.orderList);
        orderId = findViewById(R.id.orderId);
        totalAmount = findViewById(R.id.totalAmount);
        shippingAmount = findViewById(R.id.shippingAmount);
        grandTotalAmount = findViewById(R.id.grandTotalAmount);
        orderIdHead = findViewById(R.id.orderIdHead);
        paymentMethod = findViewById(R.id.paymentMethod);
        orderDate = findViewById(R.id.orderDate);
        shippingAddress = findViewById(R.id.shippingAddress);

        orderId.setText("#"+OrderId.toUpperCase());
        orderIdHead.setText("#"+OrderId.toUpperCase());
        totalAmount.setText("$"+OTotalItemAmount);
        shippingAmount.setText("$"+OTotalShippingAmount);
        grandTotalAmount.setText("$"+OTotalOverallAmount);
        paymentMethod.setText(OPaymentMethod);
        orderDate.setText(OCreatedOn);
        shippingAddress.setText(OLocationAddress);

        MainActivity.db.child("Orders").child(OID).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
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

                                View itemView = getLayoutInflater().inflate(R.layout.order_items_custom_listview,null);
                                ImageView pImage;
                                TextView pName, pQtyTextView, pPriceTextView, pPriceOffTextView, pDiscountTextView;
                                pImage = itemView.findViewById(R.id.pImage);
                                pName = itemView.findViewById(R.id.pName);
                                pPriceTextView = itemView.findViewById(R.id.pPrice);
                                pPriceOffTextView = itemView.findViewById(R.id.pPriceOff);
                                pQtyTextView = itemView.findViewById(R.id.pQty);
                                pDiscountTextView = itemView.findViewById(R.id.pDiscount);

                                pQtyTextView.setText("Qty = "+pQty);
                                Glide.with(OrderDetailActivity.this).load(datasnapshot.child("pImage").getValue().toString().trim()).into(pImage);
                                pName.setText(datasnapshot.child("pName").getValue().toString().trim());
                                pPriceOffTextView.setText("$"+(pPrice*pQty));
                                if(pDiscount > 0){
                                    pPriceOffTextView.setVisibility(View.VISIBLE);
                                    pDiscountTextView.setVisibility(View.VISIBLE);
                                    pDiscountTextView.setText(pDiscount+"% OFF");
                                }
                                pPriceTextView.setText("$"+(Math.round(totalPrice)*pQty));
                                orderList.addView(itemView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderDetailActivity.super.onBackPressed();
            }
        });
    }
}