package com.example.rckbrswatch2app;

import android.annotation.SuppressLint;
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
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, SearchView.OnQueryTextListener{

    ElementViewModel elementViewModel;
    SharedPreferences sharedPreferences;

    //Moj szajs
    ElementAdapter adapter;
    List<Element> elementList = new ArrayList<>();
    List<Element> elementFilterList;
    boolean gameState;
    List<Element> firebaseFilterList;
    List<Element> firebaseNewsList;
    List<Boolean> isWatchedList;
    static int counterNews = 0;
    static int counterData = 0;
   // private String userID = "mENkJn3iyIQDIqSh3cRc";

    //NieKoniecznie taka metoda jest właściwa, bo jest jeszcze Intent.putExtra(UserID); Memoryleaks
    static final String userID = LoginActivity.userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("SP_Test", MODE_PRIVATE);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ElementAdapter(MainActivity.this);
        recyclerView.setAdapter(adapter);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        Log.d("UserID", "is " + userID);

        long startTime = System.currentTimeMillis();

       /* elementViewModel.getFirebaseElements().observe(this, elements -> {
             firebaseFilterList = new ArrayList<>();
             firebaseFilterList.addAll(elements);
             Log.d("FirebaseDB", "FIREBASE elements size " + elements.size());
             adapter.setElementList(firebaseFilterList);
            // elementViewModel.updateList(firebaseFilterList);
            // filterList();
        }); */


        elementViewModel.readFirestore(userID).observe(this , elementsList -> {
            firebaseFilterList = new ArrayList<>();
            firebaseFilterList.addAll(elementsList);
            Log.d("Firebase", "Main firebase Elements size is " + firebaseFilterList.size());
            adapter.setElementList(firebaseFilterList);
            Log.d("Firestore", "#CounterData is " + counterData++);
            elementViewModel.getNewsCollection().observe(this, elements -> {
                firebaseNewsList = new ArrayList<>();
                firebaseNewsList.addAll(elements);
                Log.d("Firestore", "MainActivity firebaseNews size is " + firebaseNewsList.size());
                Log.d("Firestore", "#CounterNews is " + counterNews++);

            });
        });

        elementViewModel.getFilterDataNews();
        elementViewModel.getLastnNewLogin();
        elementViewModel.setActiveUserLogin();

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        Log.d("TimeBufor", "Time is " + duration+" ms");

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //elementViewModel.deleteElement(adapter.getElementAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);
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
       // searchView.setIconifiedByDefault(false);
       // searchView.setQuery("", false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        List<Element> searchedList = new ArrayList<>();

        adapter.getFilter().filter(newText);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                //elementViewModel.updateTrigger(5, "title");
                return true;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.addF:
                //Element element = new Element("Hello", "Film", true, "Rock");
               // elementViewModel.createFirebaseElement(element);
                elementViewModel.addElement(userID);
                return true;
            case R.id.deleteRoom:
              //  elementViewModel.deleteAllElements();
                return true;
            case R.id.signOut:
                //Element element = new Element("Hello", "Film", true, "Rock");
                // elementViewModel.createFirebaseElement(element);
                //elementViewModel.addDocument();
                elementViewModel.singOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
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