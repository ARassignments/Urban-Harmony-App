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

public class OrderTrackActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String OID, OrderId, OItems, OAmount, OStatus;
    ImageView pImage;
    TextView orderId, orderItems, orderAmount, statusContent;
    LinearLayout statusContainerOne, statusContainerTwo, statusContainerThree, statusContainerFour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_track);
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
            OAmount = extra.getString("OAmount");
            OStatus = extra.getString("OStatus");
        }

        pImage = findViewById(R.id.pImage);
        orderId = findViewById(R.id.orderId);
        orderItems = findViewById(R.id.orderItems);
        orderAmount = findViewById(R.id.orderAmount);
        statusContainerOne = findViewById(R.id.statusContainerOne);
        statusContainerTwo = findViewById(R.id.statusContainerTwo);
        statusContainerThree = findViewById(R.id.statusContainerThree);
        statusContainerFour = findViewById(R.id.statusContainerFour);
        statusContent = findViewById(R.id.statusContent);

        orderId.setText("#"+OrderId.toUpperCase());
        orderItems.setText(OItems);
        orderAmount.setText("$"+OAmount);

        if(OStatus.equals("1")){
            statusContainerOne.setVisibility(View.VISIBLE);
            statusContent.setText("Order In Process");
        } else if(OStatus.equals("2")){
            statusContainerTwo.setVisibility(View.VISIBLE);
            statusContent.setText("Order In Packaging");
        } else if(OStatus.equals("3")){
            statusContainerThree.setVisibility(View.VISIBLE);
            statusContent.setText("Order In Delivery");
        } else if(OStatus.equals("4")){
            statusContainerFour.setVisibility(View.VISIBLE);
            statusContent.setText("Order Delivered");
        } else if(OStatus.equals("5")){
            statusContent.setText("Order Cancelled");
        }

        MainActivity.db.child("Orders").child(OID).child("items").child("item_1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String PID = snapshot.child("PID").getValue().toString().trim();
                    MainActivity.db.child("Products").child(PID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            if(datasnapshot.exists()){
                                Glide.with(OrderTrackActivity.this).load(datasnapshot.child("pImage").getValue().toString().trim()).into(pImage);
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
                OrderTrackActivity.super.onBackPressed();
            }
        });

    }
}