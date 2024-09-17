package com.example.urbanharmony.Screens.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.urbanharmony.Adapter.SliderAdapter;
import com.example.urbanharmony.R;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    View view;
    ViewPager2 sliderViewPager;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sliderViewPager = view.findViewById(R.id.sliderViewPager);

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
}