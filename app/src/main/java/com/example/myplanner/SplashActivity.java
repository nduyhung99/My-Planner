package com.example.myplanner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION_CALENDAR_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.white), false, false, SplashActivity.this);
        setContentView(R.layout.activity_splash);
        checkPermissionCalendar();
        resetChecked();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkPermissionCalendar() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (SplashActivity.this.checkSelfPermission(Manifest.permission.READ_CALENDAR)== PackageManager.PERMISSION_GRANTED
                    && SplashActivity.this.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
            }else {
                String[] permissions= {Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR};
                requestPermissions(permissions,REQUEST_PERMISSION_CALENDAR_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CALENDAR_CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
            }else {
//                boolean showRationale = shouldShowRequestPermissionRationale(String.valueOf(permissions[0]));
//                if (showRationale){
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//                }
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void resetChecked() {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_PLANNER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("date_checked",null);
        editor.commit();
    }
}