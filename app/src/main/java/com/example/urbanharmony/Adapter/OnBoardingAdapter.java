package com.example.urbanharmony.Adapter;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanharmony.Models.OnBoardingModel;
import com.example.urbanharmony.R;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnboardingViewHolder> {
    private List<OnBoardingModel> onboardingItems;

    public OnBoardingAdapter(List<OnBoardingModel> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.on_boarding_container,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle;
        private TextView textDescription;
        private ImageView imageOnboarding;
        View dataView;

        OnboardingViewHolder(@NonNull View itemView){
            super(itemView);
            dataView = itemView;
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageOnboarding = itemView.findViewById(R.id.imageOnboarding);
        }

        void setOnboardingData(OnBoardingModel onboardingItem){
            textTitle.setText(onboardingItem.getTitle());
            textDescription.setText(onboardingItem.getDescription());
//            imageOnboarding.setImageResource(onboardingItem.getImage());
            updateImageBasedOnTheme(onboardingItem);
        }

        void updateImageBasedOnTheme(OnBoardingModel onBoardingModel){
            int currentNightMode = dataView.getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            // Determine which image to use based on the current theme
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                // Dark theme
                imageOnboarding.setImageResource(onBoardingModel.getDarkImage());
            } else {
                // Light theme
                imageOnboarding.setImageResource(onBoardingModel.getImage());
            }
        }
    }
}
