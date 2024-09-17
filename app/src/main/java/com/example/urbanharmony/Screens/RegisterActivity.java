package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput, usernameInput, contactInput, emailInput, pwdInput, cpwdInput;
    Button registerBtn;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        nameInput = findViewById(R.id.nameInput);
        usernameInput = findViewById(R.id.usernameInput);
        contactInput = findViewById(R.id.contactInput);
        emailInput = findViewById(R.id.emailInput);
        pwdInput = findViewById(R.id.pwdInput);
        cpwdInput = findViewById(R.id.cpwdInput);
        registerBtn = findViewById(R.id.registerBtn);
        loader = findViewById(R.id.loader);

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

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pwdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pwdValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cpwdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cpwdValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.super.onBackPressed();
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

    public boolean emailValidation(){
        String input = emailInput.getText().toString().trim();
        if(input.equals("")){
            emailInput.setError("Email Address is Required!!!");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(input).matches()){
            emailInput.setError("Enter Valid Email Address!!!");
            return false;
        } else {
            emailInput.setError(null);
            return true;
        }
    }

    public boolean pwdValidation(){
        String input = pwdInput.getText().toString().trim();
        if(input.equals("")){
            pwdInput.setError("Password is Required!!!");
            return false;
        } else if(input.length() < 8){
            pwdInput.setError("Password at least 8 Characters!!!");
            return false;
        } else {
            pwdInput.setError(null);
            return true;
        }
    }

    public boolean cpwdValidation(){
        String input = cpwdInput.getText().toString().trim();
        String pwd = pwdInput.getText().toString().trim();
        if(input.equals("")){
            cpwdInput.setError("Confirm Password is Required!!!");
            return false;
        } else if(input.length() < 8){
            cpwdInput.setError("Confirm Password at least 8 Characters!!!");
            return false;
        } else if(!input.equals(pwd)){
            cpwdInput.setError("Confirm Password is not matched!!!");
            return false;
        } else {
            cpwdInput.setError(null);
            return true;
        }
    }

    public void validation(){
        if(MainActivity.connectionCheck(RegisterActivity.this)){
            boolean nameErr = false, usernameErr = false, contactErr = false, emailErr = false, pwdErr = false, cpwdErr = false;
            nameErr = nameValidation();
            usernameErr = usernameValidation();
            contactErr = contactValidation();
            emailErr = emailValidation();
            pwdErr = pwdValidation();
            cpwdErr = cpwdValidation();
            if((nameErr && usernameErr && contactErr && emailErr && pwdErr && cpwdErr) == true){
                loader.setVisibility(View.VISIBLE);
                registerBtn.setVisibility(View.GONE);
                Dialog dialog = new Dialog(RegisterActivity.this);
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

                // Declare Firebase Authentication Object
                FirebaseAuth auth = FirebaseAuth.getInstance();
                // Create User into Firebase Authentication
                auth.createUserWithEmailAndPassword(emailInput.getText().toString(),pwdInput.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // Get UID from Firebase Authentication
                                String userId = auth.getCurrentUser().getUid();

                                // Create a SimpleDateFormat object with the desired format.
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault());

                                // Format the date to a string
                                String currentDate = sdf.format(new Date());

                                // Create Object by HashMap
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                                HashMap<String,String> obj = new HashMap<String,String>();
                                obj.put("name",nameInput.getText().toString().trim());
                                obj.put("username",usernameInput.getText().toString().trim());
                                obj.put("contact",contactInput.getText().toString().trim());
                                obj.put("email",auth.getCurrentUser().getEmail());
                                obj.put("pwd",pwdInput.getText().toString().trim());
                                obj.put("image","");
                                obj.put("role","user");
                                obj.put("created_on",currentDate);
                                obj.put("status","1");

                                // Upload Data into Realtime Database
                                myRef.child("Users").child(userId).setValue(obj);

                                dialog.dismiss();
                                Dialog dialog = new Dialog(RegisterActivity.this);
                                dialog.setContentView(R.layout.dialog_success);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Account Created Successfully!!!");
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        RegisterActivity.super.onBackPressed();
                                    }
                                },4000);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Dialog dialog = new Dialog(RegisterActivity.this);
                                dialog.setContentView(R.layout.dialog_error);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                dialog.getWindow().setGravity(Gravity.CENTER);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                TextView msg = dialog.findViewById(R.id.msgDialog);
                                msg.setText("Your Email Already Exist!!!");
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        loader.setVisibility(View.GONE);
                                        registerBtn.setVisibility(View.VISIBLE);
                                    }
                                },4000);
                            }
                        });
            }
        }
    }
}