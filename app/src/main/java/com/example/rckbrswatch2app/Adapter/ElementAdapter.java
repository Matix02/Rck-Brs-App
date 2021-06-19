package com.example.rckbrswatch2app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rckbrswatch2app.MainActivity;
import com.example.rckbrswatch2app.Model.Element;
import com.example.rckbrswatch2app.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementHolder> implements Filterable {

    private List<Element> elementList = new ArrayList<>();
    private List<Element> elementListAll;
    private OnItemClickListener listener;

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
       this.elementListAll = new ArrayList<>(elementList);
       notifyDataSetChanged();
    }

    public Element getElementAt(int position){
        return elementList.get(position);
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Element> fitleredList = new ArrayList<>();
            if (constraint.toString().isEmpty())
                fitleredList.addAll(elementListAll);
            else {
                for (Element e: elementListAll){
                    if(e.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
                        fitleredList.add(e);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = fitleredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            elementList.clear();
            elementList.addAll((Collection<? extends Element>) results.values);
            notifyDataSetChanged();
        }
    };

    class ElementHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTitle;
        private final TextView textViewCategory;
        private final CheckBox checkBoxWatch;
        private final TextView textViewShare;

        public ElementHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleTV);
            textViewCategory = itemView.findViewById(R.id.categoryTV);
            checkBoxWatch = itemView.findViewById(R.id.watchCB);
            textViewShare = itemView.findViewById(R.id.shareTV);

            checkBoxWatch.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Element element = elementList.get(position);
                element.setWatched(!element.isWatched());
                String userID = mainActivity.userID;
                mainActivity.elementViewModel.updateWatchedElement(userID, element);
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(elementList.get(position));
                }
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
