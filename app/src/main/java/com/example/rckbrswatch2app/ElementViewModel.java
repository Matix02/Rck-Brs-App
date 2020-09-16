package com.example.rckbrswatch2app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ElementViewModel extends AndroidViewModel {

    private ElementRoomRepository elementRoomRepository;
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();

    public ElementViewModel(@NonNull Application application) {
        super(application);
        elementRoomRepository = new ElementRoomRepository(application);
        elementLiveData = elementRoomRepository.getElementLiveData();
    }

    public LiveData<List<Element>> getAllElements() { return elementLiveData; }

    public void createElement(int id, String title, String category, String share, boolean isWatched) {
        elementRoomRepository.createElement(id, title, category, share, isWatched);
    }

    public void createElement(Element element) {
        elementRoomRepository.createElement(element);
    }

    public void updateElement(Element element) {
        elementRoomRepository.updateElement(element);
    }

    public void deleteElement(Element element) {
        elementRoomRepository.deleteElement(element);
    }

    public void clear() {
        elementRoomRepository.clear();
    }
}
