package com.example.myplanner.notification;

import static android.content.Context.ALARM_SERVICE;
import static com.example.myplanner.notification.MyNotification.CHANNEL_ID;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.myplanner.CreateEventActivity;
import com.example.myplanner.EventActivity;
import com.example.myplanner.MainActivity;
import com.example.myplanner.R;
import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyBroadcastReceiver extends BroadcastReceiver {
    int cancelNotification;
    MyPlannerDatabase myPlannerDatabase;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, EventActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Intent intentCancel = new Intent(context,NotificationCancelReceiver.class);
        intent.putExtra("notification_id",100);
        PendingIntent cancelPending = PendingIntent.getBroadcast(context,0,intentCancel,PendingIntent.FLAG_CANCEL_CURRENT);


        String idEvent = intent.getStringExtra("idEvent");
        String dtStartReceive = intent.getStringExtra("dtStart");
        String descriptionReceive = intent.getStringExtra("description");
        String titleReceive = intent.getStringExtra("title");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.txtTitle,titleReceive);
        remoteViews.setTextViewText(R.id.txtDescription,descriptionReceive);
        remoteViews.setTextViewText(R.id.txtTime,formatTime.format(Long.parseLong(dtStartReceive)));
        remoteViews.setOnClickPendingIntent(R.id.txtCancel,cancelPending);

        repeating_intent.putExtra("id_event",idEvent);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_MUTABLE);
        }
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setSmallIcon(R.drawable.ic_error)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(100,notification);


//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//
//        Intent intentCreate = new Intent(context,MyBroadcastReceiver.class);
//        intentCreate.putExtra("idEvent",idEvent);
//        intentCreate.putExtra("description",descriptionReceive);
//        intentCreate.putExtra("dtStart",dtStartReceive);
//        intentCreate.putExtra("title",titleReceive);

//        Event event = null;
//        long dateEndORTimeRepeat = 0;
//        myPlannerDatabase = new MyPlannerDatabase(context);
//        Cursor cursor = myPlannerDatabase.getData("select * from Events where IdEvent = "+Long.parseLong(idEvent)+"");
//        if (cursor!=null){
//            cursor.moveToFirst();
//            String title = cursor.getString(1);
//            long dtStart = cursor.getLong(3);
//            long dtEnd = cursor.getLong(4);
//            String description = cursor.getString(2);
//            boolean done;
//            done = (cursor.getInt(8)==1 ? true : false);
//            int notificationEvent = cursor.getInt(5);
//            int repeat = cursor.getInt(6);
//
//            event = new Event(idEvent,title,dtStart,dtEnd,description,"user",done,"user",notificationEvent,repeat,0,0);
//            dateEndORTimeRepeat = cursor.getLong(7);
//            cursor.close();
//        }
//
//        if (event!=null){
//            int requesCode = Integer.valueOf(event.getIdEvent().substring(0,event.getIdEvent().length()-3));
//            PendingIntent pendingIntentCreate = PendingIntent.getBroadcast(context,requesCode,intentCreate,PendingIntent.FLAG_UPDATE_CURRENT);
//            Calendar calendar = Calendar.getInstance();
//
//            if (event.getRepeat()==0){
//                alarmManager.cancel(pendingIntentCreate);
//            }else if (dateEndORTimeRepeat > 1000 && dateEndORTimeRepeat <= calendar.getTime().getTime()){
//                alarmManager.cancel(pendingIntentCreate);
//            }else {
//                cancelNotification = repeatOrCancelNotification(alarmManager, pendingIntentCreate,event,dateEndORTimeRepeat,context,calendar);
//                if (cancelNotification == 0){
//                    alarmManager.cancel(pendingIntentCreate);
//                }
//            }
//        }

    }

    private int repeatOrCancelNotification(AlarmManager alarmManager, PendingIntent pendingIntentCreate, Event event, long timeEnd, Context context, Calendar calendar) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(new Date(event.getDtStart()));

        switch (event.getNotification()){
            case 1:
                break;
            case 2:
                calendar.add(Calendar.MINUTE,5);
                break;
            case 3:
                calendar.add(Calendar.MINUTE,15);
                break;
            case 4:
                calendar.add(Calendar.MINUTE,30);
                break;
            default:
                break;
        }

        int set1 = Calendar.DAY_OF_YEAR, set2 = 1, maxCount = 0;
        switch (event.getRepeat()){
            case 1:
                set1 = Calendar.DAY_OF_YEAR;
                maxCount = CreateEventActivity.maxRepeatDay;
                break;
            case 2:
                set1 = Calendar.WEEK_OF_YEAR;
                maxCount = CreateEventActivity.maxRepeatWeek;
                break;
            case 3:
                set1 = Calendar.MONTH;
                maxCount = CreateEventActivity.maxRepeatMonth;
                break;
            case 4:
                set1 = Calendar.YEAR;
                maxCount = CreateEventActivity.maxRepeatYear;
                break;
            default:
                break;
        }

        int numberOfDayStart = calendarStart.get(Calendar.DAY_OF_MONTH);
        int count = 0;
        int repeat = event.getRepeat();

        if (timeEnd==0){
            long time = calendarStart.getTime().getTime();
            while (count<maxCount){
                // Cài đặt thông báo
                if (time > calendar.getTime().getTime()){
                    if (repeat!=3){
                        long timeWakeUp = getTimeWakeUp(time,event.getNotification());
                        alarmManager.set(AlarmManager.RTC_WAKEUP,timeWakeUp,pendingIntentCreate);
                        return 1;
                    }else {
                        if (calendarStart.get(Calendar.DAY_OF_MONTH)==numberOfDayStart){
                            long timeWakeUp = getTimeWakeUp(time,event.getNotification());
                            alarmManager.set(AlarmManager.RTC_WAKEUP,timeWakeUp,pendingIntentCreate);
                            return 1;
                        }
                    }
                }

                if (repeat!=3){
                    calendarStart.add(set1,set2);
                }else {
                    calendarStart.add(set1,set2);
                    if (calendarStart.get(Calendar.DAY_OF_MONTH) < numberOfDayStart){
                        if (numberOfDayStart <= calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                            calendarStart.set(Calendar.DAY_OF_MONTH,numberOfDayStart);
                        }
                    }
                }

                time = calendarStart.getTime().getTime();
                count++;
            }
        }else if (timeEnd>0 && timeEnd < 1000){
            long time = calendarStart.getTime().getTime();
            for (int i=0; i<=timeEnd; i++){
                    // Cài đặt thông báo
                    if (time > calendar.getTime().getTime()){
                        if (repeat!=3){
                            long timeWakeUp = getTimeWakeUp(time,event.getNotification());
                            alarmManager.set(AlarmManager.RTC_WAKEUP,timeWakeUp,pendingIntentCreate);
                            return 1;
                        }else {
                            if (calendarStart.get(Calendar.DAY_OF_MONTH)==numberOfDayStart){
                                long timeWakeUp = getTimeWakeUp(time,event.getNotification());
                                alarmManager.set(AlarmManager.RTC_WAKEUP,timeWakeUp,pendingIntentCreate);
                                return 1;
                            }
                        }
                    }
                    if (repeat!=3){
                        calendarStart.add(set1,set2);
                    }else {
                        calendarStart.add(set1,set2);
                        if (calendarStart.get(Calendar.DAY_OF_MONTH) < numberOfDayStart){
                            if (numberOfDayStart <= calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                                calendarStart.set(Calendar.DAY_OF_MONTH,numberOfDayStart);
                            }else {
                                i--;
                            }
                        }
                    }
                    time = calendarStart.getTime().getTime();
            }
        }else {
            long time = calendarStart.getTime().getTime();
            while (time<=timeEnd){

                // Cài đặt thông báo
                if (time > calendar.getTime().getTime()){
                    if (repeat!=3){
                        long timeWakeUp = getTimeWakeUp(time,event.getNotification());
                        alarmManager.set(AlarmManager.RTC_WAKEUP,timeWakeUp,pendingIntentCreate);
                        return 1;
                    }else {
                        if (calendarStart.get(Calendar.DAY_OF_MONTH)==numberOfDayStart){
                            long timeWakeUp = getTimeWakeUp(time,event.getNotification());
                            alarmManager.set(AlarmManager.RTC_WAKEUP,timeWakeUp,pendingIntentCreate);
                            return 1;
                        }
                    }
                }
                if (repeat!=3){
                    calendarStart.add(set1,set2);
                }else {
                    calendarStart.add(set1,set2);
                    if (calendarStart.get(Calendar.DAY_OF_MONTH) < numberOfDayStart){
                        if (numberOfDayStart <= calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                            calendarStart.set(Calendar.DAY_OF_MONTH,numberOfDayStart);
                        }
                    }
                }
                time = calendarStart.getTime().getTime();
            }
        }
        return 0;
    }

    private long getTimeWakeUp(long time, int notification) {
        switch (notification){
            case 1:
                return time;
            case 2:
                return time + (5*60*1000);
            case 3:
                return time + (15*60*1000);
            case 4:
                return time + (30*60*1000);
            default:
                break;
        }
        return 0;
    }
}
