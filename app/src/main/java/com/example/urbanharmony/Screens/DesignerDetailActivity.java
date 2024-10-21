package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.Adapter.DesignerDesignAdapter;
import com.example.urbanharmony.Adapter.DesignerProjectAdapter;
import com.example.urbanharmony.Adapter.DesignerReviewAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.DesignModel;
import com.example.urbanharmony.Models.FeedbackModel;
import com.example.urbanharmony.Models.ProjectModel;
import com.example.urbanharmony.Models.ScheduleModel;
import com.example.urbanharmony.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class DesignerDetailActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String CurrentUID = "";
    CircleImageView profileImage;
    TextView profileName, shortBio, longBio, viewMoreBtn, designsCount;
    RecyclerView projectsView, designsView;
    ArrayList<ProjectModel> datalist = new ArrayList<>();
    ArrayList<DesignModel> datalistTwo = new ArrayList<>();
    ArrayList<FeedbackModel> datalistThree = new ArrayList<>();
    ShimmerFrameLayout projectNotFound, designNotFound, reviewFound;
    ExtendedFloatingActionButton addReviewBtn;
    ViewPager2 reviewViewPager;
    TabLayout tabLayout;
    LinearLayout paginationContainer;
    Button onlyBtn, bookBtn;
    ImageView starOne, starTwo, starThree, starFour, starFive;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

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
        setContentView(R.layout.activity_designer_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            CurrentUID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            UID = extra.getString("userId");
        }

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        projectsView = findViewById(R.id.projectsView);
        designsView = findViewById(R.id.designsView);
        shortBio = findViewById(R.id.shortBio);
        longBio = findViewById(R.id.longBio);
        projectNotFound = findViewById(R.id.projectNotFound);
        designNotFound = findViewById(R.id.designNotFound);
        reviewFound = findViewById(R.id.reviewFound);
        addReviewBtn = findViewById(R.id.addReviewBtn);
        reviewViewPager = findViewById(R.id.reviewViewPager);
        tabLayout = findViewById(R.id.tabLayout);
        paginationContainer = findViewById(R.id.paginationContainer);
        viewMoreBtn = findViewById(R.id.viewMoreBtn);
        bookBtn = findViewById(R.id.bookBtn);
        onlyBtn = findViewById(R.id.onlyBtn);
        designsCount = findViewById(R.id.designsCount);
        starOne = findViewById(R.id.starOne);
        starTwo = findViewById(R.id.starTwo);
        starThree = findViewById(R.id.starThree);
        starFour = findViewById(R.id.starFour);
        starFive = findViewById(R.id.starFive);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DesignerDetailActivity.super.onBackPressed();
            }
        });

        if(DashboardActivity.getRole().equals("designer")||DashboardActivity.getRole().equals("admin")){
            onlyBtn.setVisibility(View.VISIBLE);
            bookBtn.setVisibility(View.GONE);
        }

        fetchDetails();
        fetchProjects();
        fetchDesigns();
        fetchReviews();
        checkPortfolio();
        checkSchedule();

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewForm();
            }
        });

        viewMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DesignerDetailActivity.this, DesignerReviewsActivity.class);
                intent.putExtra("userId",UID);
                startActivity(intent);
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DesignerDetailActivity.this, BookingAppointmentActivity.class);
                intent.putExtra("userId",UID);
                startActivity(intent);
            }
        });
    }

    public void fetchDetails() {
        MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileName.setText(snapshot.child("name").getValue().toString().trim());
//                    if(!snapshot.child("image").getValue().toString().equals("")){
//                        profileImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchProjects(){
        MainActivity.db.child("Projects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("userId").getValue().toString().trim().equals(UID)){
                            ProjectModel model = new ProjectModel(ds.getKey(),
                                    ds.child("pName").getValue().toString(),
                                    ds.child("pImage").getValue().toString(),
                                    ds.child("pDesc").getValue().toString(),
                                    ds.child("userId").getValue().toString()
                            );
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
                        projectsView.setVisibility(View.VISIBLE);
                        projectNotFound.setVisibility(View.GONE);
                        DesignerProjectAdapter adapter = new DesignerProjectAdapter(DesignerDetailActivity.this,datalist);
                        projectsView.setAdapter(adapter);
                    } else {
                        projectsView.setVisibility(View.GONE);
                        projectNotFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    projectsView.setVisibility(View.GONE);
                    projectNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchDesigns(){
        MainActivity.db.child("Designs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalistTwo.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("userId").getValue().toString().trim().equals(UID)){
                            DesignModel model = new DesignModel(ds.getKey(),
                                    ds.child("dName").getValue().toString(),
                                    ds.child("dImage").getValue().toString(),
                                    ds.child("userId").getValue().toString()
                            );
                            datalistTwo.add(model);
                        }
                    }
                    designsCount.setText(datalistTwo.size()+" Designs");
                    if(datalistTwo.size() > 0){
                        designsView.setVisibility(View.VISIBLE);
                        designNotFound.setVisibility(View.GONE);
                        DesignerDesignAdapter adapter = new DesignerDesignAdapter(DesignerDetailActivity.this,datalistTwo,CurrentUID);
                        designsView.setAdapter(adapter);
                    } else {
                        designsView.setVisibility(View.GONE);
                        designNotFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    designsView.setVisibility(View.GONE);
                    designNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchReviews(){
        MainActivity.db.child("Feedback").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalistThree.clear();
                    double totalRating = 0;
                    int reviewCount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("designerId").getValue().toString().trim().equals(UID)){
                            double rating = Double.parseDouble(ds.child("rating").getValue().toString());
                            totalRating += rating; // Add to total rating
                            reviewCount++; // Increment the review count
                            FeedbackModel model = new FeedbackModel(ds.getKey(),
                                    ds.child("rating").getValue().toString(),
                                    ds.child("review").getValue().toString(),
                                    ds.child("userId").getValue().toString(),
                                    ds.child("designerId").getValue().toString(),
                                    ds.child("reviewDate").getValue().toString()
                            );
                            datalistThree.add(model);
                        }
                    }
                    double averageRating = (reviewCount > 0) ? totalRating / reviewCount : 0;
                    if(averageRating >= 0.0 && averageRating < 1.0){
                        starOne.setImageResource(R.drawable.star_outline);
                        starTwo.setImageResource(R.drawable.star_outline);
                        starThree.setImageResource(R.drawable.star_outline);
                        starFour.setImageResource(R.drawable.star_outline);
                        starFive.setImageResource(R.drawable.star_outline);
                    } else if(averageRating >= 1.0 && averageRating < 2.0){
                        starOne.setImageResource(R.drawable.star);
                        starTwo.setImageResource(R.drawable.star_outline);
                        starThree.setImageResource(R.drawable.star_outline);
                        starFour.setImageResource(R.drawable.star_outline);
                        starFive.setImageResource(R.drawable.star_outline);
                    } else if(averageRating >= 2.0 && averageRating < 3.0){
                        starOne.setImageResource(R.drawable.star);
                        starTwo.setImageResource(R.drawable.star);
                        starThree.setImageResource(R.drawable.star_outline);
                        starFour.setImageResource(R.drawable.star_outline);
                        starFive.setImageResource(R.drawable.star_outline);
                    } else if(averageRating >= 3.0 && averageRating < 4.0){
                        starOne.setImageResource(R.drawable.star);
                        starTwo.setImageResource(R.drawable.star);
                        starThree.setImageResource(R.drawable.star);
                        starFour.setImageResource(R.drawable.star_outline);
                        starFive.setImageResource(R.drawable.star_outline);
                    } else if(averageRating >= 4.0 && averageRating < 5.0){
                        starOne.setImageResource(R.drawable.star);
                        starTwo.setImageResource(R.drawable.star);
                        starThree.setImageResource(R.drawable.star);
                        starFour.setImageResource(R.drawable.star);
                        starFive.setImageResource(R.drawable.star_outline);
                    } else if(averageRating >= 5.0){
                        starOne.setImageResource(R.drawable.star);
                        starTwo.setImageResource(R.drawable.star);
                        starThree.setImageResource(R.drawable.star);
                        starFour.setImageResource(R.drawable.star);
                        starFive.setImageResource(R.drawable.star);
                    }

                    if (datalistThree.size() > 0 && datalistThree.size() <= 3) {
                        // Display 1 to 3 items
                        reviewViewPager.setVisibility(View.VISIBLE);
                        paginationContainer.setVisibility(View.VISIBLE);
                        reviewFound.setVisibility(View.GONE);

                        // Setup adapter with the available datalistThree
                        DesignerReviewAdapter adapter = new DesignerReviewAdapter(DesignerDetailActivity.this, datalistThree);
                        reviewViewPager.setAdapter(adapter);

                        // Auto-slide functionality
                        reviewViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                super.onPageSelected(position);
                                sliderHandler.removeCallbacks(sliderRunnable);
                                sliderHandler.postDelayed(sliderRunnable, 4000);
                                updateTabIndicator(position);
                            }
                        });

                        new TabLayoutMediator(tabLayout, reviewViewPager, (tab, position) -> {
                            View customTab = LayoutInflater.from(DesignerDetailActivity.this).inflate(R.layout.custom_tab_indicator, null);
                            ImageView indicatorImage = customTab.findViewById(R.id.indicatorImage);
                            if (position == 0) {
                                indicatorImage.setImageResource(R.drawable.onboarding_indicator_active);
                            } else {
                                indicatorImage.setImageResource(R.drawable.onboarding_indicator_inactive);
                            }
                            tab.setCustomView(customTab);
                        }).attach();

                    } else if (datalistThree.size() > 3) {
                        // If there are more than 3 items, display only the first 3
                        reviewViewPager.setVisibility(View.VISIBLE);
                        paginationContainer.setVisibility(View.VISIBLE);
                        reviewFound.setVisibility(View.GONE);

                        // Create a sublist of the first 3 items from datalistThree
                        List<FeedbackModel> sublist = new ArrayList<>(datalistThree.subList(0, 3));

                        // Setup adapter with only the first 3 items
                        DesignerReviewAdapter adapter = new DesignerReviewAdapter(DesignerDetailActivity.this, sublist);
                        reviewViewPager.setAdapter(adapter);

                        // Auto-slide functionality
                        reviewViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                super.onPageSelected(position);
                                sliderHandler.removeCallbacks(sliderRunnable);
                                sliderHandler.postDelayed(sliderRunnable, 4000);
                                updateTabIndicator(position);
                            }
                        });

                        new TabLayoutMediator(tabLayout, reviewViewPager, (tab, position) -> {
                            View customTab = LayoutInflater.from(DesignerDetailActivity.this).inflate(R.layout.custom_tab_indicator, null);
                            ImageView indicatorImage = customTab.findViewById(R.id.indicatorImage);
                            if (position == 0) {
                                indicatorImage.setImageResource(R.drawable.onboarding_indicator_active);
                            } else {
                                indicatorImage.setImageResource(R.drawable.onboarding_indicator_inactive);
                            }
                            tab.setCustomView(customTab);
                        }).attach();

                    } else {
                        // No items to display
                        reviewViewPager.setVisibility(View.GONE);
                        paginationContainer.setVisibility(View.GONE);
                        reviewFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    reviewViewPager.setVisibility(View.GONE);
                    paginationContainer.setVisibility(View.GONE);
                    reviewFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if(datalistThree.size() > 0 && datalistThree.size() <= 3){
                int nextSlide = (reviewViewPager.getCurrentItem() + 1) % datalistThree.size();
                reviewViewPager.setCurrentItem(nextSlide, true);
            } else if(datalistThree.size() > 3){
                int nextSlide = (reviewViewPager.getCurrentItem() + 1) % 3;
                reviewViewPager.setCurrentItem(nextSlide, true);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    public void checkPortfolio(){
        MainActivity.db.child("Portfolio").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("portfolioStatus").getValue().toString().equals("1")){
                        shortBio.setText(snapshot.child("shortBio").getValue().toString());
                        longBio.setText(snapshot.child("longBio").getValue().toString());
                        Glide.with(DesignerDetailActivity.this).load(snapshot.child("image").getValue().toString().trim()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkSchedule(){
        MainActivity.db.child("Schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean status = false;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.child("userId").getValue().toString().equals(UID)&&(!ds.child("Slots").getValue().toString().equals(""))){
                            status = true;
                        }
                    }

                    if(DashboardActivity.getRole().equals("designer")||DashboardActivity.getRole().equals("admin")){
                        onlyBtn.setVisibility(View.VISIBLE);
                        bookBtn.setVisibility(View.GONE);
                    } else {
                        if(status == false){
                            onlyBtn.setVisibility(View.VISIBLE);
                            onlyBtn.setText("Appointments Is Not Available");
                            bookBtn.setVisibility(View.GONE);
                        } else {
                            onlyBtn.setVisibility(View.GONE);
                            bookBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void reviewForm(){
        reviewDialog = new Dialog(DesignerDetailActivity.this);
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
                validation();
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

    private void validation() {
        boolean reviewErr = false;
        reviewErr = reviewValidation();
        if((reviewErr) == true){
            persons();
        }
    }

    private void updateTabIndicator(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.getCustomView() != null) {
                ImageView indicatorImage = tab.getCustomView().findViewById(R.id.indicatorImage);
                if (i == position) {
                    indicatorImage.setImageResource(R.drawable.onboarding_indicator_active);
                } else {
                    indicatorImage.setImageResource(R.drawable.onboarding_indicator_inactive);
                }
            }
        }
    }

    private void persons(){
        if(MainActivity.connectionCheck(DesignerDetailActivity.this)){
            Dialog alertdialog = new Dialog(DesignerDetailActivity.this);
            alertdialog.setContentView(R.layout.dialog_success);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView message = alertdialog.findViewById(R.id.msgDialog);
            message.setText("Review Added Successfully!!!");
            alertdialog.show();

            // current date picker
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
            String dateTime = simpleDateFormat.format(calendar.getTime());

            HashMap<String, String> mydata = new HashMap<String, String>();
            mydata.put("rating", "" + ratingBar.getRating());
            mydata.put("review", reviewInput.getText().toString().trim());
            mydata.put("userId", CurrentUID);
            mydata.put("designerId", UID);
            mydata.put("reviewDate", dateTime);
            MainActivity.db.child("Feedback").push().setValue(mydata);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    reviewDialog.dismiss();
                    fetchReviews();
                }
            },2000);
        }
    }
}