package com.example.rckbrswatch2app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import io.reactivex.disposables.CompositeDisposable;

import static android.content.Context.MODE_PRIVATE;

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
    // Filter Share Preferences
    SharedPreferences sharedPreferences;

    public FirebaseRepository() {
        mFirestoreElement = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Element>> readFirestoreElements(){
        elementList = new ArrayList<>();
        //SharedPrefrence do zapisu i odczytu filtracji

        mFirestoreElement.collection("Users").document(userID).collection("Lista")
              //  .whereEqualTo("category", "Gra")
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

                        List<Element> sharedList = new ArrayList<>(elementList);
                        Log.d("Firestore2", "ShareList BEFORE data/size => " + sharedList.size());
                        sharedList = complementationList(sharedList, true, false, true, true,true,true,true,true,true,true);
                        Log.d("Firestore2", "ShareList AFTER data/size => " + sharedList.size());
                       // registerUser("Johnny", "silverhand@nightcity.pl", "Samurai");
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
        getRandomElement("Gra");
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

    public void registerUser(String name, String email, String password){
        final DocumentReference reference = mFirestoreElement.collection("Users").document();

        /*Nie potrzeba (chyba) zabezpieczenia przed tworzeniem kont o tym samym loginie oraz Emailu
        Można wtedy dodać return, że nie zadziało, bo taki email istnieje lub, hasło słabe i zwracane jest
        przy pomocy właśnie tego reutrna, jak w dokumentacji.
         */
        mFirestoreElement.runTransaction(transaction -> {

            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("password", password);

            transaction.set(reference, userData);

            return null;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "Registration successfully");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "Registration failed because of " + error);
                });


    }
    public /*MutableLiveData<Element>*/void getRandomElement(String choosenCategory){
        final CollectionReference reference = mFirestoreElement.collection("Users").document(userID).collection("Lista");
        Log.d("DatabaseSize", "ElementList Complete is " + elementList.size());

        List<Element> battleList = elementList.stream().filter(p -> !p.isWatched).collect(Collectors.toList());
        Log.d("DatabaseSize", "BattleList NoWatched is " + battleList.size());
        List<Element> filteredList = new ArrayList<>();

        if (battleList.size() >= 1){
        switch (choosenCategory){
            case "Wszystko":
                break;
            case "Gra":
                filteredList = battleList.stream().filter(c -> c.getCategory().equals("Gra")).collect(Collectors.toList());
                break;
            case "Serial":
                filteredList = battleList.stream().filter(c -> c.getCategory().equals("Serial")).collect(Collectors.toList());
                break;
            case "Film":
                filteredList = battleList.stream().filter(c -> c.getCategory().equals("Film")).collect(Collectors.toList());
                break;
            case "Książka":
                filteredList = battleList.stream().filter(c -> c.getCategory().equals("Książka")).collect(Collectors.toList());
                break;
        }
            Log.d("Random", "Random and FilteredList (before Rnaomd) is " + filteredList.size());
            int randomIndex = ThreadLocalRandom.current().nextInt(0, filteredList.size());
            Log.d("Random", "Random number is " + randomIndex);
            Element element = filteredList.get(randomIndex);
            Log.d("Firebase", "Random Element => " + element.getTitle());
        }
        else
            Log.d("Firebase", "There's NO Random Element");

        /*Dodać jeszcze Default, chyba (w zależności od sposobu zwracanych elementów) lub blok try/catch, gdy mimo posiadania
        elementów nieoglądniętych lub nieogranych to wyświetla się że brak danych na liście, bo uzytkownik nie posiada aktualnie
        żadnych elementów z kategorii aktualnie wybranej.
         */
        //reference.document().getId();




        /*return randomElement;*/
    }
    /* !!! Filtracja !!! */
    public List<Element> complementationList(List<Element> elements, boolean finished, boolean unFinished, boolean books, boolean games, boolean series, boolean films,
                                             boolean rock, boolean borys, boolean rockBorys, boolean others) {

        List<Element> elementFilters = new ArrayList<>();

        List<Element> completeList;

       /* boolean finished = elementFilters.get(0).isFinished();
        boolean unFinished = elementFilters.get(0).isUnFinished();
        boolean books = elementFilters.get(0).isBookCategory();
        boolean games = elementFilters.get(0).isGamesCategory();
        boolean series = elementFilters.get(0).isSeriesCategory();
        boolean films = elementFilters.get(0).isFilmCategory();
        boolean rock = elementFilters.get(0).isRockRecommedation();
        boolean borys = elementFilters.get(0).isBorysRecommedation();
        boolean rockBorys = elementFilters.get(0).isRockBorysRecommedation();
        boolean others = elementFilters.get(0).isOtherRecommedation();
*/
        //#1 Oglądane i Nieoglądane
        if (finished && !unFinished)
            completeList = elements.stream().filter(Element::isWatched).collect(Collectors.toList());
        else if (!finished && unFinished)
            completeList = elements.stream().filter(p -> !p.isWatched()).collect(Collectors.toList());
        else
            completeList = new ArrayList<>(elements);
        //#2 Kategorie
        elements.clear();
        if (!games || !books || !series || !films) {
            elements = categoryFilter(games, films, series, books, completeList);
        } else {
            elements = new ArrayList<>(completeList);
        }
        //#3 Polecane
        completeList.clear();
        if (!rock || !borys || !rockBorys || !others) {
            completeList = promFilter(rock, borys, rockBorys, others, elements);
        } else {
            completeList = new ArrayList<>(elements);
        }
        return completeList;
    }

    List<Element> promFilter(boolean promRock, boolean promBorys, boolean promRockBorys, boolean others, List<Element> elements) {

        List<Element> completePromList = new ArrayList<>();
        //średnio wydajne pewnie
        for (Element e : elements) {
            if (e.getShare().equals("Rock") & promRock)
                completePromList.add(e);
            else if (e.getShare().equals("Borys") & promBorys)
                completePromList.add(e);
            else if (e.getShare().equals("Rck&Brs") & promRockBorys)
                completePromList.add(e);
            else if (e.getShare().equals("Inne") & others)
                completePromList.add(e);
        }
        return completePromList;
    }

    List<Element> categoryFilter(boolean catGames, boolean catFilms, boolean catSeries, boolean catBooks, List<Element> elements) {

        List<Element> completePromList = new ArrayList<>();
        for (Element e : elements) {
            if (e.getCategory().equals("Książka") & catBooks)
                completePromList.add(e);
            else if (e.getCategory().equals("Film") & catFilms)
                completePromList.add(e);
            else if (e.getCategory().equals("Gra") & catGames)
                completePromList.add(e);
            else if (e.getCategory().equals("Serial") & catSeries)
                completePromList.add(e);
        }
        return completePromList;
    }

    /* !!! Koniec Filtracji !!! */


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
