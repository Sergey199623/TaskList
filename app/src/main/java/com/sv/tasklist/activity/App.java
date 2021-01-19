package com.sv.tasklist.activity;

import android.app.Application;

import androidx.room.Room;

import com.sv.tasklist.data.AppDatabase;
import com.sv.tasklist.data.NoteDao;

public class App extends Application {

    private AppDatabase database;
    private NoteDao noteDao;

    private static App instance;

    // SingleTon
    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                "notes-db")
        .allowMainThreadQueries()
        .build();

        noteDao = database.noteDao();
    }

}