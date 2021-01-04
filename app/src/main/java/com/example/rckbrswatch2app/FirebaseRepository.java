package com.example.rckbrswatch2app;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private String currentUserID;
    //Date Category
    private Calendar lastLogin = Calendar.getInstance();
    private Calendar newLogin = Calendar.getInstance();
    //Random
    private MutableLiveData<Element> randomElement = new MutableLiveData<>();
    // Filter Share Preferences
    SharedPreferences sharedPreferences;
    //Filters GetData
    private MutableLiveData<Filter> filtersLiveData = new MutableLiveData<>();
    //Random - sprawdzic czy mozna uzyc tej samej liveData?
    private MutableLiveData<Element> randomElementLiveData = new MutableLiveData<>();
    private List<Element> randomElementsList;
    //Test with USerID
    private MutableLiveData<String> userIDLiveData = new MutableLiveData<>();


    public FirebaseRepository() {
        mFirestoreElement = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Element>> readFirestoreElements(String userID, Filter filter){
        currentUserID = userID;
        userIDLiveData.setValue(userID);
        Log.d("MutLiveData", "User Id in MutableLiveData is " + userIDLiveData.getValue());

        //SharedPrefrence do zapisu i odczytu filtracji

        mFirestoreElement.collection("Users").document(userID).collection("Lista")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d("ReadFirebase", "ObserveElement has lost a mind" + error);
                        return;
                    }
                    //Możliwe, że zapis zawartości z ArrayListy Będzie resetowany zostawiając tutaj, sprawdzic
                    elementList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(value)) {
                        Log.d("Firestore2", "FirestoreTest data => " + documentSnapshot.getData());
                        Element element = documentSnapshot.toObject(Element.class);
                        elementList.add(element);
                    }
                    Log.d("Firestore2", "FirestoreTest data/size => " + elementList.size());

                    List<Element> sharedList = new ArrayList<>(elementList);
                    Log.d("Firestore2", "ShareList BEFORE data/size => " + sharedList.size());
                    sharedList = complementationList(sharedList, filter);
                    Log.d("Firestore2", "ShareList AFTER data/size => " + sharedList.size());
                    // registerUser("Johnny", "silverhand@nightcity.pl", "Samurai");
                    /* Odwracanie, uaktywnwnićjeśli wydajność będzie na dobrym poziomie - sfera dodatkowa
                    Collections.reverse(elementList); */
                    elementLiveData.postValue(sharedList);
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

    public void registerOutsideUser(User newUser){
        String newUserID = newUser.getUserID();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", newUser.getEmail());
        userData.put("name", newUser.getDisplayName());
        userData.put("password", newUser.getPassword());
        userData.put("NewLogin", FieldValue.serverTimestamp());
        userData.put("LastLogin", FieldValue.serverTimestamp());

        DocumentReference reference = mFirestoreElement.collection("Users").document(newUserID);
        CollectionReference elementsCollectionReference = mFirestoreElement.collection("Elements");

        Task<Void> referenceTask = reference.set(userData);
        /*zastanowić się na problemem związanym z powtórnym odpalanie funkcji,
         która jest tylko dla nowych użytkowników. Rozwiązanie, dodać tutaj funkcję przy dokumencie = .exists(),
       lub z SharePrefrences. lub jakoś z searchem. Metody te sa opisane według kolejności według których powinno zostać
       zasotosowane. I jeszcze naprawić dodwanie nowych elementów do nowej Listy użytkownika, bo dodawane są jako nowe elementy o tej samej zawartości.
       Spróbować zmodyfikować kod tak, aby Id było to samo i w ogólnej Main Tabely Elements, jak i w tej dla każdego użytkownika.
       Jeśli skończą się pomysły to zastosować, może zapisywanie podwójnie ID - jako nazwa dokumentu i samo pole Id w każdym z osobna.
         */
        referenceTask.addOnSuccessListener(documentReference -> {
            elementsCollectionReference
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            CollectionReference userCollectionReference = reference.collection("Lista");
                            for(QueryDocumentSnapshot d: Objects.requireNonNull(task.getResult())){
                                Element element = d.toObject(Element.class);
                                userCollectionReference
                                        .add(element)
                                        .addOnFailureListener(f -> Log.d("RegisterOutsideUser", "Nie udało się zrobić pętli by dodać wszystko " + f.getMessage()));
                            }
                        } else {
                            Log.d("RegisterOutsideUser", "Nie udało się zrobić pętli by dodać wszystko " + task.getException());
                        }
                    });
        })
                .addOnFailureListener(e -> Log.d("RegisterOutsideUser", "Nie udało się dodać Użytkownika " + e.getMessage()));
    }
    public void addNewElement(Element element){
        //Element element = new Element("Cyberpunk2077", "Gra", false, "Rck&Brs");
        Date creationDate = new Date();

        DocumentReference documentElementsRef = mFirestoreElement.collection("Elements").document();
        String docNewID = documentElementsRef.getId();

        element.setId(docNewID);
        Map<String, Object> elementData = new HashMap<>();
        elementData.put("id", element.getId());
        elementData.put("title", element.getTitle());
        elementData.put("category", element.getCategory());
        elementData.put("share", element.getShare());
        elementData.put("isWatched", element.isWatched());

        Map<String, Object> newsElementData = new HashMap<>(elementData);
        newsElementData.put("state", "New");
        newsElementData.put("time", creationDate);

        DocumentReference userDocument = mFirestoreElement.collection("Users").document("osJ8vFzCZIVaSgwe8UGxjPftukh2")
                .collection("Lista").document(docNewID);
        DocumentReference newsDocument = mFirestoreElement.collection("News").document(docNewID);

        mFirestoreElement.runTransaction(transaction -> {
            transaction.set(documentElementsRef, elementData);
            transaction.set(userDocument, elementData);
            transaction.set(newsDocument, newsElementData);

            return null;
        }).addOnSuccessListener(success -> {
            Log.d("AddTransaction", "TransactionAdd Success");

        }).addOnFailureListener(error -> {
            Log.d("AddTransaction", "TransactionADD Failed = " + error);

        });
    }
    //** GetNews Original **//
    /*public MutableLiveData<List<Element>> getNews() {
        Map<String, Object> booleanMap = new HashMap<>();
        // Zwraca listę oglądanych Id i Oglądanych IsWatched danego użytkownika
        String userID = "1";
        /*Zwraca tabelę News'ów. Nowości są zbierane, albo po włączeniu aplikacji albo w tracie, poprzez(...)*/
/*
        mFirestoreElement.collection("News").addSnapshotListener((value, error) -> {
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
                });*/ /*
    }*/

    public void getNews() {
        String currentUserID = userIDLiveData.getValue();
        mFirestoreElement.collection("News").addSnapshotListener((value, error) -> {
            if(error != null){
                Log.d("Firestore", "News Listener is DEAD");
                return;
            }
            if (value != null)
                updateList(currentUserID, "NewLogin");
                Log.d("Firestore2", "News data/size => ");
        });
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
        //** Nie dziala tu userID
        String currentUserID = userIDLiveData.getValue();
        //Aktualizuje podane dane, wraz z możliwością dostoswania ilości pól, i akutalizacji daty na tą aktualną w normalnym formacie
        assert currentUserID != null;
        final DocumentReference userLogRef = mFirestoreElement.collection("Users").document(
                "osJ8vFzCZIVaSgwe8UGxjPftukh2"
        );
        Log.d("MutableUserIdLiveData", "In SetTimeLogin userLiveData is " + currentUserID);

        mFirestoreElement.runTransaction(transaction -> {
            Date actualDate = new Date();
            DocumentSnapshot snapshot = transaction.get(userLogRef);
            Date newUserLoginTime = snapshot.getDate("NewLogin");
            transaction.update(userLogRef, "LastLogin", newUserLoginTime, "NewLogin", actualDate);
            //Pamiętać o zrobieniu z tego nulla, bo i po co - return'a
            return newUserLoginTime;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "TransactionTime success ");
                    updateList(currentUserID, "NewLogin");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "Transaction failed because of " + error);
                });
    }
    public void updateList(String currentUserID, String typeTimeLogin) {
        // ** Tu tez nie dziala userID
        final DocumentReference userLogRef = mFirestoreElement.collection("Users").document(
                "osJ8vFzCZIVaSgwe8UGxjPftukh2"
        );
        final CollectionReference newsListRef = mFirestoreElement.collection("News");
        //Log.d("DatabaseSize", "Size of this db in repository is " + elementList.size());
        mFirestoreElement.runTransaction(transaction -> {
            //Date Section
            DocumentSnapshot snapshot = transaction.get(userLogRef);
            Date oldUserLoginTime = snapshot.getDate(typeTimeLogin);
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
                                updateMainList(document, element, currentUserID);
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

    public void updateMainList(QueryDocumentSnapshot document, Element element, String currentUserID){
        String fieldType = (String) document.get("state");
        String docId = document.getId();

        final DocumentReference userListDocRef = mFirestoreElement.collection("Users").document(currentUserID).collection("Lista").document(docId);
        assert fieldType != null;
        switch (fieldType) {
            case "New":
                mFirestoreElement.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(userListDocRef);

                    if (snapshot.exists()) {
                        boolean updateWatch = snapshot.getBoolean("isWatched");
                        Log.d("Boolstore", "isWatched of this element = " + updateWatch);
                        element.setWatched(updateWatch);
                    }
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
                    DocumentSnapshot snapshot = transaction.get(userListDocRef);

                    if (!snapshot.exists()){
                        transaction.set(userListDocRef, element);
                    }
                    else {
                        transaction.update(userListDocRef, "title", element.getTitle());
                        transaction.update(userListDocRef, "share", element.getShare());
                        transaction.update(userListDocRef, "category", element.getCategory());
                    }


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
    //Spradza użytkownika, nie możliwe do wykorzystania
    public void isUserExist(String userID){
        boolean isUserExists;
        mFirestoreElement.collection("Users").document(userID)
                .get()
                .addOnSuccessListener(success -> {
                    if (success.exists())
                        Log.d("UserSearch", "True");
                })
                .addOnFailureListener(failure -> {
                    Log.d("UserSearch", "False");
                });
    }
    //Niedokończone, trzeba mieć podłączony backend - repository z UI
    public void updateWatchElement(Element element){
        DocumentReference listRef = mFirestoreElement.collection("Users").document(currentUserID).collection("Lista")
                .document(element.getId());
        boolean newIsWatchedElement = element.isWatched;
       // Log.d("InMainFunctionSize", "FirestoreTest data/size => " + elementList.size());

        listRef.update("isWatched", newIsWatchedElement)
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "UpdateIsWatched success");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "UpdateIsWatched failed because of " + error);
                });
    }

    public void deleteElement(String userID, String elementId){
        //Usuwa z listy tego właśnie administratora
        final DocumentReference listRef = mFirestoreElement.collection("Users").document("osJ8vFzCZIVaSgwe8UGxjPftukh2")
                .collection("Lista")
                .document(elementId);

        //Usuwa z listy Głównej - Main Elements
        final DocumentReference mainElementsListRef = mFirestoreElement.collection("Elements").document(elementId);

        //Dodaje do tabeli News dla pozostałych User'ów o statusie User, żeby przy następnym otwarciu został ten element usunięty
        final DocumentReference newsRef = mFirestoreElement.collection("News").document(elementId);



        mFirestoreElement.runTransaction(transaction -> {
            Date creationDate = new Date();
            DocumentSnapshot snapshot = transaction.get(listRef);
            Element element = snapshot.toObject(Element.class);

            assert element != null;
            element.setState("Delete");

            HashMap<String, Object> deleteElement = new HashMap<>();
            deleteElement.put("id", element.getId());
            deleteElement.put("title", element.getTitle());
            deleteElement.put("category", element.getCategory());
            deleteElement.put("share", element.getShare());
            deleteElement.put("isWatched", element.isWatched());
            deleteElement.put("state", element.getState());
            deleteElement.put("time", creationDate);

            Log.d("DeleteTransaction", "Delete title -> " + element.getTitle());
            transaction.delete(listRef);
            transaction.delete(mainElementsListRef);
            transaction.set(newsRef, deleteElement);

            return null;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "AdminTransactionDelete success");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "AdminTransactionDelete failed because of " + error);
                });
    }

    public void editElement(Element element){
        Date creationDate = new Date();
        //Edytuje dany element z listy tego właśnie administratora
        final DocumentReference listRef = mFirestoreElement.collection("Users").document("osJ8vFzCZIVaSgwe8UGxjPftukh2")
                .collection("Lista")
                .document(element.getId());

        //Edytuje główną tabelę początkową Main Elements
        final DocumentReference maineElementsListRef = mFirestoreElement.collection("Elements")
                .document(element.getId());

        //Dodaje do tabeli News dla pozostałych User'ów o statusie User, żeby przy następnym otwarciu został ten element edytowany
        final DocumentReference newsRef = mFirestoreElement.collection("News").document(element.getId());

        HashMap<String, Object> editElement = new HashMap<>();
        editElement.put("id", element.getId());
        editElement.put("title", element.getTitle());
        editElement.put("category", element.getCategory());
        editElement.put("share", element.getShare());
       // editElement.put("isWatched", element.isWatched());

        mFirestoreElement.runTransaction(transaction -> {
            element.setState("Update");
            Log.d("DeleteTransaction", "Delete title -> " + element.getTitle());
            transaction.set(listRef, element);
            transaction.set(maineElementsListRef, element);
            editElement.put("state", element.getState());
            editElement.put("time", creationDate);
            transaction.set(newsRef, editElement);
            return null;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "AdminTransactionDelete success");
                })
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "AdminTransactionDelete failed because of " + error);
                });
    }

    public void registerInsideUser(String name, String email, String password){
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

    public MutableLiveData<Element> getRandomElement(String selectedCategory, String selectedShare) {
        final CollectionReference reference = mFirestoreElement.collection("Users")
                .document("osJ8vFzCZIVaSgwe8UGxjPftukh2")
                .collection("Lista");
        randomElementsList = new ArrayList<>();
        //New
        com.google.firebase.firestore.Query noWatchedElementsQuery = reference
                .whereEqualTo("isWatched", false)
                .whereEqualTo("category", selectedCategory)
                .whereEqualTo("share", selectedShare);

        noWatchedElementsQuery.get()
                .addOnCompleteListener(task -> {
                    try {
                        Random r = new Random();
                        int rNumber;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                Log.d("RandomRoll", " " + doc.getData());
                                Element element = doc.toObject(Element.class);
                                randomElementsList.add(element);
                                Log.d("RandomRoll", " Size = " + task.getResult().size());
                            }
                            rNumber = r.nextInt(randomElementsList.size());
                            Element element = randomElementsList.get(rNumber);
                            Log.d("RandomRoll", "Element is " + element.getTitle());

                            randomElementLiveData.postValue(element);
                        } else
                            Log.d("RandomRoll", " Rolling data roll really bad");

                } catch (Exception e){
                        Element element = new Element();
                        element.setTitle("No Elements");
                        randomElementLiveData.postValue(element);
                    }
                });
        return randomElementLiveData;
    }

    public void createFirebaseElement(Element element) {
        mReferenceElement.push().setValue(element);
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /* !!! Filtracja Listy!!! */
    public List<Element> complementationList(List<Element> elements, Filter filter) {

        List<Element> completeList;

        boolean finished = filter.isFinished();
        boolean unFinished = filter.isUnfinished();
        boolean books = filter.isBook();
        boolean games = filter.isGame();
        boolean series = filter.isSeries();
        boolean films = filter.isFilm();
        boolean rock = filter.isShareRck();
        boolean borys = filter.isShareBrs();
        boolean rockBorys = filter.isShareRckBrs();
        boolean others = filter.isShareOther();

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


    //*************Filtracja pobór****************
    public MutableLiveData<Filter> getUserFilter(String userID){
        final DocumentReference listRef = mFirestoreElement.collection("Users").document(userID)
                .collection("Preferencje")
                .document("Filters");

        listRef.addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.d("MainGetFilter", "Dead filters");
                return;
            }

            if (value != null && value.exists()){
                Filter filter = value.toObject(Filter.class);
                filtersLiveData.postValue(filter);
                Log.d("MainGetFilter", "Working FIlters");

            } else {
                Log.d("MainGetFilter", "Dead filters");
            }
        });

        return filtersLiveData;
    }

    public void setUserFilters(Filter filters)
    {
        String currentUserID = userIDLiveData.getValue();

        Log.d("Firestore", "userFilter" + currentUserID);

       final DocumentReference listRef = mFirestoreElement.collection("Users").document("osJ8vFzCZIVaSgwe8UGxjPftukh2")
               .collection("Preferencje")
                .document("Filters");

        Map<String, Object> userDataFilters = new HashMap<>();
        userDataFilters.put("isFinished", filters.isFinished());
        userDataFilters.put("isUnfinished", filters.isUnfinished());
        userDataFilters.put("isBook", filters.isBook());
        userDataFilters.put("isFilm", filters.isFilm());
        userDataFilters.put("isGame", filters.isGame());
        userDataFilters.put("isSeries", filters.isSeries());
        userDataFilters.put("isShareRck", filters.isShareRck());
        userDataFilters.put("isShareBrs", filters.isShareBrs());
        userDataFilters.put("isShareRckBrs", filters.isShareRckBrs());
        userDataFilters.put("isShareOther", filters.isShareOther());

        listRef
                .update(userDataFilters)
                .addOnSuccessListener(success -> {
                    Log.d("FilterSet", "FilterSet success");

                })
                .addOnFailureListener(error -> {
                    Log.d("FilterSet", "FilterSet failed " + error);
                });
    }
    //*************END of Filter Section*********

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
