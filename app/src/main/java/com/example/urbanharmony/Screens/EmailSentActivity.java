package com.example.urbanharmony.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.urbanharmony.R;

public class EmailSentActivity extends AppCompatActivity {
    String userEmail = "";
    TextView emailMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_sent);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        emailMsg = findViewById(R.id.emailMsg);

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            userEmail = extra.getString("userEmail");
        }

        String[] splitEmail = userEmail.split("@");
        String htmlContent = "<p>We sent an email to <b>"+userEmail.charAt(0)+userEmail.charAt(1)+"*****@"+splitEmail[1]+"</b> with a link to get back into your account.</p>";
        emailMsg.setText(Html.fromHtml(htmlContent));
        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailSentActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}