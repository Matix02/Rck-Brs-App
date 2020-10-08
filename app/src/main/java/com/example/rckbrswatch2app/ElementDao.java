package com.example.rckbrswatch2app;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

/*
Zastosować opcję z Medium.com, gdzie są podane zasady optymalizacji Room'a.
Unikanie kopiowanych metod jak i przykład z dokumentacji Androida - paging
Ad.Kopiowanie metod, to gdzie jest zwróć całą listę jest tym samym - skrócić, nie powielać.
 */
@Dao
public interface ElementDao {

    @Insert
    long addElement(Element element);

    @Update
    void updateElement(Element element);

    @Delete
    void deleteElement(Element element);

    @Query("UPDATE Element\n" +
            "SET " +
            "title =:title\n" +
            "WHERE id =:Id")
    void updateBlankElement(long Id, String title);

    @Query("select * from Element")
    Flowable<List<Element>> getElements();


    @Query("SELECT * \n" +
            "FROM Element\n" +
            "ORDER BY id\n" +
            "DESC LIMIT 1")
    int getLastIndex();
    /*////////////////////////////////////////////////////
    // OFFLINE //
    ////////////////////////////////////////////////////
    @Query("UPDATE Element\n" +
            "SET " +
            "title =:title,\n" +
            "share =:recommendation,\n" +
            "category =:cat\n" +
            "WHERE id =:Id")
    void updateElementById(long Id, String title, String recommendation, String cat);
        @Query("delete from Element")
    void deleteAllElements();

    @Query("UPDATE Element\n" +
            "SET isWatched =:resultWatch\n" +
            "WHERE id =:Id")
    void updateWatchElementById(long Id, boolean resultWatch);

    @Query("SELECT *\n" +
            "FROM Element\n" +
            "WHERE category =:lookCategory\n")
    List<Element> randomListElement(String lookCategory);

    @Query("delete from Element where id ==:elementId")
    void deleteIdElement(int elementId);

    @Query("select * from Element where id ==:elementId")
    Element getElement(long elementId);

    @Query("UPDATE Element\n" +
            "SET id =:actualID\n" +
            "WHERE ID =:oldId ")
    void updateID(int actualID, int oldId);


    @Query("SELECT * \n" +
            "FROM Element \n" +
            "WHERE isWatched = 0\n" +
            "ORDER BY RANDOM()\n" +
            "LIMIT 1")
    Single<Element> getNoWatchedRandomElement();

    @Query("SELECT *\n" +
            "FROM Element\n" +
            "WHERE isWatched = 0\n" +
            "AND category =:categoryName\n" +
            "ORDER BY RANDOM()\n" +
            "LIMIT 1")
    Single<Element> getNoWatchedRandomElementByCategory(String categoryName);*/
}
