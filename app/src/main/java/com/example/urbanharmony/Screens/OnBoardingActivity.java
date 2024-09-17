package com.example.urbanharmony.Screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.urbanharmony.Adapter.OnBoardingAdapter;
import com.example.urbanharmony.Models.OnBoardingModel;
import com.example.urbanharmony.R;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    LinearLayout onBoardingIndicators;
    Button nextBtn, skipBtn;
    ViewPager2 onBoardingViewPager;
    OnBoardingAdapter onBoardingAdapter;
    String prevStarted = "yes";

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean(prevStarted, false)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        onBoardingIndicators = findViewById(R.id.onBoardingIndicators);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);
        onBoardingViewPager = findViewById(R.id.onBoardingViewPager);

        setOnboardingAdapter();
        onBoardingViewPager.setAdapter(onBoardingAdapter);
        setLayoutOnBoardingIndicators();
        setCurrentOnboardingIndicator(0);

        onBoardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBoardingViewPager.setCurrentItem(onBoardingAdapter.getItemCount());
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onBoardingViewPager.getCurrentItem() + 1 < onBoardingAdapter.getItemCount()){
                    onBoardingViewPager.setCurrentItem(onBoardingViewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void setOnboardingAdapter(){
        List<OnBoardingModel> onboardingItems = new ArrayList<>();

        OnBoardingModel itemPayOnline = new OnBoardingModel();
        itemPayOnline.setTitle("Modern Design");
        itemPayOnline.setDescription("At Friends tours and travel, we customize reliable and trustworthy educational tours to destinations all over the world.");
        itemPayOnline.setImage(R.drawable.livingroom);
        itemPayOnline.setDarkImage(R.drawable.livingroom);

        OnBoardingModel itemPayOnline2 = new OnBoardingModel();
        itemPayOnline2.setTitle("Styled Living");
        itemPayOnline2.setDescription("To get the best of your adventure you just need to leave and go where to like. We are waiting for you.");
        itemPayOnline2.setImage(R.drawable.bedroom);
        itemPayOnline2.setDarkImage(R.drawable.bedroom);

        OnBoardingModel itemPayOnline3 = new OnBoardingModel();
        itemPayOnline3.setTitle("Relaxing Furniture");
        itemPayOnline3.setDescription("To get the best of your adventure you just need to leave and go where to like. We are waiting for you.");
        itemPayOnline3.setImage(R.drawable.kitchen);
        itemPayOnline3.setDarkImage(R.drawable.kitchen);

        onboardingItems.add(itemPayOnline);
        onboardingItems.add(itemPayOnline2);
        onboardingItems.add(itemPayOnline3);

        onBoardingAdapter = new OnBoardingAdapter(onboardingItems);
    }

    private void setLayoutOnBoardingIndicators(){
        ImageView[] indicators = new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(10,0,10,0);
        for(int i = 0; i<indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            onBoardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index){
        int childCount = onBoardingIndicators.getChildCount();
        for(int i = 0; i < childCount; i++){
            ImageView imageView = (ImageView) onBoardingIndicators.getChildAt(i);
            if(i == index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive));
            }
        }
        if(index == childCount-1){
            nextBtn.setText("Get Started");
            skipBtn.setVisibility(View.GONE);
        } else {
            nextBtn.setText("Next");
            skipBtn.setVisibility(View.VISIBLE);
        }
    }
}