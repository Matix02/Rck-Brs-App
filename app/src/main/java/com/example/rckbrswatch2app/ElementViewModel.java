package com.example.rckbrswatch2app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ElementViewModel extends AndroidViewModel {


    FirebaseRepository firebaseRepository;
    private MutableLiveData<List<Element>> firebaseLiveData = new MutableLiveData<>();


    public ElementViewModel(@NonNull Application application) {
        super(application);
        firebaseRepository = new FirebaseRepository();
    }
    //*********************FirestoreOperations //////////////*******************
    public void createFirebaseElement(Element element){
        firebaseRepository.createFirebaseElement(element);
    }

    public LiveData<List<Boolean>> readFavDocumentFirestore (){ return  firebaseRepository.readUserFavElementsDocument(); }
    public LiveData<List<Element>> readFirestore (String userID){ return firebaseRepository.readFirestoreElements(userID); }
    public LiveData<List<Element>> getNewsCollection(){ return firebaseRepository.getNews(); }

    public void registerUserOutside(User user){ firebaseRepository.registerOutsideUser(user); }
    public void getNews(){ firebaseRepository.getNews(); }

    public void getFilterDataNews() { firebaseRepository.filterNews(); }
    public void getLastnNewLogin() { firebaseRepository.getDate(); }

    public void setActiveUserLogin() { firebaseRepository.setTimeLogin(); }
    public void singOut() { firebaseRepository.signOut(); }
    public void checkUser(String userID) { firebaseRepository.isUserExist(userID);}

    public void addElement(Element element) { firebaseRepository.addNewElement(element); }
    public void updateWatchedElement(Element element){ firebaseRepository.updateWatchElement(element);}
    public void deleteElement(String userID, String elementID) { firebaseRepository.deleteElement(userID, elementID);}

    public LiveData<Element> getRandomElement(String category, String share){ return firebaseRepository.getRandomElement(category, share); }

    public void setFilters(Filter filters) { firebaseRepository.setUserFilters(filters); }

    public void editElement(Element element) { firebaseRepository.editElement(element); }

    public LiveData<Filter> getUserFilters(){ return firebaseRepository.getUserFilter(); }
    //*********************FirestoreOperations //////////////*******************
}
