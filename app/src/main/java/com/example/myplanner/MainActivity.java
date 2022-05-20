package com.example.myplanner;

import static com.applandeo.materialcalendarview.utils.CalendarProperties.CALENDAR_SIZE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplanner.adapter.EventOfDayRcvMonthAdapter;
import com.example.myplanner.adapter.FileImportAdapter;
import com.example.myplanner.adapter.ListWorkAdapter;
import com.example.myplanner.adapter.ViewPagerAddFragmentsAdapter;
import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.DateOfCalendar;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;
import com.example.myplanner.model.Work;
import com.example.myplanner.notification.BroadcastReceiverTest;
import com.example.myplanner.notification.MyBroadcastReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.hsalf.smileyrating.SmileyRating;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    public static final int MAX_CALENDAR_DAYS = 42;
    private static final int REQUEST_PERMISSION_IMPORT_CODE = 1,REQUEST_PERMISSION_EXPORT_CODE = 2;
    public static final int JOB_ID = 123;
    private static ActivityResultLauncher<Intent> launcherWriteFile;
    private ImageView imgPreviousCalendar,imgForwardCalendar, imgMenu, imgAddEventUp, imgAddEventDown, imgCalendarToday, imgRefresh;
    private RelativeLayout layoutToolbar;
    private FloatingActionButton fabAddTask;
    private DrawerLayout drawerLayout;
    private RecyclerView rcvEventOfDay, rcvListWork;
    private LinearLayout layoutNoEvent;
    private TextView txtCheckedDay;
    private ViewPager2 viewPagerCalendar, viewPagerWeek;
    private FrameLayout fragmentContainer;
    String strCurrentDate = "";
    private Date currentDay = new Date();
    MyPlannerDatabase myPlannerDatabase;
    ViewPagerAddFragmentsAdapter viewPagerAddFragmentsAdapter, viewPagerAddFragmentsAdapter2;
    int countingsStars;
    String rateStatus = "RateStatus", language = "";
    LinearLayout layoutExport;
    BroadcastListenerDateChange broadcastListenerDateChange;

//    ChineseCalendar chineseCalendar = (ChineseCalendar) ChineseCalendar.getInstance();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM, yyyy");
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMMM, yyyy");
    List<Event> listEvent = new ArrayList<>();

    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm_MM/dd/yy");
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM yyyy");
    SimpleDateFormat formatNumberOfMonth = new SimpleDateFormat("MM");
    List<Date> listDate = new ArrayList<>();
    List<DateOfCalendar> listDateOfCalender = new ArrayList<>();

    EventOfDayRcvMonthAdapter eventOfDayRcvMonthAdapter;
    List<Event> listEventOfCheckedDay = new ArrayList<>();
    List<EventDone> listEventDoneOfCheckedDay = new ArrayList<>();

    ActivityResultLauncher<Intent> activityResultLauncher, launcherReadFile;
    List<CalendarFragment> calendarFragmentList=new ArrayList<>();
    int currentWeekPosition = 0;

    ListWorkAdapter listWorkAdapter;
    List<Event> listEventForListWork = new ArrayList<>();
    int currentDayPosition = 0;

    List<WeekFragment> listWeekFragment = new ArrayList<>();

    WeekFragment.CallBackInWeekFragment callBackInWeekFragment = new WeekFragment.CallBackInWeekFragment() {
        @Override
        public void onClickItemEvent(Event event) {
            Intent intent = new Intent(MainActivity.this,EventActivity.class);
            intent.putExtra("EVENT",event);
            activityResultLauncher.launch(intent);
        }
    };

    CalendarFragment.CallBack callBack = new CalendarFragment.CallBack() {
        @Override
        public void onclick(int position) {
            calendarFragmentList.get(position+1).reloadData();
            calendarFragmentList.get(position-1).reloadData();
            calendarFragmentList.get(position+2).reloadData();
            calendarFragmentList.get(position-2).reloadData();
        }

        @Override
        public void onClickDate(DateOfCalendar dateOfCalendar, List<Event> listEvent) {
            currentDay = dateOfCalendar.getDate();
            setDone(listEvent);
            listEventOfCheckedDay.clear();
            listEventOfCheckedDay = listEvent;
            eventOfDayRcvMonthAdapter.setData(listEventOfCheckedDay,listEventDoneOfCheckedDay);
            checkListEventOfDay(listEventOfCheckedDay);

            String date;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                date = DateFormat.getDateInstance(DateFormat.LONG).format(dateOfCalendar.getDate());
            }else {
                date = simpleDateFormat1.format(dateOfCalendar.getDate());
            }

            if (date.equals(strCurrentDate)){
                txtCheckedDay.setText(getString(R.string.today)+" "+date);
                txtCheckedDay.setTextColor(getResources().getColor(R.color.orange1));
            }else {
                txtCheckedDay.setText(date);
                txtCheckedDay.setTextColor(getResources().getColor(R.color.black));
            }
        }

        @Override
        public void onOtherMonthClick(DateOfCalendar dateOfCalendar, int position) {
            int i = viewPagerCalendar.getCurrentItem();
            if (position<7){
                viewPagerCalendar.setCurrentItem(i-1);
            }else {
                viewPagerCalendar.setCurrentItem(i+1);
            }
        }
    };

    private Button btnTestService, btnTestStopService;
    private MyBoundService myService;
    private boolean isServiceConnection;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBoundService.MyBinder myBinder = (MyBoundService.MyBinder) iBinder;
            myService = myBinder.getMyService();
            myService.startMusic();
            isServiceConnection=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceConnection=false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.gray1), false, false, MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
        language = sharedPreferences.getString("language","");
        if (!language.equals("")){
            setLanguage(this,language);
        }
        setContentView(R.layout.activity_main);

        addControls();

        btnTestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, MyBoundService.class);
//                bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);

//                Intent intent = new Intent(MainActivity.this,MyService.class);
//                intent.putExtra("key data intent", getString(R.string.notification_application_running));
//                startService(intent);

                startAlarmAndSchedule();
            }
        });

        btnTestStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isServiceConnection){
//                    unbindService(serviceConnection);
//                    isServiceConnection = false;
//                }

//                Intent intent = new Intent(MainActivity.this,MyService.class);
//                stopService(intent);

                stopScheduleJob(MainActivity.this,JOB_ID);
                updateWidget(MainActivity.this);
            }
        });

        launcherReadFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Uri uri = result.getData().getData();
                    if (uri !=null){
                        try {
                            File tempFile = FileUtil.from(MainActivity.this,uri);
                            readFileJsonData(tempFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Doc file khong thanh cong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        launcherWriteFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Uri uri = result.getData().getData();
                    if (uri!=null){
                        //TODO Luu file json
                        List<Work> listWork = new ArrayList<>();
                        listWork = getAllData();
                        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                        String prettyJson = prettyGson.toJson(listWork);
                        byte[] data = prettyJson.getBytes(StandardCharsets.UTF_8);
                        saveFileFromUri(data, uri);
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Luu file khong thanh cong", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        layoutExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkStoragePermission()){
                    saveFileExportFromDatabase();
                }else {
                    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
                    requestPermissions(permissions,REQUEST_PERMISSION_EXPORT_CODE);
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    reloadAll();
                } else if (result.getResultCode() == SettingActivity.RESULT_SETTINGS){
                    if (MainActivity.this.checkSelfPermission(Manifest.permission.READ_CALENDAR)== PackageManager.PERMISSION_GRANTED
                            && MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
                        listEvent = getListEventDevice(MainActivity.this);
                    }
                    reloadRcvListWork();
                    reloadDataLayoutMonth("reload");
                    reloadViewPagerWeek();
                    updateWidget(MainActivity.this);
                }
            }
        });

        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // RELOAD DỮ LIỆU
                reloadRcvListWork();
                reloadDataLayoutMonth("refresh");
                reloadViewPagerWeek();
                updateWidget(MainActivity.this);
                setAllCurrentItem();
            }
        });

        listWorkAdapter.setIOnClickEventAndWorkListener(new ListWorkAdapter.IOnClickEventAndWorkListener() {
            @Override
            public void onClickCheckItem(Event event, int position) {
                // LƯU VÀ XÓA DỮ LIỆU CÔNG VIỆC HOÀN THÀNH TRONG DATABASE
                long idEvent = Long.parseLong(event.getIdEvent());
                String dateCompleted = format.format(event.getDtStart());
                long timeStart = event.getDtStart();
                if (event.isDone()){
                    myPlannerDatabase.queryData("insert into EventsDone values ("+idEvent+",'"+dateCompleted+"',"+timeStart+")");
                    reloadDataLayoutMonth("reload");
                }else {
                    myPlannerDatabase.queryData("delete from EventsDone where IdEvent = "+idEvent+" and DateCompleted = '"+dateCompleted+"'");
                    reloadDataLayoutMonth("reload");
                }
            }

            @Override
            public void onClickItemToDetails(Event event) {
                // XEM CHI TIẾT CÔNG VIỆC VÀ SỰ KIỆN
                Intent intent = new Intent(MainActivity.this,EventActivity.class);
                intent.putExtra("EVENT",event);
                activityResultLauncher.launch(intent);
            }

            @Override
            public void onClickItemToCreate(Event event) {
                // TẠO CÔNG VIỆC MỚI TRONG NGÀY HIỆN TẠI
                Intent intent = new Intent(MainActivity.this,CreateEventActivity.class);
                Calendar calendarTemp = Calendar.getInstance();
                intent.putExtra("DATE",calendarTemp.getTime());
                intent.putExtra("MISSION","create");
                activityResultLauncher.launch(intent);
            }
        });

        imgCalendarToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setAllCurrentItem();

            }
        });

        imgAddEventUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityCreateEvent();
            }
        });

        imgAddEventDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityCreateEvent();
            }
        });

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityCreateEvent();
            }
        });

        eventOfDayRcvMonthAdapter.setOnItemClickListener(new EventOfDayRcvMonthAdapter.IOnClickEventOfDay() {
            @Override
            public void onItemClick(Event event) {
                Intent intent = new Intent(MainActivity.this,EventActivity.class);
                intent.putExtra("EVENT",event);
                activityResultLauncher.launch(intent);
            }

            @Override
            public void onCheckItemClick(Event event) {
                long idEvent = Long.parseLong(event.getIdEvent());
                String dateCompleted = format.format(event.getDtStart());
                long timeStart = event.getDtStart();
                if (event.isDone()){
                    myPlannerDatabase.queryData("insert into EventsDone values ("+idEvent+",'"+dateCompleted+"',"+timeStart+")");
                    reloadListWorkAdapter(event);
                }else {
                    myPlannerDatabase.queryData("delete from EventsDone where IdEvent = "+idEvent+" and DateCompleted = '"+dateCompleted+"'");
                    reloadListWorkAdapter(event);
                }
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        imgForwardCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = viewPagerCalendar.getCurrentItem()+1;
                viewPagerCalendar.setCurrentItem(i);
            }
        });

        imgPreviousCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = viewPagerCalendar.getCurrentItem()-1;
                viewPagerCalendar.setCurrentItem(i);
            }
        });

        viewPagerCalendar.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void startAlarmAndSchedule() {
        Intent intent = new Intent(getApplicationContext(), BroadcastReceiverTest.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),200,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis()+(24*60*60*1000),pendingIntent);

        List<Event> listWorks = new ArrayList<>();
        listWorks = getListWorkOfDay(new Date(calendar.getTimeInMillis()),new Date(calendar.getTimeInMillis()+(24*60*60*1000)),MainActivity.this);
        if (listWorks.size()>0){
            registerNotification(listWorks, MainActivity.this);
        }

        startScheduleJob(MainActivity.this);
        Toast.makeText(MainActivity.this, "set_up", Toast.LENGTH_SHORT).show();
    }

    public static void registerNotification(List<Event> listEvents, Context context) {
        for (Event event :listEvents){
            CreateEventActivity.createNotification(event.getIdEvent(),event.getDtStart(),event.getNotification(),event.getTitle(),event.getDescription(),context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Event> getListWorkOfDay(Date timeStart, Date timeEnd, Context context) {
        List<Event> list = new ArrayList<>();
        MyPlannerDatabase myPlannerDatabase = new MyPlannerDatabase(context);
        list = MainActivity.getListEventUser(timeStart,timeEnd,myPlannerDatabase);
        Iterator<Event> iterator = list.iterator();
        while (iterator.hasNext()){
            Event event = iterator.next();
            if (event.getNotification()==0){
                iterator.remove();
            }
        }
        return list;
    }

    private void setAllCurrentItem() {
        for (int i=0; i<listEventForListWork.size(); i++){
            Event event = listEventForListWork.get(i);
            if (event!=null){
                String dateOfEvent = format.format(event.getDtStart());
                if (dateOfEvent.equals(format.format(System.currentTimeMillis()))){
                    rcvListWork.scrollToPosition(i-1);
                    break;
                }
            }
        }

        for (CalendarFragment fragment :calendarFragmentList){
            if (fragment!=null){
                String dateOfFragment = simpleDateFormat.format(fragment.getmCalendar().getTime());
                if (dateOfFragment.equals(simpleDateFormat.format(System.currentTimeMillis()))){
                    viewPagerCalendar.setCurrentItem(fragment.getPosition());
                    break;
                }
            }
        }

        for (WeekFragment fragment :listWeekFragment){
            if (fragment!=null){
                Date[] spaceDate;
                spaceDate = fragment.getSpaceDate();
                if (spaceDate!=null){
                    if (spaceDate[0].getTime()<System.currentTimeMillis() && spaceDate[1].getTime()>System.currentTimeMillis()){
                        viewPagerWeek.setCurrentItem(fragment.getPositionFragment());
                        break;
                    }
                }
            }
        }
    }

    private void reloadAll() {
        reloadRcvListWork();
        reloadDataLayoutMonth("reload");
        reloadViewPagerWeek();
        updateWidget(MainActivity.this);
    }

    private void saveFileFromUri(byte[] data, Uri uri) {
        try {
            FileOutputStream fileOutputStream = (FileOutputStream) getContentResolver().openOutputStream(uri);
            fileOutputStream.write(data);
            fileOutputStream.close();
            Toast.makeText(MainActivity.this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.save_file_failed), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.save_file_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public static void stopScheduleJob(Context context, int job_id){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(job_id);
    }


    public static void startScheduleJob(Context context) {
        //TODO: Schedule

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.add(Calendar.DAY_OF_YEAR,1);
        long delay = calendar.getTimeInMillis()-System.currentTimeMillis();
        ComponentName componentName = new ComponentName(context,MyJobService.class);

        JobInfo jobInfo = new JobInfo.Builder(JOB_ID,componentName)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setOverrideDeadline(delay)
//                .setPeriodic(24*60*60*1000)
                .build();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    public static void setLanguage(Context context,String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.locale = locale;
        configuration.setLayoutDirection(locale); // for RTL changes
//        preferences.setLocalePref("en");
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    private void reloadViewPagerWeek() {
        int position = viewPagerWeek.getCurrentItem();
        if (listWeekFragment.get(position+1).getCreate()){
            listWeekFragment.get(position+1).reloadDataInFragment();
        }
        if (listWeekFragment.get(position-1).getCreate()){
            listWeekFragment.get(position-1).reloadDataInFragment();
        }
        listWeekFragment.get(position).reloadDataInFragment();
        if (listWeekFragment.get(position+2).getCreate()){
            listWeekFragment.get(position+2).reloadDataInFragment();
        }
        if (listWeekFragment.get(position-2).getCreate()){
            listWeekFragment.get(position-2).reloadDataInFragment();
        }
    }

    private void reloadRcvListWork() {
        RcvListWorkTask myTask = new RcvListWorkTask();
        myTask.execute();
    }

    private void reloadListWorkAdapter(Event event) {
        boolean done = event.isDone();
        for (int i=0; i<listEventForListWork.size(); i++){
            if (listEventForListWork.get(i).getIdEvent().equals(event.getIdEvent()) && listEventForListWork.get(i).getDtStart()==event.getDtStart()){
                listEventForListWork.get(i).setDone(done);
                listWorkAdapter.reload();
                break;
            }
        }
    }

    private void reloadDataLayoutMonth(String type) {
        //RELOAD LIST EVENT OF CHECKED DAY

        if (type.equals("refresh")){
            Calendar calendarTemp = Calendar.getInstance();
            currentDay = calendarTemp.getTime();
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                strCurrentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
            }else {
                strCurrentDate = simpleDateFormat1.format(calendar.getTime());
            }
            txtCheckedDay.setText(getString(R.string.today)+" "+strCurrentDate);
            txtCheckedDay.setTextColor(getResources().getColor(R.color.orange1));
            SharedPreferences sharedPreferences = getSharedPreferences("MY_PLANNER", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("date_checked",null);
            editor.commit();
        }

        DataLayoutMonthTask myTask = new DataLayoutMonthTask();
        myTask.execute();
    }

    private void startActivityCreateEvent() {
        Intent intent = new Intent(MainActivity.this,CreateEventActivity.class);
        if (currentDay!=null){
            intent.putExtra("DATE",currentDay);
        }else {
            intent.putExtra("DATE",calendar.getTime());
        }
        intent.putExtra("MISSION","create");
//        startActivity(intent);
        activityResultLauncher.launch(intent);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void changeDate() {
        String month = simpleDateFormat.format(calendar.getTime());
    }

    private void addControls() {
        imgForwardCalendar = findViewById(R.id.imgForwardCalendar);
        imgPreviousCalendar = findViewById(R.id.imgPreviousCalendar);
        fabAddTask = findViewById(R.id.fabAddTask);
        imgMenu = findViewById(R.id.imgMenu);
        layoutToolbar = findViewById(R.id.layoutToolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        imgRefresh = findViewById(R.id.imgRefresh);

        rcvEventOfDay = findViewById(R.id.rcvEventOfDay);
        layoutNoEvent = findViewById(R.id.layoutNoEvent);
        imgAddEventDown = findViewById(R.id.imgAddEventDown);
        imgAddEventUp = findViewById(R.id.imgAddEventUp);
        txtCheckedDay = findViewById(R.id.txtCheckedDay);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        imgCalendarToday = findViewById(R.id.imgCalendarToday);
        rcvListWork = findViewById(R.id.rcvListWork);
        viewPagerWeek = findViewById(R.id.viewPagerWeek);

        viewPagerCalendar = findViewById(R.id.viewPagerCalendar);
        myPlannerDatabase = new MyPlannerDatabase(getApplicationContext());
        currentDay = calendar.getTime();

        if (MainActivity.this.checkSelfPermission(Manifest.permission.READ_CALENDAR)== PackageManager.PERMISSION_GRANTED
                && MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            listEvent = getListEventDevice(MainActivity.this);
        }

        listEventDoneOfCheckedDay = getListEventDoneOfCheckedDay(currentDay);

        eventOfDayRcvMonthAdapter = new EventOfDayRcvMonthAdapter(MainActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,RecyclerView.VERTICAL,false);
        rcvEventOfDay.setLayoutManager(linearLayoutManager);
        eventOfDayRcvMonthAdapter.setData(getListEventOfCurrentDay(listEvent,currentDay),listEventDoneOfCheckedDay);
        rcvEventOfDay.setAdapter(eventOfDayRcvMonthAdapter);
        checkListEventOfDay(listEventOfCheckedDay);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            strCurrentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        }else {
            strCurrentDate = simpleDateFormat1.format(calendar.getTime());
        }
        txtCheckedDay.setText(getString(R.string.today)+" "+strCurrentDate);
        txtCheckedDay.setTextColor(getResources().getColor(R.color.orange1));

        viewPagerAddFragmentsAdapter = new ViewPagerAddFragmentsAdapter(getSupportFragmentManager(),getLifecycle());
        if (calendar!=null){
            List<Event> listEventPush = new ArrayList<>();
            for (int i = 0; i < listEvent.size(); i++) {
                if (!listEvent.get(i).getType().equals("user")){
                    listEventPush.add(listEvent.get(i));
                }
            }
            for (int i=0;i<CALENDAR_SIZE;i++ ){
                int j = i-1200;
                Calendar calendar1 = (Calendar) calendar.clone();
                calendar1.add(Calendar.MONTH,j);
                CalendarFragment calendarFragment1;
                if (j==0){
                    calendarFragment1 = new CalendarFragment(calendar1,calendar.getTime(),i,listEventPush);
                }else {
                    calendarFragment1 = new CalendarFragment(calendar1,null,i,listEventPush);
                }
                calendarFragmentList.add(calendarFragment1);
                calendarFragment1.setCallback(callBack);
                viewPagerAddFragmentsAdapter.addFrag(calendarFragment1);
            }
        }
        viewPagerCalendar.setAdapter(viewPagerAddFragmentsAdapter);
        viewPagerCalendar.setCurrentItem((CALENDAR_SIZE/2),false);

        listEventForListWork = getListEventForListWork();
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MainActivity.this,RecyclerView.VERTICAL,false);
        rcvListWork.setLayoutManager(linearLayoutManager1);
        listWorkAdapter = new ListWorkAdapter(MainActivity.this);
        listEventForListWork = getListEventForListWork();
        listWorkAdapter.setData(listEventForListWork,getListDone(),getCurrentDate());
        rcvListWork.setAdapter(listWorkAdapter);
        if (currentDayPosition > 0){
            rcvListWork.scrollToPosition(currentDayPosition - 1);
        }else {
            rcvListWork.scrollToPosition(currentDayPosition);
        }

        viewPagerAddFragmentsAdapter2 = new ViewPagerAddFragmentsAdapter(getSupportFragmentManager(),getLifecycle());
        if (calendar!=null){
            Date startDate, endDate;
            Calendar startCalendarTemp = Calendar.getInstance();
            Calendar endCalendarTemp = Calendar.getInstance();
            startDate = getDate(startCalendarTemp,"start");
            endDate = getDate(startCalendarTemp,"end");
            startCalendarTemp.setTime(startDate);
            endCalendarTemp.setTime(new Date(startDate.getTime()+(7*24*60*60*1000-1)));

            List<Event> listEventSend = new ArrayList<>();
            for (int i = 0; i < listEvent.size(); i++) {
                if (!listEvent.get(i).getType().equals("user")){
                    listEventSend.add(listEvent.get(i));
                }
            }

            int position = 0;
            while (startCalendarTemp.getTime().getTime() < endDate.getTime()){
                WeekFragment weekFragment = new WeekFragment(startCalendarTemp.getTime(),endCalendarTemp.getTime(),listEventSend,position);
                listWeekFragment.add(weekFragment);
                weekFragment.setCallbackWeekFragment(callBackInWeekFragment);
                viewPagerAddFragmentsAdapter2.addFrag(weekFragment);
                if (startCalendarTemp.getTimeInMillis()<=System.currentTimeMillis() && System.currentTimeMillis()<=endCalendarTemp.getTimeInMillis()){
                    currentWeekPosition = listWeekFragment.size()-1;
                }
                startCalendarTemp.add(Calendar.WEEK_OF_YEAR,1);
                endCalendarTemp.add(Calendar.WEEK_OF_YEAR,1);
                position++;
            }
        }
        viewPagerWeek.setAdapter(viewPagerAddFragmentsAdapter2);
        viewPagerWeek.setCurrentItem(currentWeekPosition,false);

        layoutExport = findViewById(R.id.layoutExport);

        btnTestService = findViewById(R.id.btnTestService);
        btnTestStopService = findViewById(R.id.btnTestStopService);
        listenerClickFromWidget();
    }

    private Date getCurrentDate() {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.HOUR_OF_DAY,0);
        currentCalendar.set(Calendar.MINUTE,0);
        currentCalendar.set(Calendar.SECOND,0);
        currentCalendar.set(Calendar.MILLISECOND,1);
        return currentCalendar.getTime();
    }

    private List<Event> getListEventForListWork() {
        List<Event> list = new ArrayList<>();
        Date startDate, endDate;
        Calendar startCalendarTemp = Calendar.getInstance();
        Calendar endCalendarTemp = Calendar.getInstance();
        startDate = getDate(startCalendarTemp,"start");
        endDate = getDate(startCalendarTemp,"end");
        startCalendarTemp.setTime(startDate);
        endCalendarTemp.setTime(new Date(startDate.getTime()+(7*24*60*60*1000-1)));


        long time = startCalendarTemp.getTime().getTime();

        String month = formatMonth.format(endCalendarTemp.getTime());
        list.add(new Event("",month,endCalendarTemp.getTime().getTime(),0,"","title_month",false,"title_month",0,endCalendarTemp.get(Calendar.MONTH)+1,0,0));
        do {
            list.add(new Event("","",time,endCalendarTemp.getTime().getTime(),"","period",false,"period",0,0,0,0));
            if (!formatMonth.format(time).equals(formatMonth.format(endCalendarTemp.getTime().getTime()+1))){
                String numberOfMonth = formatNumberOfMonth.format(endCalendarTemp.getTime().getTime()+1);
                list.add(new Event("",formatMonth.format(endCalendarTemp.getTime()),endCalendarTemp.getTime().getTime(),0,"","title_month",false,"title_month",0,Integer.valueOf(numberOfMonth),0,0));
            }
            startCalendarTemp.add(Calendar.WEEK_OF_YEAR,1);
            endCalendarTemp.add(Calendar.WEEK_OF_YEAR,1);
            time = startCalendarTemp.getTime().getTime();
        }while (startCalendarTemp.getTime().getTime() < endDate.getTime());

        if (listEvent!=null){
            for (int i=0; i<listEvent.size(); i++){
                if (listEvent.get(i).getDtStart() >= startDate.getTime() && listEvent.get(i).getDtStart() <= endDate.getTime()){
                    list.add(listEvent.get(i));
                }
            }
        }

        List<Event> listWorkOfUser = new ArrayList<>();
        listWorkOfUser = getListEventUser(startDate,endDate,myPlannerDatabase);
        if (listWorkOfUser.size()>0){
            list.addAll(listWorkOfUser);
        }

        int notHave = 0;
        Date currentDate = getCurrentDate();
        String strCurrentDate = format.format(currentDate);
        for (int i=0; i<list.size(); i++){
            Event eventTemp = list.get(i);
            if (eventTemp.getType().equals("device") || eventTemp.getType().equals("user")){
                if (strCurrentDate.equals(format.format(eventTemp.getDtStart()))){
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
                String strTemp = format.format(eventTemp.getDtStart());
                if (dateTemp.equals("") || !dateTemp.equals(strTemp)){
                    list.get(i).setVisibleDate(1);
                    dateTemp = strTemp;
                }

                if (currentDayPosition == 0 && strCurrentDate.equals(strTemp)){
                    currentDayPosition = i;
                }
            }
        }

        setDone(list);

        return list;
    }

    private Date getDate(Calendar mCalendar,String type) {
        Calendar calendar = (Calendar) mCalendar.clone();
        if (type.equals("start")){
            calendar.add(Calendar.YEAR,-1);
            calendar.set(Calendar.DAY_OF_MONTH,1);
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
        }else if (type.equals("end")){
            calendar.add(Calendar.YEAR,1);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.getMaximum(Calendar.DAY_OF_MONTH));
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

    private List<EventDone> getListDone() {
        List<EventDone> list = new ArrayList<>();
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

    //  LẤY DANH SÁCH CÔNG VIỆC ĐÃ HOÀN THÀNH TRONG NGÀY ĐANG CHỌN
    private List<EventDone> getListEventDoneOfCheckedDay(Date currentDay) {
        List<EventDone> list = new ArrayList<>();
        String currentDate = format.format(currentDay);
        Cursor cursor = myPlannerDatabase.getData("select * from EventsDone where DateCompleted = '"+currentDate+"'");
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

    private void checkListEventOfDay(List<Event> listEventOfCheckedDay) {
        if (listEventOfCheckedDay.size()==0){
            rcvEventOfDay.setVisibility(View.GONE);
            layoutNoEvent.setVisibility(View.VISIBLE);
        }else {
            rcvEventOfDay.setVisibility(View.VISIBLE);
            layoutNoEvent.setVisibility(View.GONE);
        }
    }

    private List<Event> getListEventOfCurrentDay(List<Event> listEvent, Date currentDay) {
        List<Event> list = new ArrayList<>();
        String currentDate = simpleDateFormat1.format(calendar.getTime());
        for (int i=0; i<listEvent.size(); i++){
            if (simpleDateFormat1.format(listEvent.get(i).getDtStart()).equals(currentDate)){
                list.add(listEvent.get(i));
            }
        }
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(currentDay);
        calendarStart.set(Calendar.HOUR_OF_DAY,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarStart.set(Calendar.SECOND,0);
        calendarStart.set(Calendar.MILLISECOND,0);

        Calendar calendarEnd = (Calendar) calendarStart.clone();
        calendarEnd.add(Calendar.DAY_OF_YEAR,1);
        calendarEnd.add(Calendar.MILLISECOND,-1);

        Date start = calendarStart.getTime();
        Date end = calendarEnd.getTime();
        list.addAll(getListEventUser(start,end,myPlannerDatabase));

        setDone(list);


        listEventOfCheckedDay = list;
        return listEventOfCheckedDay;
    }

    private void setDone(List<Event> list) {
        List<EventDone> listDone = getListDone();
        if (listDone!=null){
            for (int i=0; i<listDone.size(); i++){
                String idEvent = String.valueOf(listDone.get(i).getIdEvent());
                String dateCompleted = listDone.get(i).getDateCompleted();
                for (int j=0; j<list.size(); j++){
                    if (idEvent.equals(list.get(j).getIdEvent()) && dateCompleted.equals(format.format(list.get(j).getDtStart()))){
                        list.get(j).setDone(true);
                    }
                }
            }
        }
    }

    public List<Event> getListEventDevice(Context context){
        List<Event> list = new ArrayList<>();
        List<String> listUserShowOff = new ArrayList<>();
        listUserShowOff = getAllAccountShowOff();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {CalendarContract.Events.CALENDAR_ID,CalendarContract.Events.TITLE,CalendarContract.Events.DESCRIPTION,
                            CalendarContract.Events.DTSTART,CalendarContract.Events.DTEND,CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                            CalendarContract.Events.DISPLAY_COLOR,CalendarContract.Events.ACCOUNT_NAME,CalendarContract.Events.HAS_ALARM};

        Cursor cursor = contentResolver.query(uri,projection,null,null,null);

        //TODO loc su kien trung lap
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
                @SuppressLint("Range") String alarm = cursor.getString(cursor.getColumnIndex(projection[7]));
                Log.e("LOLOL", "getListEventDevice: "+ alarm );
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

    public static List<Event> getListEventUser(Date start, Date end, MyPlannerDatabase myPlannerDatabase){
        List<Event> list = new ArrayList<>();
        if (myPlannerDatabase!=null){
            Cursor cursor1 = myPlannerDatabase.getData("select * from Events");
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
        return list;
    }

    private List<String> getAllAccountShowOff() {
        List<String> list;
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.KEY_EMAIL_OFF,MODE_PRIVATE);
        String emails = sharedPreferences.getString("mail_off","");
        list = new ArrayList<>(Arrays.asList(emails.split("/")));
        return list;
    }

    public static List<Event> getEventUser(int repeat, long dateEndORTimeRepeat, long timeStart, long timeEnd, Date start, Date end, String idEvent, String title, String description, boolean done, int notification) {
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

    public void clickList(View view){
        rcvListWork.setVisibility(View.VISIBLE);
        viewPagerWeek.setVisibility(View.GONE);
        drawerLayout.close();
    }

    public void clickWeek(View view){
        rcvListWork.setVisibility(View.GONE);
        viewPagerWeek.setVisibility(View.VISIBLE);
        drawerLayout.close();
    }

    public void clickMonth(View view){
        rcvListWork.setVisibility(View.GONE);
        viewPagerWeek.setVisibility(View.GONE);
        drawerLayout.close();
    }

    public void clickImport(View view){
        if (checkStoragePermission()){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                getAllFileImport();
            }else {
                readFileAndroidQ();
            }
        }else {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
            requestPermissions(permissions,REQUEST_PERMISSION_IMPORT_CODE);
        }
    }

    public void clickSyncData(View view){
        showSyncDialog();
        drawerLayout.close();
    }

    public void clickSettings(View view){
        drawerLayout.close();
        Intent intent = new Intent(MainActivity.this,SettingActivity.class);
        activityResultLauncher.launch(intent);
    }

    public void clickRate(View view){
        drawerLayout.close();
        showRatingDialog();
    }

    public void clickShare(View view){
        drawerLayout.close();
        shareApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWidget(MainActivity.this);
    }

    private void showSyncDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_sync);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout layoutDownload = dialog.findViewById(R.id.layoutDownload);
        LinearLayout layoutUpload = dialog.findViewById(R.id.layoutUpload);
        layoutUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Upload", Toast.LENGTH_SHORT).show();
            }
        });

        layoutDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Download", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setLayout(getWindowWidth(MainActivity.this) - 50, LinearLayout.LayoutParams.WRAP_CONTENT);

        SmileyRating rating = dialog.findViewById(R.id.smileyRating);
        rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                switch (type) {
                    case TERRIBLE:
                        countingsStars = 1;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case BAD:
                        countingsStars = 2;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case OKAY:
                        countingsStars = 3;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case GOOD:
                        countingsStars = 4;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                    case GREAT:
                        countingsStars = 5;
                        ratePkg(MainActivity.this, MainActivity.this.getPackageName());
                        break;
                }
                SharedPreferences preferences = MainActivity.this.getSharedPreferences(rateStatus, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("STAR", countingsStars);
                editor.commit();
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finishAffinity();
                ratePkg(MainActivity.this, MainActivity.this.getPackageName());
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_IMPORT_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                    getAllFileImport();
                }else {
                    readFileAndroidQ();
                }
            }else{
                Toast.makeText(MainActivity.this, getString(R.string.permission_to_sync), Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_PERMISSION_EXPORT_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                saveFileExportFromDatabase();
            }else{
                Toast.makeText(MainActivity.this, getString(R.string.permission_to_save), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readFileAndroidQ() {
        //TODO
        Uri pickerInitialUri = MediaStore.Files.getContentUri("external");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
//        String[] mimeTypes = new String[]{"application/myplanner"};
//        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mimeTypes);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        launcherReadFile.launch(intent);
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        if (MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && MainActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

    public void getAllFileImport() {
        List<File> listFile = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA};
//        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        String[] selectionArgs = null;
        String sortOrder = null;
        Cursor cursor = contentResolver.query(uri, projection, null, selectionArgs, sortOrder);

        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection[0]));

                if (path.endsWith(".myplanner.jpg")){
                    listFile.add(new File(path));
                }
                Log.e("TAG", "getAllFileImport: "+path );
            }
        }

        if (listFile.size()>0){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.custom_dialog_import_file);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            TextView txtCancel = dialog.findViewById(R.id.txtCancel);
            TextView txtOK = dialog.findViewById(R.id.txtOk);
            RecyclerView rcvListFileImport = dialog.findViewById(R.id.rcvListFileImport);
            FileImportAdapter fileImportAdapter = new FileImportAdapter(this);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
            rcvListFileImport.setLayoutManager(linearLayoutManager);
            fileImportAdapter.setData(listFile);
            rcvListFileImport.setAdapter(fileImportAdapter);
            final File[] fileImport = new File[1];

            fileImportAdapter.setiFileImportOnclick(new FileImportAdapter.IFileImportOnclick() {
                @Override
                public void onClickItem(File file, int position) {
                    fileImportAdapter.selected(position);
                    fileImport[0] = file;
                }
            });

            txtOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fileImport[0]!=null){
                        readFileJsonData(fileImport[0]);
                        dialog.dismiss();
                    }else {
                        dialog.dismiss();
                    }
                }
            });

            txtCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }else {
            Toast.makeText(this, getString(R.string.have_no_data), Toast.LENGTH_SHORT).show();
        }
    }

    private void readFileJsonData(File file) {
        //https://stackoverflow.com/questions/48708561/java-parse-json-array-from-json-file
        List<Work> list = new ArrayList<>();
        String path = file.getAbsolutePath();
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            JsonReader jsonReader = new JsonReader(fileReader);
            Work[] works = new Gson().fromJson(bufferedReader,Work[].class);
//            List<Work> works = new Gson().fromJson(bufferedReader, List.class);
            if (works!=null){
                List<Work> listTemp = getAllData();
                for (int i=0; i<works.length; i++){
                    for (int j = 0; j < listTemp.size(); j++) {
                        if (works[i].getIdWork() != listTemp.get(j).getIdWork() && works[i].getTimeEnd() != listTemp.get(j).getTimeEnd()){
                            list.add(works[i]);
                        }
                    }
                }

                if (list.size()>0){
                    for (int i=0; i<list.size(); i++){
                        Work work = list.get(i);
                        if (work.getType().equals("work")){
                            myPlannerDatabase.queryData("insert into Events values("+work.getIdWork()+",'"+work.getTitle()+"','"+work.getDescription()+"',"+work.getTimeStart()+","+work.getTimeEnd()+","+work.getNotification()+","+work.getRepeat()+","+work.getDateEndOrTimeRepeat()+",0)");
                        }else if (work.getType().equals("work_done")){
                            myPlannerDatabase.queryData("insert into EventsDone values ("+work.getIdWork()+",'"+work.getTitle()+"',"+work.getTimeStart()+")");
                        }
                    }
                    reloadAll();
                    Toast.makeText(this, getString(R.string.data_synced), Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(MainActivity.this, getString(R.string.file_does_not_match), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveFileExportFromDatabase() {
        List<Work> listWork = new ArrayList<>();
        listWork = getAllData();

        if (listWork.size()>0){
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(listWork);

            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = prettyGson.toJson(listWork);

            File file = new File("/storage/emulated/0/Download",getPackageName());
            if (!file.exists()){
                file.mkdirs();
            }

            try {
                String displayName = simpleDateFormat2.format(System.currentTimeMillis());
                Uri uri = saveFile(this,prettyJson,getPackageName(),displayName);
                if (Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
                    if (uri!=null){
                        Toast.makeText(this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, getString(R.string.save_file_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.save_file_failed), Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, getString(R.string.have_no_data), Toast.LENGTH_SHORT).show();
        }
    }

    private List<Work> getAllData() {
        List<Work> list = new ArrayList<>();
        Cursor cursor = myPlannerDatabase.getData("select * from Events");
        if (cursor!=null){
            while (cursor.moveToNext()){
                long idWork = cursor.getLong(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                long timeStart = cursor.getLong(3);
                long timeEnd = cursor.getLong(4);
                int notification = cursor.getInt(5);
                int repeat = cursor.getInt(6);
                long dateEndOrTimeRepeat = cursor.getLong(7);
                int done = cursor.getInt(8);
                list.add(new Work(idWork,title,description,timeStart,timeEnd,notification,repeat,dateEndOrTimeRepeat,done,"work"));
            }
            cursor.close();
        }

        Cursor cursor1 = myPlannerDatabase.getData("select * from EventsDone");
        if (cursor1!=null){
            while (cursor1.moveToNext()){
                long idWork = cursor1.getLong(0);
                String dateCompleted = cursor1.getString(1);
                long timeStart = cursor1.getLong(2);
                list.add(new Work(idWork,dateCompleted,null,timeStart,0,0,0,0,0,"work_done"));
            }
            cursor1.close();
        }
        return list;
    }

    private void listenerClickFromWidget() {
        String signFromWidget = getIntent().getStringExtra("sign_from_widget");
        Log.e("widget", "listenerClickFromWidget: "+signFromWidget);
        if (activityResultLauncher==null){
            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        reloadRcvListWork();
                        reloadDataLayoutMonth("reload");
                        reloadViewPagerWeek();
                    }
                }
            });
        }
        if (signFromWidget!=null) {
            if (signFromWidget.equals("create")) {
                Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                Calendar calendarTemp = Calendar.getInstance();
                intent.putExtra("DATE", calendarTemp.getTime());
                intent.putExtra("MISSION", "create");
                activityResultLauncher.launch(intent);
            } else if (getIntent().getAction() != null) {
                if (signFromWidget.equals("detail")) {
                    int position = getIntent().getIntExtra(ExampleAppWidgetProvider.EXTRA_POSITION, 0);
                    Event event = (Event) getIntent().getSerializableExtra(ExampleAppWidgetProvider.EXTRA_EVENT);
                    if (event != null) {
                        if (event.getType().equals("current_day")) {
                            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                            Calendar calendarTemp = Calendar.getInstance();
                            intent.putExtra("DATE", calendarTemp.getTime());
                            intent.putExtra("MISSION", "create");
                            activityResultLauncher.launch(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, EventActivity.class);
                            intent.putExtra("EVENT", event);
                            activityResultLauncher.launch(intent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String signFromWidget = intent.getStringExtra("sign_from_widget");
        if (signFromWidget!=null){
            if (signFromWidget.equals("create")){
                Intent intent1 = new Intent(MainActivity.this,CreateEventActivity.class);
                Calendar calendarTemp = Calendar.getInstance();
                intent1.putExtra("DATE",calendarTemp.getTime());
                intent1.putExtra("MISSION","create");
                activityResultLauncher.launch(intent1);
            }
        } else if (getIntent().getAction()!=null){
            if (getIntent().getAction().equals(ExampleAppWidgetProvider.ACTION_SHOW_DETAIL)){
                int position = intent.getIntExtra(ExampleAppWidgetProvider.EXTRA_POSITION,0);
                Event event = (Event) intent.getSerializableExtra(ExampleAppWidgetProvider.EXTRA_EVENT);
                if (event!=null){
                    Intent intent1 = new Intent(MainActivity.this,EventActivity.class);
                    intent1.putExtra("EVENT",event);
                    activityResultLauncher.launch(intent1);
                }
            }
        }
    }

    public static void updateWidget(Context context) {
        //TODO
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, ExampleAppWidgetProvider.class);
        int[] appWidgetIds = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.example_widget_list_view);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.txtTitleWidget);
        appWidgetManager.getInstance(context)
                .getAppWidgetIds(thisWidget);
    }

    public static String getPathToStorage(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File f = c.getExternalFilesDir(null);
            if (f != null) {
                return f.getAbsolutePath();
            } else
                return "/storage/emulated/0/Android/data/"+c.getPackageName();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/" + c.getPackageName();
        }
    }

    public void shareApp(Context context) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "Create and organize your work schedule. Install now\n";
            shareMessage += "https://play.google.com/store/apps/details?id="+getPackageName();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ratePkg(Context context, String pkg) {
        if (pkg == null)
            return;
        Uri uri = Uri.parse("market://details?id=" + pkg);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getWindowWidth(Activity activity) {
        DisplayMetrics metrics=new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    //TODO
    public static Uri saveFile(@NonNull final Context context, @NonNull final String jsonString,
                                @NonNull final String folder,
                                @NonNull final String displayName) throws IOException {
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri pickerInitialUri = MediaStore.Files.getContentUri("external");
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/*");
            intent.putExtra(Intent.EXTRA_TITLE, displayName+".myplanner");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
            launcherWriteFile.launch(intent);
            return null;
        } else {
            String relativeLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            if (!folder.isEmpty())
                relativeLocation += File.separator + folder;
            File file = new File(relativeLocation);
            if (!file.exists()){
                file.mkdirs();
            }
            File data = new File(relativeLocation, displayName + ".myplanner");
            data.createNewFile();
            OutputStream out = new FileOutputStream(data);
            out.write(bytes);
            out.close();
//            MediaScannerConnection.scanFile(context, new String[]{data.getPath()}, null, null);
            return Uri.fromFile(data);

//            try {
//                File file1 = new File(file.getAbsolutePath()+"/",name2);
//                FileWriter fileWriter = new FileWriter(file1);
//                fileWriter.write(jsonString);
//                fileWriter.flush();
//                fileWriter.close();
//                Toast.makeText(context, context.getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
//            }catch (Exception e){
//                e.printStackTrace();
//                Toast.makeText(context, context.getString(R.string.save_file_failed), Toast.LENGTH_SHORT).show();
//            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class RcvListWorkTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            listEventForListWork = getListEventForListWork();
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            listWorkAdapter.setData(listEventForListWork,getListDone(),getCurrentDate());
            if (currentDayPosition > 0){
                rcvListWork.scrollToPosition(currentDayPosition - 1);
            }else {
                rcvListWork.scrollToPosition(currentDayPosition);
            }
        }
    }

    class DataLayoutMonthTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request

            listEvent = getListEventDevice(MainActivity.this);
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            int position = viewPagerCalendar.getCurrentItem();
            calendarFragmentList.get(position+1).reloadDataInFragment();
            calendarFragmentList.get(position-1).reloadDataInFragment();
            calendarFragmentList.get(position).reloadDataInFragment();
            calendarFragmentList.get(position+2).reloadDataInFragment();
            calendarFragmentList.get(position-2).reloadDataInFragment();
            eventOfDayRcvMonthAdapter.setData(getListEventOfCurrentDay(listEvent,currentDay),listEventDoneOfCheckedDay);
            checkListEventOfDay(listEventOfCheckedDay);
        }
    }
}