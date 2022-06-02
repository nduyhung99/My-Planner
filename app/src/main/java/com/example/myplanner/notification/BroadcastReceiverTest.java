package com.example.myplanner.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myplanner.MainActivity;
import com.example.myplanner.MyService;
import com.example.myplanner.R;
import com.example.myplanner.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BroadcastReceiverTest extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        List<Event> listWorks = new ArrayList<>();
        listWorks = MainActivity.getListWorkOfDay(new Date(calendar.getTimeInMillis()),new Date(calendar.getTimeInMillis()+(24*60*60*1000)),context);
        if (listWorks.size()>0){
            MainActivity.registerNotification(listWorks, context);
        }
//        Toast.makeText(context, "Bao thuc", Toast.LENGTH_SHORT).show();
        MainActivity.stopScheduleJob(context,MainActivity.JOB_ID);
        MainActivity.startScheduleJob(context);
    }
}
