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

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.AddressModel;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressActivity extends AppCompatActivity {

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
    ArrayList<AddressModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);
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
                Intent intent = new Intent(AddressActivity.this,AddNewAddressActivity.class);
                intent.putExtra("edit","false");
                intent.putExtra("addressId","");
                startActivity(intent);
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
                AddressActivity.super.onBackPressed();
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
        MainActivity.db.child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            if(UID.equals(ds.child("UID").getValue().toString().trim())){
                                AddressModel model = new AddressModel(ds.getKey(),
                                        ds.child("UID").getValue().toString().trim(),
                                        ds.child("name").getValue().toString().trim(),
                                        ds.child("address").getValue().toString().trim(),
                                        ds.child("defaultStatus").getValue().toString().trim()
                                );
                                datalist.add(model);
                            }
                        } else {
                            if(UID.equals(ds.child("UID").getValue().toString().trim())){
                                if(ds.child("name").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                    AddressModel model = new AddressModel(ds.getKey(),
                                            ds.child("UID").getValue().toString().trim(),
                                            ds.child("name").getValue().toString().trim(),
                                            ds.child("address").getValue().toString().trim(),
                                            ds.child("defaultStatus").getValue().toString().trim()
                                    );
                                    datalist.add(model);
                                }
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
                        MyAdapter adapter = new MyAdapter(AddressActivity.this,datalist);
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.address_custom_listview,null);
            TextView sno, addressName, defaultStatus, addressDetail;
            ImageView menu;

            sno = customListItem.findViewById(R.id.sno);
            menu = customListItem.findViewById(R.id.menu);
            addressName = customListItem.findViewById(R.id.addressName);
            defaultStatus = customListItem.findViewById(R.id.defaultStatus);
            addressDetail = customListItem.findViewById(R.id.addressDetail);

            sno.setText(""+(i+1));
            if(data.get(i).getDefaultStatus().equals("true")){
                defaultStatus.setVisibility(View.VISIBLE);
            }
            addressName.setText(data.get(i).getName());
            addressDetail.setText(data.get(i).getAddress());

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
                            Intent intent = new Intent(context,AddNewAddressActivity.class);
                            intent.putExtra("edit","true");
                            intent.putExtra("addressId",""+data.get(i).getId());
                            startActivity(intent);
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
                                    MainActivity.db.child("Address").child(data.get(i).getId()).removeValue();
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