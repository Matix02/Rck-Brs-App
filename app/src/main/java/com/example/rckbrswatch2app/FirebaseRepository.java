package com.example.rckbrswatch2app;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;

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
        compositeDisposable.add(RxFirebaseDatabase.dataChanges(mReferenceElement)
        .subscribeWith(new DisposableObserver<DataSnapshot>() {
            @Override
            public void onNext(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Element element = ds.getValue(Element.class);
                    Log.d("Bufor", "" + ds.getValue(Element.class));

                }

             //   elementList.add(dataSnapshot.getValue(Element.class));
                Log.d("Bufor", "FIREBASE READ ELEMENTS/onNext elementList ");

            }
            @Override
            public void onError(Throwable e) {

            }
            @Override
            public void onComplete() {
                Log.d("Bufor", "FIREBASE READ ELEMENTS/onComplete elementList size " + elementList.size());
               // elementLiveData.postValue(elementList);
            }
        })
        );
        return elementLiveData;
    }


}
