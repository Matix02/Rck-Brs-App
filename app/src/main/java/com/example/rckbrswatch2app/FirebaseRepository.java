package com.example.rckbrswatch2app;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;


public class FirebaseRepository {
    private DatabaseReference mReferenceElement;
    private List<Element> elementList;
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable=new CompositeDisposable();


    public FirebaseRepository() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mReferenceElement = mDatabase.getReference("Element");

    }

    //Sprobówać ogarnąć RxFirebase'a
    public MutableLiveData<List<Element>> readFirebaseElements(){
        elementList = new ArrayList<>();
        mReferenceElement.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                elementList.clear();
                for(DataSnapshot keyNode : snapshot.getChildren())
                {
                    Element element = keyNode.getValue(Element.class);
                    elementList.add(element);
                    Log.d("Xkanapka", "in foreach");
                }
                Log.d("Xkanapka", "Size = " + elementList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       /* compositeDisposable.add(RxFirebaseDatabase.dataChanges(mReferenceElement)
        .subscribe(dataSnapshot -> {
            if (dataSnapshot.exists()){
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    Element element = d.getValue(Element.class);
                    elementList.add(element);
                    Log.d("Bufor", "Firebase onNext - "+d.getValue());
                }
            }
            Log.d("Bufor", "Firebase size List - "+ elementList.size());
            elementLiveData.postValue(elementList);
        }, throwable -> Log.d("Bufor", "Firebase ReadDatabase Error - "+throwable.getMessage()))
        );*/
        return elementLiveData;
    }

    public void createFirebaseElement( Element element){
        Element element1 = new Element(5, "Dsajd", "dasdas", true, "asdasd");
        String id = mReferenceElement.getKey();

     //   Log.d("Bufor", "Element is = " +id );
        //takie mech
        element1.setTitle("asdasdasd");
        mReferenceElement.child(String.valueOf(250)).setValue(element1);


    }
}
