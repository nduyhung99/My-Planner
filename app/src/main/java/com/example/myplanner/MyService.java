package com.example.myplanner;

import static com.example.myplanner.notification.MyNotification.CHANNEL_ID;
import static com.example.myplanner.notification.MyNotification.CHANNEL_ID_2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private static final String TAG = MyService.class.getName();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String dataIntent = intent.getStringExtra("key data intent");
        if (dataIntent==null){
            dataIntent = getString(R.string.notification_application_running);
        }
        sendNotification(dataIntent);
        Log.e(TAG, "onStartCommand: ");

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    Log.e(TAG, "run: "+i );
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendNotification(String dataIntent) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("listening", 1);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setContentTitle(dataIntent)
                .setContentText(getString(R.string.click_for_more_details))
                .setSmallIcon(R.drawable.ic_add_task)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }
}
