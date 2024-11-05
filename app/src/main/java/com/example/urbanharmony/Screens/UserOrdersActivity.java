package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.OrderModel;
import com.example.urbanharmony.Models.StyleModel;
import com.example.urbanharmony.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserOrdersActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notifyBar, notfoundContainer;
    ImageView sortBtn;
    EditText searchInput;
    TextView searchedWord, totalCount;
    ArrayList<OrderModel> datalist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_orders);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = findViewById(R.id.listView);
        notifyBar = findViewById(R.id.notifyBar);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);
        searchedWord = findViewById(R.id.searchedWord);
        totalCount = findViewById(R.id.totalCount);
        searchInput = findViewById(R.id.searchInput);
        sortBtn = findViewById(R.id.sortBtn);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchedWord.setText(searchInput.getText().toString().trim());
                searchValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fetchData("");

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserOrdersActivity.super.onBackPressed();
            }
        });
    }

    public void sorting(){
        if(sortingStatus.equals("asc")){
            sortingStatus = "dsc";
            sortBtn.setImageResource(R.drawable.deasscending_order);
        } else if(sortingStatus.equals("dsc")){
            sortingStatus = "asc";
            sortBtn.setImageResource(R.drawable.asscending_order);
        }
        fetchData("");
    }

    public void fetchData(String data){
        MainActivity.db.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            OrderModel model = new OrderModel(ds.getKey(),
                                    ds.child("orderId").getValue().toString(),
                                    ds.child("UID").getValue().toString(),
                                    ds.child("totalItemAmount").getValue().toString(),
                                    ds.child("totalShippingAmount").getValue().toString(),
                                    ds.child("totalOverallAmount").getValue().toString(),
                                    ds.child("shippingName").getValue().toString(),
                                    ds.child("shippingDetail").getValue().toString(),
                                    ds.child("locationName").getValue().toString(),
                                    ds.child("locationAddress").getValue().toString(),
                                    ds.child("paymentMethod").getValue().toString(),
                                    ds.child("status").getValue().toString(),
                                    ds.child("createdOn").getValue().toString(),
                                    ds.child("items").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("orderId").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                OrderModel model = new OrderModel(ds.getKey(),
                                        ds.child("orderId").getValue().toString(),
                                        ds.child("UID").getValue().toString(),
                                        ds.child("totalItemAmount").getValue().toString(),
                                        ds.child("totalShippingAmount").getValue().toString(),
                                        ds.child("totalOverallAmount").getValue().toString(),
                                        ds.child("shippingName").getValue().toString(),
                                        ds.child("shippingDetail").getValue().toString(),
                                        ds.child("locationName").getValue().toString(),
                                        ds.child("locationAddress").getValue().toString(),
                                        ds.child("paymentMethod").getValue().toString(),
                                        ds.child("status").getValue().toString(),
                                        ds.child("createdOn").getValue().toString(),
                                        ds.child("items").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        MyAdapter adapter = new MyAdapter(UserOrdersActivity.this,datalist);
                        listView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    totalCount.setText(datalist.size()+" found");
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

    public void searchValidation(){
        String input = searchInput.getText().toString().trim();
        String regex = "^[a-zA-Z0-9\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(!matcher.matches()){
            searchInput.setError("Only text allowed!!!");
        } else {
            searchInput.setError(null);
            if(input.isEmpty()){
                notifyBar.setVisibility(View.GONE);
                fetchData("");
            } else {
                notifyBar.setVisibility(View.VISIBLE);
                fetchData(searchInput.getText().toString().trim());
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<OrderModel> data;

        public MyAdapter(Context context, ArrayList<OrderModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.user_order_custom_listview,null);
            TextView orderId, orderItems, orderAmount, orderDate, userName;
            LinearLayout item;
            ImageView pImage, userImage;
            Button orderBtn, orderStatus, userRole, statusBtn, cancelBtn;

            orderId = customListItem.findViewById(R.id.orderId);
            orderItems = customListItem.findViewById(R.id.orderItems);
            orderStatus = customListItem.findViewById(R.id.orderStatus);
            orderAmount = customListItem.findViewById(R.id.orderAmount);
            orderDate = customListItem.findViewById(R.id.orderDate);
            pImage = customListItem.findViewById(R.id.pImage);
            orderBtn = customListItem.findViewById(R.id.orderBtn);
            item = customListItem.findViewById(R.id.item);
            userName = customListItem.findViewById(R.id.userName);
            userImage = customListItem.findViewById(R.id.userImage);
            userRole = customListItem.findViewById(R.id.userRole);
            statusBtn = customListItem.findViewById(R.id.statusBtn);
            cancelBtn = customListItem.findViewById(R.id.cancelBtn);

            orderId.setText("#"+data.get(i).getOrderId().toUpperCase());
            orderAmount.setText("$"+data.get(i).getTotalOverallAmount());
            orderDate.setText(data.get(i).getCreatedOn());
            if(data.get(i).getStatus().equals("1")){
                orderStatus.setText("In Process");
                statusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog actiondialog = new Dialog(context);
                        actiondialog.setContentView(R.layout.dialog_confirm);
                        actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        actiondialog.getWindow().setGravity(Gravity.CENTER);
                        actiondialog.setCancelable(false);
                        actiondialog.setCanceledOnTouchOutside(false);
                        Button cancelBtn, yesBtn;
                        yesBtn = actiondialog.findViewById(R.id.yesBtn);
                        cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                actiondialog.dismiss();
                            }
                        });
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity.db.child("Orders").child(data.get(i).getId()).child("status").setValue("2");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Order Packaging Successfully!!!");
                                dialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        actiondialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);
                            }
                        });

                        actiondialog.show();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog actiondialog = new Dialog(context);
                        actiondialog.setContentView(R.layout.dialog_confirm);
                        actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        actiondialog.getWindow().setGravity(Gravity.CENTER);
                        actiondialog.setCancelable(false);
                        actiondialog.setCanceledOnTouchOutside(false);
                        Button cancelBtn, yesBtn;
                        yesBtn = actiondialog.findViewById(R.id.yesBtn);
                        cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                actiondialog.dismiss();
                            }
                        });
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity.db.child("Orders").child(data.get(i).getId()).child("status").setValue("5");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Order Cancelled Successfully!!!");
                                dialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        actiondialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);
                            }
                        });

                        actiondialog.show();
                    }
                });
            } else if(data.get(i).getStatus().equals("2")){
                orderStatus.setText("In Packaging");
                statusBtn.setText("Status: Shipping");
                statusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog actiondialog = new Dialog(context);
                        actiondialog.setContentView(R.layout.dialog_confirm);
                        actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        actiondialog.getWindow().setGravity(Gravity.CENTER);
                        actiondialog.setCancelable(false);
                        actiondialog.setCanceledOnTouchOutside(false);
                        Button cancelBtn, yesBtn;
                        yesBtn = actiondialog.findViewById(R.id.yesBtn);
                        cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                actiondialog.dismiss();
                            }
                        });
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity.db.child("Orders").child(data.get(i).getId()).child("status").setValue("3");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Order Shipping Successfully!!!");
                                dialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        actiondialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);
                            }
                        });

                        actiondialog.show();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog actiondialog = new Dialog(context);
                        actiondialog.setContentView(R.layout.dialog_confirm);
                        actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        actiondialog.getWindow().setGravity(Gravity.CENTER);
                        actiondialog.setCancelable(false);
                        actiondialog.setCanceledOnTouchOutside(false);
                        Button cancelBtn, yesBtn;
                        yesBtn = actiondialog.findViewById(R.id.yesBtn);
                        cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                actiondialog.dismiss();
                            }
                        });
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity.db.child("Orders").child(data.get(i).getId()).child("status").setValue("5");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Order Cancelled Successfully!!!");
                                dialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        actiondialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);
                            }
                        });

                        actiondialog.show();
                    }
                });
            } else if(data.get(i).getStatus().equals("3")){
                orderStatus.setText("In Shipping");
                statusBtn.setText("Status: Completed");
                statusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog actiondialog = new Dialog(context);
                        actiondialog.setContentView(R.layout.dialog_confirm);
                        actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        actiondialog.getWindow().setGravity(Gravity.CENTER);
                        actiondialog.setCancelable(false);
                        actiondialog.setCanceledOnTouchOutside(false);
                        Button cancelBtn, yesBtn;
                        yesBtn = actiondialog.findViewById(R.id.yesBtn);
                        cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                actiondialog.dismiss();
                            }
                        });
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity.db.child("Orders").child(data.get(i).getId()).child("status").setValue("4");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Order Completed Successfully!!!");
                                dialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        actiondialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);
                            }
                        });

                        actiondialog.show();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog actiondialog = new Dialog(context);
                        actiondialog.setContentView(R.layout.dialog_confirm);
                        actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        actiondialog.getWindow().setGravity(Gravity.CENTER);
                        actiondialog.setCancelable(false);
                        actiondialog.setCanceledOnTouchOutside(false);
                        Button cancelBtn, yesBtn;
                        yesBtn = actiondialog.findViewById(R.id.yesBtn);
                        cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                actiondialog.dismiss();
                            }
                        });
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity.db.child("Orders").child(data.get(i).getId()).child("status").setValue("5");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Order Cancelled Successfully!!!");
                                dialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        actiondialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);
                            }
                        });

                        actiondialog.show();
                    }
                });
            } else if(data.get(i).getStatus().equals("4")){
                orderStatus.setText("Completed");
                statusBtn.setText("Order is Delivered");
                cancelBtn.setVisibility(View.GONE);
            } else if(data.get(i).getStatus().equals("5")){
                orderStatus.setText("Cancelled");
                statusBtn.setText("Order is Cancelled");
                cancelBtn.setVisibility(View.GONE);
            }

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("OID",data.get(i).getId());
                    intent.putExtra("OrderId",data.get(i).getOrderId());
                    intent.putExtra("OItems",orderItems.getText().toString());
                    intent.putExtra("OTotalItemAmount",data.get(i).getTotalItemAmount());
                    intent.putExtra("OTotalShippingAmount",data.get(i).getTotalShippingAmount());
                    intent.putExtra("OTotalOverallAmount",data.get(i).getTotalOverallAmount());
                    intent.putExtra("OCreatedOn",data.get(i).getCreatedOn());
                    intent.putExtra("OLocationAddress",data.get(i).getLocationAddress());
                    intent.putExtra("OPaymentMethod",data.get(i).getPaymentMethod());
                    startActivity(intent);
                }
            });

            orderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderTrackActivity.class);
                    intent.putExtra("OID",data.get(i).getId());
                    intent.putExtra("OrderId",data.get(i).getOrderId());
                    intent.putExtra("OItems",orderItems.getText().toString());
                    intent.putExtra("OAmount",data.get(i).getTotalOverallAmount());
                    intent.putExtra("OStatus",data.get(i).getStatus());
                    startActivity(intent);
                }
            });

            MainActivity.db.child("Orders").child(data.get(i).getId()).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        int count = 0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            count++;
                        }
                        orderItems.setText("Items: "+count);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            MainActivity.db.child("Orders").child(data.get(i).getId()).child("items").child("item_1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String PID = snapshot.child("PID").getValue().toString().trim();
                        MainActivity.db.child("Products").child(PID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    Glide.with(context).load(datasnapshot.child("pImage").getValue().toString().trim()).into(pImage);
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

            MainActivity.db.child("Users").child(data.get(i).getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userName.setText(snapshot.child("name").getValue().toString());
                        userRole.setText(snapshot.child("role").getValue().toString());
                        if (!snapshot.child("image").getValue().toString().equals("")) {
                            Glide.with(context).load(snapshot.child("image").getValue().toString()).into(userImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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