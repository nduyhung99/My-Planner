package com.example.myplanner.adapter;

import static com.example.myplanner.R.color.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myplanner.R;
import com.example.myplanner.model.DateOfCalendar;
import com.example.myplanner.model.Event;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateOfCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<DateOfCalendar> mListDate;
    private List<Event> listEvent;
    private int currentCheckedPosition;

    private Date currentDate;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMMM, yyyy");

    String dateChecked;
    SharedPreferences sharedPreferences;
    Calendar calendar;

    IOnClickItemCalendarAdapter iOnClickItemCalendarAdapter;

    public DateOfCalendarAdapter(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences("MY_PLANNER",Context.MODE_PRIVATE);
        calendar = Calendar.getInstance();
//        dateChecked = sharedPreferences.getString("date_cheked",simpleDateFormat1.format(calendar.getTime()));
    }

    public void setData(List<DateOfCalendar> listDate, Date currentDate, List<Event> list){
        this.mListDate = listDate;
        this.currentDate = currentDate;
        this.listEvent = list;
        notifyDataSetChanged();
    }

    public void loadChecked(){
        notifyDataSetChanged();
    }
    public void removeAll(){

    }

    public void onClickItemListener(IOnClickItemCalendarAdapter listener){
        this.iOnClickItemCalendarAdapter = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_number_of_day,parent,false);
        return new DateOfCalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DateOfCalendar  dateOfCalendar = mListDate.get(position);
        DateOfCalendarViewHolder dateOfCalendarViewHolder = (DateOfCalendarViewHolder) holder;
        String day = simpleDateFormat.format(dateOfCalendar.getDate());
        String date = simpleDateFormat1.format(dateOfCalendar.getDate());
        dateOfCalendarViewHolder.txtNumberOfDay.setText(day);
        if (position<7){
            int number = Integer.parseInt(day);
            if (number>20){
                dateOfCalendarViewHolder.viewAlpha.setVisibility(View.VISIBLE);
            }else {
                dateOfCalendarViewHolder.viewAlpha.setVisibility(View.GONE);
            }
        }

        if (position>24){
            int number = Integer.parseInt(day);
            if (number<20){
                dateOfCalendarViewHolder.viewAlpha.setVisibility(View.VISIBLE);
            }else {
                dateOfCalendarViewHolder.viewAlpha.setVisibility(View.GONE);
            }
        }

        if (position>=6 && (position-6)%7==0 ){
            dateOfCalendarViewHolder.txtNumberOfDay.setTextColor(mContext.getResources().getColor(orange1));
        }else if (position>=5 && (position-5)%7==0){
            dateOfCalendarViewHolder.txtNumberOfDay.setTextColor(mContext.getResources().getColor(blue1));
        }else {
            dateOfCalendarViewHolder.txtNumberOfDay.setTextColor(mContext.getResources().getColor(black));
        }



        if (currentDate!=null){
            if (date.equals(simpleDateFormat1.format(currentDate))){
                dateOfCalendarViewHolder.viewCurrentDate.setVisibility(View.VISIBLE);
                dateOfCalendarViewHolder.txtNumberOfDay.setTextColor(mContext.getResources().getColor(white));
            }else {
                dateOfCalendarViewHolder.viewCurrentDate.setVisibility(View.GONE);
            }
        }

        calendar = Calendar.getInstance();
        dateChecked = sharedPreferences.getString("date_checked",simpleDateFormat1.format(calendar.getTime()));
        if (date.equals(dateChecked)){
            dateOfCalendarViewHolder.viewCheckedDate.setVisibility(View.VISIBLE);
            currentCheckedPosition=position;
        }else {
            dateOfCalendarViewHolder.viewCheckedDate.setVisibility(View.GONE);
        }

        int count=0;
        List<Event> listEventOfDay = new ArrayList<>();
        for (int i = 0; i < listEvent.size(); i++) {
            String dateEvent = simpleDateFormat1.format(listEvent.get(i).getDtStart());
            if (dateEvent.equals(date)){
                String title = listEvent.get(i).getTitle();
                if (title.equals("")){
                    title = mContext.getString(R.string.work);
                }
                if (count==0){
                    dateOfCalendarViewHolder.txtTask1.setText(title);
                    setColorEvent (listEvent.get(i),dateOfCalendarViewHolder.txtTask1);
                }else if (count==1){
                    dateOfCalendarViewHolder.txtTask2.setText(title);
                    setColorEvent (listEvent.get(i),dateOfCalendarViewHolder.txtTask2);
                }else if (count==2){
                    dateOfCalendarViewHolder.txtTask3.setText(title);
                    setColorEvent (listEvent.get(i),dateOfCalendarViewHolder.txtTask3);
                }
                listEventOfDay.add(listEvent.get(i));
                count++;
            }
        }

        dateOfCalendarViewHolder.layoutItemNumberOfDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateOfCalendarViewHolder.viewAlpha.getVisibility()==View.VISIBLE){
                    iOnClickItemCalendarAdapter.onOtherMonthItemClick(dateOfCalendar,position);
                }
                iOnClickItemCalendarAdapter.onItemClick(dateOfCalendar,listEventOfDay,position);
            }
        });

//        dateOfCalendarViewHolder.viewAlpha.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                iOnClickItemCalendarAdapter.onOtherMonthItemClick(dateOfCalendar,position);
//            }
//        });
        dateOfCalendarViewHolder.setIsRecyclable(false);
    }

    private void setColorEvent(Event event, TextView txtTask) {
        if (event.getType().equals("device")){
            if (event.getIdEvent().equals("2")){
                txtTask.setBackgroundResource(red1);
                txtTask.setTextColor(mContext.getResources().getColor(red2));
            }else {
                txtTask.setBackgroundResource(teal_700_2);
                txtTask.setTextColor(mContext.getResources().getColor(teal_700));
            }
        }else {
            txtTask.setBackgroundResource(blue2);
            txtTask.setTextColor(mContext.getResources().getColor(blue1));
        }
    }

    @Override
    public int getItemCount() {
        if (mListDate!=null){
            return mListDate.size();
        }
        return 0;
    }

    public class DateOfCalendarViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout layoutItemNumberOfDay;
        TextView txtNumberOfDay, txtTask1, txtTask2, txtTask3;
        View viewAlpha, viewCurrentDate, viewCheckedDate;
        public DateOfCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemNumberOfDay = itemView.findViewById(R.id.layoutItemNumberOfDay);
            txtNumberOfDay = itemView.findViewById(R.id.txtNumberOfDay);
            txtTask1 = itemView.findViewById(R.id.txtTask1);
            txtTask2 = itemView.findViewById(R.id.txtTask2);
            txtTask3 = itemView.findViewById(R.id.txtTask3);
            viewAlpha = itemView.findViewById(R.id.viewAlpha);
            viewCurrentDate = itemView.findViewById(R.id.viewCurrentDate);
            viewCheckedDate = itemView.findViewById(R.id.viewCheckedDate);
        }
    }

    public interface IOnClickItemCalendarAdapter{
        void onItemClick(DateOfCalendar dateOfCalendar, List<Event> listEvent, int position);
        void onOtherMonthItemClick(DateOfCalendar dateOfCalendar,int position);
    }

    public int getCurrentCheckedPosition(){
        return currentCheckedPosition;
    }
}
