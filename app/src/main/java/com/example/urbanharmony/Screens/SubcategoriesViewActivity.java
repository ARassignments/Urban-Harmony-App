package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.urbanharmony.Adapter.SubCategoriesAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.SubCategoryModel;
import com.example.urbanharmony.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubcategoriesViewActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    RecyclerView recyclerView;
    LinearLayout loader, notfoundContainer;
    TextView categoryTitle;
    ArrayList<SubCategoryModel> datalist = new ArrayList<>();
    String categoryId, categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subcategories_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            categoryId = extras.getString("categoryId");
            categoryName = extras.getString("categoryName");
        }

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        recyclerView = findViewById(R.id.recyclerView);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        categoryTitle = findViewById(R.id.categoryTitle);
        loader = findViewById(R.id.loader);

        categoryTitle.setText(categoryName);

        fetchData("");

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubcategoriesViewActivity.super.onBackPressed();
            }
        });
    }

    public void fetchData(String data){
        MainActivity.db.child("Category").child(categoryId).child("SubCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            SubCategoryModel model = new SubCategoryModel(ds.getKey(),
                                    ds.child("name").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("name").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                SubCategoryModel model = new SubCategoryModel(ds.getKey(),
                                        ds.child("name").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        // Setting the layout as Staggered Grid for vertical orientation
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(staggeredGridLayoutManager);
                        SubCategoriesAdapter adapter = new SubCategoriesAdapter(SubcategoriesViewActivity.this,datalist,categoryId,categoryName);
                        recyclerView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    loader.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}