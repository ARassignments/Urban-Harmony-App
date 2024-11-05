package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.Adapter.OrdersActiveAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.OrderModel;
import com.example.urbanharmony.Models.PaymentMethodModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.CheckoutActivity;
import com.example.urbanharmony.Screens.SelectPaymentMethodActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class OrderActiveFragment extends Fragment {
    View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    RecyclerView listView;
    LinearLayout loader, notfoundContainer;
    ArrayList<OrderModel> datalist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_active, container, false);
        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = view.findViewById(R.id.listView);
        notfoundContainer = view.findViewById(R.id.notfoundContainer);
        loader = view.findViewById(R.id.loader);

        fetchData("");
        return view;
    }

    public void fetchData(String data){
        MainActivity.db.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("UID").getValue().toString().equals(UID)){
                            if(!ds.child("status").getValue().toString().equals("4")){
                                OrderModel model = new OrderModel(ds.getKey(),
                                        ds.child("orderId").getValue().toString(),
                                        ds.child("UID").getValue().toString(),
                                        ds.child("totalItemAmount").getValue().toString(),
                                        ds.child("totalShippingAmount").getValue().toString(),
                                        ds.child("totalOverallAmount").getValue().toString(),
                                        ds.child("shippingName").getValue().toString(),
                                        ds.child("shippingDetail").getValue().toString(),
                                        ds.child("locationName").getValue().toString(),
                                        ds.child("locationAddress").getValue().toString(),
                                        ds.child("paymentMethod").getValue().toString(),
                                        ds.child("status").getValue().toString(),
                                        ds.child("createdOn").getValue().toString(),
                                        ds.child("items").getValue().toString()
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
                        OrdersActiveAdapter adapter = new OrdersActiveAdapter(getContext(),datalist);
                        listView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                    }
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

}