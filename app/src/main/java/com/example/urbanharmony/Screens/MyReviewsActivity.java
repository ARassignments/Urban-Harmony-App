package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.FeedbackModel;
import com.example.urbanharmony.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyReviewsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notfoundContainer;
    ArrayList<FeedbackModel> datalist = new ArrayList<>();

    //    Dialog Elements
    Dialog reviewDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText reviewInput;
    TextInputLayout reviewLayout;
    TextView title, rateCount;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_reviews);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = findViewById(R.id.listView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);

        fetchData();
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyReviewsActivity.super.onBackPressed();
            }
        });
    }

    public void fetchData(){
        MainActivity.db.child("Feedback").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("designerId").getValue().toString().equals(UID)){
                            FeedbackModel model = new FeedbackModel(ds.getKey(),
                                    ds.child("rating").getValue().toString(),
                                    ds.child("review").getValue().toString(),
                                    ds.child("userId").getValue().toString(),
                                    ds.child("designerId").getValue().toString(),
                                    ds.child("reviewDate").getValue().toString(),
                                    ds.child("replyRating").getValue().toString(),
                                    ds.child("replyReview").getValue().toString(),
                                    ds.child("replyReviewDate").getValue().toString()
                            );
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        MyAdapter adapter = new MyAdapter(MyReviewsActivity.this,datalist);
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

    public void reviewForm(String itemId){
        reviewDialog = new Dialog(MyReviewsActivity.this);
        reviewDialog.setContentView(R.layout.dialog_add_feedback);
        reviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reviewDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reviewDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        reviewDialog.getWindow().setGravity(Gravity.CENTER);
        reviewDialog.setCancelable(false);
        reviewDialog.setCanceledOnTouchOutside(false);
        cancelBtn = reviewDialog.findViewById(R.id.cancelBtn);
        addDataBtn = reviewDialog.findViewById(R.id.addDataBtn);
        title = reviewDialog.findViewById(R.id.title);
        ratingBar = reviewDialog.findViewById(R.id.ratingBar);
        rateCount = reviewDialog.findViewById(R.id.rateCount);
        reviewInput = reviewDialog.findViewById(R.id.reviewInput);
        reviewLayout = reviewDialog.findViewById(R.id.reviewLayout);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Float rateValue = ratingBar.getRating();
                rateCount.setText(rateValue+"/5.0");
            }
        });

        reviewInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reviewValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation(itemId);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewDialog.dismiss();
            }
        });

        reviewDialog.show();
    }

    public boolean reviewValidation(){
        String input = reviewInput.getText().toString().trim();
        String regex = "^[a-zA-Z0-9:,.'?()&\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            reviewLayout.setError("Review is Required!!!");
            return false;
        } else if(input.length() < 20){
            reviewLayout.setError("Review at least 20 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            reviewLayout.setError("Only text allowed!!!");
            return false;
        } else {
            reviewLayout.setError(null);
            return true;
        }
    }

    private void validation(String itemId) {
        boolean reviewErr = false;
        reviewErr = reviewValidation();
        if((reviewErr) == true){
            persons(itemId);
        }
    }

    private void persons(String itemId){
        if(MainActivity.connectionCheck(MyReviewsActivity.this)){
            Dialog alertdialog = new Dialog(MyReviewsActivity.this);
            alertdialog.setContentView(R.layout.dialog_success);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView message = alertdialog.findViewById(R.id.msgDialog);
            message.setText("Review Reply Added Successfully!!!");
            alertdialog.show();

            // current date picker
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
            String dateTime = simpleDateFormat.format(calendar.getTime());

            MainActivity.db.child("Feedback").child(itemId).child("replyRating").setValue(ratingBar.getRating());
            MainActivity.db.child("Feedback").child(itemId).child("replyReview").setValue(reviewInput.getText().toString().trim());
            MainActivity.db.child("Feedback").child(itemId).child("replyReviewDate").setValue(dateTime);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    reviewDialog.dismiss();
                    fetchData();
                }
            },2000);
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<FeedbackModel> data;

        public MyAdapter(Context context, ArrayList<FeedbackModel> data) {
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
            LinearLayout item, replyContainer;
            TextView sno, username, date, review, replyUsername, replyDate, replyReview;
            ImageView deleteBtn, image, starOne, starTwo, starThree, starFour, starFive;
            ImageView replyDeleteBtn, replyImage, replyStarOne, replyStarTwo, replyStarThree, replyStarFour, replyStarFive;
            Chip replyBtn;

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
            replyBtn = customListItem.findViewById(R.id.replyBtn);
            replyContainer = customListItem.findViewById(R.id.replyContainer);
            replyUsername = customListItem.findViewById(R.id.replyUsername);
            replyDate = customListItem.findViewById(R.id.replyDate);
            replyReview = customListItem.findViewById(R.id.replyReview);
            replyImage = customListItem.findViewById(R.id.replyImage);
            replyDeleteBtn = customListItem.findViewById(R.id.replyDeleteBtn);
            replyStarOne = customListItem.findViewById(R.id.replyStarOne);
            replyStarTwo = customListItem.findViewById(R.id.replyStarTwo);
            replyStarThree = customListItem.findViewById(R.id.replyStarThree);
            replyStarFour = customListItem.findViewById(R.id.replyStarFour);
            replyStarFive = customListItem.findViewById(R.id.replyStarFive);

            sno.setVisibility(View.VISIBLE);
            sno.setText(""+(i+1));
            review.setText(data.get(i).getReview());
            date.setText(data.get(i).getReviewDate());
            replyBtn.setVisibility(View.VISIBLE);

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

            MainActivity.db.child("Users").child(data.get(i).getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
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

            if(!data.get(i).getReplyReview().equals("")){
                replyContainer.setVisibility(View.VISIBLE);
                replyContainer.setAlpha(0f);
                replyContainer.animate().alpha(1f).setDuration(300).start();
                replyDeleteBtn.setVisibility(View.VISIBLE);
                replyBtn.setVisibility(View.GONE);
            }
            replyReview.setText(data.get(i).getReplyReview());
            replyDate.setText(data.get(i).getRepltReviewDate());

            String replyStar = data.get(i).getReplyRating();
            if(replyStar.equals("0.0")){
                replyStarOne.setImageResource(R.drawable.star_outline);
                replyStarTwo.setImageResource(R.drawable.star_outline);
                replyStarThree.setImageResource(R.drawable.star_outline);
                replyStarFour.setImageResource(R.drawable.star_outline);
                replyStarFive.setImageResource(R.drawable.star_outline);
            } else if(replyStar.equals("1.0")){
                replyStarOne.setImageResource(R.drawable.star);
                replyStarTwo.setImageResource(R.drawable.star_outline);
                replyStarThree.setImageResource(R.drawable.star_outline);
                replyStarFour.setImageResource(R.drawable.star_outline);
                replyStarFive.setImageResource(R.drawable.star_outline);
            } else if(replyStar.equals("2.0")){
                replyStarOne.setImageResource(R.drawable.star);
                replyStarTwo.setImageResource(R.drawable.star);
                replyStarThree.setImageResource(R.drawable.star_outline);
                replyStarFour.setImageResource(R.drawable.star_outline);
                replyStarFive.setImageResource(R.drawable.star_outline);
            } else if(replyStar.equals("3.0")){
                replyStarOne.setImageResource(R.drawable.star);
                replyStarTwo.setImageResource(R.drawable.star);
                replyStarThree.setImageResource(R.drawable.star);
                replyStarFour.setImageResource(R.drawable.star_outline);
                replyStarFive.setImageResource(R.drawable.star_outline);
            } else if(replyStar.equals("4.0")){
                replyStarOne.setImageResource(R.drawable.star);
                replyStarTwo.setImageResource(R.drawable.star);
                replyStarThree.setImageResource(R.drawable.star);
                replyStarFour.setImageResource(R.drawable.star);
                replyStarFive.setImageResource(R.drawable.star_outline);
            } else if(replyStar.equals("5.0")){
                replyStarOne.setImageResource(R.drawable.star);
                replyStarTwo.setImageResource(R.drawable.star);
                replyStarThree.setImageResource(R.drawable.star);
                replyStarFour.setImageResource(R.drawable.star);
                replyStarFive.setImageResource(R.drawable.star);
            }

            replyDeleteBtn.setOnClickListener(new View.OnClickListener() {
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
                            MainActivity.db.child("Feedback").child(data.get(i).getId()).child("replyRating").setValue("");
                            MainActivity.db.child("Feedback").child(data.get(i).getId()).child("replyReview").setValue("");
                            MainActivity.db.child("Feedback").child(data.get(i).getId()).child("replyReviewDate").setValue("");
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
                                    fetchData();
                                }
                            },2000);
                        }
                    });

                    actiondialog.show();
                }
            });

            MainActivity.db.child("Users").child(data.get(i).getDesignerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        replyUsername.setText(snapshot.child("name").getValue().toString());
                        if(!snapshot.child("image").getValue().toString().equals("")){
                            replyImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewForm(data.get(i).getId());
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