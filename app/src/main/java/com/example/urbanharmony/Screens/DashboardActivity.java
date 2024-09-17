package com.example.urbanharmony.Screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.Fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String name, email, role, image, milkRate, stockRate, created_on;
    BottomNavigationView bottomAppBar;
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

        // Check User's Status
        MainActivity.checkStatus(DashboardActivity.this,UID);

        // Check Toaster
        MainActivity.checkMaintainance(DashboardActivity.this);

        // Set Default Home Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.frame,new HomeFragment()).commit();

        bottomAppBar.setOnItemSelectedListener(item -> {
            switch (item.getTitle().toString()){
                case "Home":
                    replaceFragment(new HomeFragment());
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
    }

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
    }
}