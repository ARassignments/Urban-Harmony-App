package com.example.urbanharmony.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.UsersModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.DesignerDetailActivity;
import com.example.urbanharmony.Screens.ProductDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductWishlistAdapter extends RecyclerView.Adapter<ProductWishlistAdapter.ViewHolder>{
    Context context;
    ArrayList<WishlistModel> data;

    public ProductWishlistAdapter(Context context, ArrayList<WishlistModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ProductWishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_custom_listview, parent, false);
        // Passing view to ViewHolder
        ProductWishlistAdapter.ViewHolder viewHolder = new ProductWishlistAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductWishlistAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.wishlistBtn.setImageResource(R.drawable.heart_gradient);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("pid",data.get(position).getPID());
                v.getContext().startActivity(intent);
            }
        });

        MainActivity.db.child("Products").child(data.get(position).getPID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!snapshot.child("pDiscount").getValue().toString().equals("0")){
                        holder.pDiscount.setVisibility(View.VISIBLE);
                        holder.pDiscount.setText(snapshot.child("pDiscount").getValue().toString()+"% OFF");
                    } else {
                        holder.pPriceOff.setVisibility(View.GONE);
                    }
                    holder.pName.setText(snapshot.child("pName").getValue().toString());
                    holder.pStock.setText(snapshot.child("pStock").getValue().toString()+" Stock");
                    holder.pPriceOff.setText("$"+snapshot.child("pPrice").getValue().toString());
                    Glide.with(context).load(snapshot.child("pImage").getValue().toString()).into(holder.pImage);

                    double discount = Double.parseDouble(snapshot.child("pDiscount").getValue().toString())/100;
                    double calcDiscount = Double.parseDouble(snapshot.child("pPrice").getValue().toString()) * discount;
                    double totalPrice = Double.parseDouble(snapshot.child("pPrice").getValue().toString()) - calcDiscount;
                    holder.pPrice.setText("$"+Math.round(totalPrice));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.db.child("Wishlist").child(data.get(position).getId()).removeValue();
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
            }
        });
        if(position==data.size()-1){
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), holder.itemView.getPaddingTop(),holder.itemView.getPaddingRight(), 30);
        }
        if(position==0){
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), 0,holder.itemView.getPaddingRight(), 0);
        }
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).setStartDelay(position * 50).start();

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pImage, wishlistBtn, editBtn, deleteBtn;
        TextView pDiscount, pName, pRating, pStock, pPrice, pPriceOff;
        LinearLayout options, item;

        public ViewHolder(View view) {
            super(view);
            pImage = view.findViewById(R.id.pImage);
            wishlistBtn = view.findViewById(R.id.wishlistBtn);
            editBtn = view.findViewById(R.id.editBtn);
            deleteBtn = view.findViewById(R.id.deleteBtn);
            pDiscount = view.findViewById(R.id.pDiscount);
            pName = view.findViewById(R.id.pName);
            pRating = view.findViewById(R.id.pRating);
            pStock = view.findViewById(R.id.pStock);
            pPrice = view.findViewById(R.id.pPrice);
            pPriceOff = view.findViewById(R.id.pPriceOff);
            options = view.findViewById(R.id.options);
            item = view.findViewById(R.id.item);
        }
    }
}
