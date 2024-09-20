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
import com.example.urbanharmony.Models.BrandModel;
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

public class SubCategoryActivity extends AppCompatActivity {
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
    ArrayList<SubCategoryModel> datalist = new ArrayList<>();
    String categoryId, categoryName;

    //    Dialog Elements
    Dialog subCategoryDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText nameInput;
    TextInputLayout nameLayout;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sub_category);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            categoryId = extras.getString("categoryId");
            categoryName = extras.getString("categoryName");
        }

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
                subCategoryForm("add","");
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
                SubCategoryActivity.super.onBackPressed();
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
        MainActivity.db.child("Category").child(categoryId).child("SubCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            SubCategoryModel model = new SubCategoryModel(ds.getKey(),
                                    ds.child("name").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("name").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                SubCategoryModel model = new SubCategoryModel(ds.getKey(),
                                        ds.child("name").getValue().toString()
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
                        MyAdapter adapter = new MyAdapter(SubCategoryActivity.this,datalist);
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

    public void subCategoryForm(String purpose, String productId){
        subCategoryDialog = new Dialog(SubCategoryActivity.this);
        subCategoryDialog.setContentView(R.layout.dialog_add_subcategories);
        subCategoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        subCategoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subCategoryDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        subCategoryDialog.getWindow().setGravity(Gravity.CENTER);
        subCategoryDialog.setCancelable(false);
        subCategoryDialog.setCanceledOnTouchOutside(false);
        cancelBtn = subCategoryDialog.findViewById(R.id.cancelBtn);
        addDataBtn = subCategoryDialog.findViewById(R.id.addDataBtn);
        title = subCategoryDialog.findViewById(R.id.title);
        nameInput = subCategoryDialog.findViewById(R.id.nameInput);
        nameLayout = subCategoryDialog.findViewById(R.id.nameLayout);

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

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation(purpose, productId);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCategoryDialog.dismiss();
            }
        });

        if(purpose.equals("edit")){
            title.setText("Edit Sub Category");
            MainActivity.db.child("Category").child(categoryId).child("SubCategory").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
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

        subCategoryDialog.show();
    }

    public boolean nameValidation(){
        String input = nameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            nameLayout.setError("Sub Category Name is Required!!!");
            return false;
        } else if(input.length() < 3){
            nameLayout.setError("Sub Category Name at least 3 Characters!!!");
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
        if(MainActivity.connectionCheck(SubCategoryActivity.this)){
            Dialog alertdialog = new Dialog(SubCategoryActivity.this);
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
                MainActivity.db.child("Category").child(categoryId).child("SubCategory").push().setValue(mydata);
                message.setText("Sub Category Added Successfully!!!");
            } else if(purpose.equals("edit")){
                MainActivity.db.child("Category").child(categoryId).child("SubCategory").child(productId).child("name").setValue(nameInput.getText().toString().trim());
                message.setText("Sub Category Edited Successfully!!!");
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    subCategoryDialog.dismiss();
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
        ArrayList<SubCategoryModel> data;

        public MyAdapter(Context context, ArrayList<SubCategoryModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.subcategories_custom_listview,null);
            TextView sno, name;
            ImageView menu;

            sno = customListItem.findViewById(R.id.sno);
            name = customListItem.findViewById(R.id.name);
            menu = customListItem.findViewById(R.id.menu);

            sno.setText(""+(i+1));
            name.setText(data.get(i).getName());

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
                            subCategoryForm("edit",""+data.get(i).getId());
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
                                        MainActivity.db.child("Category").child(categoryId).child("SubCategory").setValue("");
                                    } else {
                                        MainActivity.db.child("Category").child(categoryId).child("SubCategory").child(data.get(i).getId()).removeValue();
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