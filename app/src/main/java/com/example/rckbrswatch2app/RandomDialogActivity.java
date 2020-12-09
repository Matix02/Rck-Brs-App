package com.example.rckbrswatch2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
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

            viewModel.getRandomElement(categoryName, shareName).observe(this, element -> {
                String randomTitleElement = element.getTitle();
                resultTextView.setVisibility(View.VISIBLE);
                resultTextView.setText(randomTitleElement);
            });
        });
    }
}