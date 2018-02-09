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

public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            ArrayList<Notification> todayNotes = NotificationHelper.getTodayNotification(context,NotificationHelper.GetAllNotification(context));
            for (Notification note : todayNotes){
                NotificationHelper.getNotificationManager(context).notify(new Random(100).nextInt(), note);
            }
            NotificationHelper.scheduleRepeatingRTCNotification(context);
        }
    }
}