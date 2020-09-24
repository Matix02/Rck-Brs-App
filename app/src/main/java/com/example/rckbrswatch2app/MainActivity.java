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

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    ElementViewModel elementViewModel;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences sharedPreferences;

    //Moj szajs
    ElementAdapter adapter;
    List<Element> elementList = new ArrayList<>();
    List<Element> elementFilterList;


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

       /*Wykorzystac to niżej zakomentowane, jesli nie bedzie zadnych nowych funkcji
        // elementViewModel.getAllElements().observe(this, adapter::setElementList);
        */

        elementViewModel.getAllElements().observe(this, elements -> {
            boolean game = sharedPreferences.getBoolean("GameList", false);
            Log.d("Bufor", "OBSERVE ");
            elementList.clear();
            elementList.addAll(elements);
            /* adapter.setElementList(elementList, game);*/
            adapter.setElementList(elements);
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
        /*
        Element element = new Element("title2", "Gra", false, "Rock");
            elementViewModel.createElement(element);
*/
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        boolean game = sharedPreferences.getBoolean("GameList", false);
        elementFilterList = new ArrayList<>();
        Log.d("Bufor", "Size elementList "+elementList.size() + " in onSharedPreferenceChanged");
        elementFilterList.addAll(filterList(elementList, game));
        Log.d("Bufor", "Size elementFilterList "+elementFilterList.size() + " in onSharedPreferenceChanged");
        elementViewModel.filterElement(elementFilterList, game).observe(this, elements -> {
            Log.d("Bufor", "Size MAIN Elements "+elements.size() + " in onSharedPreferenceChanged");
            adapter.setElementList(elements);
        });
        //adapter.setElementList(elementFilterList);
    }
    public List<Element> filterList(List<Element> elements, boolean state){
        List<Element> buforList = new ArrayList<>();
        for(Element e: elements){
            if(e.getCategory().equals("Film")&&state)
                buforList.add(e);
            else if (e.getCategory().equals("Gra")&&!state)
                buforList.add(e);
        }
        return buforList;
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