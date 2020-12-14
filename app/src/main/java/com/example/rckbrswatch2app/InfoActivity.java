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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.pl"));
            try {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/rckbrs/"));
            } catch (Exception e) {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
            } finally {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
            }}
        });
    }
}