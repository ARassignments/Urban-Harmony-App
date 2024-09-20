package com.example.urbanharmony.Screens.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.Adapter.SliderAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    View view;
    ViewPager2 sliderViewPager;
    TabLayout tabLayout;
    LinearLayout categoryContainer;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        categoryContainer = view.findViewById(R.id.categoryContainer);

        List<String> imageUrls = Arrays.asList(
                "https://myfolio-web.netlify.app/assets/images/projects/19.png",
                "https://myfolio-web.netlify.app/assets/images/projects/20.png",
                "https://myfolio-web.netlify.app/assets/images/projects/21.png"
        );

        SliderAdapter adapter = new SliderAdapter(getContext(), imageUrls);
        sliderViewPager.setAdapter(adapter);

        // Auto-slide functionality
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // Slide every 3 seconds
                updateTabIndicator(position);
            }
        });

        new TabLayoutMediator(tabLayout, sliderViewPager, (tab, position) -> {
            // You can customize the tab indicator appearance here
            View customTab = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_indicator, null);
            ImageView indicatorImage = customTab.findViewById(R.id.indicatorImage);

            // Customize indicator based on the selected/unselected state
            if (position == 0) { // Set first one as selected initially
                indicatorImage.setImageResource(R.drawable.onboarding_indicator_active);
            } else {
                indicatorImage.setImageResource(R.drawable.onboarding_indicator_inactive);
            }

            tab.setCustomView(customTab);
        }).attach();

        MainActivity.db.child("Category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ArrayList<CategoryModel> datalist = new ArrayList<>();
                    int i = 0;
                    for (DataSnapshot ds: snapshot.getChildren()){
                        CategoryModel model = new CategoryModel(ds.getKey(),
                                ds.child("name").getValue().toString(),
                                ds.child("image").getValue().toString(),
                                ds.child("SubCategory").getValue().toString()
                        );
                        datalist.add(model);
                        View itemView = getLayoutInflater().inflate(R.layout.categories_icon_custom_listview,null);
                        ImageView image;
                        CardView listItem;
                        image = itemView.findViewById(R.id.image);
                        listItem = itemView.findViewById(R.id.listItem);
                        image.setImageResource(Integer.parseInt(model.getImage()));
                        if(i==0){
                            itemView.setPadding(50, 0,itemView.getPaddingRight(), 0);
                        }
                        itemView.setAlpha(0f);
                        itemView.animate().alpha(1f).setDuration(300).setStartDelay(i * 100).start();
                        i++;
                        categoryContainer.addView(itemView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int nextSlide = (sliderViewPager.getCurrentItem() + 1) % 3; // Change 3 to the number of images
            sliderViewPager.setCurrentItem(nextSlide, true);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
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
}