package com.example.myplanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myplanner.adapter.WorkChartAdapter;
import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChartActivity extends AppCompatActivity {
    private RecyclerView rcvChart;
    private WorkChartAdapter workChartAdapter;
    private List<Event> listEventChart = new ArrayList<>();
    private List<Event> listAllEvent = new ArrayList<>();
    private List<EventDone> listDone = new ArrayList<>();
    private Event currentEvent;
    MyPlannerDatabase myPlannerDatabase;
    private PieChart pieChart;
    String  language = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.gray1), false, false, ChartActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
        language = sharedPreferences.getString("language","");
        if (!language.equals("")){
            MainActivity.setLanguage(this,language);
        }
        setContentView(R.layout.activity_chart);
        myPlannerDatabase = new MyPlannerDatabase(ChartActivity.this);
        rcvChart = findViewById(R.id.rcvChart);
        pieChart = findViewById(R.id.pieChart);

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.YEAR,-12);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.YEAR,12);
        listAllEvent = MainActivity.getListEventUser(calendarStart.getTime(),calendarEnd.getTime(),myPlannerDatabase);
        listDone = getListDoneChart();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rcvChart.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        rcvChart.addItemDecoration(dividerItemDecoration);

        listEventChart = getListEventChart(myPlannerDatabase);
        workChartAdapter = new WorkChartAdapter(this);
        workChartAdapter.setData(listEventChart);
        rcvChart.setAdapter(workChartAdapter);

        currentEvent = listEventChart.get(0);
        setPieChart(currentEvent);

        workChartAdapter.setOnClickListener(new WorkChartAdapter.IOnClickWorkChart() {
            @Override
            public void onClickItem(Event event) {
                if (!event.getIdEvent().equals(currentEvent.getIdEvent())){
                    setPieChart(event);
                    currentEvent = event;
                }
            }
        });

    }

    private void setPieChart(Event event) {
        int total = 0, done = 0;
        for (int i = 0; i < listAllEvent.size(); i++) {
            if (event.getIdEvent().equals(listAllEvent.get(i).getIdEvent())){
                total++;
            }
        }
        for (int i = 0; i < listDone.size(); i++) {
            if (event.getIdEvent().equals(String.valueOf(listDone.get(i).getIdEvent()))){
                done++;
            }
        }

        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(total-done,getString(R.string.incomplete)));
        visitors.add(new PieEntry(done,getString(R.string.complete)));

        PieDataSet pieDataSet = new PieDataSet(visitors,event.getTitle());
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(event.getTitle());
        pieChart.animate();
        pieChart.invalidate();
    }

    private List<Event> getListEventChart(MyPlannerDatabase myPlannerDatabase) {
        List<Event> list = new ArrayList<>();
        Cursor cursor = myPlannerDatabase.getData("select * from Events");
        if (cursor!=null){
            while (cursor.moveToNext()){
                long timeStart = cursor.getLong(3);
                long timeEnd = cursor.getLong(4);
                int repeat = cursor.getInt(6);
                String idEvent = String.valueOf(cursor.getLong(0));
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                int notification = cursor.getInt(5);
                boolean done = false;
                if (cursor.getInt(8)==1){
                    done=true;
                }
                list.add(new Event(idEvent,title,timeStart,timeEnd,description,"user",done,"user",notification,repeat,0,0));
            }
        }
        return list;
    }

    private List<EventDone> getListDoneChart() {
        List<EventDone> list = new ArrayList<>();
        Cursor cursor = myPlannerDatabase.getData("select * from EventsDone");
        if (cursor!=null){
            while (cursor.moveToNext()){
                long idEvent = cursor.getLong(0);
                String dateCompleted = cursor.getString(1);
                long timeStart = cursor.getLong(2);
                list.add(new EventDone(idEvent,dateCompleted,timeStart));
            }
            cursor.close();
        }
        return list;
    }

}