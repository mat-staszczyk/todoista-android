package com.example.mat.todoista;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mat.todoista.utilities.AlarmService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SaveTodoActivity extends AppCompatActivity {

    protected int mPriority;
    private int mYear;
    private int mMonth;
    private int mDay;

    private int mHour;
    private int mMinute;

    protected long todoId;
    protected String todoDescription;

    TextView reminderDateView;
    EditText descView;
    EditText et_show_date_time;
    Button btn_set_date_time;
    public String date_time = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        // Date picker
        et_show_date_time = (EditText) findViewById(R.id.reminderDate);
        btn_set_date_time = (Button) findViewById(R.id.datePicker);
        descView = (EditText) findViewById(R.id.editTextTodoDescription);
        reminderDateView = (TextView) findViewById(R.id.reminderDate);
    }


    /**
     * onClickAddTodo is called when the "Save" button is clicked.
     * It retrieves user input and inserts that new item data into the underlying database.
     */
    public interface onClickSaveTodo {
        void onClickSaveTodo(View view);
    }

    /**
     * onClickDatePicker is called when date picker is clicked.
     * It shows date and time picker which is added in valid format.
     *
     */

    public void onClickDatePicker(View view) {
        datePicker();
    }


    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onPrioritySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mPriority = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mPriority = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mPriority = 3;
        }
    }


    /**
     * Run pickers for time setup
     */
    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        //*************Call Time Picker Here ********************
                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        et_show_date_time.setText(date_time+" "+hourOfDay + ":" + minuteDisplay(minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    /**
     * Validate date and parse user input to instance variables
     *
     * @return true if right format is given
     */
    boolean validateAndGetDate() {
        String value = et_show_date_time.getText().toString();
        if (!value.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            try {
                Date reminderDate = dateFormat.parse(value);
                String reminderDateParsedStr = dateFormat.format(reminderDate);
                Calendar cal = dateFormat.getCalendar();

                mYear = cal.get(Calendar.YEAR);
                mMonth = cal.get(Calendar.MONTH);
                mDay = cal.get(Calendar.DAY_OF_MONTH);

                mHour = cal.get(Calendar.HOUR_OF_DAY);
                mMinute = cal.get(Calendar.MINUTE);

            } catch (ParseException e) {
                Toast.makeText(getBaseContext(), "Podaj datę w formacie 'dd-MM-yyyy hh:mm'", Toast.LENGTH_LONG).show();
                et_show_date_time.setText("");
                return false;
            }
        }
        return true;
    }

    /**
     * Schedule reminder notification
     */
    void setReminder() {
        int format24;
        stopService(new Intent(this, AlarmService.class));

        if (DateFormat.is24HourFormat(getBaseContext())) {
            format24 = 1;
        } else {
            format24 = 0;
        }

        stopService(new Intent(this, AlarmService.class));
        Intent intent = new Intent(this, AlarmService.class);
        intent.putExtra("todoId", todoId);
        intent.putExtra("todoDescription", todoDescription);

        PendingIntent pintent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, mYear);
        cal.set(Calendar.MONTH, mMonth);
        cal.set(Calendar.DAY_OF_MONTH, mDay);

        cal.set(Calendar.AM_PM, format24);
        cal.set(Calendar.HOUR_OF_DAY, mHour);
        cal.set(Calendar.MINUTE, mMinute);
        cal.set(Calendar.SECOND, 0);

        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
    }

    private String minuteDisplay(Integer minute) {
        return minute < 10 ? "0" + minute : minute.toString();
    }
}
