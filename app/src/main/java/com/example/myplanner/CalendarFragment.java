package com.example.myplanner;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myplanner.MainActivity.MAX_CALENDAR_DAYS;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplanner.adapter.DateOfCalendarAdapter;
import com.example.myplanner.adapter.ViewPagerCalendarAdapter;
import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.DateOfCalendar;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PrimitiveIterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements ViewPagerCalendarAdapter.IReload {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView rcvNumberOfDay;
    private TextView txtDate;
    private Calendar mCalendar;
    int position;
    private DateOfCalendarAdapter dateOfCalendarAdapter;
    private List<DateOfCalendar> listDateOfCalendar = new ArrayList<>();
    private Date currentDate;
    private List<Event> listEvent = new ArrayList<>();
    private List<Event> listEventOfMonth = new ArrayList<>();
    private List<EventDone> listEventDone = new ArrayList<>();
    MyPlannerDatabase myPlannerDatabase;
    String firstDay, lastDay, language="";

    public interface CallBack{
        void onclick(int position);
        void onClickDate(DateOfCalendar dateOfCalendar, List<Event> listEvent);
        void onOtherMonthClick(DateOfCalendar dateOfCalendar,int position);
    }


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM, yyyy");
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMMM, yyyy");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM/dd/yy");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarFragment() {
        // Required empty public constructor
    }
    public CalendarFragment(Calendar calendar, Date currentDate, int position, List<Event> list){
        this.mCalendar =calendar;
        this.currentDate =currentDate;
        this.position = position;
//        this.listEvent = list;
    }
    public void reloadData(){
        if (dateOfCalendarAdapter!=null){
            dateOfCalendarAdapter.loadChecked();
        }
    }

    public Calendar getmCalendar() {
        return mCalendar;
    }

    public int getPosition() {
        return position;
    }

    CallBack callBack;
    public void setCallback(CallBack callBack){
        this.callBack =callBack;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
//     TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void reloadDataInFragment(){
        if (dateOfCalendarAdapter!=null){
            listEvent = getListEventDevice(getContext());
            listEventOfMonth = getListEvent(listDateOfCalendar.get(0).getDate(),listDateOfCalendar.get(listDateOfCalendar.size()-1).getDate());
            Calendar calendar = Calendar.getInstance();
            dateOfCalendarAdapter.setData(listDateOfCalendar,calendar.getTime(),listEventOfMonth);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("KEY_LANGUAGE", MODE_PRIVATE);
        language = sharedPreferences.getString("language","");
        if (!language.equals("")){
            MainActivity.setLanguage(getContext(),language);
        }
        rcvNumberOfDay = view.findViewById(R.id.rcvNumberOfDay);
        txtDate = view.findViewById(R.id.txtDate);
        myPlannerDatabase = new MyPlannerDatabase(getContext());
        listEvent = getListEventDevice(getContext());

        // Inflate the layout for this fragment

        listDateOfCalendar = getDateOfCalendar(mCalendar);

        listEventOfMonth = getListEvent(listDateOfCalendar.get(0).getDate(),listDateOfCalendar.get(listDateOfCalendar.size()-1).getDate());

        dateOfCalendarAdapter = new DateOfCalendarAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),7);
        rcvNumberOfDay.setLayoutManager(gridLayoutManager);
        dateOfCalendarAdapter.setData(listDateOfCalendar,currentDate,listEventOfMonth);
        rcvNumberOfDay.setAdapter(dateOfCalendarAdapter);

        txtDate.setText(simpleDateFormat.format(mCalendar.getTime()));

        dateOfCalendarAdapter.onClickItemListener(new DateOfCalendarAdapter.IOnClickItemCalendarAdapter() {
            @Override
            public void onItemClick(DateOfCalendar dateOfCalendar, List<Event> listEvent, int positionItem) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_PLANNER", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date_checked",simpleDateFormat1.format(dateOfCalendar.getDate()));
                editor.commit();
                callBack.onclick(position);
                callBack.onClickDate(dateOfCalendar,listEvent);
                dateOfCalendarAdapter.loadChecked();
//                int currentCheckedPosition = dateOfCalendarAdapter.getCurrentCheckedPosition();
//                dateOfCalendarAdapter.notifyItemChanged(currentCheckedPosition);
//                dateOfCalendarAdapter.notifyItemChanged(positionItem);
            }

            @Override
            public void onOtherMonthItemClick(DateOfCalendar dateOfCalendar, int position) {
                callBack.onOtherMonthClick(dateOfCalendar,position);
            }

        });
        return view;
    }

    private List<EventDone> getListEventDone(Date dateStart, Date dateEnd) {
        List<EventDone> list = new ArrayList<>();
        Cursor cursor = myPlannerDatabase.getData("select * from EventsDone where TimeStart >= "+dateStart.getTime()+" and TimeStart <= "+dateEnd.getTime()+"");
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

    /*
    *
    *
    *
    *
    *
    *           SỬA LỖI LƯU SỰ KIỆN VÀO CÁC NGÀY 31, 30, 29
    *
    *
    *
    *
    *
    *
    * */
    private List<Event> getListEvent(Date start, Date end){
//        SimpleDateFormat formatterr = new SimpleDateFormat("hh:mm:ss MM/dd/yy");
//        Date start = formatterr.parse("00:00:00 "+firstDay);
//        Date end = formatterr.parse("23:59:59 "+lastDay);

        List<Event> list = new ArrayList<>();
        for (int i = 0; i < listEvent.size(); i++) {
            if (start.getTime()<=listEvent.get(i).getDtStart() && listEvent.get(i).getDtStart()<=end.getTime()){
                list.add(listEvent.get(i));
            }
        }
//        CREATE TABLE IF NOT EXISTS Events(IdEvent LONG,Event VARCHAR(200), Description VARCHAR(1000), TimeStart LONG, TimeEnd LONG, Notification INTEGER, Repeat INTEGER, DateEndORTimeRepeat LONG, Done INTEGER)
        Cursor cursor = myPlannerDatabase.getData("SELECT * FROM Events");
        if (cursor!=null){
            List<Event> list1 = new ArrayList<>();
            while (cursor.moveToNext()){
                long timeStart = cursor.getLong(3);
                long timeEnd = cursor.getLong(4);
                int repeat = cursor.getInt(6);
                if (repeat!=0){
                    if (timeStart<=end.getTime()){
                        long dateEndORTimeRepeat = cursor.getLong(7);
                        String idEvent = String.valueOf(cursor.getLong(0));
                        String title = cursor.getString(1);
                        String description = cursor.getString(2);
                        int notification = cursor.getInt(5);
                        boolean done = false;
                        list1 = getEventUser(repeat, dateEndORTimeRepeat, timeStart, timeEnd, start, end, idEvent, title, description, done, notification);
                    }
                }else {
                    if (timeStart<=end.getTime() && timeStart>=start.getTime()){
                        String idEvent = String.valueOf(cursor.getLong(0));
                        String title = cursor.getString(1);
                        String description = cursor.getString(2);
                        int notification = cursor.getInt(5);
                        boolean done = false;
                        if (cursor.getInt(8)==1){
                            done=true;
                        }
                        list.add(new Event(idEvent,title,timeStart,timeEnd,description,"user",done,"user",notification,repeat,0,0));
                    }
                }
                // SẮP XẾP DANH SÁCH THEO THỨ TỰ TĂNG DẦN THỜI GIAN BẮT ĐẦU SỰ KIỆN
                Collections.sort(list1, new Comparator<Event>() {
                    @Override
                    public int compare(Event event, Event t1) {
                        if (event.getDtStart() > t1.getDtStart()){
                            return -1;
                        }else if (event.getDtStart() < t1.getDtStart()){
                            return 1;
                        }
                        return 0;
                    }
                });
                list.addAll(list1);
                list1.clear();
            }
        }
        cursor.close();
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

        //Event(String idEvent, String title, long dtStart, long dtEnd, String description, String type, boolean done, String calendarDisplayName)
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

    private List<DateOfCalendar> getDateOfCalendar(Calendar mCalendar) {
        List<DateOfCalendar> list = new ArrayList<>();
        if (mCalendar!=null){
            Calendar monthCalendar = (Calendar) mCalendar.clone();
            monthCalendar.set(Calendar.DAY_OF_MONTH,1);
            int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-2;
            if (firstDayOfMonth<0){
                monthCalendar.add(Calendar.DAY_OF_MONTH,-6);
            }else {
                monthCalendar.add(Calendar.DAY_OF_MONTH,-firstDayOfMonth);
            }
            monthCalendar.set(Calendar.HOUR_OF_DAY,0);
            monthCalendar.set(Calendar.MINUTE,0);
            monthCalendar.set(Calendar.SECOND,0);
            monthCalendar.set(Calendar.MILLISECOND,0);

            while (list.size()<MAX_CALENDAR_DAYS){
                list.add(new DateOfCalendar(monthCalendar.getTime(),""));
                monthCalendar.add(Calendar.DAY_OF_MONTH,1);
                if (list.size()==MAX_CALENDAR_DAYS-1){
                    monthCalendar.set(Calendar.HOUR_OF_DAY,23);
                    monthCalendar.set(Calendar.MINUTE,59);
                    monthCalendar.set(Calendar.SECOND,59);
                    monthCalendar.set(Calendar.MILLISECOND,999);
                }
            }
        }
        return list;
    }

    public List<Event> getListEventDevice(Context context){
        List<Event> list = new ArrayList<>();
        List<String> listUserShowOff = new ArrayList<>();
        listUserShowOff = getAllAccountShowOff();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {CalendarContract.Events.CALENDAR_ID,CalendarContract.Events.TITLE,CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,CalendarContract.Events.DTEND,CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                CalendarContract.Events.DISPLAY_COLOR,CalendarContract.Events.ACCOUNT_NAME};

        Cursor cursor = contentResolver.query(uri,projection,null,null,null);
//        String titleOfLastEvent = "";
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

    private List<String> getAllAccountShowOff() {
        List<String> list;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SettingActivity.KEY_EMAIL_OFF,MODE_PRIVATE);
        String emails = sharedPreferences.getString("mail_off","");
        list = new ArrayList<>(Arrays.asList(emails.split("/")));
        return list;
    }

    @Override
    public void reload() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadChecked(){
        dateOfCalendarAdapter.loadChecked();
    }
}