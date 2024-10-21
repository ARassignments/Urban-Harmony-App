package com.example.urbanharmony.Screens.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.CustomerServiceActivity;
import com.example.urbanharmony.Screens.DesignerDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ContactUsFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        view.findViewById(R.id.whatsappBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "923410292698";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));

                try {
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Dialog alertdialog = new Dialog(getContext());
                    alertdialog.setContentView(R.layout.dialog_error);
                    alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    alertdialog.getWindow().setGravity(Gravity.CENTER);
                    alertdialog.setCancelable(false);
                    alertdialog.setCanceledOnTouchOutside(false);
                    TextView message = alertdialog.findViewById(R.id.msgDialog);
                    message.setText("WhatsApp is not installed!!!");
                    alertdialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertdialog.dismiss();
                        }
                    },2000);
                }
            }
        });

        view.findViewById(R.id.websiteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://myfolio-web.netlify.app/"));

                try {
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Dialog alertdialog = new Dialog(getContext());
                    alertdialog.setContentView(R.layout.dialog_error);
                    alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    alertdialog.getWindow().setGravity(Gravity.CENTER);
                    alertdialog.setCancelable(false);
                    alertdialog.setCanceledOnTouchOutside(false);
                    TextView message = alertdialog.findViewById(R.id.msgDialog);
                    message.setText("No browser available to open the website!!!");
                    alertdialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertdialog.dismiss();
                        }
                    },2000);
                }
            }
        });

        view.findViewById(R.id.facebookBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookPageID = "AptechMetro";
                String facebookUrl = "fb://page/" + facebookPageID;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(facebookUrl));

                // Check if Facebook app is installed
                PackageManager packageManager = requireContext().getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                } else {
                    String webUrl = "https://www.facebook.com/" + facebookPageID;
                    intent.setData(Uri.parse(webUrl));
                    startActivity(intent);
                }
            }
        });

        view.findViewById(R.id.youtubeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeChannelUrl = "https://www.youtube.com/channel/UChNkU_ivf4i5ePSx_ES0IqQ";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(youtubeChannelUrl));

                // Check if YouTube app is installed
                PackageManager packageManager = requireContext().getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                } else {
                    startActivity(intent);
                }
            }
        });

        view.findViewById(R.id.instagramBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String instagramUrl = "http://instagram.com/_u/arassignments";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));
                intent.setPackage("com.instagram.android");

                PackageManager packageManager = requireContext().getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                } else {
                    String webUrl = "https://www.instagram.com/arassignments";
                    intent.setData(Uri.parse(webUrl));
                    startActivity(intent);
                }
            }
        });

        view.findViewById(R.id.customerServiceBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CustomerServiceActivity.class));
            }
        });
        return view;
    }
}