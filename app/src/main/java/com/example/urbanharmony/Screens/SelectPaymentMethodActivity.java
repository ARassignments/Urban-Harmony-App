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

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.AddToCartModel;
import com.example.urbanharmony.Models.PaymentMethodModel;
import com.example.urbanharmony.Models.ProductModel;
import com.example.urbanharmony.Models.ShippingModel;
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class SelectPaymentMethodActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "asc";
    static String totalItemAmount, totalShippingAmount, totalOverallAmount, shippingName, shippingDetail, locationName, locationAddress;
    ListView listView;
    LinearLayout loader, notfoundContainer;
    ArrayList<PaymentMethodModel> datalist = new ArrayList<>();
    static String selectedPaymentMethodId;
    static String selectedPaymentMethodName;
    LinearLayout applyBtn;
    RadioButton selectedRadioButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_payment_method);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            totalItemAmount = extra.getString("totalItemAmount");
            totalShippingAmount = extra.getString("totalShippingAmount");
            totalOverallAmount = extra.getString("totalOverallAmount");
            shippingName = extra.getString("shippingName");
            shippingDetail = extra.getString("shippingDetail");
            locationName = extra.getString("locationName");
            locationAddress = extra.getString("locationAddress");
        }

        applyBtn = findViewById(R.id.applyBtn);
        listView = findViewById(R.id.listView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPaymentMethodActivity.super.onBackPressed();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderPlace();
            }
        });

        fetchData("");
    }

    public void fetchData(String data){
        MainActivity.db.child("PaymentMethods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        PaymentMethodModel model = new PaymentMethodModel(ds.getKey(),
                                ds.child("name").getValue().toString()
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
                        MyAdapter adapter = new MyAdapter(SelectPaymentMethodActivity.this,datalist);
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

    public void setRadioBtn(RadioButton radioBtn, String shippingIds, String shippingName){
        selectedRadioButton = radioBtn;
        selectedPaymentMethodId = shippingIds;
        selectedPaymentMethodName = shippingName;
    }

    public static String generateOrderID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "").substring(0, 10);
    }

    public void orderPlace(){
        if(selectedRadioButton == null){
            Dialog dialog = new Dialog(SelectPaymentMethodActivity.this);
            dialog.setContentView(R.layout.dialog_error);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView msg = dialog.findViewById(R.id.msgDialog);
            msg.setText("Please Select Your Payment Method!!!");
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            },3000);
        } else {
            MainActivity.db.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String OID = generateOrderID();

                    // Current date
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy | hh:mm:ss a");
                    String dateTime = simpleDateFormat.format(calendar.getTime());

                    HashMap<String, Object> mydata = new HashMap<>();
                    mydata.put("orderId", OID);
                    mydata.put("UID", UID);
                    mydata.put("totalItemAmount", "" + totalItemAmount);
                    mydata.put("totalShippingAmount", "" + totalShippingAmount);
                    mydata.put("totalOverallAmount", "" + totalOverallAmount);
                    mydata.put("shippingName", "" + shippingName);
                    mydata.put("shippingDetail", "" + shippingDetail);
                    mydata.put("locationName", "" + locationName);
                    mydata.put("locationAddress", "" + locationAddress);
                    mydata.put("paymentMethod", "" + selectedPaymentMethodName);
                    mydata.put("status", "1");
                    mydata.put("createdOn", dateTime);

                    HashMap<String, HashMap<String, String>> itemsMap = new HashMap<>();
                    int itemIndex = 1;

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (UID.equals(ds.child("UID").getValue().toString().trim())) {
                            AddToCartModel model = new AddToCartModel(
                                    ds.getKey(),
                                    ds.child("PID").getValue().toString().trim(),
                                    ds.child("UID").getValue().toString().trim(),
                                    ds.child("qty").getValue().toString().trim()
                            );

                            HashMap<String, String> itemDetails = new HashMap<>();
                            itemDetails.put("PID", model.getPID());
                            itemDetails.put("UID", model.getUID());
                            itemDetails.put("qty", model.getQty());
                            itemsMap.put("item_" + itemIndex, itemDetails);
                            itemIndex++;

                            MainActivity.db.child("AddToCart").child(ds.getKey()).removeValue();

                            MainActivity.db.child("Products").child(model.getPID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                    if (productSnapshot.exists()) {
                                        int pStock = Integer.parseInt(productSnapshot.child("pStock").getValue().toString());
                                        int qty = Integer.parseInt(model.getQty());
                                        int newStock = pStock - qty;
                                        if (newStock >= 0) {
                                            MainActivity.db.child("Products").child(model.getPID()).child("pStock").setValue(newStock);
                                        } else {
                                            Dialog dialog = new Dialog(SelectPaymentMethodActivity.this);
                                            dialog.setContentView(R.layout.dialog_error);
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                            dialog.getWindow().setGravity(Gravity.CENTER);
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.setCancelable(false);
                                            TextView msg = dialog.findViewById(R.id.msgDialog);
                                            msg.setText("Ordered Item Stock Not Available!!!");
                                            dialog.show();

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                }
                                            },2000);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    mydata.put("items", itemsMap);
                    MainActivity.db.child("Orders").push().setValue(mydata);
                    MainActivity.db.child("Users").child(UID).child("address").setValue("");
                    MainActivity.db.child("Users").child(UID).child("shipping").setValue("");

                    Dialog dialog = new Dialog(SelectPaymentMethodActivity.this);
                    dialog.setContentView(R.layout.dialog_order_successfully);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    Button viewOrderBtn, viewReceiptBtn;
                    viewOrderBtn = dialog.findViewById(R.id.viewOrderBtn);
                    viewReceiptBtn = dialog.findViewById(R.id.viewReceiptBtn);
                    dialog.show();

                    viewOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    viewReceiptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SelectPaymentMethodActivity.this, DashboardActivity.class));
                            finish();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError e) {

                }
            });

        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<PaymentMethodModel> data;

        public MyAdapter(Context context, ArrayList<PaymentMethodModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.selected_payment_methods_custom_listview,null);
            TextView paymentName;
            RadioButton radioBtn;
            LinearLayout itemContainer;
            ImageView image;

            itemContainer = customListItem.findViewById(R.id.itemContainer);
            paymentName = customListItem.findViewById(R.id.paymentName);
            image = customListItem.findViewById(R.id.image);
            radioBtn = customListItem.findViewById(R.id.radioBtn);

            paymentName.setText(data.get(i).getName());

            if(data.get(i).getName().toLowerCase().contains("paypal")){
                image.setImageResource(R.drawable.paypal);
            } else if(data.get(i).getName().toLowerCase().contains("google")){
                image.setImageResource(R.drawable.google);
            } else if(data.get(i).getName().toLowerCase().contains("apple")){
                image.setImageResource(R.drawable.apple);
            }

            itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedRadioButton != null) {
                        selectedRadioButton.setChecked(false);
                    }
                    radioBtn.setChecked(true);
                    setRadioBtn(radioBtn, data.get(i).getId(), data.get(i).getName());
                }
            });
            radioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedRadioButton != null) {
                        selectedRadioButton.setChecked(false);
                    }
                    radioBtn.setChecked(true);
                    setRadioBtn(radioBtn, data.get(i).getId(), data.get(i).getName());
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