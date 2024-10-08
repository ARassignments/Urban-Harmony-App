package com.example.urbanharmony.Screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.Fragments.AccountFragment;
import com.example.urbanharmony.Screens.Fragments.CartFragment;
import com.example.urbanharmony.Screens.Fragments.CatalogFragment;
import com.example.urbanharmony.Screens.Fragments.HomeFragment;
import com.example.urbanharmony.Screens.Fragments.WishlistFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String name, email, role, image, username, contact, created_on;
    BottomNavigationView bottomAppBar;
    public static BadgeDrawable badgeCart, badgeWishlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        bottomAppBar = findViewById(R.id.bottomAppBar);

        // Check Login Status By SharedPreferences
        if(!sharedPreferences.contains("loginStatus")){
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
            MainActivity.db.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        name = snapshot.child("name").getValue().toString();
                        email = snapshot.child("email").getValue().toString();
                        image = snapshot.child("image").getValue().toString();
                        role = snapshot.child("role").getValue().toString();
                        username = snapshot.child("username").getValue().toString();
                        contact = snapshot.child("contact").getValue().toString();
                        created_on = snapshot.child("created_on").getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // Check User's Status
        MainActivity.checkStatus(DashboardActivity.this,UID);

        // Check Toaster
        MainActivity.checkMaintainance(DashboardActivity.this);

        badgeCart = bottomAppBar.getOrCreateBadge(R.id.cart);
        badgeCart.setVisible(false);
        badgeCart.setBackgroundColor(getResources().getColor(R.color.accent_50));

        badgeWishlist = bottomAppBar.getOrCreateBadge(R.id.wishlist);
        badgeWishlist.setVisible(false);
        badgeWishlist.setBackgroundColor(getResources().getColor(R.color.accent_50));

        // Set Default Home Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.frame,new HomeFragment()).commit();

        bottomAppBar.setOnItemSelectedListener(item -> {
            switch (item.getTitle().toString()){
                case "Home":
                    replaceFragment(new HomeFragment());
                    break;
                case "Account":
                    replaceFragment(new AccountFragment());
                    break;
                case "Wishlist":
                    replaceFragment(new WishlistFragment());
                    break;
                case "Cart":
                    replaceFragment(new CartFragment());
                    break;
                case "Catalog":
                    replaceFragment(new CatalogFragment());
                    break;
//                case "Search":
//                    replaceFragment(new SearchFragment());
//                    break;
//                case "Bills":
//                    replaceFragment(new ReportsFragment());
//                    break;
            }
            return true;
        });

        MainActivity.db.child("Wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int favoriteCount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (UID.equals(ds.child("UID").getValue())) {
                        favoriteCount++;
                    }
                }
                updateWishlistCount(favoriteCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });

        MainActivity.db.child("AddToCart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartCount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (UID.equals(ds.child("UID").getValue())) {
                        cartCount++;
                    }
                }
                updateCartCount(cartCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }

    public static void updateCartCount(int number){
        if (number > 0) {
            badgeCart.setVisible(true);
            badgeCart.setNumber(number);
        } else {
            badgeCart.setVisible(false);
        }
    }
    public static void updateWishlistCount(int number){
        if (number > 0) {
            badgeWishlist.setVisible(true);
            badgeWishlist.setNumber(number);
        } else {
            badgeWishlist.setVisible(false);
        }
    }

    public void openCatalogPage(){
        replaceFragment(new CartFragment());
        bottomAppBar.setSelectedItemId(R.id.catalog);
    }

    public void openWishlistPage(){
        replaceFragment(new WishlistFragment());
        bottomAppBar.setSelectedItemId(R.id.wishlist);
    }

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

    public static String getRole() {
        return role;
    }

    public static String getImage() {
        return image;
    }

    public static String getUsername() {
        return username;
    }

    public static String getContact() {
        return contact;
    }

    public static String getCreatedOn() {
        return created_on;
    }
}