package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.BrandsActivity;
import com.example.urbanharmony.Screens.CategoryActivity;
import com.example.urbanharmony.Screens.LoginActivity;
import com.example.urbanharmony.Screens.OrderActivity;
import com.example.urbanharmony.Screens.ProductsActivity;
import com.example.urbanharmony.Screens.ProfileActivity;
import com.example.urbanharmony.Screens.StylesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    View view;
    Button logoutBtn;
    TextView profileName, userId, categoriesBtn, brandsBtn, stylesBtn, productsBtn;
    LinearLayout adminOptions;
    static String UID = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CircleImageView profileImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);

        logoutBtn = view.findViewById(R.id.logoutBtn);
        adminOptions = view.findViewById(R.id.adminOptions);
        profileName = view.findViewById(R.id.profileName);
        profileImage = view.findViewById(R.id.profileImage);
        userId = view.findViewById(R.id.userId);
        categoriesBtn = view.findViewById(R.id.categoriesBtn);
        brandsBtn = view.findViewById(R.id.brandsBtn);
        stylesBtn = view.findViewById(R.id.stylesBtn);
        productsBtn = view.findViewById(R.id.productsBtn);

        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
            userId.setText(UID);
            MainActivity.db.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        profileName.setText(snapshot.child("name").getValue().toString());
                        if(!snapshot.child("image").getValue().toString().equals("")){
                            profileImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                        }
                        if(snapshot.child("role").getValue().toString().equals("admin")){
                            adminOptions.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        view.findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileActivity.class));
            }
        });

        view.findViewById(R.id.ordersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OrderActivity.class));
            }
        });

        categoriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CategoryActivity.class));
            }
        });

        brandsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), BrandsActivity.class));
            }
        });

        stylesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), StylesActivity.class));
            }
        });

        productsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductsActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_bottom_logout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                Button cancelBtn, yesBtn;
                cancelBtn = dialog.findViewById(R.id.cancelBtn);
                yesBtn = dialog.findViewById(R.id.yesBtn);
                dialog.show();

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        editor.clear();
                        editor.commit();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return view;
    }
}