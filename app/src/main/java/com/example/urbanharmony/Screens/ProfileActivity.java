package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    EditText nameInput, usernameInput, contactInput;
    TextView emailText, createdOnText, roleText, saveBtn;
    ImageView editImageBtn, profileImage;
    static String UID = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int[] images = {
            R.drawable.boy_1,
            R.drawable.boy_2,
            R.drawable.boy_3,
            R.drawable.boy_4,
            R.drawable.boy_5,
            R.drawable.boy_6,
            R.drawable.boy_7,
            R.drawable.boy_8,
            R.drawable.boy_9,
            R.drawable.boy_10,
            R.drawable.boy_11,
            R.drawable.boy_12,
            R.drawable.boy_13,
            R.drawable.boy_14,
            R.drawable.boy_15,
            R.drawable.boy_16,
            R.drawable.boy_17,
            R.drawable.boy_18
    };

    int[] imagesGirls = {
            R.drawable.girl_1,
            R.drawable.girl_2,
            R.drawable.girl_3,
            R.drawable.girl_4,
            R.drawable.girl_5,
            R.drawable.girl_6,
            R.drawable.girl_7,
            R.drawable.girl_8,
            R.drawable.girl_9,
            R.drawable.girl_10,
            R.drawable.girl_11,
            R.drawable.girl_12,
            R.drawable.girl_13,
            R.drawable.girl_14,
            R.drawable.girl_15,
            R.drawable.girl_16,
            R.drawable.girl_17,
            R.drawable.girl_18,
            R.drawable.girl_19,
            R.drawable.girl_20
    };

    Dialog dialogImage;
    CardView boy_1, boy_2, boy_3, boy_4, boy_5, boy_6, boy_7, boy_8, boy_9, boy_10, boy_11, boy_12, boy_13, boy_14, boy_15, boy_16, boy_17, boy_18;
    CardView girl_1, girl_2, girl_3, girl_4, girl_5, girl_6, girl_7, girl_8, girl_9, girl_10, girl_11, girl_12, girl_13, girl_14, girl_15, girl_16, girl_17, girl_18, girl_19, girl_20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        nameInput = findViewById(R.id.nameInput);
        usernameInput = findViewById(R.id.usernameInput);
        contactInput = findViewById(R.id.contactInput);
        emailText = findViewById(R.id.emailText);
        createdOnText = findViewById(R.id.createdOnText);
        roleText = findViewById(R.id.roleText);
        saveBtn = findViewById(R.id.saveBtn);
        editImageBtn = findViewById(R.id.editImageBtn);
        profileImage = findViewById(R.id.profileImage);

        nameInput.setText(DashboardActivity.getName());
        emailText.setText(DashboardActivity.getEmail());
        createdOnText.setText(DashboardActivity.getCreatedOn());
        roleText.setText(DashboardActivity.getRole());

        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
            MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(!snapshot.child("image").getValue().toString().equals("")){
                            profileImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                        }
                        nameInput.setText(snapshot.child("name").getValue().toString());
                        usernameInput.setText(snapshot.child("username").getValue().toString());
                        contactInput.setText(snapshot.child("contact").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contactInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                contactValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImage = new Dialog(ProfileActivity.this);
                dialogImage.setContentView(R.layout.dialog_bottom_profile_image);
                dialogImage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogImage.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogImage.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
                dialogImage.getWindow().setGravity(Gravity.BOTTOM);
                dialogImage.setCanceledOnTouchOutside(false);
                dialogImage.setCancelable(false);
                Button cancelBtn, boyBtn, girlBtn;
                LinearLayout boysContainer, girlsContainer;
                cancelBtn = dialogImage.findViewById(R.id.cancelBtn);
                boyBtn = dialogImage.findViewById(R.id.boyBtn);
                girlBtn = dialogImage.findViewById(R.id.girlBtn);
                boysContainer = dialogImage.findViewById(R.id.boysContainer);
                girlsContainer = dialogImage.findViewById(R.id.girlsContainer);
                boy_1 = dialogImage.findViewById(R.id.boy_1);
                boy_2 = dialogImage.findViewById(R.id.boy_2);
                boy_3 = dialogImage.findViewById(R.id.boy_3);
                boy_4 = dialogImage.findViewById(R.id.boy_4);
                boy_5 = dialogImage.findViewById(R.id.boy_5);
                boy_6 = dialogImage.findViewById(R.id.boy_6);
                boy_7 = dialogImage.findViewById(R.id.boy_7);
                boy_8 = dialogImage.findViewById(R.id.boy_8);
                boy_9 = dialogImage.findViewById(R.id.boy_9);
                boy_10 = dialogImage.findViewById(R.id.boy_10);
                boy_11 = dialogImage.findViewById(R.id.boy_11);
                boy_12 = dialogImage.findViewById(R.id.boy_12);
                boy_13 = dialogImage.findViewById(R.id.boy_13);
                boy_14 = dialogImage.findViewById(R.id.boy_14);
                boy_15 = dialogImage.findViewById(R.id.boy_15);
                boy_16 = dialogImage.findViewById(R.id.boy_16);
                boy_17 = dialogImage.findViewById(R.id.boy_17);
                boy_18 = dialogImage.findViewById(R.id.boy_18);
                girl_1 = dialogImage.findViewById(R.id.girl_1);
                girl_2 = dialogImage.findViewById(R.id.girl_2);
                girl_3 = dialogImage.findViewById(R.id.girl_3);
                girl_4 = dialogImage.findViewById(R.id.girl_4);
                girl_5 = dialogImage.findViewById(R.id.girl_5);
                girl_6 = dialogImage.findViewById(R.id.girl_6);
                girl_7 = dialogImage.findViewById(R.id.girl_7);
                girl_8 = dialogImage.findViewById(R.id.girl_8);
                girl_9 = dialogImage.findViewById(R.id.girl_9);
                girl_10 = dialogImage.findViewById(R.id.girl_10);
                girl_11 = dialogImage.findViewById(R.id.girl_11);
                girl_12 = dialogImage.findViewById(R.id.girl_12);
                girl_13 = dialogImage.findViewById(R.id.girl_13);
                girl_14 = dialogImage.findViewById(R.id.girl_14);
                girl_15 = dialogImage.findViewById(R.id.girl_15);
                girl_16 = dialogImage.findViewById(R.id.girl_16);
                girl_17 = dialogImage.findViewById(R.id.girl_17);
                girl_18 = dialogImage.findViewById(R.id.girl_18);
                girl_19 = dialogImage.findViewById(R.id.girl_19);
                girl_20 = dialogImage.findViewById(R.id.girl_20);
                dialogImage.show();

                boyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boysContainer.setVisibility(View.VISIBLE);
                        girlsContainer.setVisibility(View.GONE);
                        boysContainer.setAlpha(0f);
                        boysContainer.animate().alpha(1f).setDuration(500).start();
                    }
                });

                girlBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        girlsContainer.setVisibility(View.VISIBLE);
                        boysContainer.setVisibility(View.GONE);
                        girlsContainer.setAlpha(0f);
                        girlsContainer.animate().alpha(1f).setDuration(500).start();
                    }
                });

                boy_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(0);
                    }
                });

                boy_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(1);
                    }
                });

                boy_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(2);
                    }
                });

                boy_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(3);
                    }
                });

                boy_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(4);
                    }
                });

                boy_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(5);
                    }
                });

                boy_7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(6);
                    }
                });

                boy_8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(7);
                    }
                });

                boy_9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(8);
                    }
                });

                boy_10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(9);
                    }
                });

                boy_11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(10);
                    }
                });

                boy_12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(11);
                    }
                });

                boy_13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(12);
                    }
                });

                boy_14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(13);
                    }
                });

                boy_15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(14);
                    }
                });

                boy_16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(15);
                    }
                });

                boy_17.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(16);
                    }
                });

                boy_18.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImage(17);
                    }
                });

                girl_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(0);
                    }
                });

                girl_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(1);
                    }
                });

                girl_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(2);
                    }
                });

                girl_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(3);
                    }
                });

                girl_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(4);
                    }
                });

                girl_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(5);
                    }
                });

                girl_7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(6);
                    }
                });

                girl_8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(7);
                    }
                });

                girl_9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(8);
                    }
                });

                girl_10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(9);
                    }
                });

                girl_11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(10);
                    }
                });

                girl_12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(11);
                    }
                });

                girl_13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(12);
                    }
                });

                girl_14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(13);
                    }
                });

                girl_15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(14);
                    }
                });

                girl_16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(15);
                    }
                });

                girl_17.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(16);
                    }
                });

                girl_18.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(17);
                    }
                });

                girl_19.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(18);
                    }
                });

                girl_20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProfileImageGirls(19);
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogImage.dismiss();
                    }
                });
            }
        });
    }

    public boolean nameValidation(){
        String input = nameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            nameInput.setError("Name is Required!!!");
            return false;
        } else if(input.length() < 3){
            nameInput.setError("Name at least 3 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            nameInput.setError("Only text allowed!!!");
            return false;
        } else {
            nameInput.setError(null);
            return true;
        }
    }

    public boolean usernameValidation(){
        String input = usernameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            usernameInput.setError("Username is Required!!!");
            return false;
        } else if(input.length() < 5){
            usernameInput.setError("Username at least 5 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            usernameInput.setError("Only text allowed!!!");
            return false;
        } else {
            usernameInput.setError(null);
            return true;
        }
    }

    public boolean contactValidation(){
        String input = contactInput.getText().toString().trim();
        String regex = "^[0-9\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            contactInput.setError("Contact No is Required!!!");
            return false;
        } else if(input.length() < 11){
            contactInput.setError("Contact No at least 11 Digits!!!");
            return false;
        } else if(!matcher.matches()){
            contactInput.setError("Only Digits allowed!!!");
            return false;
        } else {
            contactInput.setError(null);
            return true;
        }
    }

    public void validation(){
        boolean nameErr = false, usernameErr = false, contactErr = false;
        nameErr = nameValidation();
        usernameErr = usernameValidation();
        contactErr = contactValidation();
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView msg = dialog.findViewById(R.id.msgDialog);
        msg.setText("Loading...");
        dialog.show();
        if((nameErr && usernameErr && contactErr) == true){
            dialog.dismiss();
            // Name Update In Firebase Realtime Database
            MainActivity.db.child("Users").child(UID).child("name").setValue(nameInput.getText().toString().trim());
            MainActivity.db.child("Users").child(UID).child("username").setValue(usernameInput.getText().toString().trim());
            MainActivity.db.child("Users").child(UID).child("contact").setValue(contactInput.getText().toString().trim());
            Dialog dialogSuccess = new Dialog(ProfileActivity.this);
            dialogSuccess.setContentView(R.layout.dialog_success);
            dialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogSuccess.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialogSuccess.getWindow().setGravity(Gravity.CENTER);
            dialogSuccess.setCanceledOnTouchOutside(false);
            dialogSuccess.setCancelable(false);
            msg = dialogSuccess.findViewById(R.id.msgDialog);
            msg.setText("Profile Updated Successfully!!!");
            dialogSuccess.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialogSuccess.dismiss();
                }
            },2000);
        }
    }

    public void setProfileImage(int value){
        profileImage.setImageResource(images[value]);
        MainActivity.db.child("Users").child(UID).child("image").setValue(""+images[value]);
        Dialog dialogSuccess = new Dialog(ProfileActivity.this);
        dialogSuccess.setContentView(R.layout.dialog_success);
        dialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogSuccess.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogSuccess.getWindow().setGravity(Gravity.CENTER);
        dialogSuccess.setCanceledOnTouchOutside(false);
        dialogSuccess.setCancelable(false);
        TextView msg = dialogSuccess.findViewById(R.id.msgDialog);
        msg.setText("Profile Image Updated Successfully!!!");
        dialogSuccess.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogSuccess.dismiss();
                dialogImage.dismiss();
            }
        },2000);
    }

    public void setProfileImageGirls(int value){
        profileImage.setImageResource(imagesGirls[value]);
        MainActivity.db.child("Users").child(UID).child("image").setValue(""+imagesGirls[value]);
        Dialog dialogSuccess = new Dialog(ProfileActivity.this);
        dialogSuccess.setContentView(R.layout.dialog_success);
        dialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogSuccess.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogSuccess.getWindow().setGravity(Gravity.CENTER);
        dialogSuccess.setCanceledOnTouchOutside(false);
        dialogSuccess.setCancelable(false);
        TextView msg = dialogSuccess.findViewById(R.id.msgDialog);
        msg.setText("Profile Image Updated Successfully!!!");
        dialogSuccess.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogSuccess.dismiss();
                dialogImage.dismiss();
            }
        },2000);
    }
}