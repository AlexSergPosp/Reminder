package com.example.alexander.birthday;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alexander on 08.02.2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentToRepeat = new Intent(context, MainActivity.class);
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ArrayList<Notification> todayNotes = NotificationHelper.getTodayNotification(context,NotificationHelper.getAllNotification(context));
        for (Notification note : todayNotes){
            NotificationHelper.getNotificationManager(context).notify(new Random(100).nextInt(), note);
        }
        NotificationHelper.scheduleNotification(context);

    }
}
