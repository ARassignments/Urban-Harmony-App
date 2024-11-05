package com.example.urbanharmony.Screens;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.Adapter.DesignerAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.ProductModel;
import com.example.urbanharmony.Models.StyleModel;
import com.example.urbanharmony.Models.UsersModel;
import com.example.urbanharmony.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesignerActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    GridView gridView;
    LinearLayout loader, notifyBar, notfoundContainer;
    ImageView sortBtn;
    EditText searchInput;
    TextView searchedWord, totalCount;
    ArrayList<UsersModel> datalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_designer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        gridView = findViewById(R.id.gridView);
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
                DesignerActivity.super.onBackPressed();
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

    public void fetchData(String data){
        MainActivity.db.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    datalist.clear();
                    final int[] pendingCalls = {0};  // Track pending asynchronous calls

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("role").getValue().toString().equals("designer")) {
                            if(data.equals("")){
                                pendingCalls[0]++;  // Increment for each designer found
                                MainActivity.db.child("Portfolio").child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                        if (dsnapshot.exists() && dsnapshot.child("portfolioStatus").getValue().toString().equals("1")) {
                                            UsersModel model = new UsersModel(
                                                    ds.getKey(),
                                                    ds.child("name").getValue().toString(),
                                                    ds.child("email").getValue().toString(),
                                                    ds.child("pwd").getValue().toString(),
                                                    ds.child("image").getValue().toString(),
                                                    ds.child("role").getValue().toString(),
                                                    ds.child("address").getValue().toString(),
                                                    ds.child("shipping").getValue().toString(),
                                                    ds.child("created_on").getValue().toString(),
                                                    ds.child("status").getValue().toString(),
                                                    ds.child("contact").getValue().toString()
                                            );
                                            datalist.add(model);
                                        }

                                        // Decrement the pending calls counter
                                        pendingCalls[0]--;
                                        // If all calls are done, update the adapter
                                        if (pendingCalls[0] == 0) {
                                            updateAdapter();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pendingCalls[0]--;
                                        if (pendingCalls[0] == 0) {
                                            updateAdapter();
                                        }
                                    }
                                });
                            } else {
                                if(ds.child("name").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                    pendingCalls[0]++;  // Increment for each designer found
                                    MainActivity.db.child("Portfolio").child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                            if (dsnapshot.exists() && dsnapshot.child("portfolioStatus").getValue().toString().equals("1")) {
                                                UsersModel model = new UsersModel(
                                                        ds.getKey(),
                                                        ds.child("name").getValue().toString(),
                                                        ds.child("email").getValue().toString(),
                                                        ds.child("pwd").getValue().toString(),
                                                        ds.child("image").getValue().toString(),
                                                        ds.child("role").getValue().toString(),
                                                        ds.child("address").getValue().toString(),
                                                        ds.child("shipping").getValue().toString(),
                                                        ds.child("created_on").getValue().toString(),
                                                        ds.child("status").getValue().toString(),
                                                        ds.child("contact").getValue().toString()
                                                );
                                                datalist.add(model);
                                            }

                                            // Decrement the pending calls counter
                                            pendingCalls[0]--;
                                            // If all calls are done, update the adapter
                                            if (pendingCalls[0] == 0) {
                                                updateAdapter();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            pendingCalls[0]--;
                                            if (pendingCalls[0] == 0) {
                                                updateAdapter();
                                            }
                                        }
                                    });
                                }
                            }

                        }
                    }

                    // In case no designer was found (so no pending calls), update the adapter
                    if (pendingCalls[0] == 0) {
                        updateAdapter();
                    }
                } else {
                    loader.setVisibility(View.GONE);
                    gridView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // Helper function to update adapter
            private void updateAdapter() {
                if (datalist.size() > 0) {
                    loader.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                    notfoundContainer.setVisibility(View.GONE);
                    if(sortingStatus.equals("dsc")){
                        Collections.reverse(datalist);
                    }
                    MyAdapter adapter = new MyAdapter(DesignerActivity.this, datalist);
                    gridView.setAdapter(adapter);
                } else {
                    loader.setVisibility(View.GONE);
                    gridView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }
        });
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.designers_custom_listview,null);
            ImageView image;
            TextView name, designsCount;
            LinearLayout item;

            image = customListItem.findViewById(R.id.image);
            name = customListItem.findViewById(R.id.name);
            designsCount = customListItem.findViewById(R.id.designsCount);
            item = customListItem.findViewById(R.id.item);

            name.setText(data.get(i).getName());
            if(!data.get(i).getImage().equals("")){
                Glide.with(context).load(data.get(i).getImage()).into(image);
//                image.setImageResource(Integer.parseInt(data.get(i).getImage()));
            }

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DesignerActivity.this, DesignerDetailActivity.class);
                    intent.putExtra("userId",data.get(i).getId());
                    startActivity(intent);
                }
            });

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