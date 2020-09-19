package com.example.rckbrswatch2app;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switchCompat;
    AppCompatButton compatButton;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchCompat = findViewById(R.id.switchGame);
        compatButton = findViewById(R.id.submitButton);

        sharedPreferences = getSharedPreferences("SP_Test", MODE_PRIVATE);

        switchCompat.setChecked(sharedPreferences.getBoolean("GameList", false));

        compatButton.setOnClickListener(view -> {
            boolean isGame = switchCompat.isChecked();

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("GameList", isGame);

            editor.apply();

            finish();
        });
    }
}