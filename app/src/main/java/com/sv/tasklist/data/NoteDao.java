package com.sv.tasklist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sv.tasklist.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    // Удаляем заметку
    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    // Получаем все заметки
    @Query("SELECT * FROM Note")
    List<Note> getAll();

    @Query("SELECT * FROM Note")
    LiveData<List<Note>> getAllLiveData();

    @Query("SELECT * FROM Note WHERE uid = :uid LIMIT 1")
    Note findById(int uid);
}
