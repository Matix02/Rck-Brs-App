package com.example.rckbrswatch2app;

public class Filter {
    //MainFilter
    public boolean isFinished;
    public boolean isUnfinished;
    //CategoryFiler
    public boolean isFilm;
    public boolean isGame;
    public boolean isBook;
    public boolean isSeries;
    //ShareFilter
    public boolean isShareRck;
    public boolean isShareBrs;
    public boolean isShareRckBrs;
    public boolean isShareOther;

    public Filter(boolean isFinished, boolean isUnfinished,
                  boolean isFilm, boolean isGame, boolean isBook, boolean isSeries,
                  boolean isShareRck, boolean isShareBrs, boolean isShareRckBrs, boolean isShareOther) {
        this.isFinished = isFinished;
        this.isUnfinished = isUnfinished;
        this.isFilm = isFilm;
        this.isGame = isGame;
        this.isBook = isBook;
        this.isSeries = isSeries;
        this.isShareRck = isShareRck;
        this.isShareBrs = isShareBrs;
        this.isShareRckBrs = isShareRckBrs;
        this.isShareOther = isShareOther;
    }

    public Filter() { }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean isUnfinished() {
        return isUnfinished;
    }

    public void setUnfinished(boolean unfinished) {
        isUnfinished = unfinished;
    }

    public boolean isFilm() {
        return isFilm;
    }

    public void setFilm(boolean film) {
        isFilm = film;
    }

    public boolean isGame() {
        return isGame;
    }

    public void setGame(boolean game) {
        isGame = game;
    }

    public boolean isBook() {
        return isBook;
    }

    public void setBook(boolean book) {
        isBook = book;
    }

    public boolean isSeries() {
        return isSeries;
    }

    public void setSeries(boolean series) {
        isSeries = series;
    }

    public boolean isShareRck() {
        return isShareRck;
    }

    public void setShareRck(boolean shareRck) {
        isShareRck = shareRck;
    }

    public boolean isShareBrs() {
        return isShareBrs;
    }

    public void setShareBrs(boolean shareBrs) {
        isShareBrs = shareBrs;
    }

    public boolean isShareRckBrs() {
        return isShareRckBrs;
    }

    public void setShareRckBrs(boolean shareRckBrs) {
        isShareRckBrs = shareRckBrs;
    }

    public boolean isShareOther() {
        return isShareOther;
    }

    public void setShareOther(boolean shareOther) {
        isShareOther = shareOther;
    }
}
