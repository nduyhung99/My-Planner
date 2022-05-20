package com.example.myplanner.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.myplanner.R;

public class MyNotification extends Application {
    public static final String CHANNEL_ID = "channel_work";
    public static final String CHANNEL_ID_2 = "channel_service";

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();

    }

    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            Create channel 1:
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,getString(R.string.work_notification), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(getString(R.string.notification_work));

//            Create channel 2:
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2,getString(R.string.running_notification), NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription(getString(R.string.notification_application_running));

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
                notificationManager.createNotificationChannel(channel2);
            }
        }
    }
}
