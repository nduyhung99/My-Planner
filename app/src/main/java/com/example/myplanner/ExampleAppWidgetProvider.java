package com.example.myplanner;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_SHOW_DETAIL="show_detail";
    public static final String EXTRA_POSITION="item_position";
    public static final String EXTRA_EVENT="item_event";
    private Button btnExample;
    SimpleDateFormat formatMonth = new SimpleDateFormat("MMMM");
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

            Intent intentCreate = new Intent(context,MainActivity.class);
            intentCreate.putExtra("sign_from_widget","create");
            intentCreate.setAction("create");
//            intentCreate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intentCreate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntentCreate;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                pendingIntentCreate = PendingIntent.getActivity(context,0,intentCreate,PendingIntent.FLAG_UPDATE_CURRENT);
            }else {
                pendingIntentCreate = PendingIntent.getActivity(context,0,intentCreate,PendingIntent.FLAG_MUTABLE);
            }

//            SharedPreferences sharedPreferences = context.getSharedPreferences("KEY_TEXT_CONFIG",Context.MODE_PRIVATE);
//            String textButton = sharedPreferences.getString("text_config","example");
            Intent clickIntent = new Intent(context,ExampleAppWidgetProvider.class);
            clickIntent.setAction("actionToast");
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,0,clickIntent,0);

            Calendar calendar = Calendar.getInstance();
            String month = formatMonth.format(calendar.getTime());

            Intent serviceIntent = new Intent(context, ExampleWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            Intent serviceIntent2 = new Intent(context, ExampleWidgetService2.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_widget);
            views.setOnClickPendingIntent(R.id.layoutContainer,pendingIntent);

            //Config layout example
//            views.setOnClickPendingIntent(R.id.btnExample, pendingIntent);
//            views.setCharSequence(R.id.btnExample, "setText", textButton);
//            views.setRemoteAdapter(R.id.example_widget_stack_view, serviceIntent);
//            views.setEmptyView(R.id.example_widget_stack_view, R.id.example_widget_empty_view);

            views.setOnClickPendingIntent(R.id.imgAddWork, pendingIntentCreate);
            views.setTextViewText(R.id.txtTitleWidget,month.substring(0,1).toUpperCase()+month.substring(1));
            views.setRemoteAdapter(R.id.example_widget_list_view,serviceIntent2);
            views.setEmptyView(R.id.example_widget_list_view, R.id.example_widget_empty_list_view);
            views.setInt(R.id.imgAddWork,"setColorFilter", Color.BLACK);
            views.setPendingIntentTemplate(R.id.example_widget_list_view,clickPendingIntent);
//            views.setPendingIntentTemplate(R.id.example_widget_list_view,clickItemPendingIntent);

            Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
            resizeWidget(appWidgetOptions,views);

            Intent intentItem = new Intent(context,MainActivity.class);
            PendingIntent pendingIntentClickItemListview;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                pendingIntentClickItemListview = PendingIntent.getActivity(context,0,intentItem,PendingIntent.FLAG_UPDATE_CURRENT);
            }else {
                pendingIntentClickItemListview = PendingIntent.getActivity(context,0,intentItem,PendingIntent.FLAG_MUTABLE);
            }
            views.setPendingIntentTemplate(R.id.example_widget_list_view, pendingIntentClickItemListview);
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.example_widget);
        resizeWidget(newOptions, views);
        appWidgetManager.updateAppWidget(appWidgetId,views);
    }

    private void resizeWidget(Bundle appWidgetOptions, RemoteViews views){
        int minWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Toast.makeText(context, "onDelete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Toast.makeText(context, "onDisable", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Toast.makeText(context, "onEnable", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
//        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ExampleAppWidgetProvider.class));
//            onUpdate(context,appWidgetManager,ids);
//        }
    }
}
