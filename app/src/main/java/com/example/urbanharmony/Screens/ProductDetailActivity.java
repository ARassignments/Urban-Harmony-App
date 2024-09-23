package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    String PID = "";
    boolean foundInFvrt = false;
    String fvrtItemId = "";
    int pPrice = 0, pStock = 0, pQty = 1, pDiscount = 0;
    ImageView pImage, wishlistBtn, qtyMinus, qtyAdd;
    TextView pNameTitle, pName, pCategory, pStockText, pRating, pDesc, pQtyText, totalPriceText, pPriceOff, pDiscountText, pSubcategory, pBrand, pStyle;
    LinearLayout qtyContainer, totalContainer, btnAddToCart;
    Button btnOutOfStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            PID = extra.getString("pid");
        }

        pImage = findViewById(R.id.pImage);
        pNameTitle = findViewById(R.id.pNameTitle);
        pName = findViewById(R.id.pName);
        pCategory = findViewById(R.id.pCategory);
        pStockText = findViewById(R.id.pStockText);
        pRating = findViewById(R.id.pRating);
        pDesc = findViewById(R.id.pDesc);
        pQtyText = findViewById(R.id.pQtyText);
        totalPriceText = findViewById(R.id.totalPriceText);
        pPriceOff = findViewById(R.id.pPriceOff);
        pDiscountText = findViewById(R.id.pDiscountText);
        pSubcategory = findViewById(R.id.pSubcategory);
        pBrand = findViewById(R.id.pBrand);
        pStyle = findViewById(R.id.pStyle);
        wishlistBtn = findViewById(R.id.wishlistBtn);
        qtyMinus = findViewById(R.id.qtyMinus);
        qtyAdd = findViewById(R.id.qtyAdd);
        qtyContainer = findViewById(R.id.qtyContainer);
        totalContainer = findViewById(R.id.totalContainer);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnOutOfStock = findViewById(R.id.btnOutOfStock);

        MainActivity.db.child("Products").child(PID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Glide.with(ProductDetailActivity.this).load(snapshot.child("pImage").getValue().toString().trim()).into(pImage);
                    pName.setText(snapshot.child("pName").getValue().toString().trim());
                    pCategory.setText(snapshot.child("pCategory").getValue().toString().trim());
                    pNameTitle.setText(snapshot.child("pCategory").getValue().toString().trim());
                    pDesc.setText(snapshot.child("pDesc").getValue().toString().trim());
                    pSubcategory.setText(snapshot.child("pSubcategory").getValue().toString().trim());
                    pBrand.setText(snapshot.child("pBrand").getValue().toString().trim());
                    pStyle.setText(snapshot.child("pStyle").getValue().toString().trim());

                    if(Integer.parseInt(snapshot.child("pStock").getValue().toString().trim()) < 1){
                        pStockText.setText("Out Of Stock");
                        qtyContainer.setVisibility(View.GONE);
                        btnAddToCart.setVisibility(View.GONE);
                        totalContainer.setVisibility(View.GONE);
                        btnOutOfStock.setVisibility(View.VISIBLE);
                    } else {
                        pStockText.setText(snapshot.child("pStock").getValue().toString().trim()+" Stock");
                    }

                    if(Integer.parseInt(snapshot.child("pDiscount").getValue().toString().trim()) > 0){
                        pDiscountText.setVisibility(View.VISIBLE);
                        pPriceOff.setVisibility(View.VISIBLE);
                        pDiscountText.setText(snapshot.child("pDiscount").getValue().toString().trim()+"% OFF");
                    }

                    pQtyText.setText(""+pQty);
                    double discount = Double.parseDouble(snapshot.child("pDiscount").getValue().toString().trim())/100;
                    double calcDiscount = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) * discount;
                    double totalPrice = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                    totalPriceText.setText("$"+Math.round(totalPrice));
                    pPriceOff.setText("$"+ snapshot.child("pPrice").getValue().toString().trim());
                    setData(
                            snapshot.child("pPrice").getValue().toString().trim(),
                            snapshot.child("pStock").getValue().toString().trim(),
                            snapshot.child("pDiscount").getValue().toString().trim()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MainActivity.db.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (UID.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(PID)) {
                        setQty(ds.child("qty").getValue().toString().trim());
                        MainActivity.db.child("Products").child(ds.child("PID").getValue().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    double discount = Double.parseDouble(datasnapshot.child("pDiscount").getValue().toString())/100;
                                    double calcDiscount = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) * discount;
                                    double totalPrice = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                                    int total = ((int) Math.round(totalPrice)*Integer.parseInt(ds.child("qty").getValue().toString().trim()));
                                    totalPriceText.setText("$"+total);
                                    pPriceOff.setText("$"+(Integer.parseInt(datasnapshot.child("pPrice").getValue().toString().trim()) * Integer.parseInt(ds.child("qty").getValue().toString().trim())));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        fetchWishlist();

        qtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusQty();
            }
        });

        qtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQty();
            }
        });

        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wishlistBtnClicked();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetailActivity.super.onBackPressed();
            }
        });
    }

    public void setQty(String qty){
        pQty = Integer.parseInt(qty);
        pQtyText.setText(""+pQty);
    }
    public void fetchWishlist(){
        MainActivity.db.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int favoriteCount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (UID.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(PID)) {
                            favoriteCount++;
                            setWishlistId(ds.getKey());
                        }
                    }

                    if (favoriteCount > 0) {
                        wishlistBtn.setImageResource(R.drawable.heart_gradient);
                        setWishlistStatus(true);
                    } else {
                        setWishlistStatus(false);
                    }
                } else {
                    setWishlistStatus(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }
    public void setWishlistStatus(Boolean status){
        foundInFvrt = status;
    }
    public void setWishlistId(String Id){
        fvrtItemId = Id;
    }

    public void wishlistBtnClicked(){
        if(foundInFvrt == true){
            MainActivity.db.child("Wishlist").child(fvrtItemId).removeValue();
            wishlistBtn.setImageResource(R.drawable.heart_outlined);
            Dialog alertdialog = new Dialog(ProductDetailActivity.this);
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
        } else if(foundInFvrt == false) {
            HashMap<String, String> Obj = new HashMap<String,String>();
            Obj.put("UID",UID);
            Obj.put("PID",PID);
            MainActivity.db.child("Wishlist").push().setValue(Obj);
            Dialog alertdialog = new Dialog(ProductDetailActivity.this);
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
        fetchWishlist();
    }
    public void setData(String pPriceVal, String pStockVal, String pDiscountVal){
        pPrice = Integer.parseInt(pPriceVal);
        pStock = Integer.parseInt(pStockVal);
        pDiscount = Integer.parseInt(pDiscountVal);
    }
    public void addQty(){
        if(pQty < pStock ){
            pQty++;
            pQtyText.setText(""+pQty);
            double discount = Double.parseDouble(""+pDiscount)/100;
            double calcDiscount = Double.parseDouble(""+pPrice) * discount;
            double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
            totalPriceText.setText("$"+(Math.round(totalPrice) * pQty));
            pPriceOff.setText("$"+(pPrice * pQty));
        }
    }
    public void minusQty(){
        if (pQty > 1){
            pQty--;
            pQtyText.setText(""+pQty);
            double discount = Double.parseDouble(""+pDiscount)/100;
            double calcDiscount = Double.parseDouble(""+pPrice) * discount;
            double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
            totalPriceText.setText("$"+(Math.round(totalPrice) * pQty));
            pPriceOff.setText("$"+(pPrice * pQty));
        }
    }
    public void addToCart(){
        MainActivity.db.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartCount = 0;
                String cartId = "";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (UID.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(PID)) {
                        cartCount++;
                        cartId = ds.getKey();
                    }
                }

                if (cartCount > 0) {
                    MainActivity.db.child("AddToCart").child(cartId).child("qty").setValue(pQtyText.getText().toString().trim());
                    Dialog alertdialog = new Dialog(ProductDetailActivity.this);
                    alertdialog.setContentView(R.layout.dialog_success);
                    alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    alertdialog.getWindow().setGravity(Gravity.CENTER);
                    alertdialog.setCancelable(false);
                    alertdialog.setCanceledOnTouchOutside(false);
                    TextView message = alertdialog.findViewById(R.id.msgDialog);
                    message.setText("Product Quantity Updated into Cart Successfully");
                    alertdialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertdialog.dismiss();
                        }
                    },2000);
                } else {
                    HashMap<String,String> Obj = new HashMap<String, String>();
                    Obj.put("PID",PID);
                    Obj.put("UID",UID);
                    Obj.put("qty",pQtyText.getText().toString().trim());
                    MainActivity.db.child("AddToCart").push().setValue(Obj);
                    Dialog alertdialog = new Dialog(ProductDetailActivity.this);
                    alertdialog.setContentView(R.layout.dialog_success);
                    alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    alertdialog.getWindow().setGravity(Gravity.CENTER);
                    alertdialog.setCancelable(false);
                    alertdialog.setCanceledOnTouchOutside(false);
                    TextView message = alertdialog.findViewById(R.id.msgDialog);
                    message.setText("Product Added into Cart Successfully");
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
}