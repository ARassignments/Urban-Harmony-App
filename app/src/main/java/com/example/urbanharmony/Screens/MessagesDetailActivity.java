package com.example.urbanharmony.Screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
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

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.ChatModel;
import com.example.urbanharmony.R;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesDetailActivity extends AppCompatActivity {
    static String userId = "";
    static String userName = "";
    static String userContact = "";
    TextView titleText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    ListView listView;
    EditText chatInput;
    ImageView callBtn;
    LinearLayout sendBtn, notfoundContainer;
    ArrayList<ChatModel> datalist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = findViewById(R.id.listView);
        chatInput = findViewById(R.id.chatInput);
        sendBtn = findViewById(R.id.sendBtn);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        titleText = findViewById(R.id.titleText);
        callBtn = findViewById(R.id.callBtn);

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            userId = extra.getString("userId");
            userName = extra.getString("userName");
            userContact = extra.getString("userContact");
        }

        titleText.setText(userName+"'s Chat");

        chatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                chatValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+userContact));
                startActivity(intent);
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesDetailActivity.super.onBackPressed();
            }
        });

        fetchData();
    }

    public void fetchData(){
        MainActivity.db.child("Chats").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        ChatModel model = new ChatModel(ds.getKey(),
                                ds.child("chat").getValue().toString(),
                                ds.child("senderId").getValue().toString(),
                                ds.child("date").getValue().toString(),
                                ds.child("time").getValue().toString()
                        );
                        datalist.add(model);
                    }
                    if(datalist.size() > 0){
                        Collections.sort(datalist, new Comparator<ChatModel>() {
                            @Override
                            public int compare(ChatModel m1, ChatModel m2) {
                                // Combine date and time for accurate comparison
                                String dateTime1 = m1.getDate() + " " + m1.getTime();
                                String dateTime2 = m2.getDate() + " " + m2.getTime();
                                return dateTime1.compareTo(dateTime2);
                            }
                        });

                        listView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        MyAdapter adapter = new MyAdapter(MessagesDetailActivity.this,datalist);
                        listView.setAdapter(adapter);
                    } else {
                        listView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    listView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean chatValidation(){
        String input = chatInput.getText().toString().trim();
        String regex = "^[0-9:MPA\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
//            chatInput.setError("From Slot is Required!!!");
            return false;
        } else {
//            chatInput.setError(null);
            return true;
        }
    }

    private void validation() {
        boolean chatErr = false;
        chatErr = chatValidation();

        if((chatErr) == true){
            if(MainActivity.connectionCheck(MessagesDetailActivity.this)){
                // Create a SimpleDateFormat object with the desired format.
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault());
                String currentDate = sdf.format(new Date());
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                HashMap<String, String> mydata = new HashMap<String, String>();
                mydata.put("chat", chatInput.getText().toString().trim());
                mydata.put("senderId", UID);
                mydata.put("date", currentDate);
                mydata.put("time", currentTime);
                MainActivity.db.child("Chats").child(userId).push().setValue(mydata);
                chatInput.setText(null);
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<ChatModel> data;

        public MyAdapter(Context context, ArrayList<ChatModel> data) {
            this.context = context;
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View customListItem = LayoutInflater.from(context).inflate(R.layout.customer_service_chat_custom_listview,null);
            TextView incomingChat, incomingChatDate, outgoingChat, outgoingChatDate;
            Chip dayName;
            LinearLayout incomingItem, outgoingItem;
            CircleImageView image;

            incomingChat = customListItem.findViewById(R.id.incomingChat);
            incomingChatDate = customListItem.findViewById(R.id.incomingChatDate);
            outgoingChat = customListItem.findViewById(R.id.outgoingChat);
            outgoingChatDate = customListItem.findViewById(R.id.outgoingChatDate);
            dayName = customListItem.findViewById(R.id.dayName);
            incomingItem = customListItem.findViewById(R.id.incomingItem);
            outgoingItem = customListItem.findViewById(R.id.outgoingItem);
            image = customListItem.findViewById(R.id.image);

            if(data.get(i).getSenderId().equals(UID)){
                outgoingItem.setVisibility(View.VISIBLE);
                incomingItem.setVisibility(View.GONE);
            } else {
                outgoingItem.setVisibility(View.GONE);
                incomingItem.setVisibility(View.VISIBLE);
            }

            incomingChat.setText(data.get(i).getChat());
            incomingChatDate.setText(data.get(i).getTime());
            outgoingChat.setText(data.get(i).getChat());
            outgoingChatDate.setText(data.get(i).getTime());

            MainActivity.db.child("Users").child(data.get(i).getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if(!snapshot.child("image").getValue().toString().equals("")){
                            Glide.with(context).load(snapshot.child("image").getValue().toString()).into(image);
//                            image.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Display day name if it's the first message of a new day
            if (i == 0 || isNewDay(i)) {
                dayName.setVisibility(View.VISIBLE);
                dayName.setText(getFormattedDate(data.get(i).getDate()));
            } else {
                dayName.setVisibility(View.GONE);
            }

            if(i==data.size()-1){
                customListItem.setPadding(customListItem.getPaddingLeft(), customListItem.getPaddingTop(),customListItem.getPaddingRight(), 30);
            }

            customListItem.setAlpha(0f);
            customListItem.animate().alpha(1f).setDuration(300).setStartDelay(i * 2).start();
            return customListItem;
        }

        // Check if the current message is on a new day compared to the previous message
        private boolean isNewDay(int position) {
            if (position == 0) return true;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault());

            try {
                Date currentDate = dateFormat.parse(data.get(position).getDate());
                Date previousDate = dateFormat.parse(data.get(position - 1).getDate());

                Calendar currentCal = Calendar.getInstance();
                currentCal.setTime(currentDate);

                Calendar previousCal = Calendar.getInstance();
                previousCal.setTime(previousDate);

                return currentCal.get(Calendar.YEAR) != previousCal.get(Calendar.YEAR) ||
                        currentCal.get(Calendar.DAY_OF_YEAR) != previousCal.get(Calendar.DAY_OF_YEAR);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // Format the date for the day name (e.g., "Today," "Yesterday," or day name)
        private String getFormattedDate(String dateStr) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());

            try {
                Date date = dateFormat.parse(dateStr);
                Calendar messageCal = Calendar.getInstance();
                messageCal.setTime(date);

                Calendar todayCal = Calendar.getInstance();

                if (messageCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)) {
                    int messageDayOfYear = messageCal.get(Calendar.DAY_OF_YEAR);
                    int todayDayOfYear = todayCal.get(Calendar.DAY_OF_YEAR);

                    if (messageDayOfYear == todayDayOfYear) {
                        return "Today";
                    } else if (messageDayOfYear == todayDayOfYear - 1) {
                        return "Yesterday";
                    }
                }

                return dayFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}