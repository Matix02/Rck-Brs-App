package com.example.rckbrswatch2app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Element")
public class Element {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String share;
    private String title;
    private String category;
    private boolean isWatched;

    Element() {}

    public Element(String title, String category, boolean isWatched, String share) {
        this.title = title;
        this.category = category;
        this.isWatched = isWatched;
        this.share = share;
    }

    public Element(long id, String title, String category, boolean isWatched, String share) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.isWatched = isWatched;
        this.share = share;
    }


    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    public String getShare() { return share; }

    public void setShare(String recom) { this.share = recom; }
}


