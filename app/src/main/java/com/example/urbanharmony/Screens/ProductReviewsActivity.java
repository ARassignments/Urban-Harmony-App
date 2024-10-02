package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.FeedbackModel;
import com.example.urbanharmony.Models.ProductFeedbackModel;
import com.example.urbanharmony.R;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ProductReviewsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String PID = "";
    static String sortingStatus = "dsc";
    static String filterStatus = "all";
    ListView listView;
    LinearLayout loader, notfoundContainer;
    Chip allChip, myChip;
    ArrayList<ProductFeedbackModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_reviews);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            PID = extra.getString("PID");
        }

        listView = findViewById(R.id.listView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);
        allChip = findViewById(R.id.allChip);
        myChip = findViewById(R.id.myChip);

        fetchData("");

        allChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtering("all");
            }
        });

        myChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtering("myChip");
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductReviewsActivity.super.onBackPressed();
            }
        });
    }

    public void filtering(String status){
        filterStatus = status;
        fetchData("");
    }

    public void fetchData(String data){
        MainActivity.db.child("ProductFeedback").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            if(!ds.getKey().equals(UID)){
                                if(filterStatus.equals("all")){
                                    if(ds.child("PID").getValue().toString().equals(PID)){
                                        ProductFeedbackModel model = new ProductFeedbackModel(ds.getKey(),
                                                ds.child("rating").getValue().toString(),
                                                ds.child("review").getValue().toString(),
                                                ds.child("PID").getValue().toString(),
                                                ds.child("UID").getValue().toString(),
                                                ds.child("reviewDate").getValue().toString()
                                        );
                                        datalist.add(model);
                                    }
                                } else if(filterStatus.equals("myChip")){
                                    if(ds.child("PID").getValue().toString().equals(PID)&&ds.child("UID").getValue().toString().equals(UID)){
                                        ProductFeedbackModel model = new ProductFeedbackModel(ds.getKey(),
                                                ds.child("rating").getValue().toString(),
                                                ds.child("review").getValue().toString(),
                                                ds.child("PID").getValue().toString(),
                                                ds.child("UID").getValue().toString(),
                                                ds.child("reviewDate").getValue().toString()
                                        );
                                        datalist.add(model);
                                    }
                                }
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
                        MyAdapter adapter = new MyAdapter(ProductReviewsActivity.this,datalist);
                        listView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
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

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<ProductFeedbackModel> data;

        public MyAdapter(Context context, ArrayList<ProductFeedbackModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.designer_reviews_custom_listview,null);
            LinearLayout item;
            TextView sno, username, date, review;
            ImageView deleteBtn, image, starOne, starTwo, starThree, starFour, starFive;

            item = customListItem.findViewById(R.id.item);
            sno = customListItem.findViewById(R.id.sno);
            username = customListItem.findViewById(R.id.username);
            date = customListItem.findViewById(R.id.date);
            review = customListItem.findViewById(R.id.review);
            image = customListItem.findViewById(R.id.image);
            deleteBtn = customListItem.findViewById(R.id.deleteBtn);
            starOne = customListItem.findViewById(R.id.starOne);
            starTwo = customListItem.findViewById(R.id.starTwo);
            starThree = customListItem.findViewById(R.id.starThree);
            starFour = customListItem.findViewById(R.id.starFour);
            starFive = customListItem.findViewById(R.id.starFive);

            sno.setVisibility(View.VISIBLE);
            if(data.get(i).getUID().equals(UID)){
                deleteBtn.setVisibility(View.VISIBLE);
            }
            sno.setText(""+(i+1));
            review.setText(data.get(i).getReview());
            date.setText(data.get(i).getReviewDate());

            String star = data.get(i).getRating();
            if(star.equals("0.0")){
                starOne.setImageResource(R.drawable.star_outline);
                starTwo.setImageResource(R.drawable.star_outline);
                starThree.setImageResource(R.drawable.star_outline);
                starFour.setImageResource(R.drawable.star_outline);
                starFive.setImageResource(R.drawable.star_outline);
            } else if(star.equals("1.0")){
                starOne.setImageResource(R.drawable.star);
                starTwo.setImageResource(R.drawable.star_outline);
                starThree.setImageResource(R.drawable.star_outline);
                starFour.setImageResource(R.drawable.star_outline);
                starFive.setImageResource(R.drawable.star_outline);
            } else if(star.equals("2.0")){
                starOne.setImageResource(R.drawable.star);
                starTwo.setImageResource(R.drawable.star);
                starThree.setImageResource(R.drawable.star_outline);
                starFour.setImageResource(R.drawable.star_outline);
                starFive.setImageResource(R.drawable.star_outline);
            } else if(star.equals("3.0")){
                starOne.setImageResource(R.drawable.star);
                starTwo.setImageResource(R.drawable.star);
                starThree.setImageResource(R.drawable.star);
                starFour.setImageResource(R.drawable.star_outline);
                starFive.setImageResource(R.drawable.star_outline);
            } else if(star.equals("4.0")){
                starOne.setImageResource(R.drawable.star);
                starTwo.setImageResource(R.drawable.star);
                starThree.setImageResource(R.drawable.star);
                starFour.setImageResource(R.drawable.star);
                starFive.setImageResource(R.drawable.star_outline);
            } else if(star.equals("5.0")){
                starOne.setImageResource(R.drawable.star);
                starTwo.setImageResource(R.drawable.star);
                starThree.setImageResource(R.drawable.star);
                starFour.setImageResource(R.drawable.star);
                starFive.setImageResource(R.drawable.star);
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog actiondialog = new Dialog(context);
                    actiondialog.setContentView(R.layout.dialog_confirm);
                    actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    actiondialog.getWindow().setGravity(Gravity.CENTER);
                    actiondialog.setCancelable(false);
                    actiondialog.setCanceledOnTouchOutside(false);
                    Button cancelBtn, yesBtn;
                    yesBtn = actiondialog.findViewById(R.id.yesBtn);
                    cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actiondialog.dismiss();
                        }
                    });
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MainActivity.db.child("ProductFeedback").child(data.get(i).getId()).removeValue();
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_success);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            TextView msg = dialog.findViewById(R.id.msgDialog);
                            msg.setText("Deleted Successfully!!!");
                            dialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    actiondialog.dismiss();
                                    fetchData("");
                                }
                            },2000);
                        }
                    });

                    actiondialog.show();
                }
            });

            MainActivity.db.child("Users").child(data.get(i).getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        username.setText(snapshot.child("name").getValue().toString());
                        if(!snapshot.child("image").getValue().toString().equals("")){
                            image.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                        }
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
            customListItem.animate().alpha(1f).setDuration(200).setStartDelay(i * 20).start();
            return customListItem;
        }
    }
}