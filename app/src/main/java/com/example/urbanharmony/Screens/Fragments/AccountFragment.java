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
import com.example.urbanharmony.Screens.AddressActivity;
import com.example.urbanharmony.Screens.BrandsActivity;
import com.example.urbanharmony.Screens.CategoryActivity;
import com.example.urbanharmony.Screens.DashboardActivity;
import com.example.urbanharmony.Screens.LoginActivity;
import com.example.urbanharmony.Screens.MyDesignsActivity;
import com.example.urbanharmony.Screens.OrderActivity;
import com.example.urbanharmony.Screens.PortfolioActivity;
import com.example.urbanharmony.Screens.ProductsActivity;
import com.example.urbanharmony.Screens.ProfileActivity;
import com.example.urbanharmony.Screens.ProjectsActivity;
import com.example.urbanharmony.Screens.SchedulesActivity;
import com.example.urbanharmony.Screens.StylesActivity;
import com.example.urbanharmony.Screens.SubscriptionActivity;
import com.example.urbanharmony.Screens.UsersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    View view;
    Button logoutBtn;
    TextView profileName, userId, categoriesBtn, brandsBtn, stylesBtn, productsBtn, usersBtn, addressBtn, projectsBtn, portfolioBtn, scheduleBtn, myDesignsBtn, subscriptionBtn;
    LinearLayout adminOptions, designerOptions;
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
        designerOptions = view.findViewById(R.id.designerOptions);
        profileName = view.findViewById(R.id.profileName);
        profileImage = view.findViewById(R.id.profileImage);
        userId = view.findViewById(R.id.userId);
        categoriesBtn = view.findViewById(R.id.categoriesBtn);
        brandsBtn = view.findViewById(R.id.brandsBtn);
        stylesBtn = view.findViewById(R.id.stylesBtn);
        productsBtn = view.findViewById(R.id.productsBtn);
        usersBtn = view.findViewById(R.id.usersBtn);
        addressBtn = view.findViewById(R.id.addressBtn);
        projectsBtn = view.findViewById(R.id.projectsBtn);
        portfolioBtn = view.findViewById(R.id.portfolioBtn);
        scheduleBtn = view.findViewById(R.id.scheduleBtn);
        myDesignsBtn = view.findViewById(R.id.myDesignsBtn);
        subscriptionBtn = view.findViewById(R.id.subscriptionBtn);

        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
            userId.setText(UID);
            MainActivity.db.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        profileName.setText(snapshot.child("name").getValue().toString());
                        if (!snapshot.child("image").getValue().toString().equals("")) {
                            profileImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                        }
                        if (snapshot.child("role").getValue().toString().equals("admin")) {
                            adminOptions.setVisibility(View.VISIBLE);
                        } else if (snapshot.child("role").getValue().toString().equals("designer")) {
                            designerOptions.setVisibility(View.VISIBLE);
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

        view.findViewById(R.id.wishlistBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getContext()).openWishlistPage();
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

        usersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UsersActivity.class));
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddressActivity.class));
            }
        });

        projectsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProjectsActivity.class));
            }
        });

        portfolioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PortfolioActivity.class));
            }
        });

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SchedulesActivity.class));
            }
        });

        myDesignsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyDesignsActivity.class));
            }
        });

        subscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SubscriptionActivity.class));
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