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
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
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
import com.example.urbanharmony.Models.ProductModel;
import com.example.urbanharmony.Models.SubCategoryModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.Fragments.CatalogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilteredProductsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    GridView gridView;
    LinearLayout loader, notfoundContainer;
    TextView categoryTitle;
    ChipGroup categoryChipgroup;
    Chip allChip;
    ArrayList<ProductModel> datalist = new ArrayList<>();
    String categoryId, categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filtered_products);
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

        gridView = findViewById(R.id.gridView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);
        categoryTitle = findViewById(R.id.categoryTitle);
        categoryChipgroup = findViewById(R.id.categoryChipgroup);
        allChip = findViewById(R.id.allChip);

        allChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData("");
            }
        });

        ArrayList<String> subcategories = new ArrayList<String>();
        MainActivity.db.child("Category").child(categoryId).child("SubCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subcategories.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SubCategoryModel data = dataSnapshot.getValue(SubCategoryModel.class);
                        subcategories.add(data.getName());
                    }
                    for (String name:subcategories) {
                        Chip chip = new Chip(new ContextThemeWrapper(FilteredProductsActivity.this, R.style.CustomCategoryChipStyle));
                        chip.setChipDrawable(ChipDrawable.createFromAttributes(FilteredProductsActivity.this,null,0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice));
                        chip.setText(""+name);
                        chip.setCheckable(true);
                        chip.setChipIconSize(30);
                        chip.setCheckedIconEnabled(true);
                        chip.setChipStartPadding(10);
                        chip.setChipEndPadding(10);
                        chip.setCheckedIconVisible(true);
                        chip.setCheckedIconTint(getResources().getColorStateList(R.color.accent_50));
                        chip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchData(chip.getText().toString());
                            }
                        });
                        categoryChipgroup.addView(chip);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fetchData("");

        categoryTitle.setText(categoryName);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilteredProductsActivity.super.onBackPressed();
            }
        });
    }

    public void fetchData(String data){
        MainActivity.db.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            if(ds.child("pCategory").getValue().toString().trim().equals(categoryName)){
                                ProductModel model = new ProductModel(ds.getKey(),
                                        ds.child("pName").getValue().toString(),
                                        ds.child("pPrice").getValue().toString(),
                                        ds.child("pStock").getValue().toString(),
                                        ds.child("pDiscount").getValue().toString(),
                                        ds.child("pImage").getValue().toString(),
                                        ds.child("pDesc").getValue().toString(),
                                        ds.child("pCategory").getValue().toString(),
                                        ds.child("pSubcategory").getValue().toString(),
                                        ds.child("pBrand").getValue().toString(),
                                        ds.child("pStyle").getValue().toString(),
                                        ds.child("status").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        } else {
                            if(ds.child("pCategory").getValue().toString().trim().equals(categoryName)&&ds.child("pSubcategory").getValue().toString().trim().equals(data)){
                                ProductModel model = new ProductModel(ds.getKey(),
                                        ds.child("pName").getValue().toString(),
                                        ds.child("pPrice").getValue().toString(),
                                        ds.child("pStock").getValue().toString(),
                                        ds.child("pDiscount").getValue().toString(),
                                        ds.child("pImage").getValue().toString(),
                                        ds.child("pDesc").getValue().toString(),
                                        ds.child("pCategory").getValue().toString(),
                                        ds.child("pSubcategory").getValue().toString(),
                                        ds.child("pBrand").getValue().toString(),
                                        ds.child("pStyle").getValue().toString(),
                                        ds.child("status").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        MyAdapter adapter = new MyAdapter(FilteredProductsActivity.this,datalist);
                        gridView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    loader.setVisibility(View.GONE);
                    gridView.setVisibility(View.GONE);
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
        ArrayList<ProductModel> data;
        boolean foundInFvrt = false;

        public MyAdapter(Context context, ArrayList<ProductModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.products_custom_listview,null);
            ImageView pImage, wishlistBtn, editBtn, deleteBtn;
            TextView pDiscount, pName, pRating, pStock, pPrice, pPriceOff;
            LinearLayout options, item;

            pImage = customListItem.findViewById(R.id.pImage);
            wishlistBtn = customListItem.findViewById(R.id.wishlistBtn);
            editBtn = customListItem.findViewById(R.id.editBtn);
            deleteBtn = customListItem.findViewById(R.id.deleteBtn);
            pDiscount = customListItem.findViewById(R.id.pDiscount);
            pName = customListItem.findViewById(R.id.pName);
            pRating = customListItem.findViewById(R.id.pRating);
            pStock = customListItem.findViewById(R.id.pStock);
            pPrice = customListItem.findViewById(R.id.pPrice);
            pPriceOff = customListItem.findViewById(R.id.pPriceOff);
            options = customListItem.findViewById(R.id.options);
            item = customListItem.findViewById(R.id.item);

            options.setVisibility(View.GONE);
            if(!data.get(i).getpDiscount().equals("0")){
                pDiscount.setVisibility(View.VISIBLE);
                pDiscount.setText(data.get(i).getpDiscount()+"% OFF");
            } else {
                pPriceOff.setVisibility(View.GONE);
            }
            pName.setText(data.get(i).getpName());
            pStock.setText(data.get(i).getpStock()+" Stock");
            pPriceOff.setText("$"+data.get(i).getpPrice());
            Glide.with(context).load(data.get(i).getpImage()).into(pImage);

            double discount = Double.parseDouble(data.get(i).getpDiscount())/100;
            double calcDiscount = Double.parseDouble(data.get(i).getpPrice()) * discount;
            double totalPrice = Double.parseDouble(data.get(i).getpPrice()) - calcDiscount;
            pPrice.setText("$"+Math.round(totalPrice));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("pid",data.get(i).getId());
                    startActivity(intent);
                }
            });

            if (UID != null && !UID.equals("")) {
                try {
                    MainActivity.db.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int favoriteCount = 0;
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    WishlistModel model = ds.getValue(WishlistModel.class);
                                    if(data.size()>0){
                                        if (model.getUID().equals(UID) && model.getPID().equals(data.get(i).getId())) {
                                            favoriteCount++;
                                        }
                                    }
                                }
                                wishlistBtn.setImageResource(R.drawable.heart_outlined);
                                if (favoriteCount > 0) {
                                    wishlistBtn.setImageResource(R.drawable.heart_gradient);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "onCancelled: " + error.getMessage());
                        }
                    });

                } catch (Exception e){

                }
            }

            wishlistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.db.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (UID.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(data.get(i).getId())) {
                                        String fvrtItemId = ds.getKey();
                                        foundInFvrt = true;
                                        MainActivity.db.child("Wishlist").child(fvrtItemId).removeValue();
                                        wishlistBtn.setImageResource(R.drawable.heart_outlined);
                                        Dialog alertdialog = new Dialog(context);
                                        alertdialog.setContentView(R.layout.dialog_success);
                                        alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                        alertdialog.getWindow().setGravity(Gravity.CENTER);
                                        alertdialog.setCancelable(false);
                                        alertdialog.setCanceledOnTouchOutside(false);
                                        TextView message = alertdialog.findViewById(R.id.msgDialog);
                                        message.setText("Product Removed From Wishlist Successfully");
                                        alertdialog.show();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                alertdialog.dismiss();
                                            }
                                        },2000);

                                        break;
                                    }
                                }
                            }

                            if (!foundInFvrt) {
                                HashMap<String, String> Obj = new HashMap<String,String>();
                                Obj.put("UID",UID);
                                Obj.put("PID",data.get(i).getId());
                                MainActivity.db.child("Wishlist").push().setValue(Obj);
                                wishlistBtn.setImageResource(R.drawable.heart_gradient);
                                Dialog alertdialog = new Dialog(context);
                                alertdialog.setContentView(R.layout.dialog_success);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                TextView message = alertdialog.findViewById(R.id.msgDialog);
                                message.setText("Product is Added into Wishlist");
                                alertdialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                    }
                                },2000);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "onCancelled: " + error.getMessage());
                        }
                    });
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