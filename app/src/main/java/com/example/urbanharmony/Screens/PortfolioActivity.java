package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortfolioActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    CircleImageView profileImage;
    TextView profileName, userId, shortBio, longBio;
    Button addBtn, modifyBtn;
    StorageReference mStorage;
    StorageTask uploadTask;
    Uri imageUri;
    //    Dialog Elements
    Dialog portfolioDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText shortBioInput, longBioInput;
    TextInputLayout shortBioLayout, longBioLayout;
    TextView title, imageErrorText;
    ImageView portfolioImage, editImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_portfolio);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mStorage = FirebaseStorage.getInstance().getReference();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        userId = findViewById(R.id.userId);
        shortBio = findViewById(R.id.shortBio);
        longBio = findViewById(R.id.longBio);
        addBtn = findViewById(R.id.addBtn);
        modifyBtn = findViewById(R.id.modifyBtn);

        userId.setText(UID);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portfolioForm("add",""+UID);
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portfolioForm("edit",""+UID);
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PortfolioActivity.super.onBackPressed();
            }
        });

        checkPortfolio();
    }

    public void checkPortfolio(){
        MainActivity.db.child("Portfolio").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("portfolioStatus").getValue().toString().equals("0")){
                        Dialog alertdialog = new Dialog(PortfolioActivity.this);
                        alertdialog.setContentView(R.layout.dialog_error);
                        alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        alertdialog.getWindow().setGravity(Gravity.CENTER);
                        alertdialog.setCancelable(false);
                        alertdialog.setCanceledOnTouchOutside(false);
                        TextView message = alertdialog.findViewById(R.id.msgDialog);
                        message.setText("Your Portfolio Is Not Created!!!");
                        alertdialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertdialog.dismiss();
                            }
                        },4000);
                    } else if(snapshot.child("portfolioStatus").getValue().toString().equals("1")){
                        addBtn.setVisibility(View.GONE);
                        modifyBtn.setVisibility(View.VISIBLE);
                        shortBio.setText(snapshot.child("shortBio").getValue().toString());
                        longBio.setText(snapshot.child("longBio").getValue().toString());
                        Glide.with(PortfolioActivity.this).load(snapshot.child("image").getValue().toString().trim()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void portfolioForm(String purpose, String productId){
        portfolioDialog = new Dialog(PortfolioActivity.this);
        portfolioDialog.setContentView(R.layout.dialog_add_portfolio);
        portfolioDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        portfolioDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        portfolioDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        portfolioDialog.getWindow().setGravity(Gravity.CENTER);
        portfolioDialog.setCancelable(false);
        portfolioDialog.setCanceledOnTouchOutside(false);
        cancelBtn = portfolioDialog.findViewById(R.id.cancelBtn);
        addDataBtn = portfolioDialog.findViewById(R.id.addDataBtn);
        title = portfolioDialog.findViewById(R.id.title);
        portfolioImage = portfolioDialog.findViewById(R.id.portfolioImage);
        editImageBtn = portfolioDialog.findViewById(R.id.editImageBtn);
        imageErrorText = portfolioDialog.findViewById(R.id.imageErrorText);
        shortBioInput = portfolioDialog.findViewById(R.id.shortBioInput);
        shortBioLayout = portfolioDialog.findViewById(R.id.shortBioLayout);
        longBioInput = portfolioDialog.findViewById(R.id.longBioInput);
        longBioLayout = portfolioDialog.findViewById(R.id.longBioLayout);

        shortBioInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                shortBioValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        longBioInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                longBioValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Dialog dialog = new Dialog(PortfolioActivity.this);
                    dialog.setContentView(R.layout.dialog_error);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    TextView msg = dialog.findViewById(R.id.msgDialog);
                    msg.setText("Image Uploading In Process!!!");
                    dialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    },2000);
                } else {
                    if(purpose.equals("add")){
                        validation("false",purpose, productId);
                    } else if(purpose.equals("edit")){
                        validation("true",purpose, productId);
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portfolioDialog.dismiss();
                imageUri = null;
            }
        });

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 420);
            }
        });

        if(purpose.equals("edit")){
            MainActivity.db.child("Portfolio").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Glide.with(PortfolioActivity.this).load(snapshot.child("image").getValue().toString().trim()).into(portfolioImage);
                        shortBioInput.setText(snapshot.child("shortBio").getValue().toString().trim());
                        longBioInput.setText(snapshot.child("longBio").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        portfolioDialog.show();
    }

    public boolean shortBioValidation(){
        String input = shortBioInput.getText().toString().trim();
        String regex = "^[a-zA-Z.,'()&\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            shortBioLayout.setError("Short Biography is Required!!!");
            return false;
        } else if(input.length() < 20){
            shortBioLayout.setError("Short Biography at least 20 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            shortBioLayout.setError("Only text allowed!!!");
            return false;
        } else {
            shortBioLayout.setError(null);
            return true;
        }
    }
    public boolean longBioValidation(){
        String input = longBioInput.getText().toString().trim();
        String regex = "^[a-zA-Z.,'()&\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            longBioLayout.setError("Long Biography is Required!!!");
            return false;
        } else if(input.length() < 20){
            longBioLayout.setError("Long Biography at least 20 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            longBioLayout.setError("Only text allowed!!!");
            return false;
        } else {
            longBioLayout.setError(null);
            return true;
        }
    }
    public boolean imageValidation(){
        if(imageUri == null){
            imageErrorText.setText("Portfolio Image is Required!!!");
            imageErrorText.setVisibility(View.VISIBLE);
            return false;
        } else {
            imageErrorText.setText(null);
            imageErrorText.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 420 && resultCode == RESULT_OK){
            imageUri = data.getData();
            portfolioImage.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void validation(String imageStatus, String purpose, String productId) {
        boolean imageErr = false, shortBioErr = false, longBioErr = false;
        shortBioErr = shortBioValidation();
        longBioErr = longBioValidation();
        if(imageStatus.equals("true")){
            imageErr = true;
        } else {
            imageErr = imageValidation();
        }
        if((imageErr && shortBioErr && longBioErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(PortfolioActivity.this)){
            if(imageUri != null){
                Dialog loading = new Dialog(PortfolioActivity.this);
                loading.setContentView(R.layout.dialog_loading);
                loading.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loading.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                loading.getWindow().setGravity(Gravity.CENTER);
                loading.setCancelable(false);
                loading.setCanceledOnTouchOutside(false);
                TextView message = loading.findViewById(R.id.msgDialog);
                if(purpose.equals("edit")){
                    message.setText("Modifying...");
                } else {
                    message.setText("Creating...");
                }
                loading.show();
                uploadTask = mStorage.child("PortfolioImages/"+System.currentTimeMillis()+"."+getFileExtension(imageUri)).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                loading.dismiss();
                                String photoLink = uri.toString();

                                Dialog alertdialog = new Dialog(PortfolioActivity.this);
                                alertdialog.setContentView(R.layout.dialog_success);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                TextView messageAlert = alertdialog.findViewById(R.id.msgDialog);
                                alertdialog.show();

                                if(purpose.equals("add")){
                                    MainActivity.db.child("Portfolio").child(productId).child("image").setValue(photoLink);
                                    MainActivity.db.child("Portfolio").child(productId).child("shortBio").setValue(shortBioInput.getText().toString().trim());
                                    MainActivity.db.child("Portfolio").child(productId).child("longBio").setValue(longBioInput.getText().toString().trim());
                                    MainActivity.db.child("Portfolio").child(productId).child("portfolioStatus").setValue("1");
                                    messageAlert.setText("Portfolio Created Successfully!!!");
                                } else if(purpose.equals("edit")){
                                    MainActivity.db.child("Portfolio").child(productId).child("image").setValue(photoLink);
                                    MainActivity.db.child("Portfolio").child(productId).child("shortBio").setValue(shortBioInput.getText().toString().trim());
                                    MainActivity.db.child("Portfolio").child(productId).child("longBio").setValue(longBioInput.getText().toString().trim());
                                    messageAlert.setText("Portfolio Modified Successfully!!!");
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                        portfolioDialog.dismiss();
                                        checkPortfolio();
                                    }
                                },2000);

                            }
                        });
                    }
                });
            } else {
                Dialog alertdialog = new Dialog(PortfolioActivity.this);
                alertdialog.setContentView(R.layout.dialog_success);
                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertdialog.getWindow().setGravity(Gravity.CENTER);
                alertdialog.setCancelable(false);
                alertdialog.setCanceledOnTouchOutside(false);
                TextView message = alertdialog.findViewById(R.id.msgDialog);
                message.setText("Portfolio Modified Successfully!!!");
                alertdialog.show();

                if(purpose.equals("edit")){
                    MainActivity.db.child("Portfolio").child(productId).child("shortBio").setValue(shortBioInput.getText().toString().trim());
                    MainActivity.db.child("Portfolio").child(productId).child("longBio").setValue(longBioInput.getText().toString().trim());
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertdialog.dismiss();
                        portfolioDialog.dismiss();
                        checkPortfolio();
                    }
                },2000);
            }
        }
    }
}