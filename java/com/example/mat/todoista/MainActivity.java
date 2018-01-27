package com.example.mat.todoista;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.mat.todoista.data.TodoContract;
import com.example.mat.todoista.utilities.AlarmService;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TODO_LOADER_ID = 0;

    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView;
    private static PendingIntent alarmIntentRTC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTodos);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the todo to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = TodoContract.TodoEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all todos after a deletion
                getSupportLoaderManager().restartLoader(TODO_LOADER_ID, null, MainActivity.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTodoActivity.
         */
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTodoActivity
                Intent addTodoIntent = new Intent(MainActivity.this, AddTodoActivity.class);
                startActivity(addTodoIntent);
            }
        });

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TODO_LOADER_ID, null, this);
    }


    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTodoActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all todos
        getSupportLoaderManager().restartLoader(TODO_LOADER_ID, null, this);
    }


    /**
     * Instantiates and returns a new AsyncTodoLoader with the given ID.
     * This loader will return todo data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the todo data
            Cursor mTodoData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTodoData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTodoData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all todo data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                String order = TodoContract.TodoEntry.COLUMN_DONE + ", " + TodoContract.TodoEntry.COLUMN_PRIORITY;

                try {
                    return getContentResolver().query(TodoContract.TodoEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            order);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTodoData = data;
                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void testNotification(View view) {
        stopService(new Intent(this, AlarmService.class));
        Calendar now = Calendar.getInstance();
        Intent intent = new Intent(this, AlarmService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + 1);
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
    }

    public void todoChecked(View v) {
        ContentValues contentValues = new ContentValues();
        Uri uri = TodoContract.TodoEntry.CONTENT_URI;
        CheckBox checkBox = (CheckBox)v;

        int id = (int) checkBox.getTag();
        String stringId = Integer.toString(id);
        uri = uri.buildUpon().appendPath(stringId).build();


        if(checkBox.isChecked()){
            contentValues.put(TodoContract.TodoEntry.COLUMN_DONE, 1);
        } else {
            contentValues.put(TodoContract.TodoEntry.COLUMN_DONE, 0);
        }

        getContentResolver().update(uri, contentValues, null, null);
        getSupportLoaderManager().restartLoader(TODO_LOADER_ID, null, MainActivity.this);
    }

    public void goToEdit(View view) {
        // Create a new intent to start an EditTodoActivity
        TextView desc = (TextView)view;
        int id = (int) desc.getTag();
        Intent editTodoIntent = new Intent(MainActivity.this, EditTodoActivity.class);
        editTodoIntent.putExtra("todoId", id);
        startActivity(editTodoIntent);
    };

}


