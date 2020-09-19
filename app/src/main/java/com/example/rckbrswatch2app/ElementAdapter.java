package com.example.rckbrswatch2app;

import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementHolder> {

    private List<Element> elementList = new ArrayList<>();
    private List<Element> buforList = new ArrayList<>();

    //Mój szajs
    int randomWithMathRandom ;
    SplittableRandom splittableRandom = new SplittableRandom();


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


        this.elementList = filterList(elementList);


        notifyDataSetChanged();
    }
    //Zastosować motyw z lista zwracaną, jak w jednym z przykładów w NotePadzie++, i spróbować użyc RXJava, toObservable lub fromArray czy coś i jechane

    public List<Element> filterList(List<Element> elements){
        buforList = new ArrayList<>();
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
