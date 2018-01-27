package com.example.mat.todoista.utilities;

/**
 * Created by mat on 13.01.2018.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;


/**
 * Created by mat on 13.01.2018.
 */

public class AlarmService extends Service{
    private String todoDescription;
    private long todoId;

    @Override
    public void onCreate() {
        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        //Intent intentToRepeat = new Intent(context, MainActivity.class);
        //set flag to restart/relaunch the app
        //intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Pending intent to handle launch of Activity in intent above

        //Build notification
        //Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

        //Send local notification
        //Toast.makeText(getBaseContext(), "Alarm running", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        todoDescription = intent.getStringExtra("todoDescription");
        todoId = intent.getIntExtra("todoId",-1);
        NotificationUtils.remindUserAboutTodo(getBaseContext(), todoDescription, todoId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}