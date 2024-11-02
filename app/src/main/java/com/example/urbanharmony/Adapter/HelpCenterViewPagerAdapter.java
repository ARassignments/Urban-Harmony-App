package com.example.urbanharmony.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.urbanharmony.Screens.Fragments.ContactUsFragment;
import com.example.urbanharmony.Screens.Fragments.FaqFragment;
import com.example.urbanharmony.Screens.Fragments.OrderActiveFragment;
import com.example.urbanharmony.Screens.Fragments.OrderCompletedFragment;

public class HelpCenterViewPagerAdapter extends FragmentStateAdapter {

    public HelpCenterViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FaqFragment();
            case 1:
                return new ContactUsFragment();
            default:
                return new FaqFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
