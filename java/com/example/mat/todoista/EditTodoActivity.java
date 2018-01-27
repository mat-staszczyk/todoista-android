package com.example.mat.todoista;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.mat.todoista.data.TodoContract;


public class EditTodoActivity extends SaveTodoActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        todoId = intent.getExtras().getInt("todoId");

        Uri uri = TodoContract.TodoEntry.CONTENT_URI;
        String stringId = Long.toString(todoId);
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
    }


    /**
     * onClickAddTodo is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new item data into the underlying database.
     */
    public void onClickSaveTodo(View view) {
        // Not yet implemented
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        todoDescription = ((EditText) findViewById(R.id.editTextTodoDescription)).getText().toString();
        if (todoDescription.length() == 0) {
            return;
        }

        // Insert new todo data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        if (validateAndGetDate() == false) {
            return;
        }

        String reminderDateStr = et_show_date_time.getText().toString();
        if (reminderDateStr.length() > 0) {
            contentValues.put(TodoContract.TodoEntry.COLUMN_REMINDER_DATE, reminderDateStr);
        } else {
            contentValues.put(TodoContract.TodoEntry.COLUMN_REMINDER_DATE, "");
        }


        // Put the todo description and selected mPriority into the ContentValues
        contentValues.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, todoDescription);
        contentValues.put(TodoContract.TodoEntry.COLUMN_PRIORITY, mPriority);

        String stringId = Long.toString(todoId);
        Uri uri = TodoContract.TodoEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        // Insert the content values via a ContentResolver
        getContentResolver().update(uri, contentValues,null,null);

        if (!et_show_date_time.getText().toString().isEmpty()) {
            setReminder();
        }

        // Finish activity (this returns back to MainActivity)
        finish();

    }


}
