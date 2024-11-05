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
import com.example.urbanharmony.Models.PromoModel;
import com.example.urbanharmony.Models.ShippingModel;
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

public class PromosActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notfoundContainer;
    ExtendedFloatingActionButton addBtn;
    ArrayList<PromoModel> datalist = new ArrayList<>();

    //    Dialog Elements
    Dialog promoDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText nameInput, detailInput, priceInput;
    TextInputLayout nameLayout, detailLayout, priceLayout;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promos);
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
        addBtn = findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promoForm("add","");
            }
        });

        fetchData("");

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromosActivity.super.onBackPressed();
            }
        });
    }

    public void fetchData(String data){
        MainActivity.db.child("Promos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        PromoModel model = new PromoModel(ds.getKey(),
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
                        MyAdapter adapter = new MyAdapter(PromosActivity.this,datalist);
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

    public void promoForm(String purpose, String productId){
        promoDialog = new Dialog(PromosActivity.this);
        promoDialog.setContentView(R.layout.dialog_add_promos);
        promoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        promoDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        promoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        promoDialog.getWindow().setGravity(Gravity.CENTER);
        promoDialog.setCancelable(false);
        promoDialog.setCanceledOnTouchOutside(false);
        cancelBtn = promoDialog.findViewById(R.id.cancelBtn);
        addDataBtn = promoDialog.findViewById(R.id.addDataBtn);
        title = promoDialog.findViewById(R.id.title);
        nameInput = promoDialog.findViewById(R.id.nameInput);
        nameLayout = promoDialog.findViewById(R.id.nameLayout);
        detailInput = promoDialog.findViewById(R.id.detailInput);
        detailLayout = promoDialog.findViewById(R.id.detailLayout);
        priceInput = promoDialog.findViewById(R.id.priceInput);
        priceLayout = promoDialog.findViewById(R.id.priceLayout);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        detailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                detailValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        priceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                priceValidation();
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
                promoDialog.dismiss();
            }
        });

        if(purpose.equals("edit")){
            title.setText("Edit Promo");
            MainActivity.db.child("Promos").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        nameInput.setText(snapshot.child("name").getValue().toString().trim());
                        detailInput.setText(snapshot.child("detail").getValue().toString().trim());
                        priceInput.setText(snapshot.child("price").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        promoDialog.show();
    }

    public boolean nameValidation(){
        String input = nameInput.getText().toString().trim();
        String regex = "^[a-zA-Z0-9%\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            nameLayout.setError("Promo Name is Required!!!");
            return false;
        } else if(input.length() < 5){
            nameLayout.setError("Promo Name at least 5 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            nameLayout.setError("Only text allowed!!!");
            return false;
        } else {
            nameLayout.setError(null);
            return true;
        }
    }

    public boolean detailValidation(){
        String input = detailInput.getText().toString().trim();
        String regex = "^[a-zA-Z,!.\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            detailLayout.setError("Promo Detail is Required!!!");
            return false;
        } else if(input.length() < 10){
            detailLayout.setError("Promo Detail at least 10 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            detailLayout.setError("Only text allowed!!!");
            return false;
        } else {
            detailLayout.setError(null);
            return true;
        }
    }

    public boolean priceValidation(){
        String input = priceInput.getText().toString().trim();
        String regex = "^[0-9\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            priceLayout.setError("Promo Percent OFF is Required!!!");
            return false;
        } else if(!matcher.matches()){
            priceLayout.setError("Only digits allowed!!!");
            return false;
        } else {
            priceLayout.setError(null);
            return true;
        }
    }

    private void validation(String purpose, String productId) {
        boolean nameErr = false, detailErr = false, priceErr = false;
        nameErr = nameValidation();
        detailErr = detailValidation();
        priceErr = priceValidation();

        if((nameErr && detailErr && priceErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(PromosActivity.this)){
            Dialog alertdialog = new Dialog(PromosActivity.this);
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
                mydata.put("detail", detailInput.getText().toString().trim());
                mydata.put("price", priceInput.getText().toString().trim());
                MainActivity.db.child("Promos").push().setValue(mydata);
                message.setText("Promo Added Successfully!!!");
            } else if(purpose.equals("edit")){
                MainActivity.db.child("Promos").child(productId).child("name").setValue(nameInput.getText().toString().trim());
                MainActivity.db.child("Promos").child(productId).child("detail").setValue(detailInput.getText().toString().trim());
                MainActivity.db.child("Promos").child(productId).child("price").setValue(priceInput.getText().toString().trim());
                message.setText("Promo Edited Successfully!!!");
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    promoDialog.dismiss();
                    fetchData("");
                }
            },2000);
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<PromoModel> data;

        public MyAdapter(Context context, ArrayList<PromoModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.promos_custom_listview,null);
            TextView sno, promoName, promoDetail;
            ImageView menu;

            sno = customListItem.findViewById(R.id.sno);
            promoName = customListItem.findViewById(R.id.promoName);
            promoDetail = customListItem.findViewById(R.id.promoDetail);
            menu = customListItem.findViewById(R.id.menu);

            sno.setText(""+(i+1));
            promoName.setText(data.get(i).getName());
            promoDetail.setText(data.get(i).getDetail());

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
                            promoForm("edit",""+data.get(i).getId());
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
                                    MainActivity.db.child("Promos").child(data.get(i).getId()).removeValue();
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