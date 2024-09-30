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
import com.example.urbanharmony.Models.WishlistDesignModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.ProductDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DesignWishlistAdapter extends RecyclerView.Adapter<DesignWishlistAdapter.ViewHolder>{
    Context context;
    ArrayList<WishlistDesignModel> data;

    public DesignWishlistAdapter(Context context, ArrayList<WishlistDesignModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public DesignWishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mydesigns_custom_listview, parent, false);
        // Passing view to ViewHolder
        DesignWishlistAdapter.ViewHolder viewHolder = new DesignWishlistAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DesignWishlistAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.wishlistBtn.setImageResource(R.drawable.heart_gradient);

        MainActivity.db.child("Designs").child(data.get(position).getDID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.dName.setText(snapshot.child("dName").getValue().toString());
                    Glide.with(context).load(snapshot.child("dImage").getValue().toString()).into(holder.dImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.db.child("WishlistDesigns").child(data.get(position).getId()).removeValue();
                Dialog alertdialog = new Dialog(context);
                alertdialog.setContentView(R.layout.dialog_success);
                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertdialog.getWindow().setGravity(Gravity.CENTER);
                alertdialog.setCancelable(false);
                alertdialog.setCanceledOnTouchOutside(false);
                TextView message = alertdialog.findViewById(R.id.msgDialog);
                message.setText("Design Removed From Wishlist Successfully");
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
        ImageView dImage, editBtn, deleteBtn, wishlistBtn;
        TextView dName;
        LinearLayout options, item;

        public ViewHolder(View view) {
            super(view);
            dImage = view.findViewById(R.id.dImage);
            wishlistBtn = view.findViewById(R.id.wishlistBtn);
            editBtn = view.findViewById(R.id.editBtn);
            deleteBtn = view.findViewById(R.id.deleteBtn);
            dName = view.findViewById(R.id.dName);
            options = view.findViewById(R.id.options);
            item = view.findViewById(R.id.item);
        }
    }
}
