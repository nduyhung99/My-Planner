package com.example.myplanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExampleAppWidgetConfig extends AppCompatActivity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText edtTextButton;
    SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_app_widget_config);
        edtTextButton = findViewById(R.id.edtTextButton);

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras!=null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        setResult(RESULT_CANCELED,resultValue);

        if (appWidgetId==AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickConfirm(View v){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_MUTABLE);
        }

        String textButton = edtTextButton.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        String month = formatMonth.format(calendar.getTime());

        Intent serviceIntent = new Intent(this, ExampleWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent serviceIntent2 = new Intent(this, ExampleWidgetService2.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.example_widget);
        views.setOnClickPendingIntent(R.id.btnExample, pendingIntent);
        views.setTextViewText(R.id.txtTitleWidget,month.substring(0,1).toUpperCase()+month.substring(1));
        views.setCharSequence(R.id.btnExample, "setText", textButton);
        views.setRemoteAdapter(R.id.example_widget_stack_view, serviceIntent);
        views.setRemoteAdapter(R.id.example_widget_list_view,serviceIntent2);
        views.setEmptyView(R.id.example_widget_stack_view, R.id.example_widget_empty_view);
        views.setEmptyView(R.id.example_widget_list_view, R.id.example_widget_empty_list_view);

        appWidgetManager.updateAppWidget(appWidgetId,views);

        SharedPreferences sharedPreferences = getSharedPreferences("KEY_TEXT_CONFIG",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("text_config"+appWidgetId,textButton);
        editor.commit();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        setResult(RESULT_OK,resultValue);
        finish();
    }
}