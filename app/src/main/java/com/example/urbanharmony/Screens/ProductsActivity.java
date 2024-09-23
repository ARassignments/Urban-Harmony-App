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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.BrandModel;
import com.example.urbanharmony.Models.CategoryModel;
import com.example.urbanharmony.Models.ProductModel;
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

public class ProductsActivity extends AppCompatActivity {

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
    ArrayList<ProductModel> datalist = new ArrayList<>();

    //    Dialog Elements
    Dialog productDialog;
    Button cancelBtn, addDataBtn;
    TextInputEditText pNameInput, pDescriptionInput, pPriceInput, pDiscountInput, pStockInput;
    TextInputLayout pNameLayout, pDescriptionLayout, pPriceLayout, pDiscountLayout, pStockLayout, pCategoryLayout, pSubcategoryLayout, pBrandLayout, pStyleLayout;
    AutoCompleteTextView pCategoryInput, pSubcategoryInput, pBrandInput, pStyleInput;
    TextView title, imageErrorText;
    ImageView productImage, editImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products);
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
                productForm("add","");
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
                ProductsActivity.super.onBackPressed();
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
        MainActivity.db.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(data.equals("")){
                            ProductModel model = new ProductModel(ds.getKey(),
                                    ds.child("pName").getValue().toString(),
                                    ds.child("pPrice").getValue().toString(),
                                    ds.child("pStock").getValue().toString(),
                                    ds.child("pDiscount").getValue().toString(),
                                    ds.child("pImage").getValue().toString(),
                                    ds.child("pDesc").getValue().toString(),
                                    ds.child("pCategory").getValue().toString(),
                                    ds.child("pSubcategory").getValue().toString(),
                                    ds.child("pBrand").getValue().toString(),
                                    ds.child("pStyle").getValue().toString(),
                                    ds.child("status").getValue().toString()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("pName").getValue().toString().trim().toLowerCase().contains(data.toLowerCase().trim())){
                                ProductModel model = new ProductModel(ds.getKey(),
                                        ds.child("pName").getValue().toString(),
                                        ds.child("pPrice").getValue().toString(),
                                        ds.child("pStock").getValue().toString(),
                                        ds.child("pDiscount").getValue().toString(),
                                        ds.child("pImage").getValue().toString(),
                                        ds.child("pDesc").getValue().toString(),
                                        ds.child("pCategory").getValue().toString(),
                                        ds.child("pSubcategory").getValue().toString(),
                                        ds.child("pBrand").getValue().toString(),
                                        ds.child("pStyle").getValue().toString(),
                                        ds.child("status").getValue().toString()
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
                        MyAdapter adapter = new MyAdapter(ProductsActivity.this,datalist);
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

    public void productForm(String purpose, String productId){
        productDialog = new Dialog(ProductsActivity.this);
        productDialog.setContentView(R.layout.dialog_add_products);
        productDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        productDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        productDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        productDialog.getWindow().setGravity(Gravity.CENTER);
        productDialog.setCancelable(false);
        productDialog.setCanceledOnTouchOutside(false);
        cancelBtn = productDialog.findViewById(R.id.cancelBtn);
        addDataBtn = productDialog.findViewById(R.id.addDataBtn);
        title = productDialog.findViewById(R.id.title);
        productImage = productDialog.findViewById(R.id.productImage);
        editImageBtn = productDialog.findViewById(R.id.editImageBtn);
        imageErrorText = productDialog.findViewById(R.id.imageErrorText);
        pNameInput = productDialog.findViewById(R.id.pNameInput);
        pNameLayout = productDialog.findViewById(R.id.pNameLayout);
        pDescriptionLayout = productDialog.findViewById(R.id.pDescriptionLayout);
        pDescriptionInput = productDialog.findViewById(R.id.pDescriptionInput);
        pPriceLayout = productDialog.findViewById(R.id.pPriceLayout);
        pPriceInput = productDialog.findViewById(R.id.pPriceInput);
        pDiscountLayout = productDialog.findViewById(R.id.pDiscountLayout);
        pDiscountInput = productDialog.findViewById(R.id.pDiscountInput);
        pStockLayout = productDialog.findViewById(R.id.pStockLayout);
        pStockInput = productDialog.findViewById(R.id.pStockInput);
        pCategoryLayout = productDialog.findViewById(R.id.pCategoryLayout);
        pCategoryInput = productDialog.findViewById(R.id.pCategoryInput);
        pSubcategoryLayout = productDialog.findViewById(R.id.pSubcategoryLayout);
        pSubcategoryInput = productDialog.findViewById(R.id.pSubcategoryInput);
        pBrandLayout = productDialog.findViewById(R.id.pBrandLayout);
        pBrandInput = productDialog.findViewById(R.id.pBrandInput);
        pStyleLayout = productDialog.findViewById(R.id.pStyleLayout);
        pStyleInput = productDialog.findViewById(R.id.pStyleInput);

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
        pPriceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pPriceValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pStockInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pStockValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pDiscountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pDiscountValidation();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ArrayList<CategoryModel> categoriesList = new ArrayList<>();
        ArrayList<String> subcategoriesList = new ArrayList<>();
        ArrayList<BrandModel> brandList = new ArrayList<>();
        ArrayList<StyleModel> styleList = new ArrayList<>();
        MainActivity.db.child("Category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CategoryModel model = new CategoryModel(ds.getKey(),
                                ds.child("name").getValue().toString(),
                                ds.child("image").getValue().toString(),
                                ds.child("SubCategory").getValue().toString()
                        );
                        categoriesList.add(model);
                    }

                    // Populate categoryInput with category names
                    ArrayList<String> categoryNames = new ArrayList<>();
                    for (CategoryModel category : categoriesList) {
                        categoryNames.add(category.getName());
                    }
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(ProductsActivity.this, android.R.layout.simple_dropdown_item_1line, categoryNames);
                    pCategoryInput.setAdapter(categoryAdapter);

                    // Add listener to categoryInput to handle subcategory population
                    pCategoryInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Get the selected category
                            String selectedCategory = categoryNames.get(position);

                            // Clear previous subcategories
                            subcategoriesList.clear();
                            pSubcategoryInput.setText("");

                            // Find the selected category model and populate subcategories
                            for (CategoryModel categoryModel : categoriesList) {
                                if (categoryModel.getName().equals(selectedCategory)) {
                                    // Fetch the subcategories from the model
                                    DataSnapshot subcategorySnapshot = snapshot.child(categoryModel.getId()).child("SubCategory");
                                    for (DataSnapshot subcategory : subcategorySnapshot.getChildren()) {
                                        subcategoriesList.add(subcategory.child("name").getValue().toString());
                                    }
                                    break;
                                }
                            }

                            // Populate subcategoryInput with subcategories
                            ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(ProductsActivity.this, android.R.layout.simple_dropdown_item_1line, subcategoriesList);
                            pSubcategoryInput.setAdapter(subcategoryAdapter);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        MainActivity.db.child("Brands").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        BrandModel model = new BrandModel(ds.getKey(),
                                ds.child("name").getValue().toString()
                        );
                        brandList.add(model);
                    }

                    ArrayList<String> brandNames = new ArrayList<>();
                    for (BrandModel brand : brandList) {
                        brandNames.add(brand.getName());
                    }
                    ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(ProductsActivity.this, android.R.layout.simple_dropdown_item_1line, brandNames);
                    pBrandInput.setAdapter(brandAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        MainActivity.db.child("Styles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        StyleModel model = new StyleModel(ds.getKey(),
                                ds.child("name").getValue().toString()
                        );
                        styleList.add(model);
                    }

                    ArrayList<String> styleNames = new ArrayList<>();
                    for (StyleModel style : styleList) {
                        styleNames.add(style.getName());
                    }
                    ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(ProductsActivity.this, android.R.layout.simple_dropdown_item_1line, styleNames);
                    pStyleInput.setAdapter(styleAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Dialog dialog = new Dialog(ProductsActivity.this);
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
                productDialog.dismiss();
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
            title.setText("Edit Product");
            MainActivity.db.child("Products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Glide.with(ProductsActivity.this).load(snapshot.child("pImage").getValue().toString().trim()).into(productImage);
                        pNameInput.setText(snapshot.child("pName").getValue().toString().trim());
                        pDescriptionInput.setText(snapshot.child("pDesc").getValue().toString().trim());
                        pPriceInput.setText(snapshot.child("pPrice").getValue().toString().trim());
                        pStockInput.setText(snapshot.child("pStock").getValue().toString().trim());
                        pDiscountInput.setText(snapshot.child("pDiscount").getValue().toString().trim());
                        pCategoryInput.setText(snapshot.child("pCategory").getValue().toString().trim());
                        pSubcategoryInput.setText(snapshot.child("pSubcategory").getValue().toString().trim());
                        pBrandInput.setText(snapshot.child("pBrand").getValue().toString().trim());
                        pStyleInput.setText(snapshot.child("pStyle").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        productDialog.show();
    }

    public boolean pNameValidation(){
        String input = pNameInput.getText().toString().trim();
        String regex = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            pNameLayout.setError("Product Name is Required!!!");
            return false;
        } else if(input.length() < 3){
            pNameLayout.setError("Product Name at least 3 Characters!!!");
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
            pDescriptionLayout.setError("Product Description is Required!!!");
            return false;
        } else if(input.length() < 20){
            pDescriptionLayout.setError("Product Description at least 20 Characters!!!");
            return false;
        } else if(!matcher.matches()){
            pDescriptionLayout.setError("Only text allowed!!!");
            return false;
        } else {
            pDescriptionLayout.setError(null);
            return true;
        }
    }
    public boolean pPriceValidation(){
        String input = pPriceInput.getText().toString().trim();
        String regex = "^[0-9\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            pPriceLayout.setError("Product Price is Required!!!");
            return false;
        } else if(!matcher.matches()){
            pPriceLayout.setError("Only digits allowed!!!");
            return false;
        } else {
            pPriceLayout.setError(null);
            return true;
        }
    }
    public boolean pStockValidation(){
        String input = pStockInput.getText().toString().trim();
        String regex = "^[0-9\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            pStockLayout.setError("Product Stock is Required!!!");
            return false;
        } else if(!matcher.matches()){
            pStockLayout.setError("Only digits allowed!!!");
            return false;
        } else {
            pStockLayout.setError(null);
            return true;
        }
    }
    public boolean pDiscountValidation(){
        String input = pDiscountInput.getText().toString().trim();
        String regex = "^[0-9\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(input.equals("")){
            pDiscountLayout.setError("Product Discount is Required!!!");
            return false;
        } else if(!matcher.matches()){
            pDiscountLayout.setError("Only digits allowed!!!");
            return false;
        } else {
            pDiscountLayout.setError(null);
            return true;
        }
    }
    public boolean pCategoryValidation(){
        String input = pCategoryInput.getText().toString().trim();
        if(input.equals("")){
            pCategoryLayout.setError("Product Category is Required!!!");
            return false;
        } else {
            pCategoryLayout.setError(null);
            return true;
        }
    }
    public boolean pSubcategoryValidation(){
        String input = pSubcategoryInput.getText().toString().trim();
        if(input.equals("")){
            pSubcategoryLayout.setError("Product Sub Category is Required!!!");
            return false;
        } else {
            pSubcategoryLayout.setError(null);
            return true;
        }
    }
    public boolean pBrandValidation(){
        String input = pBrandInput.getText().toString().trim();
        if(input.equals("")){
            pBrandLayout.setError("Product Brand is Required!!!");
            return false;
        } else {
            pBrandLayout.setError(null);
            return true;
        }
    }
    public boolean pStyleValidation(){
        String input = pStyleInput.getText().toString().trim();
        if(input.equals("")){
            pStyleLayout.setError("Product Style is Required!!!");
            return false;
        } else {
            pStyleLayout.setError(null);
            return true;
        }
    }

    public boolean imageValidation(){
        if(imageUri == null){
            imageErrorText.setText("Product Image is Required!!!");
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
            productImage.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void validation(String imageStatus, String purpose, String productId) {
        boolean imageErr = false, pNameErr = false, pDescErr = false, pPriceErr = false, pStockErr = false, pDiscountErr = false, pCategoryErr = false, pSubcategoryErr = false, pBrandErr = false, pStyleErr = false;
        pNameErr = pNameValidation();
        pDescErr = pDescValidation();
        pPriceErr = pPriceValidation();
        pStockErr = pStockValidation();
        pDiscountErr = pDiscountValidation();
        pCategoryErr = pCategoryValidation();
        pSubcategoryErr = pSubcategoryValidation();
        pBrandErr = pBrandValidation();
        pStyleErr = pStyleValidation();
        if(imageStatus.equals("true")){
            imageErr = true;
        } else {
            imageErr = imageValidation();
        }
        if((imageErr && pNameErr && pDescErr && pPriceErr && pStockErr && pDiscountErr && pCategoryErr && pSubcategoryErr && pBrandErr && pStyleErr) == true){
            persons(purpose, productId);
        }
    }

    private void persons(String purpose, String productId){
        if(MainActivity.connectionCheck(ProductsActivity.this)){
            if(imageUri != null){
                Dialog loading = new Dialog(ProductsActivity.this);
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
                uploadTask = mStorage.child("Images/"+System.currentTimeMillis()+"."+getFileExtension(imageUri)).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                loading.dismiss();
                                String photoLink = uri.toString();

                                Dialog alertdialog = new Dialog(ProductsActivity.this);
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
                                    mydata.put("pStock", pStockInput.getText().toString().trim());
                                    mydata.put("pPrice", pPriceInput.getText().toString().trim());
                                    mydata.put("pDiscount", pDiscountInput.getText().toString().trim());
                                    mydata.put("pCategory", pCategoryInput.getText().toString().trim());
                                    mydata.put("pSubcategory", pSubcategoryInput.getText().toString().trim());
                                    mydata.put("pBrand", pBrandInput.getText().toString().trim());
                                    mydata.put("pStyle", pStyleInput.getText().toString().trim());
                                    mydata.put("status", "1");
                                    MainActivity.db.child("Products").push().setValue(mydata);
                                    messageAlert.setText("Product Added Successfully!!!");
                                } else if(purpose.equals("edit")){
                                    MainActivity.db.child("Products").child(productId).child("pImage").setValue(photoLink);
                                    MainActivity.db.child("Products").child(productId).child("pName").setValue(pNameInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pDesc").setValue(pDescriptionInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pStock").setValue(pStockInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pPrice").setValue(pPriceInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pDiscount").setValue(pDiscountInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pCategory").setValue(pCategoryInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pSubcategory").setValue(pSubcategoryInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pBrand").setValue(pBrandInput.getText().toString().trim());
                                    MainActivity.db.child("Products").child(productId).child("pStyle").setValue(pStyleInput.getText().toString().trim());
                                    messageAlert.setText("Product Edited Successfully!!!");

                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                        productDialog.dismiss();
                                        fetchData("");
                                    }
                                },2000);

                            }
                        });
                    }
                });
            } else {
                Dialog alertdialog = new Dialog(ProductsActivity.this);
                alertdialog.setContentView(R.layout.dialog_success);
                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertdialog.getWindow().setGravity(Gravity.CENTER);
                alertdialog.setCancelable(false);
                alertdialog.setCanceledOnTouchOutside(false);
                TextView message = alertdialog.findViewById(R.id.msgDialog);
                message.setText("Product Edited Successfully!!!");
                alertdialog.show();

                if(purpose.equals("edit")){
                    MainActivity.db.child("Products").child(productId).child("pName").setValue(pNameInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pDesc").setValue(pDescriptionInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pStock").setValue(pStockInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pPrice").setValue(pPriceInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pDiscount").setValue(pDiscountInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pCategory").setValue(pCategoryInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pSubcategory").setValue(pSubcategoryInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pBrand").setValue(pBrandInput.getText().toString().trim());
                    MainActivity.db.child("Products").child(productId).child("pStyle").setValue(pStyleInput.getText().toString().trim());
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertdialog.dismiss();
                        productDialog.dismiss();
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
        ArrayList<ProductModel> data;

        public MyAdapter(Context context, ArrayList<ProductModel> data) {
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
            View customListItem = LayoutInflater.from(context).inflate(R.layout.products_custom_listview,null);
            ImageView pImage, wishlistBtn, editBtn, deleteBtn;
            TextView pDiscount, pName, pRating, pStock, pPrice, pPriceOff;
            LinearLayout options, item;

            pImage = customListItem.findViewById(R.id.pImage);
            wishlistBtn = customListItem.findViewById(R.id.wishlistBtn);
            editBtn = customListItem.findViewById(R.id.editBtn);
            deleteBtn = customListItem.findViewById(R.id.deleteBtn);
            pDiscount = customListItem.findViewById(R.id.pDiscount);
            pName = customListItem.findViewById(R.id.pName);
            pRating = customListItem.findViewById(R.id.pRating);
            pStock = customListItem.findViewById(R.id.pStock);
            pPrice = customListItem.findViewById(R.id.pPrice);
            pPriceOff = customListItem.findViewById(R.id.pPriceOff);
            options = customListItem.findViewById(R.id.options);
            item = customListItem.findViewById(R.id.item);

            if(!data.get(i).getpDiscount().equals("0")){
                pDiscount.setVisibility(View.VISIBLE);
                pDiscount.setText(data.get(i).getpDiscount()+"% OFF");
            } else {
                pPriceOff.setVisibility(View.GONE);
            }
            pName.setText(data.get(i).getpName());
            pStock.setText(data.get(i).getpStock()+" Stock");
            pPriceOff.setText("$"+data.get(i).getpPrice());
            Glide.with(context).load(data.get(i).getpImage()).into(pImage);

            double discount = Double.parseDouble(data.get(i).getpDiscount())/100;
            double calcDiscount = Double.parseDouble(data.get(i).getpPrice()) * discount;
            double totalPrice = Double.parseDouble(data.get(i).getpPrice()) - calcDiscount;
            pPrice.setText("$"+Math.round(totalPrice));

            wishlistBtn.setVisibility(View.GONE);

            MainActivity.db.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String roleCheck = snapshot.child("role").getValue().toString().trim();
                        if(roleCheck.equals("user")){
                            options.setVisibility(View.GONE);
                        } else if(roleCheck.equals("admin")){
                            options.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
                            MainActivity.db.child("Products").child(data.get(i).getId()).removeValue();
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
                    productForm("edit",""+data.get(i).getId());
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