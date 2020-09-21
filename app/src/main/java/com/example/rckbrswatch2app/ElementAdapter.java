package com.example.rckbrswatch2app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementHolder> {

    private List<Element> elementList = new ArrayList<>();


    //Mój szajs
    int randomWithMathRandom ;
    SplittableRandom splittableRandom = new SplittableRandom();
    private List<Element> buforRxList = new ArrayList<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<Element> buforRxList2 = new ArrayList<>();

    @NonNull
    @Override
    public ElementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_item, parent, false);
        return new ElementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementHolder holder, int position) {
        Element element = elementList.get(position);
        holder.textViewTitle.setText(element.getTitle());
        holder.textViewCategory.setText(element.getCategory());
        holder.checkBoxWatch.setChecked(element.isWatched());
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public void setElementList(List<Element> elementList){
       this.elementList = elementList;
       notifyDataSetChanged();
    }

    public void setElementFilterList(List<Element> elementList, boolean gameState){
        this.elementList = filterNormalList(elementList, gameState);
        Log.d("Bufor", "ElementList from NormalList " + elementList.size() + " size");

        notifyDataSetChanged();
    }
    public void setElementList(List<Element> elementList, boolean gameState){
        //buforRxList2.addAll(filterRxList(elementList, gameState));
        Single<List<Element>> listSingle = filterRxList(elementList, gameState);
        compositeDisposable.add(filterRxList(elementList, gameState).subscribe(elements -> {
            buforRxList2.addAll(elements);
                    Log.d("Bufor", "Successfully in RxFilterList2 subscribe and " + buforRxList2.size() + " size");
                }
        ));
        Log.d("Bufor", "Successfully in RxFilterList2 and " + buforRxList2.size() + " size");
//Jak się nie znajdzie rozwiązanie to zrobić to prosta funkcją, czyli tak jak na początku tutaj
        this.elementList = buforRxList2;
        notifyDataSetChanged();
    }
    //Zastosować motyw z lista zwracaną, jak w jednym z przykładów w NotePadzie++, i spróbować użyc RXJava, toObservable lub fromArray czy coś i jechane

    public List<Element> filterList(List<Element> elements){
        List<Element> buforList = new ArrayList<>();
            randomWithMathRandom = splittableRandom.nextInt(1,3);
        Log.d("Bufor", "Random = " + randomWithMathRandom);
        for(Element e: elements){
            if(e.getCategory().equals("Film")&&randomWithMathRandom==1)
                buforList.add(e);
            else if (e.getCategory().equals("Gra")&&randomWithMathRandom==2)
                buforList.add(e);
        }
        return buforList;
    }

    public List<Element> filterNormalList(List<Element> elements, boolean gameState){
        Log.d("Bufor", "ElementList from NormalListFunction before start filtering " + elements.size() + " size");
        if(gameState)
            return elements.stream().filter(element -> element.getCategory().equals("Gra")).collect(Collectors.toList());
        else return elements;
    }

    public Single<List<Element>> filterRxList(List<Element> elements, boolean wannaGame){
        return Observable.fromIterable(elements)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(element -> {
                  //  if (wannaGame && element.getCategory().equals("Gra"))
                        return true;
                  //  else return !wannaGame && element.getCategory().equals("Film");
                }).toList();
       // Log.d("Bufor", "RxFilterList/End and " + buforRxList.size() + " size");
       // return buforRxList;
    }
    public void clear() { compositeDisposable.clear(); }


    static class ElementHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewCategory;
        private CheckBox checkBoxWatch;

        public ElementHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleTV);
            textViewCategory = itemView.findViewById(R.id.categoryTV);
            checkBoxWatch = itemView.findViewById(R.id.watchCB);

        }
    }
}
