package com.example.rckbrswatch2app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

public class FiltersDialogActivity extends AppCompatActivity{

    private String unSelectedAllShareText = "Zaznacz Wszystko";
    private String selectedAllShareText = "Odznacz Wszystko";

    private SwitchCompat finishSwitch;
    private SwitchCompat unFinishSwitch;
    private AppCompatCheckBox filmsChBox;
    private AppCompatCheckBox seriesChBox;
    private AppCompatCheckBox booksChBox;
    private AppCompatCheckBox gamesChBox;
    private SwitchCompat rckSwitch;
    private SwitchCompat brsSwitch;
    private SwitchCompat rcknBrsSwitch;
    private SwitchCompat othersSwitch;
    private AppCompatButton categorySelAllButton;
    private AppCompatButton shareSelAllButton;
    private AppCompatButton saveButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);
        ElementViewModel viewModel = new ViewModelProvider(this).get(ElementViewModel.class);
        finishSwitch = findViewById(R.id.ukonczoneSW);
        unFinishSwitch = findViewById(R.id.nieUkonczoneSW);
        filmsChBox = findViewById(R.id.filmyCHB);
        seriesChBox = findViewById(R.id.serialeCHB);
        booksChBox = findViewById(R.id.ksiazkiCHB);
        gamesChBox = findViewById(R.id.gryCHB);
        rckSwitch = findViewById(R.id.RckSWx);
        brsSwitch = findViewById(R.id.BrsSWx);
        rcknBrsSwitch = findViewById(R.id.RckBrsSWx);
        othersSwitch = findViewById(R.id.InneSWx);
        categorySelAllButton = findViewById(R.id.zaznaczWszystkeKategorieBT);
        shareSelAllButton = findViewById(R.id.zaznaczWszystkoPolecaneBT);
        saveButton = findViewById(R.id.saveBTx);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Filters");

        Intent intent = getIntent();
        String localUserID = intent.getStringExtra("userID");
        Log.d("UserID", "is " + localUserID + " in Filter");

        viewModel.getUserFilters(localUserID).observe(this, filter1 -> {
            finishSwitch.setChecked(filter1.isFinished());
            unFinishSwitch.setChecked(filter1.isUnfinished());
            filmsChBox.setChecked(filter1.isFilm());
            seriesChBox.setChecked(filter1.isSeries());
            booksChBox.setChecked(filter1.isBook());
            gamesChBox.setChecked(filter1.isGame());
            rckSwitch.setChecked(filter1.isShareRck());
            brsSwitch.setChecked(filter1.isShareBrs());
            rcknBrsSwitch.setChecked(filter1.isShareRckBrs());
            othersSwitch.setChecked(filter1.isShareOther());
        });

        shareSelAllButton.setOnClickListener(click -> {
           /* if(rckSwitch.isChecked() && brsSwitch.isChecked() && rcknBrsSwitch.isChecked() && othersSwitch.isChecked()) {
                shareSelAllButton.setText(selectedAllShareText);
            } else {
                resetSharesControls();
            }*/
           resetSharesControls();
        });

        categorySelAllButton.setOnClickListener(click -> {
            resetCategoryControls();
        });


        saveButton.setOnClickListener(click -> {
            Filter currentFilters = new Filter();
            currentFilters.setFinished(finishSwitch.isChecked());
            currentFilters.setUnfinished(unFinishSwitch.isChecked());
            currentFilters.setFilm(filmsChBox.isChecked());
            currentFilters.setSeries(seriesChBox.isChecked());
            currentFilters.setBook(booksChBox.isChecked());
            currentFilters.setGame(gamesChBox.isChecked());
            currentFilters.setShareRck(rckSwitch.isChecked());
            currentFilters.setShareBrs(brsSwitch.isChecked());
            currentFilters.setShareRckBrs(rcknBrsSwitch.isChecked());
            currentFilters.setShareOther(othersSwitch.isChecked());

            viewModel.setFilters(localUserID, currentFilters);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reset_filter) {//saveFilterResult();
            resetlAllFilter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void resetlAllFilter(){
        finishSwitch.setChecked(true);
        unFinishSwitch.setChecked(true);
        resetCategoryControls();
        resetSharesControls();
    }

    public void resetSharesControls(){
        rckSwitch.setChecked(true);
        brsSwitch.setChecked(true);
        rcknBrsSwitch.setChecked(true);
        othersSwitch.setChecked(true);
     //   shareSelAllButton.setText(unSelectedAllShareText);
    }

    public void resetCategoryControls(){
        filmsChBox.setChecked(true);
        seriesChBox.setChecked(true);
        booksChBox.setChecked(true);
        gamesChBox.setChecked(true);
     //   categorySelAllButton.setText(unSelectedAllShareText);
    }
}
