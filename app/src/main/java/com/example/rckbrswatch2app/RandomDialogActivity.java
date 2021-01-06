package com.example.rckbrswatch2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import java.util.Objects;

public class RandomDialogActivity extends AppCompatActivity {

    private RadioGroup radioCategoryGroup;
    private AppCompatRadioButton radioCategoryButton;
    private RadioGroup radioShareGroup;
    private AppCompatRadioButton radioShareButton;
    private AppCompatTextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_pop);

        radioCategoryGroup = findViewById(R.id.kategorieRadioG);
        radioShareGroup = findViewById(R.id.shareRadioG);
        resultTextView = findViewById(R.id.randomResultTV);
        AppCompatButton rollButton = findViewById(R.id.rollRandomBT);

        ElementViewModel viewModel = new ViewModelProvider(this).get(ElementViewModel.class);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_back);
        setTitle("Wylosuj Element");

        Intent intent = getIntent();
        String localUserID = intent.getStringExtra("userID");
        Log.d("UserID", "is " + localUserID + " in Filter");

        rollButton.setOnClickListener(click -> {
            //Category
           int radioCategoryId = radioCategoryGroup.getCheckedRadioButtonId();
           radioCategoryButton = findViewById(radioCategoryId);
           String categoryName = radioCategoryButton.getText().toString();

           //Share
            int radioShareId = radioShareGroup.getCheckedRadioButtonId();
            radioShareButton = findViewById(radioShareId);
            String shareName = radioShareButton.getText().toString();
            Log.d("RollMainFun", "Category = " + categoryName + " shareName = " + shareName);

            viewModel.getRandomElement(localUserID, categoryName, shareName).observe(this, element -> {
                resultTextView.setText("");
                String randomTitleElement = element.getTitle();
                resultTextView.setVisibility(View.VISIBLE);
                resultTextView.setText(randomTitleElement);
            });
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.random_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reset_filter) {
            int defaultShareSwtichID = 2131230799;
            int defaultCategorySwtichID = 2131230798;

            radioCategoryGroup.check(defaultCategorySwtichID);
            radioShareGroup.check(defaultShareSwtichID);
            resultTextView.setText("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}