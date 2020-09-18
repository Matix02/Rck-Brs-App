package com.example.rckbrswatch2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ElementViewModel elementViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final ElementAdapter adapter = new ElementAdapter();
        recyclerView.setAdapter(adapter);

        elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);
        elementViewModel.getAllElements().observe(this, new Observer<List<Element>>() {
            @Override
            public void onChanged(List<Element> elements) {
                adapter.setElementList(elements);
                Toast.makeText(MainActivity.this, "onChanged ", Toast.LENGTH_LONG).show();
            }
        });
       /* Element element = new Element("title2", "Gra", false, "Rock");
        elementViewModel.createElement(element);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        //test
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                elementViewModel.updateTrigger(5, "title");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}