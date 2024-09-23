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
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.ProductModel;
import com.example.urbanharmony.Models.ProjectModel;
import com.example.urbanharmony.Models.StyleModel;
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

public class ProjectsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    static String sortingStatus = "dsc";
    GridView gridView;
    LinearLayout loader, notifyBar, notfoundContainer;
    ImageView sortBtn;
    EditText searchInput;
    TextView searchedWord, totalCount;
    ExtendedFloatingActionButton addBtn;
    StorageReference mStorage;
    StorageTask uploadTask;
    Uri imageUri;
    ArrayList<ProjectModel> datalist = new ArrayList<>();

    //    Dialog Elements
    Dialog projectDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText pNameInput, pDescriptionInput;
    TextInputLayout pNameLayout, pDescriptionLayout;
    TextView title, imageErrorText;
    ImageView projectImage, editImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_projects);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mStorage = FirebaseStorage.getInstance().getReference();

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
        addBtn = findViewById(R.id.addBtn);
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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                projectForm("add","");
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
                ProjectsActivity.super.onBackPressed();
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
        MainActivity.db.child("Projects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            if(ds.child("userId").getValue().toString().trim().equals(UID)){
                                ProjectModel model = new ProjectModel(ds.getKey(),
                                        ds.child("pName").getValue().toString(),
                                        ds.child("pImage").getValue().toString(),
                                        ds.child("pDesc").getValue().toString(),
                                        ds.child("userId").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        } else {
                            if(ds.child("pName").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())&&ds.child("userId").getValue().toString().trim().equals(UID)){
                                ProjectModel model = new ProjectModel(ds.getKey(),
                                        ds.child("pName").getValue().toString(),
                                        ds.child("pImage").getValue().toString(),
                                        ds.child("pDesc").getValue().toString(),
                                        ds.child("userId").getValue().toString()
                                );
                                datalist.add(model);
                            }
                        }
                    }
                    if(datalist.size() > 0){
                        loader.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        if(sortingStatus.equals("dsc")){
                            Collections.reverse(datalist);
                        }
                        MyAdapter adapter = new MyAdapter(ProjectsActivity.this,datalist);
                        gridView.setAdapter(adapter);
                    } else {
                        loader.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    totalCount.setText(datalist.size()+" found");
                } else {
                    loader.setVisibility(View.GONE);
                    gridView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void projectForm(String purpose, String productId){
        projectDialog = new Dialog(ProjectsActivity.this);
        projectDialog.setContentView(R.layout.dialog_add_projects);
        projectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        projectDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        projectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        projectDialog.getWindow().setGravity(Gravity.CENTER);
        projectDialog.setCancelable(false);
        projectDialog.setCanceledOnTouchOutside(false);
        cancelBtn = projectDialog.findViewById(R.id.cancelBtn);
        addDataBtn = projectDialog.findViewById(R.id.addDataBtn);
        title = projectDialog.findViewById(R.id.title);
        projectImage = projectDialog.findViewById(R.id.projectImage);
        editImageBtn = projectDialog.findViewById(R.id.editImageBtn);
        imageErrorText = projectDialog.findViewById(R.id.imageErrorText);
        pNameInput = projectDialog.findViewById(R.id.pNameInput);
        pNameLayout = projectDialog.findViewById(R.id.pNameLayout);
        pDescriptionLayout = projectDialog.findViewById(R.id.pDescriptionLayout);
        pDescriptionInput = projectDialog.findViewById(R.id.pDescriptionInput);

        pNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pNameValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pDescriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pDescValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Dialog dialog = new Dialog(ProjectsActivity.this);
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
                projectDialog.dismiss();
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
            title.setText("Edit Project");
            MainActivity.db.child("Projects").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Glide.with(ProjectsActivity.this).load(snapshot.child("pImage").getValue().toString().trim()).into(projectImage);
                        pNameInput.setText(snapshot.child("pName").getValue().toString().trim());
                        pDescriptionInput.setText(snapshot.child("pDesc").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        projectDialog.show();
    }

    public boolean pNameValidation(){
        String input = pNameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            pNameLayout.setError("Project Name is Required!!!");
            return false;
        } else if(input.length() < 3){
            pNameLayout.setError("Project Name at least 3 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            pNameLayout.setError("Only text allowed!!!");
            return false;
        } else {
            pNameLayout.setError(null);
            return true;
        }
    }
    public boolean pDescValidation(){
        String input = pDescriptionInput.getText().toString().trim();
        String regex = "^[a-zA-Z.,'()\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            pDescriptionLayout.setError("Project Description is Required!!!");
            return false;
        } else if(input.length() < 20){
            pDescriptionLayout.setError("Project Description at least 20 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            pDescriptionLayout.setError("Only text allowed!!!");
            return false;
        } else {
            pDescriptionLayout.setError(null);
            return true;
        }
    }
    public boolean imageValidation(){
        if(imageUri == null){
            imageErrorText.setText("Project Image is Required!!!");
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
            projectImage.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void validation(String imageStatus, String purpose, String productId) {
        boolean imageErr = false, pNameErr = false, pDescErr = false;
        pNameErr = pNameValidation();
        pDescErr = pDescValidation();
        if(imageStatus.equals("true")){
            imageErr = true;
        } else {
            imageErr = imageValidation();
        }
        if((imageErr && pNameErr && pDescErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(ProjectsActivity.this)){
            if(imageUri != null){
                Dialog loading = new Dialog(ProjectsActivity.this);
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
                uploadTask = mStorage.child("ProjectImages/"+System.currentTimeMillis()+"."+getFileExtension(imageUri)).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                loading.dismiss();
                                String photoLink = uri.toString();

                                Dialog alertdialog = new Dialog(ProjectsActivity.this);
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
                                    HashMap<String, String> mydata = new HashMap<String, String>();
                                    mydata.put("pImage", "" + photoLink);
                                    mydata.put("pName", pNameInput.getText().toString().trim());
                                    mydata.put("pDesc", pDescriptionInput.getText().toString().trim());
                                    mydata.put("userId", UID);
                                    MainActivity.db.child("Projects").push().setValue(mydata);
                                    messageAlert.setText("Project Added Successfully!!!");
                                } else if(purpose.equals("edit")){
                                    MainActivity.db.child("Projects").child(productId).child("pImage").setValue(photoLink);
                                    MainActivity.db.child("Projects").child(productId).child("pName").setValue(pNameInput.getText().toString().trim());
                                    MainActivity.db.child("Projects").child(productId).child("pDesc").setValue(pDescriptionInput.getText().toString().trim());
                                    messageAlert.setText("Project Edited Successfully!!!");
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                        projectDialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);

                            }
                        });
                    }
                });
            } else {
                Dialog alertdialog = new Dialog(ProjectsActivity.this);
                alertdialog.setContentView(R.layout.dialog_success);
                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertdialog.getWindow().setGravity(Gravity.CENTER);
                alertdialog.setCancelable(false);
                alertdialog.setCanceledOnTouchOutside(false);
                TextView message = alertdialog.findViewById(R.id.msgDialog);
                message.setText("Project Edited Successfully!!!");
                alertdialog.show();

                if(purpose.equals("edit")){
                    MainActivity.db.child("Projects").child(productId).child("pName").setValue(pNameInput.getText().toString().trim());
                    MainActivity.db.child("Projects").child(productId).child("pDesc").setValue(pDescriptionInput.getText().toString().trim());
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertdialog.dismiss();
                        projectDialog.dismiss();
                        fetchData("");
                    }
                },2000);
            }
        }
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
        ArrayList<ProjectModel> data;

        public MyAdapter(Context context, ArrayList<ProjectModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.projects_custom_listview,null);
            ImageView pImage, editBtn, deleteBtn;
            TextView pName;
            LinearLayout options, item;

            pImage = customListItem.findViewById(R.id.pImage);
            editBtn = customListItem.findViewById(R.id.editBtn);
            deleteBtn = customListItem.findViewById(R.id.deleteBtn);
            pName = customListItem.findViewById(R.id.pName);
            options = customListItem.findViewById(R.id.options);
            item = customListItem.findViewById(R.id.item);

            pName.setText(data.get(i).getpName());
            Glide.with(context).load(data.get(i).getpImage()).into(pImage);
            options.setVisibility(View.VISIBLE);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog actiondialog = new Dialog(context);
                    actiondialog.setContentView(R.layout.dialog_confirm);
                    actiondialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    actiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    actiondialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    actiondialog.getWindow().setGravity(Gravity.CENTER);
                    actiondialog.setCancelable(false);
                    actiondialog.setCanceledOnTouchOutside(false);
                    Button cancelBtn, yesBtn;
                    yesBtn = actiondialog.findViewById(R.id.yesBtn);
                    cancelBtn = actiondialog.findViewById(R.id.cancelBtn);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actiondialog.dismiss();
                        }
                    });
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MainActivity.db.child("Projects").child(data.get(i).getId()).removeValue();
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_success);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            TextView msg = dialog.findViewById(R.id.msgDialog);
                            msg.setText("Deleted Successfully!!!");
                            dialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    actiondialog.dismiss();
                                    fetchData("");
                                }
                            },2000);
                        }
                    });

                    actiondialog.show();
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    projectForm("edit",""+data.get(i).getId());
                }
            });

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProjectDetailActivity.class);
                    intent.putExtra("pid",data.get(i).getId());
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