package com.example.urbanharmony.Screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProjectDetailActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    String PID = "";
    ImageView pImage;
    TextView pName, pDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            PID = extra.getString("pid");
        }

        pImage = findViewById(R.id.pImage);
        pName = findViewById(R.id.pName);
        pDesc = findViewById(R.id.pDesc);

        MainActivity.db.child("Projects").child(PID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Glide.with(ProjectDetailActivity.this).load(snapshot.child("pImage").getValue().toString().trim()).into(pImage);
                    pName.setText(snapshot.child("pName").getValue().toString().trim());
                    pDesc.setText(snapshot.child("pDesc").getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectDetailActivity.super.onBackPressed();
            }
        });
    }
}