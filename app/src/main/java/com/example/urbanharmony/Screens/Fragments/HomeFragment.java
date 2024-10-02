package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
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
import com.example.urbanharmony.Adapter.DesignerAdapter;
import com.example.urbanharmony.Adapter.ProductAdapter;
import com.example.urbanharmony.Adapter.SliderAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.FeedbackModel;
import com.example.urbanharmony.Models.ProductModel;
import com.example.urbanharmony.Models.UsersModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.DashboardActivity;
import com.example.urbanharmony.Screens.DesignerActivity;
import com.example.urbanharmony.Screens.DesignerDetailActivity;
import com.example.urbanharmony.Screens.SubCategoryActivity;
import com.example.urbanharmony.Screens.SubcategoriesViewActivity;
import com.example.urbanharmony.Screens.UsersActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    View view;
    static String UID = "";
    static String sortingStatus = "dsc";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ViewPager2 sliderViewPager;
    TabLayout tabLayout;
    LinearLayout categoryContainer;
    RecyclerView designerView, productsView;
    ShimmerFrameLayout productsNotFound, designersNotFound;
    ArrayList<UsersModel> datalist = new ArrayList<>();
    ArrayList<ProductModel> datalistTwo = new ArrayList<>();
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        categoryContainer = view.findViewById(R.id.categoryContainer);
        designerView = view.findViewById(R.id.designerView);
        productsView = view.findViewById(R.id.productsView);
        productsNotFound = view.findViewById(R.id.productsNotFound);
        designersNotFound = view.findViewById(R.id.designersNotFound);

        List<String> imageUrls = Arrays.asList(
                "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/home-banner1.jpg?alt=media&token=6fcc7087-b0f3-4f28-89b3-fa7372da2986",
                "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/home-banner2.jpg?alt=media&token=ef6112d2-40e6-436d-ad12-8ddc91fc3409",
                "https://firebasestorage.googleapis.com/v0/b/urban-harmony-8fd99.appspot.com/o/home-banner3.jpg?alt=media&token=dc1e55f6-3c13-4996-bc5e-8d80d3855030"
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

        view.findViewById(R.id.seemoreProductsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getContext()).openCatalogPage();
            }
        });

        view.findViewById(R.id.seemoreDesignersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), DesignerActivity.class));
            }
        });

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
                        listItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), SubcategoriesViewActivity.class);
                                intent.putExtra("categoryId",model.getId());
                                intent.putExtra("categoryName",model.getName());
                                startActivity(intent);
                            }
                        });
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
        fetchDesigners();
        fetchProducts();
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

    public void fetchDesigners(){

        MainActivity.db.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    datalist.clear();
                    final int[] pendingCalls = {0};  // Track pending asynchronous calls

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("role").getValue().toString().equals("designer")) {
                            pendingCalls[0]++;  // Increment for each designer found

                            MainActivity.db.child("Portfolio").child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                    if (dsnapshot.exists() && dsnapshot.child("portfolioStatus").getValue().toString().equals("1")) {
                                        UsersModel model = new UsersModel(
                                                ds.getKey(),
                                                ds.child("name").getValue().toString(),
                                                ds.child("email").getValue().toString(),
                                                ds.child("pwd").getValue().toString(),
                                                ds.child("image").getValue().toString(),
                                                ds.child("role").getValue().toString(),
                                                ds.child("address").getValue().toString(),
                                                ds.child("shipping").getValue().toString(),
                                                ds.child("created_on").getValue().toString(),
                                                ds.child("status").getValue().toString()
                                        );
                                        datalist.add(model);
                                    }

                                    // Decrement the pending calls counter
                                    pendingCalls[0]--;
                                    // If all calls are done, update the adapter
                                    if (pendingCalls[0] == 0) {
                                        updateAdapter();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    pendingCalls[0]--;
                                    if (pendingCalls[0] == 0) {
                                        updateAdapter();
                                    }
                                }
                            });
                        }
                    }

                    // In case no designer was found (so no pending calls), update the adapter
                    if (pendingCalls[0] == 0) {
                        updateAdapter();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // Helper function to update adapter
            private void updateAdapter() {
                if (datalist.size() > 0) {
                    designerView.setVisibility(View.VISIBLE);
                    designersNotFound.setVisibility(View.GONE);
                    DesignerAdapter adapter = new DesignerAdapter(getContext(), datalist);
                    designerView.setAdapter(adapter);
                } else {
                    designerView.setVisibility(View.GONE);
                    designersNotFound.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void fetchProducts(){
        MainActivity.db.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalistTwo.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        ProductModel model = new ProductModel(ds.getKey(),
                                ds.child("pName").getValue().toString(),
                                ds.child("pPrice").getValue().toString(),
                                ds.child("pStock").getValue().toString(),
                                ds.child("pDiscount").getValue().toString(),
                                ds.child("pImage").getValue().toString(),
                                ds.child("pDesc").getValue().toString(),
                                ds.child("pCategory").getValue().toString(),
                                ds.child("pSubcategory").getValue().toString(),
                                ds.child("pBrand").getValue().toString(),
                                ds.child("pStyle").getValue().toString(),
                                ds.child("status").getValue().toString()
                        );
                        datalistTwo.add(model);
                    }
                    int productCount = 10;
                    if(datalistTwo.size() > 0 && datalistTwo.size() <= productCount){
                        productsView.setVisibility(View.VISIBLE);
                        productsNotFound.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalistTwo);
                        }
                        ProductAdapter adapter = new ProductAdapter(getContext(),datalistTwo,UID);
                        productsView.setAdapter(adapter);
                    } else if(datalistTwo.size() > productCount){
                        productsView.setVisibility(View.VISIBLE);
                        productsNotFound.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalistTwo);
                        }
                        List<ProductModel> sublist = new ArrayList<>(datalistTwo.subList(0, productCount));
                        ProductAdapter adapter = new ProductAdapter(getContext(),sublist,UID);
                        productsView.setAdapter(adapter);
                    } else {
                        productsView.setVisibility(View.GONE);
                        productsNotFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    productsView.setVisibility(View.GONE);
                    productsNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}