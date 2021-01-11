package com.example.rckbrswatch2app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rckbrswatch2app.Model.Element;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    public static final int ADD_ELEMENT_REQUEST = 1;
    public static final int EDIT_ELEMENT_REQUEST = 2;

    ElementViewModel elementViewModel;

    ElementAdapter adapter;
    List<Element> firebaseFilterList;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionAddButton = findViewById(R.id.addFab);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ElementAdapter(MainActivity.this);
        recyclerView.setAdapter(adapter);
        elementViewModel = new ViewModelProvider(this).get(ElementViewModel.class);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        Log.d("UserID", "is " + userID);

        elementViewModel.getUserFilters(userID).observe(this, filter ->
                elementViewModel.readFirestore(userID, filter).observe(this , elementsList -> {
                    findViewById(R.id.loadingElements).setVisibility(View.GONE);
                    firebaseFilterList = new ArrayList<>();
                    firebaseFilterList.addAll(elementsList);
                    Log.d("Firebase", "Main firebase Elements size is " + firebaseFilterList.size());
                    adapter.setElementList(firebaseFilterList);
        }));

        elementViewModel.getNewsCollection(userID);
        //Zapisuej dane logowania daty
        elementViewModel.setActiveUserLogin(userID);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String selectedElementID = adapter.getElementAt(viewHolder.getAdapterPosition()).getId();
                elementViewModel.deleteElement(userID, selectedElementID);
               // adapter.notifyDataSetChanged();
                Log.d("DeleteTransaction", "MainActivity Delete elementID arg -> " + selectedElementID);
                Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);

        floatingActionAddButton.setOnClickListener(v -> {
            Intent intentToAdd = new Intent(MainActivity.this, EditElementActivity.class);
            startActivityForResult(intentToAdd, ADD_ELEMENT_REQUEST);
        });

        adapter.setOnItemClickListener(element -> {
            Intent elementIntent = new Intent(MainActivity.this, EditElementActivity.class);
            elementIntent.putExtra(EditElementActivity.EXTRA_RESULT_NUMBER, element.getId());
            elementIntent.putExtra(EditElementActivity.EXTRA_ID, element.getId());
            elementIntent.putExtra(EditElementActivity.EXTRA_TITLE, element.getTitle());
            elementIntent.putExtra(EditElementActivity.EXTRA_CATEGORY, element.getCategory());
            elementIntent.putExtra(EditElementActivity.EXTRA_SHARE, element.getShare());
            elementIntent.putExtra(EditElementActivity.EXTRA_IS_WATCHED, element.isWatched());
            startActivityForResult(elementIntent, EDIT_ELEMENT_REQUEST);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.filterPage:
                intent = new Intent(MainActivity.this, FiltersDialogActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                return true;
            case R.id.randomElementPage:
               intent = new Intent(MainActivity.this, RandomDialogActivity.class);
                intent.putExtra("userID", userID);
               startActivity(intent);
                return true;
            case R.id.infoPage:
                intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
                return true;
            case R.id.signOut:
                elementViewModel.singOut();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ELEMENT_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            String title = data.getStringExtra(EditElementActivity.EXTRA_TITLE);
            String category = data.getStringExtra(EditElementActivity.EXTRA_CATEGORY);
            String share = data.getStringExtra(EditElementActivity.EXTRA_SHARE);

            Element element = new Element(title, category, share);
            elementViewModel.addElement(element);
            Log.d("UserID", "onActivityResult " + userID);

            Toast.makeText(this, "Dodano Element", Toast.LENGTH_LONG).show();
        } else if (requestCode == EDIT_ELEMENT_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            String resultNumber = data.getStringExtra(EditElementActivity.EXTRA_ID);

            if (resultNumber == null) {
                Toast.makeText(this, "Elementu nie mozna zauktualizować", Toast.LENGTH_LONG).show();
                return;
            }
            String id = data.getStringExtra(EditElementActivity.EXTRA_ID);
            String title = data.getStringExtra(EditElementActivity.EXTRA_TITLE);
            String category = data.getStringExtra(EditElementActivity.EXTRA_CATEGORY);
            String share = data.getStringExtra(EditElementActivity.EXTRA_SHARE);
            boolean isWatched = data.getBooleanExtra(EditElementActivity.EXTRA_IS_WATCHED, false);

            Element element = new Element(id, title, category, isWatched, share);
            elementViewModel.editElement(userID, element);
        } else {
            Log.d("AddEditActivity", "Nie udało się dodać Elementu");
        }
    }
}