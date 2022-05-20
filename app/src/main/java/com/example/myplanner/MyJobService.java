package com.example.myplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.Event;
import com.example.myplanner.notification.BroadcastReceiverTest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getName();
    private boolean jobCanceled;
    private List<Event> listEvents;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        doBackgroundWork(jobParameters);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void doBackgroundWork(JobParameters jobParameters) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        Intent intent = new Intent(getApplicationContext(), BroadcastReceiverTest.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),200,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis()+(24*60*60*1000),pendingIntent);

        MainActivity.updateWidget(this);

        Intent intent1 = new Intent(this, MyService.class);
        intent1.putExtra("key data intent", this.getString(R.string.notification_application_running));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(intent1);
        }

        jobFinished(jobParameters,true);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCanceled=true;
        return false;
    }

}
