package com.sv.tasklist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sv.tasklist.activity.App;
import com.sv.tasklist.model.Note;

import java.util.List;

public class MainViewModel extends ViewModel {

    private LiveData<List<Note>> noteLiveDate = App.getInstance().getNoteDao().getAllLiveData();

    public LiveData<List<Note>> getNoteLiveDate() {
        return noteLiveDate;
    }
}
