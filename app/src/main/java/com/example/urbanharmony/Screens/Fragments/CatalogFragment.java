package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.ProductModel;
import com.example.urbanharmony.Models.StyleModel;
import com.example.urbanharmony.Models.SubCategoryModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.FilteredProductsActivity;
import com.example.urbanharmony.Screens.ProductDetailActivity;
import com.example.urbanharmony.Screens.ProductsActivity;
import com.example.urbanharmony.Screens.ProfileActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatalogFragment extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    static float defaultMinPrice = 0;
    static float defaultMaxPrice = 40000;
    GridView gridView;
    LinearLayout loader, notifyBar, notfoundContainer;
    ImageView filterBtn;
    EditText searchInput;
    TextView searchedWord, totalCount;
    ArrayList<ProductModel> datalist = new ArrayList<>();
    View view;

    ChipGroup categoryChipgroup, brandsChipgroup, stylesChipgroup, sortsChipgroup;
    RangeSlider priceRangeSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_catalog, container, false);
        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        gridView = view.findViewById(R.id.gridView);
        notifyBar = view.findViewById(R.id.notifyBar);
        notfoundContainer = view.findViewById(R.id.notfoundContainer);
        loader = view.findViewById(R.id.loader);
        searchedWord = view.findViewById(R.id.searchedWord);
        totalCount = view.findViewById(R.id.totalCount);
        searchInput = view.findViewById(R.id.searchInput);
        filterBtn = view.findViewById(R.id.filterBtn);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchedWord.setText(searchInput.getText().toString().trim());
                searchValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialogFilter = new Dialog(getContext());
                dialogFilter.setContentView(R.layout.dialog_bottom_product_filter);
                dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogFilter.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogFilter.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
                dialogFilter.getWindow().setGravity(Gravity.BOTTOM);
                dialogFilter.setCanceledOnTouchOutside(false);
                dialogFilter.setCancelable(false);
                dialogFilter.show();
                Button cancelBtn, applyBtn;
                Chip allChipCategory, allChipBrand, allChipStyle, ascChip, descChip, priceHighChip, priceLowChip, atozChip, ztoacChip;
                cancelBtn = dialogFilter.findViewById(R.id.cancelBtn);
                applyBtn = dialogFilter.findViewById(R.id.applyBtn);
                priceRangeSlider = dialogFilter.findViewById(R.id.priceRangeSlider);
                categoryChipgroup = dialogFilter.findViewById(R.id.categoryChipgroup);
                brandsChipgroup = dialogFilter.findViewById(R.id.brandsChipgroup);
                stylesChipgroup = dialogFilter.findViewById(R.id.stylesChipgroup);
                sortsChipgroup = dialogFilter.findViewById(R.id.sortsChipgroup);
                allChipCategory = dialogFilter.findViewById(R.id.allChipCategory);
                allChipBrand = dialogFilter.findViewById(R.id.allChipBrand);
                allChipStyle = dialogFilter.findViewById(R.id.allChipStyle);
                ascChip = dialogFilter.findViewById(R.id.ascChip);
                descChip = dialogFilter.findViewById(R.id.descChip);
                priceHighChip = dialogFilter.findViewById(R.id.priceHighChip);
                priceLowChip = dialogFilter.findViewById(R.id.priceLowChip);
                atozChip = dialogFilter.findViewById(R.id.atozChip);
                ztoacChip = dialogFilter.findViewById(R.id.ztoacChip);

                // Initialize SharedPreferences
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

                String lastCategory = sharedPrefs.getString("selectedCategory", "All");
                String lastBrand = sharedPrefs.getString("selectedBrand", "All");
                String lastStyle = sharedPrefs.getString("selectedStyle", "All");
                String lastSort = sharedPrefs.getString("selectedSort", "Descending");
                float savedMinPrice = sharedPrefs.getFloat("selectedMinPrice", defaultMinPrice);
                float savedMaxPrice = sharedPrefs.getFloat("selectedMaxPrice", defaultMaxPrice);

                priceRangeSlider.setValueFrom(defaultMinPrice);
                priceRangeSlider.setValueTo(defaultMaxPrice);
                priceRangeSlider.setValues(savedMinPrice, savedMaxPrice);

                MainActivity.db.child("Category").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Chip chip = new Chip(new ContextThemeWrapper(getContext(), R.style.CustomCategoryChipStyle));
                                chip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(),null,0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice));
                                chip.setText(""+dataSnapshot.child("name").getValue().toString());
                                chip.setCheckable(true);
                                chip.setChipIconSize(27);
                                chip.setCheckedIconEnabled(true);
                                chip.setChipStartPadding(10);
                                chip.setChipEndPadding(0);
                                chip.setCheckedIconVisible(true);
                                chip.setCheckedIconTint(getResources().getColorStateList(R.color.accent_50));
                                if(dataSnapshot.child("name").getValue().toString().equals(lastCategory)){
                                    chip.setChecked(true);
                                }
                                categoryChipgroup.addView(chip);
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                MainActivity.db.child("Brands").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Chip chip = new Chip(new ContextThemeWrapper(getContext(), R.style.CustomCategoryChipStyle));
                                chip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(),null,0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice));
                                chip.setText(""+dataSnapshot.child("name").getValue().toString());
                                chip.setCheckable(true);
                                chip.setChipIconSize(27);
                                chip.setCheckedIconEnabled(true);
                                chip.setChipStartPadding(10);
                                chip.setChipEndPadding(0);
                                chip.setCheckedIconVisible(true);
                                chip.setCheckedIconTint(getResources().getColorStateList(R.color.accent_50));
                                if(dataSnapshot.child("name").getValue().toString().equals(lastBrand)){
                                    chip.setChecked(true);
                                }
                                brandsChipgroup.addView(chip);
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                MainActivity.db.child("Styles").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Chip chip = new Chip(new ContextThemeWrapper(getContext(), R.style.CustomCategoryChipStyle));
                                chip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(),null,0, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice));
                                chip.setText(""+dataSnapshot.child("name").getValue().toString());
                                chip.setCheckable(true);
                                chip.setChipIconSize(27);
                                chip.setCheckedIconEnabled(true);
                                chip.setChipStartPadding(10);
                                chip.setChipEndPadding(0);
                                chip.setCheckedIconVisible(true);
                                chip.setCheckedIconTint(getResources().getColorStateList(R.color.accent_50));
                                if(dataSnapshot.child("name").getValue().toString().equals(lastStyle)){
                                    chip.setChecked(true);
                                }
                                stylesChipgroup.addView(chip);
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                for (int i = 0; i < sortsChipgroup.getChildCount(); i++) {
                    Chip chip = (Chip) sortsChipgroup.getChildAt(i);
                    if (chip.getText().toString().equals(lastSort)) {
                        chip.setChecked(true);
                        break;
                    }
                }

                applyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selectedCategory = getSelectedChipText(categoryChipgroup);
                        String selectedBrand = getSelectedChipText(brandsChipgroup);
                        String selectedStyle = getSelectedChipText(stylesChipgroup);
                        String selectedSort = getSelectedChipText(sortsChipgroup);
                        float selectedMinPrice = priceRangeSlider.getValues().get(0);
                        float selectedMaxPrice = priceRangeSlider.getValues().get(1);

                        sharedPrefs.edit()
                                .putString("selectedCategory", selectedCategory)
                                .putString("selectedBrand", selectedBrand)
                                .putString("selectedStyle", selectedStyle)
                                .putString("selectedSort", selectedSort)
                                .putFloat("selectedMinPrice", selectedMinPrice)
                                .putFloat("selectedMaxPrice", selectedMaxPrice)
                                .apply();

                        // Fetch data based on selected filters
                        fetchData("", (int) selectedMinPrice, (int) selectedMaxPrice, selectedCategory, selectedBrand, selectedStyle, selectedSort);
                        dialogFilter.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoryChipgroup.clearCheck();
                        brandsChipgroup.clearCheck();
                        stylesChipgroup.clearCheck();
                        sortsChipgroup.clearCheck();
                        priceRangeSlider.setValues(defaultMinPrice, defaultMaxPrice);

                        sharedPrefs.edit()
                                .remove("selectedCategory")
                                .remove("selectedBrand")
                                .remove("selectedStyle")
                                .remove("selectedSort")
                                .remove("selectedMinPrice")
                                .remove("selectedMaxPrice")
                                .apply();

                        // Fetch default data (without any filter)
                        fetchData("", (int) defaultMinPrice, (int) defaultMaxPrice, "", "", "", "");

                        dialogFilter.dismiss();
                    }
                });
            }
        });

        fetchData("", (int) defaultMinPrice, (int) defaultMaxPrice, "", "", "", "Descending");

        return view;
    }

    private String getSelectedChipText(ChipGroup chipGroup) {
        int selectedChipId = chipGroup.getCheckedChipId();
        if (selectedChipId != -1) {
            Chip selectedChip = chipGroup.findViewById(selectedChipId);
            if (selectedChip != null && selectedChip.getText().toString().equalsIgnoreCase("All")) {
                return "";
            }
            return selectedChip.getText().toString();
        }
        return "";
    }

    public void fetchData(String data, int minPrice, int maxPrice, String category, String brand, String style, String sort){
        MainActivity.db.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        String pName = ds.child("pName").getValue().toString();
                        int pPrice = Integer.parseInt(ds.child("pPrice").getValue().toString());
                        String pCategory = ds.child("pCategory").getValue().toString();
                        String pBrand = ds.child("pBrand").getValue().toString();
                        String pStyle = ds.child("pStyle").getValue().toString();

                        boolean matchesSearch = data.isEmpty() || pName.toLowerCase().contains(data.toLowerCase());
                        boolean matchesPriceRange = pPrice >= minPrice && pPrice <= maxPrice;
                        boolean matchesCategory = category.isEmpty() || pCategory.toLowerCase().equals(category.toLowerCase());
                        boolean matchesBrand = brand.isEmpty() || pBrand.toLowerCase().equals(brand.toLowerCase());
                        boolean matchesStyle = style.isEmpty() || pStyle.toLowerCase().equals(style.toLowerCase());

                        if(matchesSearch && matchesPriceRange && matchesCategory && matchesBrand && matchesStyle){
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
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sort.equals("Descending")){
                            Collections.reverse(datalist);
                        } else if(sort.equals("Ascending")){
                            Collections.sort(datalist, Comparator.comparing(ProductModel::getpName));
                        } else if(sort.equals("A to Z")){
                            Collections.sort(datalist, Comparator.comparing(ProductModel::getpName));
                        } else if(sort.equals("Z to A")){
                            Collections.sort(datalist, (a, b) -> b.getpName().compareTo(a.getpName()));
                        } else if(sort.equals("Price High")){
                            Collections.sort(datalist, (a, b) -> Integer.compare(Integer.parseInt(b.getpPrice()), Integer.parseInt(a.getpPrice())));
                        } else if(sort.equals("Price Low")){
                            Collections.sort(datalist, (a, b) -> Integer.compare(Integer.parseInt(a.getpPrice()), Integer.parseInt(b.getpPrice())));
                        }
                        MyAdapter adapter = new MyAdapter(getContext(),datalist);
                        gridView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    totalCount.setText(datalist.size()+" found");
                } else {
                    loader.setVisibility(View.GONE);
                    gridView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void searchValidation(){
        String input = searchInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(!matcher.matches()){
            searchInput.setError("Only text allowed!!!");
        } else {
            searchInput.setError(null);
            String selectedCategory = getSelectedChipText(categoryChipgroup);
            String selectedBrand = getSelectedChipText(brandsChipgroup);
            String selectedStyle = getSelectedChipText(stylesChipgroup);
            String selectedSort = getSelectedChipText(sortsChipgroup);
            float selectedMinPrice = priceRangeSlider.getValues().get(0);
            float selectedMaxPrice = priceRangeSlider.getValues().get(1);

            if (input.isEmpty()) {
                notifyBar.setVisibility(View.GONE);
            } else {
                notifyBar.setVisibility(View.VISIBLE);
            }

            fetchData(input, (int) selectedMinPrice, (int) selectedMaxPrice, selectedCategory, selectedBrand, selectedStyle, selectedSort);
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<ProductModel> data;
        boolean foundInFvrt = false;

        public MyAdapter(Context context, ArrayList<ProductModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.products_custom_listview,null);
            ImageView pImage, wishlistBtn, editBtn, deleteBtn;
            TextView pDiscount, pName, pRating, pStock, pPrice, pPriceOff;
            LinearLayout options, item;

            pImage = customListItem.findViewById(R.id.pImage);
            wishlistBtn = customListItem.findViewById(R.id.wishlistBtn);
            editBtn = customListItem.findViewById(R.id.editBtn);
            deleteBtn = customListItem.findViewById(R.id.deleteBtn);
            pDiscount = customListItem.findViewById(R.id.pDiscount);
            pName = customListItem.findViewById(R.id.pName);
            pRating = customListItem.findViewById(R.id.pRating);
            pStock = customListItem.findViewById(R.id.pStock);
            pPrice = customListItem.findViewById(R.id.pPrice);
            pPriceOff = customListItem.findViewById(R.id.pPriceOff);
            options = customListItem.findViewById(R.id.options);
            item = customListItem.findViewById(R.id.item);

            options.setVisibility(View.GONE);
            if(!data.get(i).getpDiscount().equals("0")){
                pDiscount.setVisibility(View.VISIBLE);
                pDiscount.setText(data.get(i).getpDiscount()+"% OFF");
            } else {
                pPriceOff.setVisibility(View.GONE);
            }
            pName.setText(data.get(i).getpName());
            pStock.setText(data.get(i).getpStock()+" Stock");
            pPriceOff.setText("$"+data.get(i).getpPrice());
            Glide.with(context).load(data.get(i).getpImage()).into(pImage);

            double discount = Double.parseDouble(data.get(i).getpDiscount())/100;
            double calcDiscount = Double.parseDouble(data.get(i).getpPrice()) * discount;
            double totalPrice = Double.parseDouble(data.get(i).getpPrice()) - calcDiscount;
            pPrice.setText("$"+Math.round(totalPrice));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("pid",data.get(i).getId());
                    startActivity(intent);
                }
            });

            if (UID != null && !UID.equals("")) {
                try {
                    MainActivity.db.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int favoriteCount = 0;
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    WishlistModel model = ds.getValue(WishlistModel.class);
                                    if(data.size()>0){
                                        if (model.getUID().equals(UID) && model.getPID().equals(data.get(i).getId())) {
                                            favoriteCount++;
                                        }
                                    }
                                }
                                wishlistBtn.setImageResource(R.drawable.heart_outlined);
                                if (favoriteCount > 0) {
                                    wishlistBtn.setImageResource(R.drawable.heart_gradient);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "onCancelled: " + error.getMessage());
                        }
                    });

                } catch (Exception e){

                }
            }

            wishlistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.db.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (UID.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(data.get(i).getId())) {
                                        String fvrtItemId = ds.getKey();
                                        foundInFvrt = true;
                                        MainActivity.db.child("Wishlist").child(fvrtItemId).removeValue();
                                        wishlistBtn.setImageResource(R.drawable.heart_outlined);
                                        Dialog alertdialog = new Dialog(context);
                                        alertdialog.setContentView(R.layout.dialog_success);
                                        alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                        alertdialog.getWindow().setGravity(Gravity.CENTER);
                                        alertdialog.setCancelable(false);
                                        alertdialog.setCanceledOnTouchOutside(false);
                                        TextView message = alertdialog.findViewById(R.id.msgDialog);
                                        message.setText("Product Removed From Wishlist Successfully");
                                        alertdialog.show();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                alertdialog.dismiss();
                                            }
                                        },2000);

                                        break;
                                    }
                                }
                            }

                            if (!foundInFvrt) {
                                HashMap<String, String> Obj = new HashMap<String,String>();
                                Obj.put("UID",UID);
                                Obj.put("PID",data.get(i).getId());
                                MainActivity.db.child("Wishlist").push().setValue(Obj);
                                wishlistBtn.setImageResource(R.drawable.heart_gradient);
                                Dialog alertdialog = new Dialog(context);
                                alertdialog.setContentView(R.layout.dialog_success);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                TextView message = alertdialog.findViewById(R.id.msgDialog);
                                message.setText("Product is Added into Wishlist");
                                alertdialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                    }
                                },2000);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "onCancelled: " + error.getMessage());
                        }
                    });
                }
            });

            if(i==data.size()-1){
                customListItem.setPadding(customListItem.getPaddingLeft(), customListItem.getPaddingTop(),customListItem.getPaddingRight(), 30);
            }
            if(i==0){
                customListItem.setPadding(customListItem.getPaddingLeft(), 0,customListItem.getPaddingRight(), 0);
            }
            customListItem.setAlpha(0f);
            customListItem.animate().alpha(1f).setDuration(200).setStartDelay(i * 2).start();
            return customListItem;
        }
    }
}