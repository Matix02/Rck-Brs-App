package com.example.rckbrswatch2app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

public class FiltersDialogActivity extends AppCompatActivity{



    private SwitchCompat finishSwitch;
    private SwitchCompat unFinishSwitch;
    private AppCompatCheckBox filmsChBox;
    private AppCompatCheckBox seriesChBox;
    private AppCompatCheckBox booksChBox;
    private AppCompatCheckBox gamesChBox;
    private SwitchCompat RckSwitch;
    private SwitchCompat BrsSwitch;
    private SwitchCompat RcknBrsSwitch;
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
        RckSwitch = findViewById(R.id.RckSWx);
        BrsSwitch = findViewById(R.id.BrsSWx);
        RcknBrsSwitch = findViewById(R.id.RckBrsSWx);
        othersSwitch = findViewById(R.id.InneSWx);
        categorySelAllButton = findViewById(R.id.zaznaczWszystkeKategorieBT);
        shareSelAllButton = findViewById(R.id.zaznaczWszystkoPolecaneBT);
        saveButton = findViewById(R.id.saveBTx);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Filters");

        saveButton.setOnClickListener(click -> {
            Filter currentFilters = new Filter();
            currentFilters.setFinished(finishSwitch.isChecked());
            currentFilters.setUnfinished(unFinishSwitch.isChecked());
            currentFilters.setFilm(filmsChBox.isChecked());
            currentFilters.setSeries(seriesChBox.isChecked());
            currentFilters.setBook(booksChBox.isChecked());
            currentFilters.setGame(gamesChBox.isChecked());
            currentFilters.setShareRck(RckSwitch.isChecked());
            currentFilters.setShareBrs(BrsSwitch.isChecked());
            currentFilters.setShareRckBrs(RcknBrsSwitch.isChecked());
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
