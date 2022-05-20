package com.example.myplanner;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplanner.adapter.EventOfWeekAdapter;
import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.DateOfCalendar;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekFragment extends Fragment {
    private Date dateStart, dateEnd;
    private int positionFragment;
    private List<Event> listEventDevice = new ArrayList<>();
    private TextView txtDay1,txtDay2,txtDay3,txtDay4,txtDay5,txtDay6,txtDay7;
    private TextView txtNumberOfDay1,txtNumberOfDay2,txtNumberOfDay3,txtNumberOfDay4,txtNumberOfDay5,txtNumberOfDay6,txtNumberOfDay7,txtMonth;
    private RecyclerView rcv1,rcv2,rcv3,rcv4,rcv5,rcv6,rcv7;
    private EventOfWeekAdapter adapter1,adapter2,adapter3,adapter4,adapter5,adapter6,adapter7;
    long day1=0,day2=0,day3=0,day4=0,day5=0,day6=0,day7=0;
    List<Event> list1,list2,list3,list4,list5,list6,list7;
    List<EventDone> listDone = new ArrayList<>();
    MyPlannerDatabase myPlannerDatabase;
    SimpleDateFormat formatNumberOfDay = new SimpleDateFormat("dd");
    SimpleDateFormat formatDay = new SimpleDateFormat("EEE");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM, yyyy");
    CallBackInWeekFragment callBackInWeekFragment;
    boolean create = false;
    String language = "";
    SharedPreferences sharedPreferences;

    public interface CallBackInWeekFragment{
        void onClickItemEvent(Event event);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeekFragment() {
        // Required empty public constructor
    }

    public boolean getCreate(){
        return create;
    }

    public WeekFragment(Date dateSt, Date dateEd, List<Event> list, int position){
        this.dateStart = dateSt;
        this.dateEnd = dateEd;
        this.positionFragment = position;
//        this.listEventDevice = list;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public int getPositionFragment(){
        return positionFragment;
    }

    public void setCallbackWeekFragment(CallBackInWeekFragment callback){
        this.callBackInWeekFragment = callback;
    }

    public Date[] getSpaceDate(){
        Date[] spaceDate = new Date[2];
        spaceDate[0] = this.dateStart;
        spaceDate[1] = this.dateEnd;
        return spaceDate;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeekFragment newInstance(String param1, String param2) {
        WeekFragment fragment = new WeekFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Date getDateOfFragment(){
        if (dateStart!=null){
            return dateStart;
        }
        return null;
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
        View view = inflater.inflate(R.layout.fragment_week, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("KEY_LANGUAGE", MODE_PRIVATE);
        language = sharedPreferences.getString("language","");
        if (!language.equals("")){
            MainActivity.setLanguage(getContext(),language);
        }
        addControls(view);
        setTextTxtDay();
        create = true;
        // Inflate the layout for this fragment
        adapter1.setIOnClickItemListener(new EventOfWeekAdapter.IOnClickEventOfWeekListener() {
            @Override
            public void onClickItem(Event event) {
                if (callBackInWeekFragment!=null){
                    callBackInWeekFragment.onClickItemEvent(event);
                }
            }
        });

        adapter2.setIOnClickItemListener(new EventOfWeekAdapter.IOnClickEventOfWeekListener() {
            @Override
            public void onClickItem(Event event) {
                if (callBackInWeekFragment!=null){
                    callBackInWeekFragment.onClickItemEvent(event);
                }
            }
        });

        adapter3.setIOnClickItemListener(new EventOfWeekAdapter.IOnClickEventOfWeekListener() {
            @Override
            public void onClickItem(Event event) {
                if (callBackInWeekFragment!=null){
                    callBackInWeekFragment.onClickItemEvent(event);
                }
            }
        });

        adapter4.setIOnClickItemListener(new EventOfWeekAdapter.IOnClickEventOfWeekListener() {
            @Override
            public void onClickItem(Event event) {
                if (callBackInWeekFragment!=null){
                    callBackInWeekFragment.onClickItemEvent(event);
                }
            }
        });

        adapter5.setIOnClickItemListener(new EventOfWeekAdapter.IOnClickEventOfWeekListener() {
            @Override
            public void onClickItem(Event event) {
                if (callBackInWeekFragment!=null){
                    callBackInWeekFragment.onClickItemEvent(event);
                }
            }
        });

        adapter6.setIOnClickItemListener(new EventOfWeekAdapter.IOnClickEventOfWeekListener() {
            @Override
            public void onClickItem(Event event) {
                if (callBackInWeekFragment!=null){
                    callBackInWeekFragment.onClickItemEvent(event);
                }
            }
        });

        adapter7.setIOnClickItemListener(new EventOfWeekAdapter.IOnClickEventOfWeekListener() {
            @Override
            public void onClickItem(Event event) {
                if (callBackInWeekFragment!=null){
                    callBackInWeekFragment.onClickItemEvent(event);
                }
            }
        });
        return view;
    }

    private void setTextTxtDay() {
        if (dateStart!=null){
            Calendar calendarDay = Calendar.getInstance();
            String currentDay = formatDate.format(calendarDay.getTimeInMillis());
            calendarDay.setTime(dateStart);
            txtDay1.setText(formatDay.format(day1));
            txtNumberOfDay1.setText(formatNumberOfDay.format(day1));
            checkCurrentDay(currentDay,day1,txtDay1,txtNumberOfDay1);

            txtDay2.setText(formatDay.format(day2));
            txtNumberOfDay2.setText(formatNumberOfDay.format(day2));
            checkCurrentDay(currentDay,day2,txtDay2,txtNumberOfDay2);

            txtDay3.setText(formatDay.format(day3));
            txtNumberOfDay3.setText(formatNumberOfDay.format(day3));
            checkCurrentDay(currentDay,day3,txtDay3,txtNumberOfDay3);

            txtDay4.setText(formatDay.format(day4));
            txtNumberOfDay4.setText(formatNumberOfDay.format(day4));
            checkCurrentDay(currentDay,day4,txtDay4,txtNumberOfDay4);

            txtDay5.setText(formatDay.format(day5));
            txtNumberOfDay5.setText(formatNumberOfDay.format(day5));
            checkCurrentDay(currentDay,day5,txtDay5,txtNumberOfDay5);

            txtDay6.setText(formatDay.format(day6));
            txtNumberOfDay6.setText(formatNumberOfDay.format(day6));
            checkCurrentDay(currentDay,day6,txtDay6,txtNumberOfDay6);

            txtDay7.setText(formatDay.format(day7));
            txtNumberOfDay7.setText(formatNumberOfDay.format(day7));
            checkCurrentDay(currentDay,day7,txtDay7,txtNumberOfDay7);
        }
    }

    private void checkCurrentDay(String currentDay,long timeInMillis, TextView txtDay, TextView txtNumberOfDay) {
        if (currentDay.equals(formatDate.format(timeInMillis))){
            txtDay.setTextColor(getContext().getResources().getColor(R.color.blue1));
            txtNumberOfDay.setTextColor(Color.WHITE);
            txtNumberOfDay.setBackgroundResource(R.drawable.background_current_day);
        }
    }

    private void addControls(View view) {
        txtDay1 = view.findViewById(R.id.txtDay1);
        txtDay2 = view.findViewById(R.id.txtDay2);
        txtDay3 = view.findViewById(R.id.txtDay3);
        txtDay4 = view.findViewById(R.id.txtDay4);
        txtDay5 = view.findViewById(R.id.txtDay5);
        txtDay6 = view.findViewById(R.id.txtDay6);
        txtDay7 = view.findViewById(R.id.txtDay7);
        txtNumberOfDay1 = view.findViewById(R.id.txtNumberOfDay1);
        txtNumberOfDay2 = view.findViewById(R.id.txtNumberOfDay2);
        txtNumberOfDay3 = view.findViewById(R.id.txtNumberOfDay3);
        txtNumberOfDay4 = view.findViewById(R.id.txtNumberOfDay4);
        txtNumberOfDay5 = view.findViewById(R.id.txtNumberOfDay5);
        txtNumberOfDay6 = view.findViewById(R.id.txtNumberOfDay6);
        txtNumberOfDay7 = view.findViewById(R.id.txtNumberOfDay7);
        rcv1 = view.findViewById(R.id.rcv1);
        rcv2 = view.findViewById(R.id.rcv2);
        rcv3 = view.findViewById(R.id.rcv3);
        rcv4 = view.findViewById(R.id.rcv4);
        rcv5 = view.findViewById(R.id.rcv5);
        rcv6 = view.findViewById(R.id.rcv6);
        rcv7 = view.findViewById(R.id.rcv7);
        txtMonth = view.findViewById(R.id.txtMonth);

        if (dateStart!=null){
            day1 = dateStart.getTime();
            Calendar calendarDay = Calendar.getInstance();
            calendarDay.setTime(dateStart);
            for (int i=0; i<6; i++){
                calendarDay.add(Calendar.DAY_OF_YEAR,1);
                switch (i){
                    case 0:
                        day2 = calendarDay.getTimeInMillis();
                        break;
                    case 1:
                        day3 = calendarDay.getTimeInMillis();
                        break;
                    case 2:
                        day4 = calendarDay.getTimeInMillis();
                        break;
                    case 3:
                        day5 = calendarDay.getTimeInMillis();
                        break;
                    case 4:
                        day6 = calendarDay.getTimeInMillis();
                        break;
                    case 5:
                        day7 = calendarDay.getTimeInMillis();
                        break;
                    default:
                        break;
                }
            }
        }
        myPlannerDatabase = new MyPlannerDatabase(getContext());
        listEventDevice = getListEventDevice(getContext());
        listDone = getListDone();

        sharedPreferences = getContext().getSharedPreferences(SettingActivity.KEY_EMAIL_OFF,MODE_PRIVATE);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        adapter1 = new EventOfWeekAdapter(getContext());
        list1 = getListEvent(day1);
        rcv1.setLayoutManager(linearLayoutManager1);
        adapter1.setData(list1,listDone);
        rcv1.setAdapter(adapter1);

        adapter2 = new EventOfWeekAdapter(getContext());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        list2 = getListEvent(day2);
        rcv2.setLayoutManager(linearLayoutManager2);
        adapter2.setData(list2,listDone);
        rcv2.setAdapter(adapter2);

        adapter3 = new EventOfWeekAdapter(getContext());
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        list3 = getListEvent(day3);
        rcv3.setLayoutManager(linearLayoutManager3);
        adapter3.setData(list3,listDone);
        rcv3.setAdapter(adapter3);

        adapter4 = new EventOfWeekAdapter(getContext());
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        list4 = getListEvent(day4);
        rcv4.setLayoutManager(linearLayoutManager4);
        adapter4.setData(list4,listDone);
        rcv4.setAdapter(adapter4);

        adapter5 = new EventOfWeekAdapter(getContext());
        LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        list5 = getListEvent(day5);
        rcv5.setLayoutManager(linearLayoutManager5);
        adapter5.setData(list5,listDone);
        rcv5.setAdapter(adapter5);

        adapter6 = new EventOfWeekAdapter(getContext());
        LinearLayoutManager linearLayoutManager6 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        list6 = getListEvent(day6);
        rcv6.setLayoutManager(linearLayoutManager6);
        adapter6.setData(list6,listDone);
        rcv6.setAdapter(adapter6);

        adapter7 = new EventOfWeekAdapter(getContext());
        LinearLayoutManager linearLayoutManager7 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        list7 = getListEvent(day7);
        rcv7.setLayoutManager(linearLayoutManager7);
        adapter7.setData(list7,listDone);
        rcv7.setAdapter(adapter7);

        String month = formatMonth.format(day1);
        txtMonth.setText(month.substring(0,1).toUpperCase()+month.substring(1));
    }

    private List<EventDone> getListDone() {
        List<EventDone> list = new ArrayList<>();
        if (myPlannerDatabase!=null){
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
        }
        return list;
    }

    private List<Event> getListEvent(long day) {
        List<Event> list = new ArrayList<>();
        long endDay = day + (24*60*60*1000) - 1;
        String currentDate = formatDate.format(day);
        for (int i=0; i<listEventDevice.size(); i++){
            if (formatDate.format(listEventDevice.get(i).getDtStart()).equals(currentDate)){
                list.add(listEventDevice.get(i));
            }
        }
        list.addAll(getListEventUser(new Date(day),new Date(endDay)));

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
        return list;
    }

    private List<Event> getListEventUser(Date start, Date end){
        List<Event> list = new ArrayList<>();
        if (myPlannerDatabase!=null){
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

    public List<Event> getListEventDevice(Context context){
        List<Event> list = new ArrayList<>();
        List<String> listUserShowOff = new ArrayList<>();
        listUserShowOff = getAllAccountShowOff(context);
        if (context!=null){
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
        }

        return list;
    }

    private List<String> getAllAccountShowOff(Context context) {
        List<String> list;
        String emails = "";
        if (sharedPreferences!=null){
            emails = sharedPreferences.getString("mail_off","");
        }
        list = new ArrayList<>(Arrays.asList(emails.split("/")));
        return list;
    }

    public void reloadDataInFragment(){
        listEventDevice = getListEventDevice(getContext());
        listDone = getListDone();
        list1 = getListEvent(day1);
        if (adapter1!=null){
            adapter1.setData(list1,listDone);
        }
        list2 = getListEvent(day2);
        if (adapter2!=null){
            adapter2.setData(list2,listDone);
        }
        list3 = getListEvent(day3);
        if (adapter3!=null){
            adapter3.setData(list3,listDone);
        }
        list4 = getListEvent(day4);
        if (adapter4!=null){
            adapter4.setData(list4,listDone);
        }
        list5 = getListEvent(day5);
        if (adapter5!=null){
            adapter5.setData(list5,listDone);
        }
        list6 = getListEvent(day6);
        if (adapter6!=null){
            adapter6.setData(list6,listDone);
        }
        list7 = getListEvent(day7);
        if (adapter7!=null){
            adapter7.setData(list7,listDone);
        }
    }
}