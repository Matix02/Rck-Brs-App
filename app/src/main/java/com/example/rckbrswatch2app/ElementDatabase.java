package com.example.rckbrswatch2app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;


@Database(entities = {Element.class}, version = 2)
public abstract class ElementDatabase extends RoomDatabase {

    private static ElementDatabase instace;
    public abstract ElementDao getElementDao();

    public static synchronized ElementDatabase getInstance(Context context){
        if (instace == null){
            instace = Room.databaseBuilder(context.getApplicationContext(),
                    ElementDatabase.class, "ElementDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instace;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    instace.getElementDao().addElement(new Element("title", "Film", false, "Rock"));
                    Log.d("Bufor", "Weszło from ElementDatabase - Callback");
                }
            });
        }
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    static Migration migration = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Element' ADD COLUMN 'recom' TEXT DEFAULT ''");
        }
    };
}
