package com.example.urbanharmony.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.ProductModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.DashboardActivity;
import com.example.urbanharmony.Screens.ProductDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<ProductModel> data;
    String UID;
    boolean foundInFvrt = false;
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOAD_MORE = 1;

    public ProductAdapter(Context context, List<ProductModel> data, String UID) {
        this.context = context;
        this.data = data;
        this.UID = UID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_home_custom_listview, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_loadmore_home_custom_listview, parent, false);
            return new LoadMoreViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(holder instanceof ViewHolder){
            ((ViewHolder) holder).item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                    intent.putExtra("pid",data.get(position).getId());
                    v.getContext().startActivity(intent);
                }
            });

            ((ViewHolder) holder).options.setVisibility(View.GONE);
            if(!data.get(position).getpDiscount().equals("0")){
                ((ViewHolder) holder).pDiscount.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).pDiscount.setText(data.get(position).getpDiscount()+"% OFF");
            } else {
                ((ViewHolder) holder).pPriceOff.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).pName.setText(data.get(position).getpName());
            ((ViewHolder) holder).pStock.setText(data.get(position).getpStock()+" Stock");
            ((ViewHolder) holder).pPriceOff.setText("$"+data.get(position).getpPrice());
            Glide.with(context).load(data.get(position).getpImage()).into(((ViewHolder) holder).pImage);

            double discount = Double.parseDouble(data.get(position).getpDiscount())/100;
            double calcDiscount = Double.parseDouble(data.get(position).getpPrice()) * discount;
            double totalPrice = Double.parseDouble(data.get(position).getpPrice()) - calcDiscount;
            ((ViewHolder) holder).pPrice.setText("$"+Math.round(totalPrice));

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
                                        if (model.getUID().equals(UID) && model.getPID().equals(data.get(position).getId())) {
                                            favoriteCount++;
                                        }
                                    }
                                }
                                ((ViewHolder) holder).wishlistBtn.setImageResource(R.drawable.heart_outlined);
                                if (favoriteCount > 0) {
                                    ((ViewHolder) holder).wishlistBtn.setImageResource(R.drawable.heart_gradient);
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

            ((ViewHolder) holder).wishlistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.db.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (UID.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(data.get(position).getId())) {
                                        String fvrtItemId = ds.getKey();
                                        foundInFvrt = true;
                                        MainActivity.db.child("Wishlist").child(fvrtItemId).removeValue();
                                        ((ViewHolder) holder).wishlistBtn.setImageResource(R.drawable.heart_outlined);
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
                                Obj.put("PID",data.get(position).getId());
                                MainActivity.db.child("Wishlist").push().setValue(Obj);
                                ((ViewHolder) holder).wishlistBtn.setImageResource(R.drawable.heart_gradient);
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
        } else if(holder instanceof LoadMoreViewHolder){
            ((LoadMoreViewHolder) holder).item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DashboardActivity) context).openCatalogPage();
                }
            });
        }



//        if(position==data.size()-1){
//            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), holder.itemView.getPaddingTop(),holder.itemView.getPaddingRight(), 30);
//        }
        if(position==0){
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), 0,holder.itemView.getPaddingRight(), 0);
        }
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).setStartDelay(position * 2).start();

    }

    @Override
    public int getItemCount() {
        return data.size() + 1; // Add one for the "Load More" button
    }

    @Override
    public int getItemViewType(int position) {
        return position == data.size() ? VIEW_TYPE_LOAD_MORE : VIEW_TYPE_ITEM;
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

    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
        }
    }
}
