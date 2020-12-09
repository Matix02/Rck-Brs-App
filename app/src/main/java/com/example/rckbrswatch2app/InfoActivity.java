package com.example.rckbrswatch2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class InfoActivity extends AppCompatActivity {

    private AppCompatButton facebookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        facebookButton = findViewById(R.id.facebookButton);

        facebookButton.setOnClickListener(v -> {
            try {
                Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/rckbrs/"));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                startActivity(intent);
            }
        });
    }
}