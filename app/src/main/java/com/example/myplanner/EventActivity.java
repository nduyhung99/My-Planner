package com.example.myplanner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;
import com.example.myplanner.notification.MyBroadcastReceiver;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;

public class EventActivity extends AppCompatActivity {
    private ImageView imgBack,imgDelete,imgRepair;
    private TextView txtTitle, txtDescription, txtDuration, txtDisplayName, txtNotification;
    private Event event;
    private EventDone eventDone;
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd MMMM, yyyy");
    SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    MyPlannerDatabase myPlannerDatabase;
    ActivityResultLauncher<Intent> activityResultLauncher;

    RelativeLayout layoutCompleted;
    TextView txtCompleted;
    ImageView imgCompleted, imgUncompleted;
    int completed = 0;
    String language = "";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.gray1), false, false, EventActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
        language = sharedPreferences.getString("language","");
        if (!language.equals("")){
            MainActivity.setLanguage(this,language);
        }
        setContentView(R.layout.activity_event);
        addControls();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK,resultIntent);
                    finish();
                }
            }
        });

        imgUncompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgCompleted.setVisibility(View.VISIBLE);
                //LƯU CÔNG VIỆC ĐÃ HOÀN THÀNH TRONG DATABASE
                String dateEventDone = format.format(event.getDtStart());
                myPlannerDatabase.queryData("insert into EventsDone values("+event.getIdEvent()+",'"+dateEventDone+"','"+event.getDtStart()+"')");
                if (completed==0){
                    completed=1;
                }else {
                    completed=0;
                }
            }
        });

        imgCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgCompleted.setVisibility(View.GONE);
                //XÓA CÔNG VIỆC ĐÃ HOÀN THÀNH TRONG DATABASE
                String dateEventDone = format.format(event.getDtStart());
                myPlannerDatabase.queryData("delete from EventsDone where IdEvent = "+event.getIdEvent()+" and DateCompleted = '"+dateEventDone+"'");
                if (completed==0){
                    completed=1;
                }else {
                    completed=0;
                }
            }
        });

        imgRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this,CreateEventActivity.class);
                intent.putExtra("EVENT",event);
                intent.putExtra("MISSION","repair");
                activityResultLauncher.launch(intent);
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(EventActivity.this).inflate(R.layout.bottom_sheet_delete_event,null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EventActivity.this,R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                TextView txtCancel = bottomSheetDialog.findViewById(R.id.txtCancel);
                TextView txtAgree = bottomSheetDialog.findViewById(R.id.txtAgree);

                txtAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myPlannerDatabase.queryData("delete from Events where IdEvent=" + event.getIdEvent() + "");
                        int requestCode = Integer.parseInt(event.getIdEvent().substring(0,event.getIdEvent().length()-3));
                        Intent intent = new Intent(EventActivity.this, MyBroadcastReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(EventActivity.this,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);

                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK,resultIntent);
                        bottomSheetDialog.dismiss();
                        finish();
                        Toast.makeText(EventActivity.this, getString(R.string.deleted_work), Toast.LENGTH_SHORT).show();
                    }
                });

                txtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void addControls() {
        imgBack = findViewById(R.id.imgBack);
        imgDelete = findViewById(R.id.imgDelete);
        imgRepair = findViewById(R.id.imgRepair);
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtDuration = findViewById(R.id.txtDuration);
        txtDisplayName = findViewById(R.id.txtDisplayName);
        txtNotification = findViewById(R.id.txtNotification);
        layoutCompleted = findViewById(R.id.layoutCompleted);
        txtCompleted = findViewById(R.id.txtCompleted);
        imgCompleted = findViewById(R.id.imgCompleted);
        imgUncompleted = findViewById(R.id.imgUncompleted);

        txtCompleted.setPaintFlags(txtCompleted.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        myPlannerDatabase = new MyPlannerDatabase(getApplicationContext());

        event = (Event) getIntent().getSerializableExtra("EVENT");
        if (event == null){
            String idEventReceive = getIntent().getStringExtra("id_event");
            if (idEventReceive!=null){
                Cursor cursor = myPlannerDatabase.getData("select * from Events where IdEvent = "+Long.parseLong(idEventReceive)+"");
                if (cursor!=null){
                    cursor.moveToFirst();
                    String title = cursor.getString(1);
                    long dtStart = cursor.getLong(3);
                    long dtEnd = cursor.getLong(4);
                    String description = cursor.getString(2);
                    boolean done;
                    done = (cursor.getInt(8)==1 ? true : false);
                    int notificationEvent = cursor.getInt(5);
                    int repeat = cursor.getInt(6);
                    event = new Event(idEventReceive,title,dtStart,dtEnd,description,"user",done,"user",notificationEvent,repeat,0,0);
                    cursor.close();
                }
            }
        }


        if (event!=null){
            if (event.getType().equals("device")){
                imgDelete.setVisibility(View.GONE);
                imgRepair.setVisibility(View.GONE);
            }

            txtTitle.setText(event.getTitle());
            txtDescription.setText(event.getDescription());

            setTextDuration(event);

            txtDisplayName.setText(event.getCalendarDisplayName());

            if (event.getType().equals("user")){
                switch (event.getNotification()){
                    case 0:
                        txtNotification.setText(getString(R.string.no_notification));
                        break;
                    case 1:
                        txtNotification.setText(getString(R.string.at_time));
                        break;
                    case 2:
                        txtNotification.setText(getString(R.string.before_5_minutes));
                        break;
                    case 3:
                        txtNotification.setText(getString(R.string.before_15_minutes));
                        break;
                    case 4:
                        txtNotification.setText(getString(R.string.before_30_minutes));
                        break;
                    default:
                        break;
                }
            }

            if (event.getType().equals("device")){
                txtDisplayName.setText(getString(R.string.calendar)+" "+event.getCalendarDisplayName());
                layoutCompleted.setVisibility(View.GONE);
            }else if (event.getType().equals("user")){
                txtDisplayName.setText(getString(R.string.calendar)+" "+getString(R.string.user));
                layoutCompleted.setVisibility(View.VISIBLE);
                String dateCompleted = format.format(event.getDtStart());
                Cursor cursor = myPlannerDatabase.getData("select * from EventsDone where IdEvent = "+event.getIdEvent()+" and DateCompleted = '"+dateCompleted+"'");
                if (cursor!=null){
                    if (cursor.moveToFirst()){
                        imgCompleted.setVisibility(View.VISIBLE);
                    }else {
                        imgCompleted.setVisibility(View.GONE);
                    }
                }
                if (event.getRepeat()==0){
                    txtCompleted.setText(getString(R.string.completed));
                }else {
                    txtCompleted.setText(getString(R.string.work_completed)+" "+format.format(event.getDtStart()));
                }
            }
        }
    }

    private void setTextDuration(Event event) {
        String start = formatTime.format(event.getDtStart());
        String end = formatTime.format(event.getDtEnd());
        String startDay = formatDate.format(event.getDtStart());
        String endDay = formatDate.format(event.getDtEnd());
        String yearStart = formatYear.format(event.getDtStart());
        String yearEnd = formatYear.format(event.getDtEnd());

        String date;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            date = DateFormat.getDateInstance(DateFormat.LONG).format(event.getDtStart());
        }else {
            date = formatDate.format(event.getDtStart());
        }
        if (startDay.equals(endDay)){
            if (start.equals(end)){
                txtDuration.setText(date);
            }else {
                txtDuration.setText(start+" - "+end+" "+date);
            }
        }else {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                String dateEnd = DateFormat.getDateInstance(DateFormat.LONG).format(event.getDtEnd());
                if (yearStart.equals(yearEnd)){
                    txtDuration.setText(start+" "+date.substring(0,date.length()-6)+" - "+end +" "+dateEnd.substring(0,dateEnd.length()-6));
                }else {
                    txtDuration.setText(start+" "+date+" - "+end +" "+dateEnd);
                }
            }else {
                if (yearStart.equals(yearEnd)){
                    txtDuration.setText(start+" "+startDay.substring(0,startDay.length()-6)+" - "+end +" "+endDay.substring(0,endDay.length()-6));
                }else {
                    txtDuration.setText(start+" "+startDay+" - "+end +" "+endDay);
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (completed==1){
            Intent resultIntent = new Intent();
            setResult(RESULT_OK,resultIntent);
            finish();
        }
        super.onBackPressed();
    }
}