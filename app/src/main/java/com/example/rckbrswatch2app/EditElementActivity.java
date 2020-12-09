package com.example.rckbrswatch2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Objects;

public class EditElementActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT_NUMBER = "WatchApp.RESULT_NUMBER";
    public static final String EXTRA_ID = "WatchApp.EXTRA_ID";
    public static final String EXTRA_TITLE = "WatchApp.EXTRA_TITLE";
    public static final String EXTRA_CATEGORY = "WatchApp.EXTRA_CATEGORY";
    public static final String EXTRA_SHARE = "WatchApp.EXTRA_SHARE";
    public static final String EXTRA_IS_WATCHED = "WatchApp.EXTRA_IS_WATCHED";


    private AppCompatEditText editTextTitle;
    private RadioGroup categoryRadioGroup;
    private AppCompatRadioButton categoryRadioButton;
    private RadioGroup shareRadioGroup;
    private AppCompatRadioButton shareRadioButton;
    private AppCompatButton saveEditsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_element);

        editTextTitle = findViewById(R.id.editTitleText);
        categoryRadioGroup = findViewById(R.id.kategorieEditRadioG);
        shareRadioGroup = findViewById(R.id.shareEditRadioG);
        saveEditsButton = findViewById(R.id.saveEditBT);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_RESULT_NUMBER)) {
            setTitle("Edytuj Element");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            //Uzupełnić RadioButtony x2

        } else {
            setTitle("Dodaj Element");
        }

        saveEditsButton.setOnClickListener(v -> {

            saveElement();
        });

    }
    public void saveElement() {
        try {
            String title = Objects.requireNonNull(editTextTitle.getText()).toString();
            int radioCategoryId = categoryRadioGroup.getCheckedRadioButtonId();
            categoryRadioButton = findViewById(radioCategoryId);
            String categoryName = categoryRadioButton.getText().toString();
            int radioShareId = shareRadioGroup.getCheckedRadioButtonId();
            shareRadioButton = findViewById(radioShareId);
            String shareName = shareRadioButton.getText().toString();

            Intent data = new Intent();
            data.putExtra(EXTRA_TITLE, title);
            data.putExtra(EXTRA_CATEGORY, categoryName);
            data.putExtra(EXTRA_SHARE, shareName);

            Log.d("ResultNumber", "" + getIntent().getStringExtra(EXTRA_RESULT_NUMBER));
            String id = getIntent().getStringExtra(EXTRA_ID);
            boolean isWatched = getIntent().getBooleanExtra(EXTRA_IS_WATCHED, false);
            if (id != null) {
                data.putExtra(EXTRA_ID, id);
                data.putExtra(EXTRA_IS_WATCHED, isWatched);
            }

            setResult(RESULT_OK, data);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Proszę uzpełnić puste pola", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_element:
                saveElement();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}