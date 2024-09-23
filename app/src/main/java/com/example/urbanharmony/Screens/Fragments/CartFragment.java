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

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.AddToCartModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.CheckoutActivity;
import com.example.urbanharmony.Screens.ProductDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    ListView listView;
    LinearLayout notfoundContainer, cartContainer, btnCheckout;
    TextView totalPrice;
    ArrayList<AddToCartModel> datalist = new ArrayList<>();
    View view;
    int grandTotal = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = view.findViewById(R.id.listView);
        notfoundContainer = view.findViewById(R.id.notfoundContainer);
        cartContainer = view.findViewById(R.id.cartContainer);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        totalPrice = view.findViewById(R.id.totalPrice);
        fetchData("");

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), CheckoutActivity.class));
            }
        });

        MainActivity.db.child("AddToCart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setGrandTotal(0,false);
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(UID.equals(ds.child("UID").getValue().toString().trim())){
                        MainActivity.db.child("Products").child(ds.child("PID").getValue().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    double discount = Double.parseDouble(datasnapshot.child("pDiscount").getValue().toString())/100;
                                    double calcDiscount = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) * discount;
                                    double totalPrice = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                                    int total = ((int) Math.round(totalPrice)*Integer.parseInt(ds.child("qty").getValue().toString().trim()));
                                    setGrandTotal(total,true);
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

            }
        });

        return view;
    }

    public void fetchData(String data){
        MainActivity.db.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("UID").getValue().toString().equals(UID)){
                            AddToCartModel model = new AddToCartModel(ds.getKey(),
                                    ds.child("PID").getValue().toString().trim(),
                                    ds.child("UID").getValue().toString().trim(),
                                    ds.child("qty").getValue().toString().trim()
                            );
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
                        cartContainer.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        MyAdapter adapter = new MyAdapter(getContext(),datalist);
                        listView.setAdapter(adapter);
                    } else {
                        cartContainer.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    cartContainer.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void GrandTotal(int itemPrice, TextView totalTextView){
        totalTextView.setText("$"+itemPrice);
    }

    public void setGrandTotal(int itemPrice,boolean status){
        if(status == true){
            grandTotal += itemPrice;
            CartFragment.GrandTotal(grandTotal,totalPrice);
        } else {
            grandTotal = 0;
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<AddToCartModel> data;

        public MyAdapter(Context context, ArrayList<AddToCartModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.addtocart_custom_listview,null);
            ImageView pImage, qtyMinus, qtyAdd, delete;
            TextView pName, pQtyTextView, pPriceTextView, pPriceOffTextView, pDiscountTextView;

            pImage = customListItem.findViewById(R.id.pImage);
            pName = customListItem.findViewById(R.id.pName);
            pPriceTextView = customListItem.findViewById(R.id.pPrice);
            pPriceOffTextView = customListItem.findViewById(R.id.pPriceOff);
            pQtyTextView = customListItem.findViewById(R.id.pQty);
            qtyMinus = customListItem.findViewById(R.id.qtyMinus);
            qtyAdd = customListItem.findViewById(R.id.qtyAdd);
            delete = customListItem.findViewById(R.id.delete);
            pDiscountTextView = customListItem.findViewById(R.id.pDiscount);

            MainActivity.db.child("Products").child(data.get(i).getPID()).addListenerForSingleValueEvent(new ValueEventListener() {
                int pPrice = 0, pStock = 0, pQty = 0, pDiscount = 0;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        pQty = Integer.parseInt(data.get(i).getQty());
                        pPrice = Integer.parseInt(snapshot.child("pPrice").getValue().toString().trim());
                        pStock = Integer.parseInt(snapshot.child("pStock").getValue().toString().trim());
                        pDiscount = Integer.parseInt(snapshot.child("pDiscount").getValue().toString().trim());
                        pQtyTextView.setText(""+pQty);
                        Glide.with(context).load(snapshot.child("pImage").getValue().toString().trim()).into(pImage);
                        pName.setText(snapshot.child("pName").getValue().toString().trim());
                        pPriceOffTextView.setText("$"+(pPrice*pQty));
                        if(pDiscount > 0){
                            pPriceOffTextView.setVisibility(View.VISIBLE);
                            pDiscountTextView.setVisibility(View.VISIBLE);
                            pDiscountTextView.setText(pDiscount+"% OFF");
                        }
                        double discount = Double.parseDouble(snapshot.child("pDiscount").getValue().toString())/100;
                        double calcDiscount = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) * discount;
                        double totalPrice = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                        pPriceTextView.setText("$"+(Math.round(totalPrice)*pQty));

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog loaddialog = new Dialog(context);
                                loaddialog.setContentView(R.layout.dialog_bottom_cart_delete);
                                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
                                loaddialog.getWindow().setGravity(Gravity.BOTTOM);
                                loaddialog.setCancelable(false);
                                loaddialog.setCanceledOnTouchOutside(false);
                                Button cancelBtn, yesBtn;
                                TextView pQtyDialog, pNameDialog, pPriceDialog, pPriceOffDialog, pDiscountDialog;
                                ImageView pImageDialog;
                                cancelBtn = loaddialog.findViewById(R.id.cancelBtn);
                                yesBtn = loaddialog.findViewById(R.id.yesBtn);
                                pQtyDialog = loaddialog.findViewById(R.id.pQty);
                                pImageDialog = loaddialog.findViewById(R.id.pImage);
                                pNameDialog = loaddialog.findViewById(R.id.pName);
                                pPriceDialog = loaddialog.findViewById(R.id.pPrice);
                                pPriceOffDialog = loaddialog.findViewById(R.id.pPriceOff);
                                pDiscountDialog = loaddialog.findViewById(R.id.pDiscount);
                                if(pDiscount > 0){
                                    pPriceOffDialog.setVisibility(View.VISIBLE);
                                    pDiscountDialog.setVisibility(View.VISIBLE);
                                    pDiscountDialog.setText(pDiscount+"% OFF");
                                }
                                pQtyDialog.setText(""+pQty);
                                pNameDialog.setText(snapshot.child("pName").getValue().toString().trim());
                                pPriceDialog.setText("$"+(Math.round(totalPrice)*pQty));
                                pPriceOffDialog.setText("$"+(pPrice*pQty));
                                Glide.with(context).load(snapshot.child("pImage").getValue().toString().trim()).into(pImageDialog);
                                yesBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MainActivity.db.child("AddToCart").child(data.get(i).getId()).removeValue();
                                        fetchData("");
                                        Dialog alertdialog = new Dialog(context);
                                        alertdialog.setContentView(R.layout.dialog_success);
                                        alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                        alertdialog.getWindow().setGravity(Gravity.CENTER);
                                        alertdialog.setCancelable(false);
                                        alertdialog.setCanceledOnTouchOutside(false);
                                        TextView message = alertdialog.findViewById(R.id.msgDialog);
                                        message.setText("Product Remove Successfully From Cart!!!");
                                        alertdialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                alertdialog.dismiss();
                                                loaddialog.dismiss();
                                            }
                                        },2000);
                                    }
                                });
                                cancelBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loaddialog.dismiss();
                                    }
                                });
                                loaddialog.show();
                            }
                        });

                        qtyAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(pQty < pStock ){
                                    pQty++;
                                    pQtyTextView.setText(""+pQty);
                                    MainActivity.db.child("AddToCart").child(data.get(i).getId()).child("qty").setValue(""+pQty);
                                    double discount = Double.parseDouble(""+pDiscount)/100;
                                    double calcDiscount = Double.parseDouble(""+pPrice) * discount;
                                    double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
                                    pPriceTextView.setText("$"+(Math.round(totalPrice) * pQty));
                                    pPriceOffTextView.setText("$"+(pPrice * pQty));
                                }
                            }
                        });
                        qtyMinus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pQty > 1){
                                    pQty--;
                                    pQtyTextView.setText(""+pQty);
                                    MainActivity.db.child("AddToCart").child(data.get(i).getId()).child("qty").setValue(""+pQty);
                                    double discount = Double.parseDouble(""+pDiscount)/100;
                                    double calcDiscount = Double.parseDouble(""+pPrice) * discount;
                                    double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
                                    pPriceTextView.setText("$"+(Math.round(totalPrice) * pQty));
                                    pPriceOffTextView.setText("$"+(pPrice * pQty));
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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