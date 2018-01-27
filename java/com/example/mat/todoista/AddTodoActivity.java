package com.example.mat.todoista;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.mat.todoista.data.TodoContract;


public class AddTodoActivity extends SaveTodoActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize to highest mPriority by default (mPriority = 1)
        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        mPriority = 1;
    }

    /**
     * onClickAddTodo is called when the "Save" button is clicked.
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

        // Insert new task data via a ContentResolver
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


        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, todoDescription);
        contentValues.put(TodoContract.TodoEntry.COLUMN_PRIORITY, mPriority);
        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI, contentValues);

        // Get added item it
        todoId = Long.valueOf(uri.getLastPathSegment());

        if (!et_show_date_time.getText().toString().isEmpty()) {
            setReminder();
        }

        // Finish activity (this returns back to MainActivity)
        finish();

    }
}
