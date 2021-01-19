package com.sv.tasklist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.sv.tasklist.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertAll(Note... notes);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM note")
    List<Note> getAll();

    @Query("SELECT * FROM note")
    LiveData<List<Note>> getAllLiveData();
}
