package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

public class UserProfileActivity extends AppCompatActivity {
    TextView nameText, emailText, createdOnText, roleText, pwdText, usernameText, contactText;
    static String UID = "";
    Button activateBtn, deactivateBtn;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            UID = extra.getString("userId");
        }

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        createdOnText = findViewById(R.id.createdOnText);
        roleText = findViewById(R.id.roleText);
        pwdText = findViewById(R.id.pwdText);
        activateBtn = findViewById(R.id.activateBtn);
        deactivateBtn = findViewById(R.id.deactivateBtn);
        profileImage = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        contactText = findViewById(R.id.contactText);

        fetchDetails();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.super.onBackPressed();
            }
        });


        activateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.db.child("Users").child(UID).child("status").setValue("1");
                Dialog dialogSuccess = new Dialog(UserProfileActivity.this);
                dialogSuccess.setContentView(R.layout.dialog_success);
                dialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogSuccess.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialogSuccess.getWindow().setGravity(Gravity.CENTER);
                dialogSuccess.setCanceledOnTouchOutside(false);
                dialogSuccess.setCancelable(false);
                TextView msg = dialogSuccess.findViewById(R.id.msgDialog);
                msg.setText("User Activate Successfully!!!");
                dialogSuccess.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogSuccess.dismiss();
                        fetchDetails();
                    }
                }, 4000);
            }
        });

        deactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.db.child("Users").child(UID).child("status").setValue("0");
                Dialog dialogSuccess = new Dialog(UserProfileActivity.this);
                dialogSuccess.setContentView(R.layout.dialog_success);
                dialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogSuccess.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialogSuccess.getWindow().setGravity(Gravity.CENTER);
                dialogSuccess.setCanceledOnTouchOutside(false);
                dialogSuccess.setCancelable(false);
                TextView msg = dialogSuccess.findViewById(R.id.msgDialog);
                msg.setText("User Deactivate Successfully!!!");
                dialogSuccess.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogSuccess.dismiss();
                        fetchDetails();
                    }
                }, 4000);
            }
        });
    }

    public void fetchDetails() {
        MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameText.setText(snapshot.child("name").getValue().toString().trim());
                    emailText.setText(snapshot.child("email").getValue().toString().trim());
                    createdOnText.setText(snapshot.child("created_on").getValue().toString().trim());
                    roleText.setText(snapshot.child("role").getValue().toString().trim());
                    pwdText.setText(snapshot.child("pwd").getValue().toString().trim());
                    usernameText.setText(snapshot.child("username").getValue().toString().trim());
                    contactText.setText(snapshot.child("contact").getValue().toString().trim());
                    if(!snapshot.child("image").getValue().toString().equals("")){
                        Glide.with(UserProfileActivity.this).load(snapshot.child("image").getValue().toString()).into(profileImage);
//                        profileImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                    }
                    String status = snapshot.child("status").getValue().toString().trim();
                    if (status.equals("1")) {
                        deactivateBtn.setVisibility(View.VISIBLE);
                        activateBtn.setVisibility(View.GONE);
                    } else if (status.equals("0")) {
                        deactivateBtn.setVisibility(View.GONE);
                        activateBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}