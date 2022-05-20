package com.example.myplanner;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyBoundService extends Service {
    private MyBinder myBinder = new MyBinder();
    private MediaPlayer mediaPlayer;

    public class MyBinder extends Binder {
        MyBoundService getMyService(){
            return MyBoundService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("service", "onBind: " );
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "onCreate: " );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("service", "onUnBind: " );
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("service", "onDestroy: " );
        if (mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service", "onStartCommand: " );
        return super.onStartCommand(intent, flags, startId);
    }

    public void startMusic(){
            if (mediaPlayer!=null){
                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.thi_thoi);
                mediaPlayer.start();
            }
    }
}
