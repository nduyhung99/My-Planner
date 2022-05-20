package com.example.myplanner.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewPagerAddFragmentsAdapter extends FragmentStateAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private Calendar mCalendar;

    public void setData(Calendar calendar){
        this.mCalendar = calendar;
        notifyDataSetChanged();
    }


    public ViewPagerAddFragmentsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        if (mFragmentList!=null){
            return mFragmentList.size();
        }
        return 0;
    }


    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
    }


}