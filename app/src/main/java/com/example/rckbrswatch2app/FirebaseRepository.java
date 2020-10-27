package com.example.rckbrswatch2app;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;
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

    public FirebaseRepository() {
        mFirestoreElement = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Element>> readFirestoreElements(){
        elementList = new ArrayList<>();
        mFirestoreElement.collection("Elements")
                .whereEqualTo("category", "serial")
                .whereEqualTo("share", "Rock")
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()))
                        {
                            Log.d("Firestore2", "Firestore data => " + documentSnapshot.getData());
                            elementList.add(documentSnapshot.toObject(Element.class));
                        }
                        Log.d("Firestore2", "Firestore data/size => " + elementList.size());

                        elementLiveData.postValue(elementList);

                    } else {
                        Log.d("Firesotre2", "! Firestore error = " + task.getException());
                    }
                }
                );
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


    public void createFirebaseElement(Element element){ mReferenceElement.push().setValue(element); }
}
