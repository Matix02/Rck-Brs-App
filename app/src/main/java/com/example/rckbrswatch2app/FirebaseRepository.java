package com.example.rckbrswatch2app;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.rckbrswatch2app.Model.Element;
import com.example.rckbrswatch2app.Model.Filter;
import com.example.rckbrswatch2app.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class FirebaseRepository {
    private List<Element> elementList;
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();
    private FirebaseFirestore mFirestoreElement;
    //Filters GetData
    private MutableLiveData<Filter> filtersLiveData = new MutableLiveData<>();
    private MutableLiveData<Element> randomElementLiveData = new MutableLiveData<>();
    private List<Element> randomElementsList;

    public FirebaseRepository() {
        mFirestoreElement = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Element>> readFirestoreElements(String userID, Filter filter){

        mFirestoreElement.collection("Users").document(userID).collection("Lista")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d("ReadFirebase", "ObserveElement has lost a mind" + error);
                        return;
                    }
                    elementList = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(value)) {
                        Log.d("Firestore2", "FirestoreTest data => " + documentSnapshot.getData());
                        Element element = documentSnapshot.toObject(Element.class);
                        elementList.add(element);
                    }

                    List<Element> sharedList = new ArrayList<>(elementList);
                    sharedList = complementationList(sharedList, filter);
                    elementLiveData.postValue(sharedList);
                });

        return elementLiveData;
    }

    //From Google
    public void registerOutsideUser(User newUser){
        String newUserID = newUser.getUserID();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", newUser.getEmail());
        userData.put("name", newUser.getDisplayName());
        userData.put("password", newUser.getPassword());
        userData.put("NewLogin", FieldValue.serverTimestamp());
        userData.put("LastLogin", FieldValue.serverTimestamp());

        Map<String, Object> userDataFilters = new HashMap<>();
        userDataFilters.put("isFinished", true);
        userDataFilters.put("isUnfinished", true);
        userDataFilters.put("isBook", true);
        userDataFilters.put("isFilm", true);
        userDataFilters.put("isGame", true);
        userDataFilters.put("isSeries", true);
        userDataFilters.put("isShareRck", true);
        userDataFilters.put("isShareBrs", true);
        userDataFilters.put("isShareRckBrs", true);
        userDataFilters.put("isShareOther", true);

        DocumentReference reference = mFirestoreElement.collection("Users").document(newUserID);
        CollectionReference elementsCollectionReference = mFirestoreElement.collection("Elements");

        Task<Void> referenceTask = reference.set(userData);
        referenceTask.addOnSuccessListener(documentReference -> elementsCollectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        CollectionReference userCollectionReference = reference.collection("Lista");
                        for(QueryDocumentSnapshot d: Objects.requireNonNull(task.getResult())){
                            Element element = d.toObject(Element.class);
                            userCollectionReference.document(element.getId())
                                    .set(element)
                                    .addOnFailureListener(f -> Log.d("RegisterOutsideUser", "Nie udało się zrobić pętli by dodać wszystko " + f.getMessage()));
                        }
                       reference.collection("Preferencje").document("Filters").set(userDataFilters);

                    } else {
                        Log.d("RegisterOutsideUser", "Nie udało się zrobić pętli by dodać wszystko " + task.getException());
                    }
                }))
                .addOnFailureListener(e -> Log.d("RegisterOutsideUser", "Nie udało się dodać Użytkownika " + e.getMessage()));
    }

    public void addNewElement(Element element){
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
        }).addOnSuccessListener(success -> Log.d("AddTransaction", "TransactionAdd Success"))
                .addOnFailureListener(error -> Log.d("AddTransaction", "TransactionADD Failed = " + error));
    }

    public void getNews(String userID) {
        mFirestoreElement.collection("News").addSnapshotListener((value, error) -> {
            if(error != null){
                Log.d("Firestore", "News Listener is DEAD");
                return;
            }
            if (value != null)
                updateList(userID, "NewLogin");
        });
    }


    public void setTimeLogin(String userID){
        //Aktualizuje podane dane, wraz z możliwością dostoswania ilości pól, i akutalizacji daty na tą aktualną w normalnym formacie
        final DocumentReference userLogRef = mFirestoreElement.collection("Users").document(userID);
        Log.d("MutableUserIdLiveData", "In SetTimeLogin userLiveData is " + userID);

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
                    updateList(userID, "NewLogin");
                })
                .addOnFailureListener(error -> Log.d("Firestore", "Transaction failed because of " + error));
    }
    public void updateList(String currentUserID, String typeTimeLogin) {

        final DocumentReference userLogRef = mFirestoreElement.collection("Users").document(currentUserID);
        final CollectionReference newsListRef = mFirestoreElement.collection("News");

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
                        }}
                        else
                            Log.d("FilterStore", "TransactionNewsData - NewsDataTable error");
                    });
            return null;
        })
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "TransactionList success");
                })
                .addOnFailureListener(error -> Log.d("Firestore", "Transaction failed because of " + error));
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

                    if (!snapshot.exists()) {
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

    public void updateWatchElement(String userID, Element element){
        DocumentReference listRef = mFirestoreElement.collection("Users").document(userID).collection("Lista")
                .document(element.getId());
        boolean newIsWatchedElement = element.isWatched;

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
        final DocumentReference listRef = mFirestoreElement.collection("Users").document(userID)
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

    public void editElement(String userID, Element element){
        Date creationDate = new Date();
        //Edytuje dany element z listy tego właśnie administratora
        final DocumentReference listRef = mFirestoreElement.collection("Users").document(userID)
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
            editElement.put("isWatched", element.isWatched());
            transaction.set(listRef, editElement);
            editElement.remove("isWatched");
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
                .addOnSuccessListener(command -> Log.d("Firestore", "Registration successfully"))
                .addOnFailureListener(error -> {
                    Log.d("Firestore", "Registration failed because of " + error);
                });
    }

    public MutableLiveData<Element> getRandomElement(String userID, String selectedCategory, String selectedShare) {
        final CollectionReference reference = mFirestoreElement.collection("Users")
                .document(userID)
                .collection("Lista");
        randomElementsList = new ArrayList<>();
        com.google.firebase.firestore.Query noWatchedElementsQuery = reference
                .whereEqualTo("isWatched", false);
        //New
        if (!selectedCategory.equals("Wszystko")) {
            noWatchedElementsQuery = noWatchedElementsQuery.whereEqualTo("category", selectedCategory);
        }
        if (!selectedShare.equals("Wszystko")) {
            noWatchedElementsQuery = noWatchedElementsQuery.whereEqualTo("share", selectedShare);
        }

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
                        element.setTitle("Nie znaleziono");
                        randomElementLiveData.postValue(element);
                    }
                });
        return randomElementLiveData;
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

    public void setUserFilters(String userID, Filter filters)
    {
        final DocumentReference listRef = mFirestoreElement.collection("Users").document(userID)
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

        listRef.update(userDataFilters)
                .addOnSuccessListener(success -> {
                    Log.d("FilterSet", "FilterSet success");

                })
                .addOnFailureListener(error -> {
                    Log.d("FilterSet", "FilterSet failed " + error);
                });
    }
}
