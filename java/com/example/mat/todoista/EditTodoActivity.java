package com.example.mat.todoista;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.mat.todoista.data.TodoContract;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;


public class EditTodoActivity extends AppCompatActivity {

    // Declare a member variable to keep track of a item's selected mPriority
    private int todoId;
    private int mPriority;
    public String date_time = "";
    private int mYear;
    private int mMonth;
    private int mDay;

    private int mHour;
    private int mMinute;

    EditText et_show_date_time;
    Button btn_set_date_time;
    EditText descView;
    TextView reminderDateView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        et_show_date_time = (EditText) findViewById(R.id.reminderDate);
        btn_set_date_time = (Button) findViewById(R.id.datePicker);
        descView = (EditText) findViewById(R.id.editTextTodoDescription);
        reminderDateView = (TextView) findViewById(R.id.reminderDate);

        Intent intent = getIntent();
        todoId = intent.getExtras().getInt("todoId");

        Uri uri = TodoContract.TodoEntry.CONTENT_URI;
        String stringId = Integer.toString(todoId);
        uri = uri.buildUpon().appendPath(stringId).build();

        Cursor mCursor;

        mCursor = getContentResolver().query(uri,null,null,null,null);

        if (mCursor.getCount() == 1) {
            int idIndex = mCursor.getColumnIndex(TodoContract.TodoEntry._ID);
            int descriptionIndex = mCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DESCRIPTION);
            int priorityIndex = mCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_PRIORITY);
            int dateIndex = mCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_REMINDER_DATE);

            mCursor.moveToPosition(0); // get to the right location in the cursor

            // Determine the values of the wanted data
            String description = mCursor.getString(descriptionIndex);
            mPriority = mCursor.getInt(priorityIndex);
            String date = mCursor.getString(dateIndex).toString();

            // Set values
            descView.setText(description);

            reminderDateView.setText(date);

            switch (mPriority) {
                case 1:
                    ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
                    break;
                case 2:
                    ((RadioButton) findViewById(R.id.radButton2)).setChecked(true);
                    break;
                case 3:
                    ((RadioButton) findViewById(R.id.radButton3)).setChecked(true);
                    break;
                default:
                    ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
                    break;
            }


        } else {
            throw new Error("Błąd podczas pobierania danych");
        }

        // Initialize to highest mPriority by default (mPriority = 1)
        //((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        //mPriority = 1;


    }

    /**
     * onClickAddTodo is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new item data into the underlying database.
     */
    public void onClickSaveTodo(View view) {
        // Not yet implemented
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String input = descView.getText().toString();
        if (input.length() == 0) {
            return;
        }

        // Insert new todo data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        if (validateDateFormat() == false) {
            return;
        }

        String reminderDateStr = et_show_date_time.getText().toString();
        if (reminderDateStr.length() > 0) {
            contentValues.put(TodoContract.TodoEntry.COLUMN_REMINDER_DATE, reminderDateStr);
        } else {
            contentValues.put(TodoContract.TodoEntry.COLUMN_REMINDER_DATE, "");
        }


        // Put the todo description and selected mPriority into the ContentValues
        contentValues.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(TodoContract.TodoEntry.COLUMN_PRIORITY, mPriority);

        String stringId = Integer.toString(todoId);
        Uri uri = TodoContract.TodoEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        // Insert the content values via a ContentResolver
        getContentResolver().update(uri, contentValues,null,null);

        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
//        if(uri != null) {
//            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
//        }

        // Finish activity (this returns back to MainActivity)
        finish();

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

                        et_show_date_time.setText(date_time+" "+hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private boolean validateDateFormat() {
        String value = et_show_date_time.getText().toString();
        if (!value.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            try {
                Date reminderDate = dateFormat.parse(value);
                String reminderDateParsedStr = dateFormat.format(reminderDate);
            } catch (ParseException e) {
                Toast.makeText(getBaseContext(), "Podaj datę w formacie 'dd-MM-yyyy hh:mm'", Toast.LENGTH_LONG).show();
                et_show_date_time.setText("");
                return false;
            }
        }
        return true;
    }
}
