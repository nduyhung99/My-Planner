package com.example.myplanner.adapter;

import static com.applandeo.materialcalendarview.utils.CalendarProperties.CALENDAR_SIZE;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.applandeo.materialcalendarview.extensions.CalendarGridView;
import com.example.myplanner.CalendarFragment;
import com.example.myplanner.R;
import com.example.myplanner.model.DateOfCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewPagerCalendarAdapter extends PagerAdapter {
    private Context mContext;
    private RecyclerView recyclerView;
    private DateOfCalendarAdapter dateOfCalendarAdapter;
    private List<DateOfCalendar> listDate;

    List<Date> dateList = new ArrayList<>();
    List<DateOfCalendar> listDateOfCalender = new ArrayList<>();
    Calendar mCalendar;

    public ViewPagerCalendarAdapter(Context context, Calendar calendar) {
        this.mContext = context;
        this.mCalendar = calendar;
    }

    public void setData(List<DateOfCalendar> list){
        this.listDate = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (listDate!=null){
            return CALENDAR_SIZE;
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object.getClass()==view.getClass();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_calendar,container,false);
        recyclerView = view.findViewById(R.id.rcvNumber);

//        setUpDate(position);
//        loadListDateOfCalendar();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,7);
        recyclerView.setLayoutManager(gridLayoutManager);
        dateOfCalendarAdapter = new DateOfCalendarAdapter(mContext);
        dateOfCalendarAdapter.setData(listDate,null,null);
        recyclerView.setAdapter(dateOfCalendarAdapter);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        CalendarFragment calendarFragment = (CalendarFragment) object;
//        calendarFragment.reload();
//        return super.getItemPosition(object);
        return POSITION_NONE;
    }

    private void setUpDate(int position){
        dateList.clear();

        mCalendar.add(Calendar.MONTH,position);

        Calendar monthCalendar = (Calendar) mCalendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-2;
        if (firstDayOfMonth<0){
            monthCalendar.add(Calendar.DAY_OF_MONTH,-6);
        }else {
            monthCalendar.add(Calendar.DAY_OF_MONTH,-firstDayOfMonth);
        }

        while (dateList.size()<42){
            dateList.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);
        }
    }

    private void loadListDateOfCalendar() {
        listDateOfCalender.clear();
        for (int i=0; i<dateList.size(); i++){
            listDateOfCalender.add(new DateOfCalendar(dateList.get(i),""));
        }
    }


    public interface IReload{
        void reload();
    }
}
