package com.example.myplanner.notification;

import static com.example.myplanner.notification.MyNotification.CHANNEL_ID;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class
NotificationCancelReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = 100;
        if (id != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
            notificationManager.cancel("channel_service_example",id);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,  0, new Intent(), 0);
            NotificationCompat.Builder mb = new NotificationCompat.Builder(context,CHANNEL_ID);
            mb.setContentIntent(resultPendingIntent);

//            NotificationManagerCompat.from(context).cancel(id);
        }
    }
}
