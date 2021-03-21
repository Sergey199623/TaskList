package com.sv.tasklist.activity.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.sv.tasklist.R;
import com.sv.tasklist.activity.App;
import com.sv.tasklist.activity.Loggi;
import com.sv.tasklist.model.Note;
import com.sv.tasklist.notification.MyNotificationPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NoteActivity.EXTRA_NOTE";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    final Calendar myCalendar = Calendar.getInstance();

    private Note note;
    private EditText etTitle;
    private EditText etDesc;
    private CheckBox cbNotification;
    private Button btnDate;

    int DIALOG_DATE = 1;
    int DIALOG_TIME = 2;
    int myYear = 2021;
    int myMonth = 1;
    int myDay = 1;

    int myHour = 12;
    int myMinute = 01;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd-MM-yyyy");

    public static void start(Activity caller, Note note) {
        Intent intent = new Intent(caller, NoteActivity.class);
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
        cbNotification = findViewById(R.id.cb_check_notification);
        btnDate = findViewById(R.id.btnDate);
        btnDate.setBackgroundColor(Color.DKGRAY);

        if (getIntent().hasExtra(EXTRA_NOTE)) {
            setTitle(getString(R.string.note_recreate));
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
            etTitle.setText(note.title);
            etDesc.setText(note.text);
            cbNotification.setVisibility(View.GONE);

            if(note.date == null){
                btnDate.setVisibility(View.GONE);
            } else {
                btnDate.setVisibility(View.VISIBLE);
            }

            try {
                myCalendar.setTime(sdf.parse(note.date));
                myYear = myCalendar.get(Calendar.YEAR);
                myMonth = myCalendar.get(Calendar.MONTH);
                myDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                myHour = myCalendar.get(Calendar.HOUR_OF_DAY);
                myMinute = myCalendar.get(Calendar.MINUTE);

                btnDate.setText(DateFormat.format(getString(R.string.date_of_notification) + " dd.MM.yyyy HH:mm", myCalendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                btnDate.setText(note.date);
            }

        } else {
            note = new Note();
            cbNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked) {
                    btnDate.setVisibility(View.VISIBLE);
                } else if(!isChecked){
                    btnDate.setVisibility(View.GONE);
                }
            }
            );
        }
    }

    private void scheduleNotification(Notification notification, long delay) {
        Loggi.text("scheduleNotification: " +delay);
        Loggi.text("scheduleNotification2: " +Calendar.getInstance().getTimeInMillis());
        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    private Notification getNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                default_notification_channel_id);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
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
            case R.id.action_share:
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plan");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject test");
                    String body = etTitle.getText().toString() + "\n" + etDesc.getText().toString()
                            + "\n" + getString(R.string.share_from) + getString(R.string.app_name) + "\n";
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(intent, "Share with :"));
                } catch (Exception e) {
                    Toast.makeText(this, "Hmm... Sorry, \nCannot be share", Toast.LENGTH_SHORT).show();
                }
            case R.id.action_save:
                if ((etDesc.getText().length() > 0) && (etTitle.getText().length() > 0)) {
                    note.title = etTitle.getText().toString();
                    note.text = etDesc.getText().toString();
                    note.done = false;
                    note.date = sdf.format(myCalendar.getTime());
                    note.timestamp = System.currentTimeMillis();
                    if (getIntent().hasExtra(EXTRA_NOTE)) {
                        App.getInstance().getNoteDao().update(note);
                    } else {
                        App.getInstance().getNoteDao().insert(note);
                    }
                    updateLabel();
                    finish();
                } else if ((etDesc.getText().length() > 0) && (etTitle.getText().length() < 0)) {
                    Toast.makeText(this, "Заполните поле Название заметки",
                            Toast.LENGTH_SHORT).show();
                } else if ((etDesc.getText().length() < 0) && (etTitle.getText().length() < 0)) {
                    Toast.makeText(this, "Заполните поле описание заметки",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Заполните поля названия и описания заметки",
                            Toast.LENGTH_SHORT).show();
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
            return new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
        } else if (id == DIALOG_TIME) {
            return new TimePickerDialog(this, myCallBackTime, myHour, myMinute, true);
        }
        return super.onCreateDialog(id);
    }


    TimePickerDialog.OnTimeSetListener myCallBackTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            myCalendar.set(Calendar.YEAR, myYear);
            myCalendar.set(Calendar.MONTH, myMonth);
            myCalendar.set(Calendar.DAY_OF_MONTH, myDay);
            myCalendar.set(Calendar.HOUR_OF_DAY,myHour);
            myCalendar.set(Calendar.MINUTE,myMinute);

            btnDate.setText(DateFormat.format(getString(R.string.date_of_notification) +" dd.MM.yyyy HH:mm", myCalendar.getTime()));
           // updateLabel();
        }
    };

    DatePickerDialog.OnDateSetListener myCallBack = (view, year, monthOfYear, dayOfMonth) -> {

        myYear = year;
        myMonth = monthOfYear;
        myDay = dayOfMonth;

        showDialog(DIALOG_TIME);

    };

    private void updateLabel() {
//        String myFormat = "dd/MM/yy" ; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat , Locale.getDefault()) ;
        Date date = myCalendar.getTime();
//        btnDate.setText(sdf.format(date)) ;
        //scheduleNotification(getNotification(btnDate.getText().toString()), date.getTime());
        scheduleNotification(getNotification(note.title,note.text), myCalendar.getTimeInMillis());
    }
}
