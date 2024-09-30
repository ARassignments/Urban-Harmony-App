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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.DesignModel;
import com.example.urbanharmony.Models.ProjectModel;
import com.example.urbanharmony.Models.WishlistDesignModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.ProjectDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DesignerDesignAdapter extends RecyclerView.Adapter<DesignerDesignAdapter.ViewHolder>{
    Context context;
    ArrayList<DesignModel> data;
    String UID;
    boolean foundInFvrt = false;

    public DesignerDesignAdapter(Context context, ArrayList<DesignModel> data, String UID) {
        this.context = context;
        this.data = data;
        this.UID = UID;
    }

    @NonNull
    @Override
    public DesignerDesignAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.designer_designs_custom_listview, parent, false);
        // Passing view to ViewHolder
        DesignerDesignAdapter.ViewHolder viewHolder = new DesignerDesignAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DesignerDesignAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.pName.setText(data.get(position).getdName());
        Glide.with(holder.itemView.getContext()).load(data.get(position).getdImage()).into(holder.dImage);

        if (UID != null && !UID.equals("")) {
            try {
                MainActivity.db.child("WishlistDesigns").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int favoriteCount = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                WishlistDesignModel model = ds.getValue(WishlistDesignModel.class);
                                if(data.size()>0){
                                    if (model.getUID().equals(UID) && model.getDID().equals(data.get(position).getId())) {
                                        favoriteCount++;
                                    }
                                }
                            }
                            holder.wishlistBtn.setImageResource(R.drawable.heart_outlined);
                            if (favoriteCount > 0) {
                                holder.wishlistBtn.setImageResource(R.drawable.heart_gradient);
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
        holder.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.db.child("WishlistDesigns").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (UID.equals(ds.child("UID").getValue()) && ds.child("DID").getValue().equals(data.get(position).getId())) {
                                    String fvrtItemId = ds.getKey();
                                    foundInFvrt = true;
                                    MainActivity.db.child("WishlistDesigns").child(fvrtItemId).removeValue();
                                    holder.wishlistBtn.setImageResource(R.drawable.heart_outlined);
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

                                    break;
                                }
                            }
                        }

                        if (!foundInFvrt) {
                            HashMap<String, String> Obj = new HashMap<String,String>();
                            Obj.put("UID",UID);
                            Obj.put("DID",data.get(position).getId());
                            MainActivity.db.child("WishlistDesigns").push().setValue(Obj);
                            holder.wishlistBtn.setImageResource(R.drawable.heart_gradient);
                            Dialog alertdialog = new Dialog(context);
                            alertdialog.setContentView(R.layout.dialog_success);
                            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            alertdialog.getWindow().setGravity(Gravity.CENTER);
                            alertdialog.setCancelable(false);
                            alertdialog.setCanceledOnTouchOutside(false);
                            TextView message = alertdialog.findViewById(R.id.msgDialog);
                            message.setText("Design is Added into Wishlist");
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
//        holder.item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ProjectDetailActivity.class);
//                intent.putExtra("pid",data.get(position).getId());
//                v.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pName;
        ImageView dImage, wishlistBtn;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            pName = view.findViewById(R.id.pName);
            item = view.findViewById(R.id.item);
            dImage = view.findViewById(R.id.dImage);
            wishlistBtn = view.findViewById(R.id.wishlistBtn);
        }
    }
}
