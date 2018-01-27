package com.example.mat.todoista.data;

/**
 * Created by mat on 12.01.2018.
 */

import android.net.Uri;
import android.provider.BaseColumns;


public class TodoContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.mat.todoista";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "todos" directory
    public static final String PATH_TODOS = "todos";

    /* TodoEntry is an inner class that defines the contents of the task table */
    public static final class TodoEntry implements BaseColumns {

        // TodoEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODOS).build();


        // Task table and column names
        public static final String TABLE_NAME = "todos";

        // Since TodoEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_REMINDER_DATE = "reminder_date";
        public static final String COLUMN_DONE = "done";

    }
}
