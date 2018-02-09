package com.example.alexander.birthday;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;


import com.example.alexander.birthday.data.BirthContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {

    public static int ALARM_TYPE_RTC = 100;
    private static AlarmManager alarmManagerRTC;
    private static PendingIntent alarmIntentRTC;

    public static void scheduleRepeatingRTCNotification(Context context) {
        Calendar calendar = Calendar.getInstance();
        Notify note = GetNextNotification(context);
        calendar.setTimeInMillis(note.date.getTime());
        Calendar currentDate =  Calendar.getInstance();

        calendar.set(currentDate.get(Calendar.YEAR), 8,0, 8,0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntentRTC = PendingIntent.getBroadcast(context, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManagerRTC = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        //alarmManagerRTC.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentRTC);
    }

    public static void cancelAlarmRTC() {
        if (alarmManagerRTC!= null) {
            alarmManagerRTC.cancel(alarmIntentRTC);
        }
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Disable boot receiver when user cancels/opt-out from notifications
     */
    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    public static NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {

        Notify note = GetNextNotification(context);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setContentTitle("Birthday notification " + note.name)
                        .setAutoCancel(true);

        return builder;
    }

    public static Notify GetNextNotification(Context context){

        String[] projection = {
                BirthContract.ManEntry._ID,
                BirthContract.ManEntry.COLUMN_NAME,
                BirthContract.ManEntry.COLUMN_DATE};

        Cursor cursor = context.getContentResolver().query(
                BirthContract.ManEntry.CONTENT_URI,   // The content URI of the words table
                projection,                        // The columns to return for each row
                null,                   // Selection criteria
                null,                     // Selection criteria
                null);

        Notify notify = new Notify("", new Date(Long.MAX_VALUE));

        if (null != cursor && cursor.getCount() >= 1){
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(BirthContract.ManEntry.COLUMN_NAME));
                String date = cursor.getString(cursor.getColumnIndex(BirthContract.ManEntry.COLUMN_DATE));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                try {
                    Date result = sdf.parse(date);
                    if (notify.date.getTime() <  result.getTime()){
                        notify.date = result;
                        notify.name = name;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return notify;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, 0); // to get previous year add -1
        Date nextYear = cal.getTime();
    }


    private static class Notify{
        public String name;
        public Date date;

        public Notify(String name, Date date) {
            this.name = name;
            this.date = date;
        }
    }
}
