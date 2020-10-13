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

public class FirebaseRepository {
    private DatabaseReference mReferenceElement;
    private List<Element> elementList;
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();

    public FirebaseRepository() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mReferenceElement = mDatabase.getReference("Elements");
    }

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
                }
                Log.d("Xkanapka", "Size = " + elementList.size());
                elementLiveData.postValue(elementList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return elementLiveData;
    }

    public void createFirebaseElement(Element element){
        mReferenceElement.push().setValue(element);
    }
}
