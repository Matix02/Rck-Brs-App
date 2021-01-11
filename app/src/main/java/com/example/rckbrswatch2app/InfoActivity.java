package com.example.rckbrswatch2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.Objects;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        AppCompatImageButton facebookButton = findViewById(R.id.facebookButton);
        AppCompatImageButton youTubeButton = findViewById(R.id.youTubeButton);
        AppCompatImageButton spotifyButton = findViewById(R.id.spotifyButton);
        AppCompatImageButton linkedinButton = findViewById(R.id.linkedinButton);
        AppCompatImageButton gitHubButton = findViewById(R.id.githubButton);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_back);

        facebookButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.pl"));
            try {
                //intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/rckbrs/"));
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/AndroidOfficial"));
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
               // intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/RockAlone2k/videos"));
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/c/android/videos"));
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
               // intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/show/3wMgSXoca4MTuEzBK76tud"));
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com"));

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