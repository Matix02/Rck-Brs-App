package com.example.rckbrswatch2app.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.rckbrswatch2app.Repository.FirebaseRepository;
import com.example.rckbrswatch2app.Model.Element;
import com.example.rckbrswatch2app.Model.Filter;
import com.example.rckbrswatch2app.Model.User;

import java.util.List;

public class ElementViewModel extends AndroidViewModel {


    FirebaseRepository firebaseRepository;

    public ElementViewModel(@NonNull Application application) {
        super(application);
        firebaseRepository = new FirebaseRepository();
    }

    public LiveData<List<Element>> readFirestore (String userID, Filter filter){ return firebaseRepository.readFirestoreElements(userID, filter); }
    public void getNewsCollection(String userID){ firebaseRepository.getNews(userID); }

    public void registerUserOutside(User user){ firebaseRepository.registerOutsideUser(user); }
    public void singOut() { firebaseRepository.signOut(); }

    public void setActiveUserLogin(String userID) { firebaseRepository.setTimeLogin(userID); }

    public void addElement(Element element) { firebaseRepository.addNewElement(element); }
    public void updateWatchedElement(String userID, Element element){ firebaseRepository.updateWatchElement(userID, element);}
    public void deleteElement(String userID, String elementID) { firebaseRepository.deleteElement(userID, elementID);}
    public void editElement(String userID, Element element) { firebaseRepository.editElement(userID, element); }

    public LiveData<Element> getRandomElement(String userID, String category, String share){ return firebaseRepository.getRandomElement(userID, category, share); }

    public LiveData<Filter> getUserFilters(String userID){ return firebaseRepository.getUserFilter(userID); }
    public void setFilters(String userID, Filter filters) { firebaseRepository.setUserFilters(userID, filters); }


    public void checkUserState() { firebaseRepository.isUserRegistered("awdawd");}
}
