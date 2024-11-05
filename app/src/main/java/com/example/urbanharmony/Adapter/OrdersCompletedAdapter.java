package com.example.urbanharmony.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.urbanharmony.Models.OrderModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.OrderDetailActivity;
import com.example.urbanharmony.Screens.OrderTrackActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersCompletedAdapter extends RecyclerView.Adapter<OrdersCompletedAdapter.ViewHolder>{
    Context context;
    ArrayList<OrderModel> data;

    public OrdersCompletedAdapter(Context context, ArrayList<OrderModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public OrdersCompletedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_custom_listview, parent, false);
        // Passing view to ViewHolder
        OrdersCompletedAdapter.ViewHolder viewHolder = new OrdersCompletedAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersCompletedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int i) {
        holder.orderId.setText("#"+data.get(i).getOrderId().toUpperCase());
        holder.orderAmount.setText("$"+data.get(i).getTotalOverallAmount());
        if(data.get(i).getStatus().equals("1")){
            holder.orderStatus.setText("In Process");
        } else if(data.get(i).getStatus().equals("2")){
            holder.orderStatus.setText("In Packaging");
        } else if(data.get(i).getStatus().equals("3")){
            holder.orderStatus.setText("In Shipping");
        } else if(data.get(i).getStatus().equals("4")){
            holder.orderStatus.setText("Completed");
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
                intent.putExtra("OID",data.get(i).getId());
                intent.putExtra("OrderId",data.get(i).getOrderId());
                intent.putExtra("OItems",holder.orderItems.getText().toString());
                intent.putExtra("OTotalItemAmount",data.get(i).getTotalItemAmount());
                intent.putExtra("OTotalShippingAmount",data.get(i).getTotalShippingAmount());
                intent.putExtra("OTotalOverallAmount",data.get(i).getTotalOverallAmount());
                intent.putExtra("OCreatedOn",data.get(i).getCreatedOn());
                intent.putExtra("OLocationAddress",data.get(i).getLocationAddress());
                intent.putExtra("OPaymentMethod",data.get(i).getPaymentMethod());
                v.getContext().startActivity(intent);
            }
        });

        holder.orderBtn.setVisibility(View.GONE);

        holder.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OrderTrackActivity.class);
                intent.putExtra("OID",data.get(i).getId());
                intent.putExtra("OrderId",data.get(i).getOrderId());
                intent.putExtra("OItems",holder.orderItems.getText().toString());
                intent.putExtra("OAmount",data.get(i).getTotalOverallAmount());
                intent.putExtra("OStatus",data.get(i).getStatus());
                v.getContext().startActivity(intent);
            }
        });

        MainActivity.db.child("Orders").child(data.get(i).getId()).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int count = 0;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        count++;
                    }
                    holder.orderItems.setText("Items: "+count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MainActivity.db.child("Orders").child(data.get(i).getId()).child("items").child("item_1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String PID = snapshot.child("PID").getValue().toString().trim();
                    MainActivity.db.child("Products").child(PID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            if(datasnapshot.exists()){
                                Glide.with(context).load(datasnapshot.child("pImage").getValue().toString().trim()).into(holder.pImage);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(i==data.size()-1){
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), holder.itemView.getPaddingTop(),holder.itemView.getPaddingRight(), 30);
        }
        if(i==0){
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(), 0,holder.itemView.getPaddingRight(), 0);
        }
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).setStartDelay(i * 50).start();

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderItems, orderStatus, orderAmount;
        LinearLayout item;
        ImageView pImage;
        Button orderBtn;

        public ViewHolder(View view) {
            super(view);
            orderId = view.findViewById(R.id.orderId);
            orderItems = view.findViewById(R.id.orderItems);
            orderStatus = view.findViewById(R.id.orderStatus);
            orderAmount = view.findViewById(R.id.orderAmount);
            pImage = view.findViewById(R.id.pImage);
            orderBtn = view.findViewById(R.id.orderBtn);
            item = view.findViewById(R.id.item);
        }
    }
}
