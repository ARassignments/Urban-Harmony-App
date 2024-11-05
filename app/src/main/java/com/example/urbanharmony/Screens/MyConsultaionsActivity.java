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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.BookingModel;
import com.example.urbanharmony.Models.StyleModel;
import com.example.urbanharmony.Models.UsersModel;
import com.example.urbanharmony.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyConsultaionsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notfoundContainer;
    ArrayList<BookingModel> datalist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_consultaions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = findViewById(R.id.listView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);

        fetchData("");

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConsultaionsActivity.super.onBackPressed();
            }
        });

    }

    public void fetchData(String data){
        MainActivity.db.child("Booking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(DashboardActivity.getRole().equals("designer")){
                            if(ds.child("designerId").getValue().toString().equals(UID)){
                                BookingModel model = new BookingModel(ds.getKey(),
                                        ds.child("designerId").getValue().toString(),
                                        ds.child("userId").getValue().toString(),
                                        ds.child("day").getValue().toString(),
                                        ds.child("slot").getValue().toString(),
                                        ds.child("onBooking").getValue().toString(),
                                        ds.child("status").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        } else if(DashboardActivity.getRole().equals("user")){
                            if(ds.child("userId").getValue().toString().equals(UID)){
                                BookingModel model = new BookingModel(ds.getKey(),
                                        ds.child("designerId").getValue().toString(),
                                        ds.child("userId").getValue().toString(),
                                        ds.child("day").getValue().toString(),
                                        ds.child("slot").getValue().toString(),
                                        ds.child("onBooking").getValue().toString(),
                                        ds.child("status").getValue().toString()
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
                        MyAdapter adapter = new MyAdapter(MyConsultaionsActivity.this,datalist);
                        listView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
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

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<BookingModel> data;

        public MyAdapter(Context context, ArrayList<BookingModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.booking_consultation_custom_listview,null);
            TextView name, bookingDay, bookingDate, bookingSlots;
            ImageView image;
            Button bookingStatus, viewDetailsBtn, cancelBtn;

            name = customListItem.findViewById(R.id.name);
            bookingDay = customListItem.findViewById(R.id.bookingDay);
            bookingDate = customListItem.findViewById(R.id.bookingDate);
            bookingSlots = customListItem.findViewById(R.id.bookingSlots);
            image = customListItem.findViewById(R.id.image);
            bookingStatus = customListItem.findViewById(R.id.bookingStatus);
            viewDetailsBtn = customListItem.findViewById(R.id.viewDetailsBtn);
            cancelBtn = customListItem.findViewById(R.id.cancelBtn);

            bookingStatus.setText(data.get(i).getStatus());
            bookingDay.setText(data.get(i).getDay());
            bookingDate.setText("Date: "+data.get(i).getOnBooking());
            bookingSlots.setText("Timing Slots: "+data.get(i).getSlot());

            if(DashboardActivity.getRole().equals("designer")){
                viewDetailsBtn.setText("Approve Appointment");
                viewDetailsBtn.setOnClickListener(new View.OnClickListener() {
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
                                MainActivity.db.child("Booking").child(data.get(i).getId()).child("status").setValue("Approved");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Appointment Approved Successfully!!!");
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
                                MainActivity.db.child("Booking").child(data.get(i).getId()).child("status").setValue("Cancelled");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Appointment Cancelled Successfully!!!");
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

                MainActivity.db.child("Users").child(data.get(i).getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            name.setText(snapshot.child("name").getValue().toString());
                            if (!snapshot.child("image").getValue().toString().equals("")) {
                                Glide.with(context).load(snapshot.child("image").getValue().toString()).into(image);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(data.get(i).getStatus().equals("Approved")){
                    viewDetailsBtn.setVisibility(View.GONE);
                } else if(data.get(i).getStatus().equals("Cancelled")){
                    viewDetailsBtn.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.GONE);
                }

            } else if(DashboardActivity.getRole().equals("user")){

                viewDetailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DesignerDetailActivity.class);
                        intent.putExtra("userId",data.get(i).getDesignerId());
                        startActivity(intent);
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
                                MainActivity.db.child("Booking").child(data.get(i).getId()).child("status").setValue("Cancelled");
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Appointment Cancelled Successfully!!!");
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

                MainActivity.db.child("Users").child(data.get(i).getDesignerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            name.setText(snapshot.child("name").getValue().toString());
                            if (!snapshot.child("image").getValue().toString().equals("")) {
                                Glide.with(context).load(snapshot.child("image").getValue().toString()).into(image);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(data.get(i).getStatus().equals("Approved") || data.get(i).getStatus().equals("Cancelled")){
                    cancelBtn.setText("Write a Review");
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, DesignerDetailActivity.class);
                            intent.putExtra("userId",data.get(i).getDesignerId());
                            startActivity(intent);
                        }
                    });
                }
            }

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