package com.example.rckbrswatch2app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    ElementViewModel elementViewModel;
    SharedPreferences sharedPreferences;

    //Moj szajs
    ElementAdapter adapter;
    List<Element> elementList = new ArrayList<>();
    List<Element> elementFilterList;
    boolean gameState;
    List<Element> firebaseFilterList;

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

        //Firebase
        elementViewModel.getFirebaseElements().observe(this, elements ->
        {
             firebaseFilterList = new ArrayList<>();
             firebaseFilterList.addAll(elements);
             Log.d("FirebaseDB", "FIREBASE elements size " + elements.size());

            //Room
            elementViewModel.getAllElements().observe(this, elements1 -> {
                Log.d("RoomDB", "ROOM elements size " + elements1.size());
                elementList.clear();
                elementList.addAll(elements1);
                Log.d("RoomDB", "ROOM after stream elements size " + elementList.size());
                adapter.setElementList(firebaseFilterList);

                // filterList();
            });
        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                elementViewModel.deleteElement(adapter.getElementAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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
            case R.id.addF:
                Element element = new Element("Hello", "Film", true, "Rock");
                elementViewModel.createFirebaseElement(element);
                return true;
            case R.id.deleteRoom:
                elementViewModel.deleteAllElements();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        filterList();
    }
    public void filterList(){
        elementFilterList = new ArrayList<>();
        gameState = sharedPreferences.getBoolean("GameList", false);
        elementFilterList.addAll(elementList);

        elementViewModel.filterElement(elementFilterList, gameState).observe(this, elements -> {
            Log.d("Bufor", "_FILTER_ Size Elements "+elements.size() + " in onSharedPreferenceChanged/FilterElement/Observe");
            adapter.setElementList(elements);
        });

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