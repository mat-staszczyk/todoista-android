package com.example.mat.todoista.data;

/**
 * Created by mat on 12.01.2018.
 */

import android.net.Uri;
import android.provider.BaseColumns;


public class TodoContract {

    /* Add content provider constants to the Contract
     Clients need to know how to access the task data, and it's your job to provide
     these content URI's for the path to that data:
        1) Content authority,
        2) Base content URI,
        3) Path(s) to the todos directory
        4) Content URI for data in the TodoEntry class
      */

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.mat.todoista";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "todos" directory
    public static final String PATH_TODOS = "todos";

    /* TodoEntry is an inner class that defines the contents of the todo table */
    public static final class TodoEntry implements BaseColumns {

        // TodoEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODOS).build();


        // Todo table and column names
        public static final String TABLE_NAME = "todos";

        // Since TodoEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_REMINDER_DATE = "reminder_date";
        public static final String COLUMN_DONE = "done";

    }
}
