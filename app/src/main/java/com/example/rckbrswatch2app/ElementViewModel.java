package com.example.rckbrswatch2app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ElementViewModel extends AndroidViewModel {

    private ElementRoomRepository elementRoomRepository;
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();

    //Start mojego szajsu
    FirebaseRepository firebaseRepository;
    private MutableLiveData<List<Element>> firebaseLiveData = new MutableLiveData<>();

    //Koniec Mojego szajus
    public ElementViewModel(@NonNull Application application) {
        super(application);
     //   elementRoomRepository = new ElementRoomRepository(application);
       // elementLiveData = elementRoomRepository.getElementLiveData();
        //Start mojego szajsu
        firebaseRepository = new FirebaseRepository();
    }

  //  public LiveData<List<Element>> getFirebaseElements(){ return firebaseRepository.readFirebaseElements(); }
    public void createFirebaseElement(Element element){
        firebaseRepository.createFirebaseElement(element);
    }

    public LiveData<List<Element>> getAllElements() { return elementLiveData; }

  /*  public void createElement(int id, String title, String category, String share, boolean isWatched) {
        elementRoomRepository.createElement(id, title, category, share, isWatched);
    }*/
    //*********************FirestoreOperations //////////////*******************
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

    public void addElement(String userID) { firebaseRepository.addNewElement(userID); }
    public void updateWatchedElement(Element element){ firebaseRepository.updateWatchElement(element);}
    public void deleteElement(String userID, String elementID) { firebaseRepository.deleteElement(userID, elementID);}
    //*********************FirestoreOperations //////////////*******************

    public LiveData<List<Element>> filterElement(List<Element> elements, boolean gamestate)
    {
        Log.d("Bufor", "Filter !!!");
        return elementRoomRepository.filterList(elements, gamestate);
    }


    public LiveData<List<Element>> updateList(List<Element> elements){ return  elementRoomRepository.equalData(elements); }

    public void createElement(Element element) {
        elementRoomRepository.createElement(element);
    }

    public void updateElement(Element element) {
        elementRoomRepository.updateElement(element);
    }

  /*  public void deleteElement(Element element) {
        elementRoomRepository.deleteElement(element);
    }*/

    public void deleteAllElements(){ elementRoomRepository.deleteAllElements();}

    public void clear() {
        elementRoomRepository.clear();
    }

    public void updateTrigger(long id, String title) { elementRoomRepository.updateElement(id, title); }
}
