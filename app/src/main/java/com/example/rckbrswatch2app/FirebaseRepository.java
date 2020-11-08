package com.example.rckbrswatch2app;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

public class FirebaseRepository {
    private DatabaseReference mReferenceElement;
    private List<Element> elementList;
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();
    //Moj szajs
    private CompositeDisposable compositeDisposable=new CompositeDisposable();
    private FirebaseFirestore mFirestoreElement;
    private MutableLiveData<List<Boolean>> isWatchedLiveData = new MutableLiveData<>();
    private List<Element> newsList;
    private List<Boolean> booleanList;

    public FirebaseRepository() {
        mFirestoreElement = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Element>> readFirestoreElements(){
        elementList = new ArrayList<>();

        mFirestoreElement.collection("Users").document("WJolg7rxMmz9SFXfVwnc").collection("Lista")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()))
                        {
                            Log.d("Firestore2", "FirestoreTest data => " + documentSnapshot.getData());
                            elementList.add(documentSnapshot.toObject(Element.class));
                        }
                        Log.d("Firestore2", "FirestoreTest data/size => " + elementList.size());

                        elementLiveData.postValue(elementList);
                    } else {
                        Log.d("Firesotre2", "! FirestoreTest error = " + task.getException());
                    }
                });
        return elementLiveData;
    }

    public MutableLiveData<List<Boolean>> readUserFavElementsDocument(){
        DocumentReference dR = mFirestoreElement.collection("WatchCollection").document("4");
        List<Boolean> watchList = new ArrayList<>();
        dR.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if(document.exists()) {
                    //com
                    for(int i=1; i<= Objects.requireNonNull(document.getData()).size();i++ ) {
                        String nameElement = "Element" + i;
                      //  Log.d("Firesotre2", "! Firestore boolean" + i + " is = " + document.getBoolean(nameElement));
                        watchList.add(document.getBoolean(nameElement));
                    }
                    isWatchedLiveData.postValue(watchList);
                   // Log.d("Firesotre2", "! Firestore size boolean = " + watchList.size());
                } else
                    Log.d("Firesotre2", "! Firestore error = " + task.getException());
            } else
                Log.d("Firesotre2", "! Firestore error = " + task.getException());
        });
        return isWatchedLiveData;
    }

    public MutableLiveData<List<Element>> readFirebaseElements(){
        elementList = new ArrayList<>();
        Query query = mReferenceElement.orderByChild("category").equalTo("Gra");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    elementList.clear();
                    for(DataSnapshot keyNode : snapshot.getChildren())
                    {
                        Element element = keyNode.getValue(Element.class);
                        elementList.add(element);
                    }
                    Log.d("Xkanapka", "Size = " + elementList.size());
                    elementLiveData.postValue(elementList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return elementLiveData;
    }

    public void addDocument(){
        //Dodawanie od Admina, przekazać na FAB'a
        Element element = new Element("Ku jezioru", "Film", false, "Borys", "New");

        Map<String, Object> elementData = new HashMap<>();
        elementData.put("title", element.getTitle());
        elementData.put("category", element.getCategory());
        elementData.put("share", element.getShare());
        elementData.put("isWatched", element.isWatched());

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", "DSASd@wp.pl");
        userData.put("name", "Tetrix02");
        userData.put("password", "1234567");

        mFirestoreElement.collection("News").add(element);

       /* Dodawanie Dokuemntów i Kolekcji w jednym, zatrzymując też ID nowo stworzonego elementu */
       CollectionReference reference = mFirestoreElement.collection("Users");
        Task<DocumentReference> referenceTask = reference.add(userData);
        referenceTask.addOnSuccessListener(documentReference -> {
            String d = documentReference.getId();
            CollectionReference collectionReference = reference.document(d).collection("Lista");
            for(Element e: elementList){
                collectionReference.add(e)
                .addOnFailureListener(f -> Log.d("Firestore", "Nie udało się zrobić pętli by dodać wszystko"));
            }
        })
        .addOnFailureListener(e -> Log.d("Firestore", "Nie udało się dodać Użytkownika"));
    }
    public void addCompletelyNewElement(){
        Element element = new Element("Wściekłe Psy", "Film", false, "Borys");
        //Pobieranie UserId itd.
        String userID = "hjGb7smtlF4kjzvaYFnl";
        mFirestoreElement.collection("Users").document(userID).collection("Lista").add(element);
    }
    public void getNews() {
        newsList = new ArrayList<>();
        Map<String, Object> booleanMap = new HashMap<>();
        // Zwraca listę oglądanych Id i Oglądanych IsWatched danego użytkownika
        String userID = "1";
        /*Zwraca tabelę News'ów. Nowości są zbierane, albo po włączeniu aplikacji albo w tracie, poprzez(...)*/
        mFirestoreElement.collection("News")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()))
                        {
                            Log.d("Firestore2", "News data => " + documentSnapshot.getData());
                            newsList.add(documentSnapshot.toObject(Element.class));
                        }
                        Log.d("Firestore2", "News data/size => " + newsList.size());
                    } else {
                        Log.d("Firesotre2", "! News error = " + task.getException());
                    }
                });
       /*  NotActual-outdated Plan:
        1.Rejestracja:
            a) dodanie do tabeli UserTest - emaila, name'a, passworda
            b) w tym dokumencie przechowywujemy IdDokumentu i dodajemy kolecję Oglądane
            c) w doc "Oglądane" dodajemy rekordy xElementsSize - tyle ile jest kolekcji "ElementsTest"
            d) wszystkie muszą:
                - mieć to samo id
                - pole ma nazywać się isWatched
                - być typu boolean
            e)

       NieUdany eksperyment związany w osobną tabelę dla każdego użytkownika, gdzie miałby rozpiskę co oglądał, a czego nie
       mFirestoreElement.collection("UsersTest").document("2").collection("Oglądane").document("1")
                .get()
                .addOnCompleteListener(documentSnapshot -> {
                    if(documentSnapshot.isSuccessful()){
                        DocumentSnapshot documentSnapshot1 = documentSnapshot.getResult();
                        assert documentSnapshot1 != null;
                        if(documentSnapshot1.exists()){
                            Log.d("Firestore", "Data => " + documentSnapshot1.getData());
                            booleanMap.putAll(Objects.requireNonNull(documentSnapshot1.getData()));
                            Log.d("Firestore", "Data => " + booleanMap.size());
                        }
                    }*/
                    //Log.d("Firestore2", "News/Boolean data/size => " + booleanMap.size());
                         /*   if (documentSnapshot.isSuccessful()) {
                                for(QueryDocumentSnapshot document : Objects.requireNonNull(documentSnapshot.getResult()))
                                {
                                    Log.d("Firestore2", "News/Boolean data => " + document.getData());
                                    booleanMap.put(document.getData().toString(), document.getData());
                                }
                                   // booleanList.addAll(documentSnapshot1.getData());
                                   // booleanMap.putAll(Objects.requireNonNull(documentSnapshot1.getData()));
                             //   Log.d("Firestore2", "News/Boolean data/size => " + booleanList.size());
                            } else {
                                Log.d("Firesotre2", "! News error = " + documentSnapshot.getException());
                            }
                });*/
    }

    public void addElement(Element element){

    }

    public void updateElement(Element element){
        mFirestoreElement.collection("Users");
    }

    public void createFirebaseElement(Element element) {
        mReferenceElement.push().setValue(element);
    }
   /*
   !!! Dodawanie Dokumentów !!!
    public void addDocument(){
        Element element = new Element("THX", "Film", false, "Other");

        Map<String, Object> elementData = new HashMap<>();
        elementData.put("title", element.getTitle());
        elementData.put("category", element.getCategory());
        elementData.put("share", element.getShare());
        elementData.put("isWatched", element.isWatched());

        Map<String, Object> data = new HashMap<>();
        data.put("title", "Mr.Robot");
        data.put("isWatched", "true");
        data.put("share", "Brs");
        data.put("category", "Serial");

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", "DSASd@wp.pl");
        userData.put("name", "Tetrix02");
        userData.put("password", "1234567");

        CollectionReference reference = mFirestoreElement.collection("Users");


        Task<DocumentReference> referenceTask = reference.add(userData);
        referenceTask.addOnSuccessListener(documentReference -> {
            String d = documentReference.getId();
            reference.document(d).collection("Lista")
                    .add(elementData)
                    .addOnFailureListener(e -> Log.d("Firestore", "Nie udało się dodać kolekcji List'y i jej elementów"));
        })
                .addOnFailureListener(e -> Log.d("Firestore", "Nie udało się dodać Użytkownika"));
    }*/
}
