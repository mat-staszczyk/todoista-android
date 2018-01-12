package com.example.mat.todoista.data;

/**
 * Created by mat on 12.01.2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mat.todoista.data.TodoContract.TodoEntry;


public class TodoDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "todosDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;


    // Constructor
    TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the todos database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create todos table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + TodoEntry.TABLE_NAME + " (" +
                TodoEntry._ID                + " INTEGER PRIMARY KEY, " +
                TodoEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                TodoEntry.COLUMN_PRIORITY    + " INTEGER NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME);
        onCreate(db);
    }
}
