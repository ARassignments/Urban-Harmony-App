package com.example.urbanharmony.Screens;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.ScheduleModel;
import com.example.urbanharmony.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingAppointmentActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String CurrentUID = "";
    CircleImageView profileImage;
    TextView profileName;
    TextInputLayout dayLayout, slotLayout;
    AutoCompleteTextView dayInput, slotInput;
    Button bookBtn, cancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_appointment);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            CurrentUID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            UID = extra.getString("userId");
        }

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        dayLayout = findViewById(R.id.dayLayout);
        slotLayout = findViewById(R.id.slotLayout);
        dayInput = findViewById(R.id.dayInput);
        slotInput = findViewById(R.id.slotInput);
        bookBtn = findViewById(R.id.bookBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingAppointmentActivity.super.onBackPressed();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingAppointmentActivity.super.onBackPressed();
            }
        });

        ArrayList<ScheduleModel> scheduleList = new ArrayList<>();
        ArrayList<String> slotList = new ArrayList<>();

        MainActivity.db.child("Schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    scheduleList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.child("userId").getValue().toString().equals(UID)){
                            ScheduleModel model = new ScheduleModel(ds.getKey(),
                                    ds.child("name").getValue().toString(),
                                    ds.child("userId").getValue().toString(),
                                    ds.child("Slots").getValue().toString()
                            );
                            scheduleList.add(model);
                        }
                    }

                    ArrayList<String> dayNames = new ArrayList<>();
                    for (ScheduleModel day : scheduleList) {
                        dayNames.add(day.getName());
                    }
                    ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(BookingAppointmentActivity.this, android.R.layout.simple_dropdown_item_1line, dayNames);
                    dayInput.setAdapter(dayAdapter);

                    dayInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedDay = dayNames.get(position);

                            slotList.clear();
                            slotInput.setText("");

                            for (ScheduleModel scheduleModel : scheduleList) {
                                if (scheduleModel.getName().equals(selectedDay)) {
                                    DataSnapshot slotSnapshot = snapshot.child(scheduleModel.getId()).child("Slots");
                                    for (DataSnapshot slot : slotSnapshot.getChildren()) {
                                        slotList.add("From: "+slot.child("from").getValue().toString()+" - To: "+slot.child("to").getValue().toString());
                                    }
                                    break;
                                }
                            }

                            ArrayAdapter<String> slotAdapter = new ArrayAdapter<>(BookingAppointmentActivity.this, android.R.layout.simple_dropdown_item_1line, slotList);
                            slotInput.setAdapter(slotAdapter);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        fetchDetails();
        checkPortfolio();

        dayInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                daysValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        slotInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                slotValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
    }

    public void checkPortfolio(){
        MainActivity.db.child("Portfolio").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("portfolioStatus").getValue().toString().equals("1")){
                        Glide.with(BookingAppointmentActivity.this).load(snapshot.child("image").getValue().toString().trim()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchDetails() {
        MainActivity.db.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileName.setText(snapshot.child("name").getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean daysValidation(){
        String input = dayInput.getText().toString().trim();
        if(input.equals("")){
            dayLayout.setError("Day is Required!!!");
            return false;
        } else {
            dayLayout.setError(null);
            return true;
        }
    }

    public boolean slotValidation(){
        String input = slotInput.getText().toString().trim();
        if(input.equals("")){
            slotLayout.setError("Slot is Required!!!");
            return false;
        } else {
            slotLayout.setError(null);
            return true;
        }
    }

    private void validation() {
        boolean dayErr = false, slotErr = false;
        dayErr = daysValidation();
        slotErr = slotValidation();
        if((dayErr && slotErr) == true){
            persons();
        }
    }

    private void persons(){
        if(MainActivity.connectionCheck(BookingAppointmentActivity.this)){
            Dialog alertdialog = new Dialog(BookingAppointmentActivity.this);
            alertdialog.setContentView(R.layout.dialog_success);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView messageAlert = alertdialog.findViewById(R.id.msgDialog);
            alertdialog.show();

            // current date picker
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            String dateTime = simpleDateFormat.format(calendar.getTime());

            HashMap<String, String> mydata = new HashMap<String, String>();
            mydata.put("designerId", UID);
            mydata.put("userId", CurrentUID);
            mydata.put("day", dayInput.getText().toString().trim());
            mydata.put("slot", slotInput.getText().toString().trim());
            mydata.put("onBooking", dateTime);
            mydata.put("status", "Pending");
            MainActivity.db.child("Booking").push().setValue(mydata);
            messageAlert.setText("Appointment Booked Successfully!!!");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                    BookingAppointmentActivity.super.onBackPressed();
                }
            },2000);
        }
    }
}