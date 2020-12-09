package com.example.rckbrswatch2app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;

import java.util.ArrayList;

public class FilterPopUpActivity extends AppCompatActivity {

    //Załatwić te całe SuppressLint - to jest rzutowanie z Switcha na Switcha z androidx
    String zazWszt = "Zaznacz Wszystko";
    String odzWszt = "Odznacz Wszystko";
    Switch finishSwitch;
    Switch unFinishSwitch;
    CheckBox filmsCheckBox;
    CheckBox gamesCheckBox;
    CheckBox booksCheckBox;
    CheckBox seriesCheckBox;
    CheckBox allCheckBox;
    Switch rockSwitch;
    Switch borysSwitch;
    Switch rckAndBorysSwitch;
    Switch otherSwitch;
    Switch allSwitch;
    Button saveButton;
    Button defaultButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_pop);

        Window window = this.getWindow();


        finishSwitch = findViewById(R.id.finishSW);
        unFinishSwitch = findViewById(R.id.unfinishSW);
        filmsCheckBox = findViewById(R.id.filmsCB);
        gamesCheckBox = findViewById(R.id.gamesCB);
        booksCheckBox = findViewById(R.id.booksCB);
        seriesCheckBox = findViewById(R.id.seriesCB);
        allCheckBox = findViewById(R.id.allCB);
        rockSwitch = findViewById(R.id.rockSW);
        borysSwitch = findViewById(R.id.borysSW);
        rckAndBorysSwitch = findViewById(R.id.rockBorysSW);
        otherSwitch = findViewById(R.id.otherSW);
        allSwitch = findViewById(R.id.allSW);
        saveButton = findViewById(R.id.saveFilterButton);
        defaultButton = findViewById(R.id.defaultFilterButton);

        /* Część graficzna */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;


        window.setLayout((int) (width * .8), (int) (height * .9));

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        /* Część Przypisywania Wartości z Bazy */

    }
}