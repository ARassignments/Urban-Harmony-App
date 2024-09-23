package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Context;
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

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.SlotModel;
import com.example.urbanharmony.Models.SubCategoryModel;
import com.example.urbanharmony.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlotsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notfoundContainer;
    ExtendedFloatingActionButton addBtn;
    ArrayList<SlotModel> datalist = new ArrayList<>();
    String scheduleId;

    //    Dialog Elements
    Dialog slotDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText fromInput, toInput;
    TextInputLayout fromLayout, toLayout;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_slots);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scheduleId = extras.getString("scheduleId");
        }

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = findViewById(R.id.listView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);
        addBtn = findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slotForm("add","");
            }
        });

        fetchData("");

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlotsActivity.super.onBackPressed();
            }
        });
    }

    public void fetchData(String data){
        MainActivity.db.child("Schedule").child(scheduleId).child("Slots").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            SlotModel model = new SlotModel(ds.getKey(),
                                    ds.child("from").getValue().toString(),
                                    ds.child("to").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("name").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                SlotModel model = new SlotModel(ds.getKey(),
                                        ds.child("from").getValue().toString(),
                                        ds.child("to").getValue().toString()
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
                        MyAdapter adapter = new MyAdapter(SlotsActivity.this,datalist);
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

    public void slotForm(String purpose, String productId){
        slotDialog = new Dialog(SlotsActivity.this);
        slotDialog.setContentView(R.layout.dialog_add_slots);
        slotDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        slotDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slotDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        slotDialog.getWindow().setGravity(Gravity.CENTER);
        slotDialog.setCancelable(false);
        slotDialog.setCanceledOnTouchOutside(false);
        cancelBtn = slotDialog.findViewById(R.id.cancelBtn);
        addDataBtn = slotDialog.findViewById(R.id.addDataBtn);
        title = slotDialog.findViewById(R.id.title);
        fromLayout = slotDialog.findViewById(R.id.fromLayout);
        fromInput = slotDialog.findViewById(R.id.fromInput);
        toLayout = slotDialog.findViewById(R.id.toLayout);
        toInput = slotDialog.findViewById(R.id.toInput);

        fromInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fromValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        toInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                toValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation(purpose, productId);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slotDialog.dismiss();
            }
        });

        if(purpose.equals("edit")){
            title.setText("Edit Slot");
            MainActivity.db.child("Schedule").child(scheduleId).child("Slots").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        fromInput.setText(snapshot.child("from").getValue().toString().trim());
                        toInput.setText(snapshot.child("to").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        slotDialog.show();
    }

    public boolean fromValidation(){
        String input = fromInput.getText().toString().trim();
        String regex = "^[0-9:mpa\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            fromLayout.setError("From Slot is Required!!!");
            return false;
        } else if(input.length() < 5){
            fromLayout.setError("From Slot at least 5 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            fromLayout.setError("Only digit allowed!!!");
            return false;
        } else {
            fromLayout.setError(null);
            return true;
        }
    }

    public boolean toValidation(){
        String input = toInput.getText().toString().trim();
        String regex = "^[0-9:\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            toLayout.setError("To Slot is Required!!!");
            return false;
        } else if(input.length() < 5){
            toLayout.setError("To Slot at least 5 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            toLayout.setError("Only digit allowed!!!");
            return false;
        } else {
            toLayout.setError(null);
            return true;
        }
    }

    private void validation(String purpose, String productId) {
        boolean fromErr = false, toErr = false;
        fromErr = fromValidation();
        toErr = toValidation();

        if((fromErr && toErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(SlotsActivity.this)){
            Dialog alertdialog = new Dialog(SlotsActivity.this);
            alertdialog.setContentView(R.layout.dialog_success);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView message = alertdialog.findViewById(R.id.msgDialog);
            alertdialog.show();

            if(purpose.equals("add")){
                HashMap<String, String> mydata = new HashMap<String, String>();
                mydata.put("from", fromInput.getText().toString().trim());
                mydata.put("to", toInput.getText().toString().trim());
                MainActivity.db.child("Schedule").child(scheduleId).child("Slots").push().setValue(mydata);
                message.setText("Slot Added Successfully!!!");
            } else if(purpose.equals("edit")){
                MainActivity.db.child("Schedule").child(scheduleId).child("Slots").child(productId).child("from").setValue(fromInput.getText().toString().trim());
                MainActivity.db.child("Schedule").child(scheduleId).child("Slots").child(productId).child("to").setValue(toInput.getText().toString().trim());
                message.setText("Slot Edited Successfully!!!");
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    slotDialog.dismiss();
                    fetchData("");
                }
            },2000);
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<SlotModel> data;

        public MyAdapter(Context context, ArrayList<SlotModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.slots_custom_listview,null);
            TextView sno, from, to;
            ImageView menu;

            sno = customListItem.findViewById(R.id.sno);
            from = customListItem.findViewById(R.id.from);
            to = customListItem.findViewById(R.id.to);
            menu = customListItem.findViewById(R.id.menu);

            sno.setText(""+(i+1));
            from.setText(data.get(i).getFrom());
            to.setText(data.get(i).getTo());

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog loaddialog = new Dialog(context);
                    loaddialog.setContentView(R.layout.dialog_actions);
                    loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    loaddialog.getWindow().setGravity(Gravity.CENTER);
                    Button editBtn, deleteBtn;
                    editBtn = loaddialog.findViewById(R.id.editBtn);
                    deleteBtn = loaddialog.findViewById(R.id.deleteBtn);
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            slotForm("edit",""+data.get(i).getId());
                            loaddialog.dismiss();
                        }
                    });

                    deleteBtn.setOnClickListener(new View.OnClickListener() {
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
                                    if(data.size() < 2){
                                        MainActivity.db.child("Schedule").child(scheduleId).child("Slots").setValue("");
                                    } else {
                                        MainActivity.db.child("Schedule").child(scheduleId).child("Slots").child(data.get(i).getId()).removeValue();
                                    }
                                    Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.dialog_success);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                    dialog.getWindow().setGravity(Gravity.CENTER);
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                    TextView msg = dialog.findViewById(R.id.msgDialog);
                                    msg.setText("Deleted Successfully!!!");
                                    dialog.show();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            actiondialog.dismiss();
                                            loaddialog.dismiss();
                                            fetchData("");
                                        }
                                    },2000);
                                }
                            });

                            actiondialog.show();
                        }
                    });

                    loaddialog.show();
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