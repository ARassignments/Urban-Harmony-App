package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.Adapter.DesignerProjectAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.ProjectModel;
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class DesignerDetailActivity extends AppCompatActivity {
    static String UID = "";
    CircleImageView profileImage;
    TextView profileName, shortBio, longBio;
    RecyclerView projectsView;
    ArrayList<ProjectModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_designer_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            UID = extra.getString("userId");
        }

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        projectsView = findViewById(R.id.projectsView);
        shortBio = findViewById(R.id.shortBio);
        longBio = findViewById(R.id.longBio);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DesignerDetailActivity.super.onBackPressed();
            }
        });

        fetchDetails();
        fetchProjects();
        checkPortfolio();
    }

    public void fetchDetails() {
        MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileName.setText(snapshot.child("name").getValue().toString().trim());
//                    if(!snapshot.child("image").getValue().toString().equals("")){
//                        profileImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchProjects(){
        MainActivity.db.child("Projects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("userId").getValue().toString().trim().equals(UID)){
                            ProjectModel model = new ProjectModel(ds.getKey(),
                                    ds.child("pName").getValue().toString(),
                                    ds.child("pImage").getValue().toString(),
                                    ds.child("pDesc").getValue().toString(),
                                    ds.child("userId").getValue().toString()
                            );
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
                        DesignerProjectAdapter adapter = new DesignerProjectAdapter(DesignerDetailActivity.this,datalist);
                        projectsView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkPortfolio(){
        MainActivity.db.child("Portfolio").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("portfolioStatus").getValue().toString().equals("1")){
                        shortBio.setText(snapshot.child("shortBio").getValue().toString());
                        longBio.setText(snapshot.child("longBio").getValue().toString());
                        Glide.with(DesignerDetailActivity.this).load(snapshot.child("image").getValue().toString().trim()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}