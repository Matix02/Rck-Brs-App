package com.example.rckbrswatch2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.Objects;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        AppCompatButton facebookButton = findViewById(R.id.facebookButton);
        AppCompatButton youTubeButton = findViewById(R.id.youTubeButton);
        AppCompatButton spotifyButton = findViewById(R.id.spotifyButton);
        AppCompatButton linkedinButton = findViewById(R.id.linkedinButton);
        AppCompatButton gitHubButton = findViewById(R.id.gitHubButton);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_back);

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

        youTubeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.pl"));
            try {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/RockAlone2k/videos"));
            } catch (Exception e) {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"));
            } finally {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }}
        });
        spotifyButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.pl"));
            try {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/show/3wMgSXoca4MTuEzBK76tud"));
            } catch (Exception e) {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.spotify.com"));
            } finally {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }}
        });
        linkedinButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.pl"));
            try {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://pl.linkedin.com/"));
            } catch (Exception e) {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://pl.linkedin.com/"));
            } finally {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }}
        });
        gitHubButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.pl"));
            try {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Matix02"));
            } catch (Exception e) {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/"));
            } finally {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }}
        });
    }
}