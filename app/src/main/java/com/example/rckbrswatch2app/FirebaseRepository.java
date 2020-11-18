package com.example.rckbrswatch2app;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    private MutableLiveData<List<Element>> newsElementLiveData = new MutableLiveData<>();
    private String userID = "mENkJn3iyIQDIqSh3cRc";
    //Date Category
    private Calendar lastLogin = Calendar.getInstance();
    private Calendar newLogin = Calendar.getInstance();
    //Random
    private MutableLiveData<Element> randomElement = new MutableLiveData<>();


    public FirebaseRepository() {
        mFirestoreElement = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Element>> readFirestoreElements(){
        elementList = new ArrayList<>();

        mFirestoreElement.collection("Users").document(userID).collection("Lista")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()))
                        {
                            Log.d("Firestore2", "FirestoreTest data => " + documentSnapshot.getData());
                            Element element = documentSnapshot.toObject(Element.class);
                            elementList.add(element);
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
        userData.put("NewLogin", FieldValue.serverTimestamp());
        userData.put("LastLogin", FieldValue.serverTimestamp());

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
    public MutableLiveData<List<Element>> getNews() {
        Map<String, Object> booleanMap = new HashMap<>();
        // Zwraca listę oglądanych Id i Oglądanych IsWatched danego użytkownika
        String userID = "1";
        /*Zwraca tabelę News'ów. Nowości są zbierane, albo po włączeniu aplikacji albo w tracie, poprzez(...)*/

        mFirestoreElement.collection("News").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.d("Firestore", "News Listener is DEAD");
                    return;
                }

                newsList = new ArrayList<>();
                assert value != null;
                for(QueryDocumentSnapshot documentSnapshot : value)
                    {
                        Log.d("Firestore2", "News data => " + documentSnapshot.getData());
                        newsList.add(documentSnapshot.toObject(Element.class));
                    }
                    newsElementLiveData.postValue(newsList);
                    Log.d("Firestore2", "News data/size => " + newsList.size());
                }
        });
        return newsElementLiveData;
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

    public void getDate(){
        //Zwracanie dat logowania użytkownika (aktualnego i poprzedniego zalogowania)
        mFirestoreElement.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(document ->{
                    if (document.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = document.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                         //   lastLogin = Calendar.getInstance();
                            lastLogin.setTime(Objects.requireNonNull(Objects.requireNonNull(document.getResult()).getDate("LastLogin")));
                          //newLogin = Calendar.getInstance();
                          newLogin.setTime(Objects.requireNonNull(Objects.requireNonNull(document.getResult()).getDate("NewLogin")));
                          //Date date = new Calendar(String.valueOf(Objects.requireNonNull(document.getResult()).getDate("LastLogin")));
                          Log.d("Firestore", "LastLogin -> " + Objects.requireNonNull(document.getResult()).getDate("LastLogin"));
                          Log.d("Firestore", "NewLogin -> " + Objects.requireNonNull(document.getResult()).getDate("NewLogin"));
                        }
                        else
                            Log.d("Firestore", "LoginTime error - No Such Document");
                    } else
                        Log.d("Firestore", "LoginTime error with " + document.getException());
                });
    }

    private Calendar getLastLogin() {
        mFirestoreElement.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(document ->{
                    if (document.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = document.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            lastLogin.setTime(Objects.requireNonNull(Objects.requireNonNull(document.getResult()).getDate("LastLogin")));
                            Log.d("Firestore", "LastLogin from Personal Inner Function -> " + lastLogin.getTime());

                        }else
                            Log.d("Firestore", "LoginTime error - No Such Document");
                    } else
                        Log.d("Firestore", "LoginTime error with " + document.getException());
                });
        Log.d("Firestore", "LastLogin from PersonalFunction -> " + lastLogin.getTime());
        return lastLogin;
    }

    public void filterNews(){
        //Zwraca dane elementy z tabeli NEWS, które są starsze od daty dzisiejszej lub nowsze

        Date creationDate = new Date();
        Log.d("FilterStore", "% ActuallDate => " + creationDate);

       /* mFirestoreElement.collection("News")
                .whereGreaterThan("time", creationDate)
                .get()
                .addOnCompleteListener(command -> {
                    if(command.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(command.getResult()))
                            Log.d("FilterStore", "NewsDataTable => " + document.getDate("time"));

                    }
                    else
                        Log.d("FilterStore", "NewsDataTable error");
                });*/
    }
    public void setTimeLogin(){
        transTime();
        //Aktualizuje podane dane, wraz z możliwością dostoswania ilości pól, i akutalizacji daty na tą aktualną w normalnym formacie
    }
    public void transTime(){
        final DocumentReference userLogRef = mFirestoreElement.collection("Users").document(userID);

        mFirestoreElement.runTransaction(transaction -> {
            Date actualDate = new Date();
            DocumentSnapshot snapshot = transaction.get(userLogRef);
            Date newUserLoginTime = snapshot.getDate("NewLogin");
           // transaction.update(userLogRef, "LastLogin", newUserLoginTime, "NewLogin", actualDate);
            //Pamiętać o zrobieniu z tego nulla, bo i po co - return'a
            return newUserLoginTime;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "TransactionTime success ");

                    transList();
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "Transaction failed because of " + error);
                });
    }

    public void transList() {
        final DocumentReference userLogRef = mFirestoreElement.collection("Users").document(userID);
        final CollectionReference newsListRef = mFirestoreElement.collection("News");
        Log.d("DatabaseSize", "Size of this db in repository is " + elementList.size());
        getRandomElement();
        mFirestoreElement.runTransaction(transaction -> {
            //Date Section
            DocumentSnapshot snapshot = transaction.get(userLogRef);
            Date oldUserLoginTime = snapshot.getDate("LastLogin");
            Log.d("Firestore", "TransactionList | NewLogin is " + oldUserLoginTime);
            //NewsList Section
            assert oldUserLoginTime != null;
            newsListRef.whereGreaterThan("time", oldUserLoginTime)
                    .get()
                    .addOnCompleteListener(command -> {
                        if(command.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(command.getResult())){
                                Log.d("FilterStore", "TransactionNewsData - NewsDataTable => " + document.toObject(Element.class)); //Sprawdzić elementy w tym elemencie po przepisaniu
                                Element element = document.toObject(Element.class);
                                updateMainList(document, element);
                            //Wywołanie innej transakcji, która będzie dodawać, edytować bądź usuwać elementy z NewsTable
                        }}
                        else
                            Log.d("FilterStore", "TransactionNewsData - NewsDataTable error");
                    });
            return null;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "TransactionList success");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "Transaction failed because of " + error);
                });
    }

    public void updateMainList(QueryDocumentSnapshot document, Element element){
        String fieldType = (String) document.get("state");
        String docId = document.getId();

        final DocumentReference userListDocRef = mFirestoreElement.collection("Users").document(userID).collection("Lista").document(docId);
        assert fieldType != null;
        switch (fieldType) {
            case "New":
                mFirestoreElement.runTransaction(transaction -> {
                    //  DocumentSnapshot snapshot = transaction.get(userListDocRef);
                    transaction.set(userListDocRef, element);
                    return null;
                }).addOnSuccessListener(success -> {
                    Log.d("Firestore", "TransactionADD success");
                })
                        .addOnFailureListener(error -> {
                            Log.d("Firestore", "TransactionADD failed = " + error);
                        });
                break;
            case "Delete":
                mFirestoreElement.runTransaction(transaction -> {
                    transaction.delete(userListDocRef);
                    return null;
                }).addOnSuccessListener(success -> {
                    Log.d("Firestore", "TransactionDELETE success");
                })
                        .addOnFailureListener(error -> {
                            Log.d("Firestore", "TransactionDELETE failed = " + error);
                        });
                break;
            case "Update":
                mFirestoreElement.runTransaction(transaction -> {
                    transaction.update(userListDocRef, "title", element.getTitle());
                    transaction.update(userListDocRef, "share", element.getShare());
                    transaction.update(userListDocRef, "category", element.getCategory());

                    return null;
                }).addOnSuccessListener(success -> {
                    Log.d("Firestore", "TransactionUPDATE success");
                })
                        .addOnFailureListener(error -> {
                            Log.d("Firestore", "TransactionUPDATE failed = " + error);
                        });
                break;
        }
    }

    //Niedokończone, trzeba mieć podłączony backend - repository z UI
    public void setWatchElement(Element element, boolean isWatched){
        final DocumentReference listRef = mFirestoreElement.collection("Users").document(userID).collection("Lista")
                .document(/*potrzebny jest ID tego elementu*/);

        mFirestoreElement.runTransaction(transaction -> {
            transaction.update(listRef, "watched", isWatched);
            return null;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "UserTransactionIsWatched success");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "UserTransactionIsWatched failed because of " + error);
                });

    }

    public void deleteElement(Element element, String elementId){
        //Usuwa z listy tego właśnie administratora
        final DocumentReference listRef = mFirestoreElement.collection("Users").document(userID).collection("Lista")
                .document(/*brakuje ID ktory to dokument*/);

        //Dodaje do tabeli News dla pozostałych User'ów o statusie User, żeby przy następnym otwarciu został ten element usunięty
        final DocumentReference newsRef = mFirestoreElement.collection("News").document(/*musi być konkretny ID tego elementu*/);

        mFirestoreElement.runTransaction(transaction -> {
           transaction.delete(listRef);
           transaction.set(newsRef, element);

            return null;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "AdminTransactionDelete success");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "AdminTransactionDelete failed because of " + error);
                });
    }

    public /*MutableLiveData<Element>*/void getRandomElement(){
        final CollectionReference reference = mFirestoreElement.collection("Users").document(userID).collection("Lista");
        String choosenCategory = "Gra";
        Log.d("DatabaseSize", "ElementList Complete is " + elementList.size());
        List<Element> checkList = new ArrayList<>(elementList);

        List<Element> battleList = elementList.stream().filter(Element::isWatched).collect(Collectors.toList());
        Log.d("DatabaseSize", "BattleList NoWatched is " + battleList.size());

        reference.document().getId();




        int randomIndex = ThreadLocalRandom.current().nextInt(0, elementList.size());
        Log.d("Random", "Random number is " + randomIndex);


        Element element = elementList.get(randomIndex);



        Log.d("Firebase", "Random Element => " + element.getTitle());
        /*return randomElement;*/
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
