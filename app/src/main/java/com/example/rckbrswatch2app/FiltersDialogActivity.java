package com.example.rckbrswatch2app;

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

    FiltersAdapter filtersAdapter;
    //Z Zew. menu
    AppCompatButton resetButton;
    //ViewModel

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

        viewModel.getUserFilters().observe(this, filter1 -> {
            Log.d("FilterTestOmg", "" + filter1.isFinished);
        });

        shareSelAllButton.setOnClickListener(click -> {

            if(rckSwitch.isChecked() && brsSwitch.isChecked() && rcknBrsSwitch.isChecked() && othersSwitch.isChecked()) {

                shareSelAllButton.setText(selectedAllShareText);

            } else {
                resetSharesControls();
            }
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

            viewModel.setFilters(currentFilters);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.filter_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_filter:
                //saveFilterResult();
                resetlAllFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        shareSelAllButton.setText(unSelectedAllShareText);
    }

    public void resetCategoryControls(){
        filmsChBox.setChecked(true);
        seriesChBox.setChecked(true);
        booksChBox.setChecked(true);
        gamesChBox.setChecked(true);
        categorySelAllButton.setText(unSelectedAllShareText);
    }
}
