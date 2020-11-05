package com.example.rckbrswatch2app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementHolder> {

    private List<Element> elementList = new ArrayList<>();
    private OnItemClickListener listener;
    //Mój szajs
    MainActivity mainActivity;

    public ElementAdapter(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

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
        holder.textViewShare.setText(element.getShare());
    }
    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public void setElementList(List<Element> elementList){
       this.elementList = elementList;
       notifyDataSetChanged();
    }

    public Element getElementAt(int position){
        return elementList.get(position);
    }

    class ElementHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewCategory;
        private CheckBox checkBoxWatch;
        private TextView textViewShare;

        public ElementHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleTV);
            textViewCategory = itemView.findViewById(R.id.categoryTV);
            checkBoxWatch = itemView.findViewById(R.id.watchCB);
            textViewShare = itemView.findViewById(R.id.shareTV);

            checkBoxWatch.setOnClickListener(v -> {
                int position = getAdapterPosition();
                    Element element = elementList.get(position);
                    Log.d("Bufor", "ONCHECKBOX before is " + element.isWatched());

                    element.setWatched(!element.isWatched());
                    mainActivity.elementViewModel.updateElement(element);
                    Log.d("Bufor", "ONCHECKBOX after is " + element.isWatched());

            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(Element element);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
