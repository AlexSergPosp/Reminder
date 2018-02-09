package com.example.alexander.birthday;

import android.app.AlarmManager;
import android.app.Notification;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {

    public static int ALARM_TYPE_RTC = 100;
    private static AlarmManager alarmManagerRTC;
    private static PendingIntent alarmIntentRTC;

    public static void scheduleRepeatingRTCNotification(Context context) {
        Date date = getNextDateNotification(GetAllNotification(context));
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntentRTC = PendingIntent.getBroadcast(context, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManagerRTC = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManagerRTC.setExact(AlarmManager.RTC_WAKEUP, date.getTime(),alarmIntentRTC);
    }

    public static void cancelAlarmRTC() {
        if (alarmManagerRTC!= null) {
            alarmManagerRTC.cancel(alarmIntentRTC);
        }
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static Notification buildLocalNotification(Context context, String name) {

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setContentTitle("Today Birthday of " + name)
                        .setWhen(Calendar.getInstance().getTime().getTime())
                        .setAutoCancel(true);

        return builder.build();
    }


    public static ArrayList<Notification> getTodayNotification(Context context, ArrayList<Notify> notifies){
        ArrayList<Notification> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (Notify note : notifies){
            if (calendar.getTime().getDay() == note.date.getDay() && calendar.getTime().getMonth() ==  note.date.getMonth()){
                result.add(buildLocalNotification(context, note.name));
            }
        }
        return result;
    }

    public static Date getNextDateNotification(ArrayList<Notify> notifies){

        Calendar calendar = Calendar.getInstance();
        Date next;
        for (Notify note : notifies){

             Calendar currentYear = Calendar.getInstance();
             currentYear.setTime(note.date);
             currentYear.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
             currentYear.add(Calendar.DAY_OF_MONTH, 1);

             if (currentYear.getTime().getTime() >= calendar.getTime().getTime()){
                 next = currentYear.getTime();
                 return next;
             }
        }

        for (Notify note : notifies){
            Calendar currentYear = Calendar.getInstance();
            currentYear.setTime(note.date);
            currentYear.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            currentYear.add(Calendar.YEAR, 1);

            if (currentYear.getTime().getTime() >= calendar.getTime().getTime()){
                next = currentYear.getTime();
                return next;
            }
        }
        return new Date();
    }

    public static ArrayList<Notify> GetAllNotification(Context context){

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


        ArrayList<Notify> resultList = new ArrayList<>();

        if (null != cursor && cursor.getCount() >= 1){
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(BirthContract.ManEntry.COLUMN_NAME));
                String date = cursor.getString(cursor.getColumnIndex(BirthContract.ManEntry.COLUMN_DATE));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                try {
                    Date result = sdf.parse(date);
                    Notify notify = new Notify(name, result);
                    resultList.add(notify);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultList;
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
