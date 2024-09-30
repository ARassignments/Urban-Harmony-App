package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.Adapter.ProductWishlistAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.RecyclerDecoration.GridSpacingItemDecoration;
import com.example.urbanharmony.Screens.ProductDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductWishlistFragment extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    ListView gridView;
    RecyclerView productView;
    LinearLayout notfoundContainer;
    ArrayList<WishlistModel> datalist = new ArrayList<>();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_wishlist, container, false);
        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        gridView = view.findViewById(R.id.gridView);
        productView = view.findViewById(R.id.productView);
        notfoundContainer = view.findViewById(R.id.notfoundContainer);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2); // 2 columns
        productView.setLayoutManager(gridLayoutManager);

        // Add spacing of 10dp between items
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing); // Define this in dimens.xml or hardcode
        GridSpacingItemDecoration spacingItemDecoration = new GridSpacingItemDecoration(2, spacingInPixels, true);

        // Add ItemDecoration to the RecyclerView
        productView.addItemDecoration(spacingItemDecoration);


        fetchData("");
        return view;
    }

    public void fetchData(String data){
        MainActivity.db.child("Wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("UID").getValue().toString().equals(UID)){
                            WishlistModel model = new WishlistModel(ds.getKey(),
                                    ds.child("UID").getValue().toString(),
                                    ds.child("PID").getValue().toString()
                            );
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
//                        gridView.setVisibility(View.VISIBLE);
                        productView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        ProductWishlistAdapter adapter = new ProductWishlistAdapter(getContext(),datalist);
                        productView.setAdapter(adapter);
//                        gridView.setAdapter(adapter);
                    } else {
//                        gridView.setVisibility(View.GONE);
                        productView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
//                    gridView.setVisibility(View.GONE);
                    productView.setVisibility(View.GONE);
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
        ArrayList<WishlistModel> data;

        public MyAdapter(Context context, ArrayList<WishlistModel> data) {
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

            MainActivity.db.child("Products").child(data.get(i).getPID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(!snapshot.child("pDiscount").getValue().toString().equals("0")){
                            pDiscount.setVisibility(View.VISIBLE);
                            pDiscount.setText(snapshot.child("pDiscount").getValue().toString()+"% OFF");
                        } else {
                            pPriceOff.setVisibility(View.GONE);
                        }
                        pName.setText(snapshot.child("pName").getValue().toString());
                        pStock.setText(snapshot.child("pStock").getValue().toString()+" Stock");
                        pPriceOff.setText("$"+snapshot.child("pPrice").getValue().toString());
                        Glide.with(context).load(snapshot.child("pImage").getValue().toString()).into(pImage);

                        double discount = Double.parseDouble(snapshot.child("pDiscount").getValue().toString())/100;
                        double calcDiscount = Double.parseDouble(snapshot.child("pPrice").getValue().toString()) * discount;
                        double totalPrice = Double.parseDouble(snapshot.child("pPrice").getValue().toString()) - calcDiscount;
                        pPrice.setText("$"+Math.round(totalPrice));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("pid",data.get(i).getPID());
                    startActivity(intent);
                }
            });

            wishlistBtn.setImageResource(R.drawable.heart_gradient);

            wishlistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.db.child("Wishlist").child(data.get(i).getId()).removeValue();
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
                            fetchData("");
                        }
                    },2000);
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