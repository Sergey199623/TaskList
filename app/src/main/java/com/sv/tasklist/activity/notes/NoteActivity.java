package com.sv.tasklist.activity.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sv.tasklist.R;
import com.sv.tasklist.activity.AlarmReceiver;
import com.sv.tasklist.activity.App;
import com.sv.tasklist.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NoteActivity.EXTRA_NOTE";

    private Note note;
    private EditText etTitle;
    private EditText etDesc;
    private Button btnDate;

    int DIALOG_DATE = 1;
    int myYear = 2021;
    int myMonth = 1;
    int myDay = 1;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd-MM-yyyy");

    public static void start(Activity caller, Note note) {
        Intent intent = new Intent (caller, NoteActivity.class);
        if (note != null) {
            intent.putExtra(EXTRA_NOTE, note);
        }
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbarNote);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(getString(R.string.note_create));

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        btnDate = findViewById(R.id.btnDate);
        btnDate.setBackgroundColor(Color.DKGRAY);

        if (getIntent().hasExtra(EXTRA_NOTE)) {
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
            etTitle.setText(note.title);
            etTitle.setText(note.text);
        } else {
            note = new Note();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                if ((etDesc.getText().length() > 0) && (etTitle.getText().length() > 0)) {
                    note.title = etTitle.getText().toString();
                    note.text = etDesc.getText().toString();
                    note.done = false;
                    note.date = sdf.format(Calendar.getInstance().getTime());
                    note.timestamp = System.currentTimeMillis();


                    if (getIntent().hasExtra(EXTRA_NOTE)) {
                        App.getInstance().getNoteDao().update(note);
                    } else {
                        App.getInstance().getNoteDao().insert(note);
                    }
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onChangeDate(View view) {
        showDialog(DIALOG_DATE);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            btnDate.setText("Задана дата " + myDay + "/" + myMonth + "/" + myYear);
        }
    };
}
