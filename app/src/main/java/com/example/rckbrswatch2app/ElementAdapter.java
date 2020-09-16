package com.example.rckbrswatch2app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementHolder> {

    private List<Element> elementList = new ArrayList<>();

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
