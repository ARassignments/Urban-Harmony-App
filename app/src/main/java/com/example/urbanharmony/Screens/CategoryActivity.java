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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.SubCategoryModel;
import com.example.urbanharmony.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryActivity extends AppCompatActivity {

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
    ArrayList<CategoryModel> datalist = new ArrayList<>();

    //    Dialog Elements
    Dialog categoryDialog, dialogImage;
    Button cancelBtn, addDataBtn;
    TextInputEditText nameInput;
    TextInputLayout nameLayout;
    TextView title, imageErrorText, imageIdText;
    ImageView categoryImage, editImageBtn;
    CardView sofa, bed, dining, stove, chair;
    int[] images = {
            R.drawable.sofa,
            R.drawable.bed,
            R.drawable.dining,
            R.drawable.stove,
            R.drawable.chair
    };

    String[] imagesUrl = {
            "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/CategoriesImages%2FSofa%20(3).png?alt=media&token=21bf22b7-4168-4927-ad9d-08b541309336",
            "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/CategoriesImages%2FBed.png?alt=media&token=215ad6f8-39b7-430e-9344-e2cde09570df",
            "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/CategoriesImages%2FDining.png?alt=media&token=e3369ae5-f9d3-466c-b19b-f82254653be5",
            "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/CategoriesImages%2FStove.png?alt=media&token=2c684018-2cdb-4b7b-9c42-b2d492167b43",
            "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/CategoriesImages%2FChair.png?alt=media&token=479d900c-ef59-4a51-929a-de80e73f0450"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
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
                categoryForm("add","");
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
                CategoryActivity.super.onBackPressed();
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
        MainActivity.db.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            CategoryModel model = new CategoryModel(ds.getKey(),
                                    ds.child("name").getValue().toString(),
                                    ds.child("image").getValue().toString(),
                                    ds.child("SubCategory").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("name").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                CategoryModel model = new CategoryModel(ds.getKey(),
                                        ds.child("name").getValue().toString(),
                                        ds.child("image").getValue().toString(),
                                        ds.child("SubCategory").getValue().toString()
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
                        MyAdapter adapter = new MyAdapter(CategoryActivity.this,datalist);
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

    public void categoryForm(String purpose, String productId){
        categoryDialog = new Dialog(CategoryActivity.this);
        categoryDialog.setContentView(R.layout.dialog_add_categories);
        categoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        categoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        categoryDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        categoryDialog.getWindow().setGravity(Gravity.CENTER);
        categoryDialog.setCancelable(false);
        categoryDialog.setCanceledOnTouchOutside(false);
        cancelBtn = categoryDialog.findViewById(R.id.cancelBtn);
        addDataBtn = categoryDialog.findViewById(R.id.addDataBtn);
        title = categoryDialog.findViewById(R.id.title);
        nameInput = categoryDialog.findViewById(R.id.nameInput);
        nameLayout = categoryDialog.findViewById(R.id.nameLayout);
        categoryImage = categoryDialog.findViewById(R.id.categoryImage);
        editImageBtn = categoryDialog.findViewById(R.id.editImageBtn);
        imageErrorText = categoryDialog.findViewById(R.id.imageErrorText);
        imageIdText = categoryDialog.findViewById(R.id.imageIdText);

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
                categoryDialog.dismiss();
            }
        });

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImage = new Dialog(CategoryActivity.this);
                dialogImage.setContentView(R.layout.dialog_bottom_category_image);
                dialogImage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogImage.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogImage.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
                dialogImage.getWindow().setGravity(Gravity.BOTTOM);
                dialogImage.setCanceledOnTouchOutside(false);
                dialogImage.setCancelable(false);
                Button cancelBtnImage;
                cancelBtnImage = dialogImage.findViewById(R.id.cancelBtn);
                sofa = dialogImage.findViewById(R.id.sofa);
                bed = dialogImage.findViewById(R.id.bed);
                dining = dialogImage.findViewById(R.id.dining);
                stove = dialogImage.findViewById(R.id.stove);
                chair = dialogImage.findViewById(R.id.chair);
                dialogImage.show();

                sofa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(0);
                    }
                });

                bed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(1);
                    }
                });

                dining.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(2);
                    }
                });

                stove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(3);
                    }
                });

                chair.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(4);
                    }
                });

                cancelBtnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogImage.dismiss();
                    }
                });
            }
        });

        if(purpose.equals("edit")){
            title.setText("Edit Category");
            MainActivity.db.child("Category").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        nameInput.setText(snapshot.child("name").getValue().toString().trim());
                        if(!snapshot.child("image").getValue().toString().trim().equals("")){
//                            categoryImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString().trim()));
                            Glide.with(CategoryActivity.this).load(snapshot.child("image").getValue().toString().trim()).into(categoryImage);
                            imageIdText.setText(snapshot.child("image").getValue().toString().trim());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        categoryDialog.show();
    }

    public boolean nameValidation(){
        String input = nameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            nameLayout.setError("Category Name is Required!!!");
            return false;
        } else if(input.length() < 3){
            nameLayout.setError("Category Name at least 3 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            nameLayout.setError("Only text allowed!!!");
            return false;
        } else {
            nameLayout.setError(null);
            return true;
        }
    }

    public boolean imageValidation(){
        String input = imageIdText.getText().toString().trim();
        if(input.equals("")){
            imageErrorText.setText("Category Image is required!!!");
            imageErrorText.setVisibility(View.VISIBLE);
            return false;
        } else {
            imageErrorText.setText(null);
            imageErrorText.setVisibility(View.GONE);
            return true;
        }
    }

    private void validation(String purpose, String productId) {
        boolean nameErr = false, imageErr = false;
        nameErr = nameValidation();
        imageErr = imageValidation();

        if((nameErr && imageErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(CategoryActivity.this)){
            Dialog alertdialog = new Dialog(CategoryActivity.this);
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
                mydata.put("image", imageIdText.getText().toString().trim());
                mydata.put("SubCategory", "");
                MainActivity.db.child("Category").push().setValue(mydata);
                message.setText("Category Added Successfully!!!");
            } else if(purpose.equals("edit")){
                MainActivity.db.child("Category").child(productId).child("name").setValue(nameInput.getText().toString().trim());
                MainActivity.db.child("Category").child(productId).child("image").setValue(imageIdText.getText().toString().trim());
                message.setText("Category Edited Successfully!!!");
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    categoryDialog.dismiss();
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

    public void setProfileImage(int value){
        categoryImage.setImageResource(images[value]);
        imageIdText.setText(""+imagesUrl[value]);
        imageValidation();
        Dialog dialogSuccess = new Dialog(CategoryActivity.this);
        dialogSuccess.setContentView(R.layout.dialog_success);
        dialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogSuccess.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogSuccess.getWindow().setGravity(Gravity.CENTER);
        dialogSuccess.setCanceledOnTouchOutside(false);
        dialogSuccess.setCancelable(false);
        TextView msg = dialogSuccess.findViewById(R.id.msgDialog);
        msg.setText("Category Image Selected!!!");
        dialogSuccess.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogSuccess.dismiss();
                dialogImage.dismiss();
            }
        },2000);
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<CategoryModel> data;

        public MyAdapter(Context context, ArrayList<CategoryModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.categories_custom_listview,null);
            LinearLayout listItem;
            TextView sno, name;
            ImageView menu, image;
            ChipGroup subcategoryChipgroup;
            ArrayList<String> subcategories = new ArrayList<String>();

            listItem = customListItem.findViewById(R.id.listItem);
            sno = customListItem.findViewById(R.id.sno);
            name = customListItem.findViewById(R.id.name);
            menu = customListItem.findViewById(R.id.menu);
            image = customListItem.findViewById(R.id.image);
            subcategoryChipgroup = customListItem.findViewById(R.id.subcategoryChipgroup);

            sno.setText(""+(i+1));
            name.setText(data.get(i).getName());
            if(!data.get(i).getImage().equals("")){
//                image.setImageResource(Integer.parseInt(data.get(i).getImage()));
                Glide.with(CategoryActivity.this).load(data.get(i).getImage()).into(image);
            }

            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SubCategoryActivity.class);
                    intent.putExtra("categoryId",data.get(i).getId());
                    intent.putExtra("categoryName",data.get(i).getName());
                    startActivity(intent);
                }
            });

            MainActivity.db.child("Category").child(data.get(i).getId()).child("SubCategory").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        subcategories.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SubCategoryModel data = dataSnapshot.getValue(SubCategoryModel.class);
                            subcategories.add(data.getName());
                        }
                        for (String name:subcategories) {
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
                            subcategoryChipgroup.addView(chip);
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
                            categoryForm("edit",""+data.get(i).getId());
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
                                    MainActivity.db.child("Category").child(data.get(i).getId()).removeValue();
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