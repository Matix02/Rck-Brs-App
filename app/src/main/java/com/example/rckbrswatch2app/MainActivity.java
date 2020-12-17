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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, SearchView.OnQueryTextListener{

    public static final int ADD_ELEMENT_REQUEST = 1;
    public static final int EDIT_ELEMENT_REQUEST = 2;

    ElementViewModel elementViewModel;
    SharedPreferences sharedPreferences;

    //Moj szajs
    ElementAdapter adapter;
    List<Element> elementList = new ArrayList<>();
    List<Element> elementFilterList;
    List<Element> firebaseFilterList;
    List<Element> firebaseNewsList;
    List<Boolean> isWatchedList;
    static int counterNews = 0;
    static int counterData = 0;
   //private String userID = "mENkJn3iyIQDIqSh3cRc";

    //NieKoniecznie taka metoda jest właściwa, bo jest jeszcze Intent.putExtra(UserID); Memoryleaks
    static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("SP_Test", MODE_PRIVATE);

        FloatingActionButton floatingActionAddButton = findViewById(R.id.addFab);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ElementAdapter(MainActivity.this);
        recyclerView.setAdapter(adapter);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        elementViewModel = new ViewModelProvider(this).get(ElementViewModel.class);

        Intent intent = getIntent();
        String localUserID = intent.getStringExtra("userID");
        Log.d("UserID", "is " + localUserID);
        userID = localUserID;
        Log.d("UserID", "is " + userID);

        long startTime = System.currentTimeMillis();

        elementViewModel.readFirestore(userID).observe(this , elementsList -> {
            firebaseFilterList = new ArrayList<>();
            firebaseFilterList.addAll(elementsList);
            Log.d("Firebase", "Main firebase Elements size is " + firebaseFilterList.size());
            adapter.setElementList(firebaseFilterList);
            Log.d("Firestore", "#CounterData is " + counterData++);
        });
        elementViewModel.getNewsCollection();

     //   elementViewModel.getFilterDataNews();
        elementViewModel.getLastnNewLogin();
        //Zapisuej dane logowania daty
        elementViewModel.setActiveUserLogin();

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        Log.d("TimeBufor", "Time is " + duration+" ms");

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

   public void filterList(){
     /*  public void checkUser(String userID) { firebaseRepository.isUserExist(userID);}
       gameState = sharedPreferences.getBoolean("GameList", false);

        elementViewModel.filterElement(elementFilterList, gameState).observe(this, elements -> {
            Log.d("Bufor", "_FILTER_ Size Elements " + elements.size() + " in onSharedPreferenceChanged/FilterElement/Observe");
            adapter.setElementList(elements);
        }); */
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            case R.id.filter:
                intent = new Intent(getApplicationContext(), FilterPopUpActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:
               intent = new Intent(MainActivity.this, RandomDialogActivity.class);
               startActivity(intent);
                return true;
            case R.id.addF:
                intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
                return true;
            case R.id.deleteRoom:
                intent = new Intent(MainActivity.this, FiltersDialogActivity.class);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        filterList();
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
            elementViewModel.editElement(element);
        } else {
            Toast.makeText(this, "Nie udało się dodać Elementu", Toast.LENGTH_LONG).show();
        }
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