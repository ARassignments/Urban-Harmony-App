package com.example.urbanharmony.Screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.UsersModel;
import com.example.urbanharmony.R;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    ListView listView;
    LinearLayout loader, notifyBar, notfoundContainer;
    ImageView sortBtn;
    EditText searchInput;
    TextView searchedWord, totalCount;
    ArrayList<UsersModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        listView = findViewById(R.id.listView);
        notifyBar = findViewById(R.id.notifyBar);
        notfoundContainer = findViewById(R.id.notfoundContainer);
        loader = findViewById(R.id.loader);
        searchedWord = findViewById(R.id.searchedWord);
        totalCount = findViewById(R.id.totalCount);
        searchInput = findViewById(R.id.searchInput);
        sortBtn = findViewById(R.id.sortBtn);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchedWord.setText(searchInput.getText().toString().trim());
                searchValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fetchData("");

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesActivity.super.onBackPressed();
            }
        });
    }

    public void sorting(){
        if(sortingStatus.equals("asc")){
            sortingStatus = "dsc";
            sortBtn.setImageResource(R.drawable.deasscending_order);
        } else if(sortingStatus.equals("dsc")){
            sortingStatus = "asc";
            sortBtn.setImageResource(R.drawable.asscending_order);
        }
        fetchData("");
    }

    public void fetchData(String data) {
        MainActivity.db.child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    datalist.clear();
                    int totalChats = (int) snapshot.getChildrenCount();
                    AtomicInteger processedChats = new AtomicInteger(0); // To track completion of inner calls

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        MainActivity.db.child("Users").child(ds.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot usnapshot) {
                                        if (usnapshot.exists()) {
                                            if (data.equals("")) {
                                                UsersModel model = new UsersModel(
                                                        ds.getKey(),
                                                        usnapshot.child("name").getValue(String.class),
                                                        usnapshot.child("email").getValue(String.class),
                                                        usnapshot.child("pwd").getValue(String.class),
                                                        usnapshot.child("image").getValue(String.class),
                                                        usnapshot.child("role").getValue(String.class),
                                                        usnapshot.child("address").getValue(String.class),
                                                        usnapshot.child("shipping").getValue(String.class),
                                                        usnapshot.child("created_on").getValue(String.class),
                                                        usnapshot.child("status").getValue(String.class)
                                                );
                                                datalist.add(model);
                                            } else if(usnapshot.child("name").getValue().toString().toLowerCase().contains(data.toLowerCase())){
                                                UsersModel model = new UsersModel(
                                                        ds.getKey(),
                                                        usnapshot.child("name").getValue(String.class),
                                                        usnapshot.child("email").getValue(String.class),
                                                        usnapshot.child("pwd").getValue(String.class),
                                                        usnapshot.child("image").getValue(String.class),
                                                        usnapshot.child("role").getValue(String.class),
                                                        usnapshot.child("address").getValue(String.class),
                                                        usnapshot.child("shipping").getValue(String.class),
                                                        usnapshot.child("created_on").getValue(String.class),
                                                        usnapshot.child("status").getValue(String.class)
                                                );
                                                datalist.add(model);
                                            }

                                            // Increment the count of processed chats
                                            if (processedChats.incrementAndGet() == totalChats) {
                                                // Only update the UI once all items are fetched
                                                updateUI();
                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle database error if necessary
                                    }
                                });
                    }
                } else {
                    handleNoDataFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if necessary
            }
        });
    }

    // Helper method to update UI once data is fully loaded
    private void updateUI() {
        if (datalist.size() > 0) {
            loader.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            notfoundContainer.setVisibility(View.GONE);

            if (sortingStatus.equals("dsc")) {
                Collections.reverse(datalist);
            }

            MyAdapter adapter = new MyAdapter(MessagesActivity.this, datalist);
            listView.setAdapter(adapter);
        } else {
            handleNoDataFound();
        }

        totalCount.setText(datalist.size() + " found");
    }

    // Helper method to handle when no data is found
    private void handleNoDataFound() {
        loader.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        notfoundContainer.setVisibility(View.VISIBLE);
        totalCount.setText("0 found");
    }


    public void searchValidation(){
        String input = searchInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(!matcher.matches()){
            searchInput.setError("Only text allowed!!!");
        } else {
            searchInput.setError(null);
            if(input.isEmpty()){
                notifyBar.setVisibility(View.GONE);
                fetchData("");
            } else {
                notifyBar.setVisibility(View.VISIBLE);
                fetchData(searchInput.getText().toString().trim());
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<UsersModel> data;

        public MyAdapter(Context context, ArrayList<UsersModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.users_custom_listview,null);
            LinearLayout listItem;
            TextView sno, name, status;
            CircleImageView image;

            listItem = customListItem.findViewById(R.id.listItem);
            sno = customListItem.findViewById(R.id.sno);
            name = customListItem.findViewById(R.id.name);
            status = customListItem.findViewById(R.id.status);
            image = customListItem.findViewById(R.id.image);

            sno.setText(""+(i+1));
            name.setText(data.get(i).getName());
            status.setVisibility(View.GONE);

            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,MessagesDetailActivity.class);
                    intent.putExtra("userId",data.get(i).getId());
                    intent.putExtra("userName",data.get(i).getName());
                    startActivity(intent);
                }
            });

            if(!data.get(i).getImage().equals("")){
                image.setImageResource(Integer.parseInt(data.get(i).getImage()));
            }

            if(i==data.size()-1){
                customListItem.setPadding(customListItem.getPaddingLeft(), customListItem.getPaddingTop(),customListItem.getPaddingRight(), 30);
            }
            if(i==0){
                customListItem.setPadding(customListItem.getPaddingLeft(), 0,customListItem.getPaddingRight(), 0);
            }
            customListItem.setAlpha(0f);
            customListItem.animate().alpha(1f).setDuration(200).setStartDelay(i * 2).start();
            return customListItem;
        }
    }
}