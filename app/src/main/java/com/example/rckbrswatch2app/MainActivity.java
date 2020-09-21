package com.example.rckbrswatch2app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    ElementViewModel elementViewModel;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences sharedPreferences;

    //Moj szajs
    ElementAdapter adapter;
    List<Element> elementList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("SP_Test", MODE_PRIVATE);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ElementAdapter();
        recyclerView.setAdapter(adapter);

        elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);
       // elementViewModel.getAllElements().observe(this, adapter::setElementList);

        elementViewModel.getAllElements().observe(this, elements -> {
            boolean game = sharedPreferences.getBoolean("GameList", false);
            Log.d("Bufor", "Boolean is " + game + " in Observe");
            /*elementList.clear();
            elementList.addAll(elements);
            adapter.setElementList(elementList, game);*/
            adapter.setElementList(elements);
        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }       /* Element element = new Element("title2", "Gra", false, "Rock");
        elementViewModel.createElement(element); */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                elementViewModel.updateTrigger(5, "title");
                return true;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        boolean game = sharedPreferences.getBoolean("GameList", false);
        Log.d("Bufor", game + " in onSharedPreferenceChanged");
        Log.d("Bufor", "Size elementList "+elementList.size() + " in onSharedPreferenceChanged");

        adapter.setElementFilterList(elementList, game);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferences(MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
    }
}