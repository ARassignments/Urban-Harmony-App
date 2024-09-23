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
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.ScheduleModel;
import com.example.urbanharmony.Models.SlotModel;
import com.example.urbanharmony.Models.SubCategoryModel;
import com.example.urbanharmony.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

public class SchedulesActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notifyBar, notfoundContainer;
    ImageView sortBtn;
    EditText searchInput;
    TextView searchedWord, totalCount;
    ExtendedFloatingActionButton addBtn;
    ArrayList<ScheduleModel> datalist = new ArrayList<>();

    //    Dialog Elements
    Dialog scheduleDialog;
    Button cancelBtn, addDataBtn;
    AutoCompleteTextView nameInput;
    TextInputLayout nameLayout;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedules);
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
        addBtn = findViewById(R.id.addBtn);
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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleForm("add","");
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
                SchedulesActivity.super.onBackPressed();
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
        MainActivity.db.child("Schedule").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            if(ds.child("userId").getValue().toString().trim().equals(UID)){

                            }
                            ScheduleModel model = new ScheduleModel(ds.getKey(),
                                    ds.child("name").getValue().toString(),
                                    ds.child("userId").getValue().toString(),
                                    ds.child("Slots").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("name").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim()) && ds.child("userId").getValue().toString().trim().equals(UID)){
                                ScheduleModel model = new ScheduleModel(ds.getKey(),
                                        ds.child("name").getValue().toString(),
                                        ds.child("userId").getValue().toString(),
                                        ds.child("Slots").getValue().toString()
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
                        MyAdapter adapter = new MyAdapter(SchedulesActivity.this,datalist);
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

    public void scheduleForm(String purpose, String productId){
        scheduleDialog = new Dialog(SchedulesActivity.this);
        scheduleDialog.setContentView(R.layout.dialog_add_schedules);
        scheduleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        scheduleDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scheduleDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        scheduleDialog.getWindow().setGravity(Gravity.CENTER);
        scheduleDialog.setCancelable(false);
        scheduleDialog.setCanceledOnTouchOutside(false);
        cancelBtn = scheduleDialog.findViewById(R.id.cancelBtn);
        addDataBtn = scheduleDialog.findViewById(R.id.addDataBtn);
        title = scheduleDialog.findViewById(R.id.title);
        nameInput = scheduleDialog.findViewById(R.id.nameInput);
        nameLayout = scheduleDialog.findViewById(R.id.nameLayout);

        ArrayList<String> daysList = new ArrayList<>();
        daysList.add("Monday");
        daysList.add("Tuesday");
        daysList.add("Wednesday");
        daysList.add("Thursday");
        daysList.add("Friday");
        daysList.add("Saturday");
        daysList.add("Sunday");

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(SchedulesActivity.this, android.R.layout.simple_dropdown_item_1line, daysList);
        nameInput.setAdapter(daysAdapter);

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation(purpose, productId);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleDialog.dismiss();
            }
        });


        if(purpose.equals("edit")){
            title.setText("Edit Schedule");
            MainActivity.db.child("Schedule").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        nameInput.setText(snapshot.child("name").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        scheduleDialog.show();
    }

    public boolean nameValidation(){
        String input = nameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            nameLayout.setError("Days is Required!!!");
            return false;
        } else if(!matcher.matches()){
            nameLayout.setError("Only text allowed!!!");
            return false;
        } else {
            nameLayout.setError(null);
            return true;
        }
    }

    private void validation(String purpose, String productId) {
        boolean nameErr = false;
        nameErr = nameValidation();

        if((nameErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(SchedulesActivity.this)){
            Dialog alertdialog = new Dialog(SchedulesActivity.this);
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
                mydata.put("name", nameInput.getText().toString().trim());
                mydata.put("userId", UID);
                mydata.put("Slots", "");
                MainActivity.db.child("Schedule").push().setValue(mydata);
                message.setText("Schedule Added Successfully!!!");
            } else if(purpose.equals("edit")){
                MainActivity.db.child("Schedule").child(productId).child("name").setValue(nameInput.getText().toString().trim());
                message.setText("Schedule Edited Successfully!!!");
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    scheduleDialog.dismiss();
                    fetchData("");
                }
            },2000);
        }
    }

    public void searchValidation(){
        String input = searchInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
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
        ArrayList<ScheduleModel> data;

        public MyAdapter(Context context, ArrayList<ScheduleModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.schedules_custom_listview,null);
            LinearLayout listItem;
            TextView sno, name;
            ImageView menu;
            ChipGroup slotsChipgroup;
            ArrayList<String> slots = new ArrayList<String>();

            listItem = customListItem.findViewById(R.id.listItem);
            sno = customListItem.findViewById(R.id.sno);
            name = customListItem.findViewById(R.id.name);
            menu = customListItem.findViewById(R.id.menu);
            slotsChipgroup = customListItem.findViewById(R.id.slotsChipgroup);

            sno.setText(""+(i+1));
            name.setText(data.get(i).getName());

            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SlotsActivity.class);
                    intent.putExtra("scheduleId",data.get(i).getId());
                    startActivity(intent);
                }
            });

            MainActivity.db.child("Schedule").child(data.get(i).getId()).child("Slots").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        slots.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SlotModel data = dataSnapshot.getValue(SlotModel.class);
                            slots.add(data.getFrom()+"-"+data.getTo());
                        }
                        for (String name:slots) {
                            Chip chip = new Chip(new ContextThemeWrapper(context, R.style.CustomChipStyle));
                            chip.setText(""+name);
                            chip.setCheckable(false);
                            chip.setEnabled(false);
                            int heightInPixels = (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 35, context.getResources().getDisplayMetrics());
                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,  // Set width as WRAP_CONTENT
                                    heightInPixels
                            );
                            chip.setLayoutParams(params);
                            slotsChipgroup.addView(chip);
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
                            scheduleForm("edit",""+data.get(i).getId());
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
                                    MainActivity.db.child("Schedule").child(data.get(i).getId()).removeValue();
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