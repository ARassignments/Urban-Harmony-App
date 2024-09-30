package com.example.urbanharmony.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.urbanharmony.Screens.Fragments.DesignsWishlistFragment;
import com.example.urbanharmony.Screens.Fragments.OrderActiveFragment;
import com.example.urbanharmony.Screens.Fragments.OrderCompletedFragment;
import com.example.urbanharmony.Screens.Fragments.ProductWishlistFragment;

public class WishlistViewPagerAdapter extends FragmentStateAdapter {

    public WishlistViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProductWishlistFragment();
            case 1:
                return new DesignsWishlistFragment();
            default:
                return new ProductWishlistFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
