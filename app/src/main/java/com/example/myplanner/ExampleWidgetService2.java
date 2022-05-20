package com.example.myplanner;

import static com.example.myplanner.ExampleAppWidgetProvider.ACTION_SHOW_DETAIL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ExampleWidgetService2 extends RemoteViewsService {
    SimpleDateFormat formatNumberOfDay = new SimpleDateFormat("dd");
    SimpleDateFormat formatDay = new SimpleDateFormat("EEE");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM yyyy");
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        List<Event> list = new ArrayList<>();
        list = getListEvent();
        return new ExampleWidgetItemFactory2(getApplicationContext(),intent,list);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<Event> getListEvent() {
        List<Event> list = new ArrayList<>();
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        Date startDate = getDate(calendarEnd,"start");
        Date endDate = getDate(calendarEnd,"end");

        List<Event> listEventDevice = new ArrayList<>();
        if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_CALENDAR)== PackageManager.PERMISSION_GRANTED
                && getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            listEventDevice = getListDevice(getApplicationContext());
        }


        long time = getTimeStartNextWeek(calendarStart);
        Calendar calendarStartNextWeek = Calendar.getInstance();
        calendarStartNextWeek.setTime(new Date(time));
        calendarEnd.setTime(new Date(startDate.getTime()+(7*24*60*60*1000)));
        calendarEnd.set(Calendar.HOUR_OF_DAY,23);
        calendarEnd.set(Calendar.MINUTE,59);
        calendarEnd.set(Calendar.SECOND,59);
        calendarEnd.set(Calendar.MILLISECOND,999);
        calendarEnd.set(Calendar.DAY_OF_WEEK,8);

        if (endDate.getTime()<calendarEnd.getTimeInMillis()){
            endDate = calendarEnd.getTime();
        }

        while (calendarStartNextWeek.getTime().getTime() <= endDate.getTime()){
            list.add(new Event("","",time,calendarEnd.getTime().getTime(),"","period",false,"period",0,0,0,0));
            calendarStartNextWeek.add(Calendar.WEEK_OF_YEAR,1);
            calendarEnd.add(Calendar.WEEK_OF_YEAR,1);
            time = calendarStartNextWeek.getTime().getTime();
        }

        if (listEventDevice.size()>0){
            for (int i=0; i<listEventDevice.size(); i++){
                if (listEventDevice.get(i).getDtStart() >= startDate.getTime() && listEventDevice.get(i).getDtStart() <= endDate.getTime()){
                    list.add(listEventDevice.get(i));
                }
            }
        }

        List<Event> listWorkOfUser = new ArrayList<>();
        listWorkOfUser = getListEventUser(startDate,endDate,getApplicationContext());
        if (listWorkOfUser.size()>0){
            list.addAll(listWorkOfUser);
        }

        int notHave = 0;
        Date currentDate = getCurrentDate();
        String strCurrentDate = formatDate.format(currentDate);
        for (int i=0; i<list.size(); i++){
            Event eventTemp = list.get(i);
            if (eventTemp.getType().equals("device") || eventTemp.getType().equals("user")){
                if (strCurrentDate.equals(formatDate.format(eventTemp.getDtStart()))){
                    notHave = 1;
                }
            }
        }

        if (notHave==0){
            list.add(new Event("","",currentDate.getTime(),0,"","current_day",false,"current_day",0,0,0,1));
        }

        Collections.sort(list, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                if (event.getDtStart() > t1.getDtStart()){
                    return 1;
                }else if (event.getDtStart() < t1.getDtStart()){
                    return -1;
                }
                return 0;
            }
        });

        String dateTemp = "";
        for (int i=0; i<list.size(); i++){
            Event eventTemp = list.get(i);
            if (eventTemp.getType().equals("device") || eventTemp.getType().equals("user") || eventTemp.getType().equals("current_day")){
                String strTemp = formatDate.format(eventTemp.getDtStart());
                if (dateTemp.equals("") || !dateTemp.equals(strTemp)){
                    list.get(i).setVisibleDate(1);
                    dateTemp = strTemp;
                }
            }
        }

        setDone(list);

        return list;
    }

    class ExampleWidgetItemFactory2 implements RemoteViewsFactory{
        private Context context;
        private int appWidgetId;
        private String exampleData[] = {"one","two","three","four","five","six","seven","eight","nine","ten"};
        private List<Event> listEvent;

        public ExampleWidgetItemFactory2(Context context, Intent intent, List<Event> list){
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            this.listEvent = list;
        }

        @Override
        public void onCreate() {
//            SystemClock.sleep(2000);
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (listEvent!=null){
                return listEvent.size();
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_widget_item_2);
            Event event = listEvent.get(i);
            if (event.getType().equals("user") || event.getType().equals("device")){
                views.setViewVisibility(R.id.layoutEvent,View.VISIBLE);
                views.setViewVisibility(R.id.layoutPeriod,View.GONE);
                views.setViewVisibility(R.id.layoutContainer,View.VISIBLE);
                views.setViewVisibility(R.id.txtHaveNoWork,View.GONE);

                views.setTextViewText(R.id.txtTitle,event.getTitle());
                if (event.getType().equals("user")){
                    views.setViewVisibility(R.id.layoutCheck, View.VISIBLE);
                    if (event.isDone()){
                        views.setInt(R.id.txtTitle, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    }else {
                        views.setInt(R.id.txtTitle, "setPaintFlags", 0);
                    }
                }else {
                    views.setViewVisibility(R.id.layoutCheck, View.GONE);
                }

                if (event.getType().equals("device")){
                    if (event.getIdEvent().equals("2")){
                        views.setInt(R.id.layoutContainer, "setBackgroundResource", R.drawable.background_event_and_work_device_2);
                    }else if (event.getIdEvent().equals("6")){
                        views.setInt(R.id.layoutContainer, "setBackgroundResource", R.drawable.background_event_and_work_device_1);
                    }else {
                        views.setInt(R.id.layoutContainer, "setBackgroundResource", R.drawable.background_event_and_work_device_3);
                    }
                }else {
                    views.setInt(R.id.layoutContainer, "setBackgroundResource", R.drawable.background_event_and_work_user);
                }
            }else if (event.getType().equals("current_day")){
                views.setViewVisibility(R.id.layoutEvent,View.VISIBLE);
                views.setViewVisibility(R.id.layoutPeriod,View.GONE);
                views.setViewVisibility(R.id.layoutContainer,View.GONE);
                views.setViewVisibility(R.id.txtHaveNoWork,View.VISIBLE);

                views.setTextViewText(R.id.txtHaveNoWork,getString(R.string.have_no_work));
            }else if (event.getType().equals("period")){
                views.setViewVisibility(R.id.layoutEvent,View.GONE);
                views.setViewVisibility(R.id.layoutPeriod,View.VISIBLE);

                String dateStart = DateFormat.getDateInstance().format(event.getDtStart());
                String dateEnd = DateFormat.getDateInstance().format(event.getDtEnd());
                String title = dateStart.substring(0,dateStart.length()-6)+" - "+dateEnd.substring(0,dateEnd.length()-6);
                views.setTextViewText(R.id.txtPeriod,title);
            }
//            views.setTextViewText(R.id.txtTitle,event.getTitle());

            String currentDate = formatDate.format(System.currentTimeMillis());
            if (event.getVisibleDate()==1){
                views.setViewVisibility(R.id.layoutDate,View.VISIBLE);
                views.setTextViewText(R.id.txtDay,formatDay.format(event.getDtStart()));
                views.setTextViewText(R.id.txtNumberOfDay,formatNumberOfDay.format(event.getDtStart()));
                if (currentDate.equals(formatDate.format(event.getDtStart()))){
                    views.setTextColor(R.id.txtDay,context.getResources().getColor(R.color.blue1));
                    views.setTextColor(R.id.txtNumberOfDay,Color.WHITE);
                    views.setInt(R.id.txtNumberOfDay, "setBackgroundResource", R.drawable.background_current_day);
                }else {
                    views.setTextColor(R.id.txtDay,context.getResources().getColor(R.color.black));
                    views.setTextColor(R.id.txtNumberOfDay,Color.BLACK);
                    views.setInt(R.id.txtNumberOfDay, "setBackgroundResource", R.color.white);
                }
            }else {
                views.setViewVisibility(R.id.layoutDate,View.GONE);
            }

            Intent fillIntent = new Intent();
            fillIntent.putExtra(ExampleAppWidgetProvider.EXTRA_POSITION,i);
            fillIntent.putExtra(ExampleAppWidgetProvider.EXTRA_EVENT,event);
            fillIntent.putExtra("sign_from_widget","detail");
            fillIntent.setAction(ACTION_SHOW_DETAIL);
            fillIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            views.setOnClickFillInIntent(R.id.layoutEvent,fillIntent);
//            SystemClock.sleep(500);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    private void setDone(List<Event> list) {
        List<EventDone> listDone = getListDone();
        if (listDone!=null){
            for (int i=0; i<listDone.size(); i++){
                String idEvent = String.valueOf(listDone.get(i).getIdEvent());
                String dateCompleted = listDone.get(i).getDateCompleted();
                for (int j=0; j<list.size(); j++){
                    if (idEvent.equals(list.get(j).getIdEvent()) && dateCompleted.equals(formatDate.format(list.get(j).getDtStart()))){
                        list.get(j).setDone(true);
                    }
                }
            }
        }
    }

    private List<EventDone> getListDone() {
        List<EventDone> list = new ArrayList<>();
        MyPlannerDatabase myPlannerDatabase = new MyPlannerDatabase(getApplicationContext());
        Cursor cursor = myPlannerDatabase.getData("select * from EventsDone");
        if (cursor!=null){
            while (cursor.moveToNext()){
                long idEvent = cursor.getLong(0);
                String dateCompleted = cursor.getString(1);
                long timeStart = cursor.getLong(2);
                list.add(new EventDone(idEvent,dateCompleted,timeStart));
            }
            cursor.close();
        }
        return list;
    }

    private long getTimeStartNextWeek(Calendar mCalendar) {
        Calendar calendar = (Calendar) mCalendar.clone();
        calendar.add(Calendar.WEEK_OF_YEAR,1);
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)-2;
        if (firstDayOfMonth<0){
            calendar.add(Calendar.DAY_OF_MONTH,-6);
        }else {
            calendar.add(Calendar.DAY_OF_MONTH,-firstDayOfMonth);
        }
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    private Date getCurrentDate() {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.HOUR_OF_DAY,0);
        currentCalendar.set(Calendar.MINUTE,0);
        currentCalendar.set(Calendar.SECOND,0);
        currentCalendar.set(Calendar.MILLISECOND,1);
        return currentCalendar.getTime();
    }

    private Date getDate(Calendar mCalendar, String type) {
        Calendar calendar = (Calendar) mCalendar.clone();
        if (type.equals("start")){
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
        }else if (type.equals("end")){
            calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            int firstDayOfMonth = 8-calendar.get(Calendar.DAY_OF_WEEK);
            if (firstDayOfMonth!=7){
                calendar.add(Calendar.DAY_OF_MONTH,firstDayOfMonth);
            }else {
                calendar.add(Calendar.DAY_OF_MONTH,-firstDayOfMonth);
            }
            calendar.set(Calendar.HOUR_OF_DAY,23);
            calendar.set(Calendar.MINUTE,59);
            calendar.set(Calendar.SECOND,59);
            calendar.set(Calendar.MILLISECOND,999);
        }
        return calendar.getTime();
    }

    private List<Event> getListEventUser(Date start, Date end, Context context) {
        List<Event> list = new ArrayList<>();
        MyPlannerDatabase myPlannerDatabase = new MyPlannerDatabase(context);
        Cursor cursor1 = myPlannerDatabase.getData("SELECT * FROM Events");
        if (cursor1!=null){
            while (cursor1.moveToNext()){
                long timeStart = cursor1.getLong(3);
                long timeEnd = cursor1.getLong(4);
                int repeat = cursor1.getInt(6);
                List<Event> list1 = new ArrayList<>();
                if (repeat!=0){
                    if (timeStart<=end.getTime()){
                        long dateEndORTimeRepeat = cursor1.getLong(7);
                        String idEvent = String.valueOf(cursor1.getLong(0));
                        String title = cursor1.getString(1);
                        String description = cursor1.getString(2);
                        int notification = cursor1.getInt(5);
                        boolean done = false;
                        list1 = getEventUser(repeat, dateEndORTimeRepeat, timeStart, timeEnd, start, end, idEvent, title, description, done, notification);
                    }
                }else {
                    if (timeStart<=end.getTime() && timeStart>=start.getTime()){
                        String idEvent = String.valueOf(cursor1.getLong(0));
                        String title = cursor1.getString(1);
                        String description = cursor1.getString(2);
                        int notification = cursor1.getInt(5);
                        boolean done = false;
                        if (cursor1.getInt(8)==1){
                            done=true;
                        }
                        list.add(new Event(idEvent,title,timeStart,timeEnd,description,"user",done,"user",notification,repeat,0,0));
                    }
                }
                list.addAll(list1);
            }
        }
        cursor1.close();
        return list;
    }

    private List<Event> getListDevice(Context applicationContext) {
        List<Event> list = new ArrayList<>();
        List<String> listUserShowOff = new ArrayList<>();
        listUserShowOff = getAllAccountShowOff(applicationContext);
        ContentResolver contentResolver = applicationContext.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {CalendarContract.Events.CALENDAR_ID,CalendarContract.Events.TITLE,CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,CalendarContract.Events.DTEND,CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                CalendarContract.Events.DISPLAY_COLOR};

        Cursor cursor = contentResolver.query(uri,projection,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String idEvent = cursor.getString(cursor.getColumnIndex(projection[0]));
                @SuppressLint("Range") long longId = cursor.getLong(cursor.getColumnIndex(projection[0]));
                @SuppressLint("Range") String nameEvent = cursor.getString(cursor.getColumnIndex(projection[1]));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(projection[2]));
                @SuppressLint("Range") long dtStart = cursor.getLong(cursor.getColumnIndex(projection[3]));
                @SuppressLint("Range") long dtEnd = cursor.getLong(cursor.getColumnIndex(projection[4]));
                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(projection[5]));
                @SuppressLint("Range") int colorEvent = cursor.getInt(cursor.getColumnIndex(projection[6]));
                if (!listUserShowOff.contains(displayName)){
                    int check=0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTitle().equals(nameEvent) && list.get(i).getDtStart() == dtStart){
                            check=1;
                            break;
                        }
                    }
                    if (check==0){
                        list.add(new Event(idEvent,nameEvent,dtStart,dtEnd,description,"device",false,displayName,0,0,colorEvent,0));
                    }
                }
            }
            cursor.close();
        }

        return list;
    }

    private List<Event> getEventUser(int repeat, long dateEndORTimeRepeat, long timeStart, long timeEnd, Date start, Date end, String idEvent, String title, String description, boolean done, int notification) {
        List<Event> list = new ArrayList<>();
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarStart.setTime(new Date(timeStart));
        calendarEnd.setTime(new Date(timeEnd));

        int set1 = Calendar.DAY_OF_YEAR, set2 = 1, maxCount = 0;
        switch (repeat){
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
        int numberOfDayStart = calendarStart.get(Calendar.DAY_OF_MONTH), numberOfDayEnd = calendarEnd.get(Calendar.DAY_OF_MONTH);
        int count = 0;

        if (dateEndORTimeRepeat==0){
            long time = calendarStart.getTime().getTime();
            while (time<=end.getTime() && count<maxCount){
                // Lưu công việc
                if (time>=start.getTime()){
                    if (repeat!=3){
                        list.add(new Event(idEvent,title,time,calendarEnd.getTime().getTime(),description,"user",false,"user",notification,repeat,0,0));
                    }else {
                        if (calendarStart.get(Calendar.DAY_OF_MONTH)==numberOfDayStart){
                            list.add(new Event(idEvent,title,time,calendarEnd.getTime().getTime(),description,"user",false,"user",notification,repeat,0,0));
                        }
                    }
                }

                if (repeat!=3){
                    calendarEnd.add(set1,set2);
                    calendarStart.add(set1,set2);
                }else {
                    calendarEnd.add(set1,set2);
                    calendarStart.add(set1,set2);
                    if (calendarStart.get(Calendar.DAY_OF_MONTH) < numberOfDayStart){
                        if (numberOfDayStart <= calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                            calendarStart.set(Calendar.DAY_OF_MONTH,numberOfDayStart);
                        }
                    }
                    if (calendarEnd.get(Calendar.DAY_OF_MONTH) < numberOfDayEnd){
                        if (numberOfDayEnd <= calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)){
                            calendarEnd.set(Calendar.DAY_OF_MONTH,numberOfDayEnd);
                        }
                    }
                }

                time = calendarStart.getTime().getTime();
                count++;
            }
        }else if (dateEndORTimeRepeat>0 && dateEndORTimeRepeat<1000){
            long time = calendarStart.getTime().getTime();

            for (int i=0; i<=dateEndORTimeRepeat; i++){
                if (time<=end.getTime()){
                    // Lưu công việc
                    if (time>=start.getTime()){
                        if (repeat!=3){
                            list.add(new Event(idEvent,title,time,calendarEnd.getTime().getTime(),description,"user",false,"user",notification,repeat,0,0));
                        }else {
                            if (calendarStart.get(Calendar.DAY_OF_MONTH)==numberOfDayStart){
                                list.add(new Event(idEvent,title,time,calendarEnd.getTime().getTime(),description,"user",false,"user",notification,repeat,0,0));
                            }
                        }
                    }
                    if (repeat!=3){
                        calendarEnd.add(set1,set2);
                        calendarStart.add(set1,set2);
                    }else {
                        calendarEnd.add(set1,set2);
                        calendarStart.add(set1,set2);
                        if (calendarStart.get(Calendar.DAY_OF_MONTH) < numberOfDayStart){
                            if (numberOfDayStart <= calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                                calendarStart.set(Calendar.DAY_OF_MONTH,numberOfDayStart);
                            }else {
                                i--;
                            }
                        }
                        if (calendarEnd.get(Calendar.DAY_OF_MONTH) < numberOfDayEnd){
                            if (numberOfDayEnd <= calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)){
                                calendarEnd.set(Calendar.DAY_OF_MONTH,numberOfDayEnd);
                            }
                        }
                    }

                    time = calendarStart.getTime().getTime();
                }else {
                    break;
                }
            }
        }else {
            long time = calendarStart.getTime().getTime();
            if (dateEndORTimeRepeat >= start.getTime()){
                while (time<=end.getTime()){

                    // Lưu công việc
                    if (time>=start.getTime()){
                        if (time<=dateEndORTimeRepeat){
                            if (repeat!=3){
                                list.add(new Event(idEvent,title,time,calendarEnd.getTime().getTime(),description,"user",false,"user",notification,repeat,0,0));
                            }else {
                                if (calendarStart.get(Calendar.DAY_OF_MONTH)==numberOfDayStart){
                                    list.add(new Event(idEvent,title,time,calendarEnd.getTime().getTime(),description,"user",false,"user",notification,repeat,0,0));
                                }
                            }
                        }
                    }
                    if (repeat!=3){
                        calendarEnd.add(set1,set2);
                        calendarStart.add(set1,set2);
                    }else {
                        calendarEnd.add(set1,set2);
                        calendarStart.add(set1,set2);
                        if (calendarStart.get(Calendar.DAY_OF_MONTH) < numberOfDayStart){
                            if (numberOfDayStart <= calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                                calendarStart.set(Calendar.DAY_OF_MONTH,numberOfDayStart);
                            }
                        }
                        if (calendarEnd.get(Calendar.DAY_OF_MONTH) < numberOfDayEnd){
                            if (numberOfDayEnd <= calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)){
                                calendarEnd.set(Calendar.DAY_OF_MONTH,numberOfDayEnd);
                            }
                        }
                    }
                    time = calendarStart.getTime().getTime();
                }
            }
        }
        return list;
    }

    private List<String> getAllAccountShowOff(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingActivity.KEY_EMAIL_OFF, MODE_PRIVATE);
        List<String> list;
        String emails = "";
        if (sharedPreferences!=null){
            emails = sharedPreferences.getString("mail_off","");
        }
        list = new ArrayList<>(Arrays.asList(emails.split("/")));
        return list;
    }
}
