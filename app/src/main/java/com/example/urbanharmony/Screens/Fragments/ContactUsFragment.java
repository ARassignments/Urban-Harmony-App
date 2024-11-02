package com.example.urbanharmony.Screens.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.CustomerServiceActivity;
import com.example.urbanharmony.Screens.DashboardActivity;
import com.example.urbanharmony.Screens.DesignerDetailActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ContactUsFragment extends Fragment {
    View view;
    ShimmerFrameLayout mapLoaderContainer;
    WebView mapView;
    CardView mapViewContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        mapLoaderContainer = view.findViewById(R.id.mapLoaderContainer);
        mapView = view.findViewById(R.id.mapView);
        mapViewContainer = view.findViewById(R.id.mapViewContainer);

        WebSettings webSettings = mapView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mapView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mapLoaderContainer.setVisibility(View.GONE);
                mapViewContainer.setVisibility(View.VISIBLE);
                mapViewContainer.setAlpha(0f);
                mapViewContainer.animate().alpha(1f).setDuration(500).start();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        });

        // Load the Google Maps URL
        String latitude = "40.7128";
        String longitude = "-74.0060";
        String zoom = "15";
        String mapsUrl = "https://www.google.com/maps/@?api=1&map_action=map&center=" + latitude + "," + longitude + "&zoom=" + zoom;
        String iframeHtml = "<html><body>" +
                "<style>" +
                "*{box-sizing: border-box;padding:0;margin:0;}" +
                "</style>" +
                "<iframe width=\"100%\" height=\"100%\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\" " +
                "src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3619.3141351278095!2d67.1492499742525!3d24.887264277911743!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3eb339999415e0c3%3A0x36742eee0fd9c291!2sAptech%20Metro%20Star%20Gate!5e0!3m2!1sen!2smy!4v1730368351178!5m2!1sen!2smy\""+
                "</iframe>" +
                "</body></html>";
        mapView.loadData(iframeHtml, "text/html", "UTF-8");

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

        if(DashboardActivity.getRole().equals("admin")){
            view.findViewById(R.id.customerServiceBtn).setVisibility(View.GONE);
        }
        return view;
    }
}