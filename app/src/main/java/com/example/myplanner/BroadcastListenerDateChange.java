package com.example.myplanner;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class  BroadcastListenerDateChange extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIME_TICK)) {
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
//            ComponentName thisWidget = new ComponentName(context.getApplicationContext(), ExampleAppWidgetProvider.class);
//            int[] appWidgetIds = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(thisWidget);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.example_widget_list_view);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.txtTitleWidget);

            Toast.makeText(context, "Date changed", Toast.LENGTH_SHORT).show();
        }
    }
}
