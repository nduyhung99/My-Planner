package com.example.myplanner.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myplanner.R;
import com.example.myplanner.model.Event;

import java.io.File;
import java.util.List;

public class WorkChartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Event> mListEvent;
    Context mContext;
    IOnClickWorkChart iOnClickWorkChart;
    int selectedPosition = 0;

    public WorkChartAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<Event> list){
        this.mListEvent = list;
        notifyDataSetChanged();
    }

    public void selected(int position){
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public void setOnClickListener(IOnClickWorkChart i){
        this.iOnClickWorkChart = i;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_chart,parent,false);
        return new WorkChartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Event event = mListEvent.get(position);
        WorkChartViewHolder workChartViewHolder = (WorkChartViewHolder) holder;
        workChartViewHolder.txtWorkName.setText(event.getTitle());
        if (!event.getDescription().equals("")){
            workChartViewHolder.txtDescription.setText(mContext.getString(R.string.description)+" "+event.getDescription());
        }else {
            workChartViewHolder.txtDescription.setText(mContext.getString(R.string.description)+" "+mContext.getString(R.string.have_no_description));
        }

        if (selectedPosition == position){
            workChartViewHolder.imgSelect.setVisibility(View.VISIBLE);
        }else {
            workChartViewHolder.imgSelect.setVisibility(View.GONE);
        }
        workChartViewHolder.layoutWorkChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iOnClickWorkChart!=null){
                    iOnClickWorkChart.onClickItem(event);
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListEvent!=null){
            return mListEvent.size();
        }
        return 0;
    }

    class WorkChartViewHolder extends RecyclerView.ViewHolder{
        private TextView txtWorkName,txtDescription;
        private ImageView imgSelect;
        private RelativeLayout layoutWorkChart;

        public WorkChartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWorkName = itemView.findViewById(R.id.txtWorkName);
            imgSelect = itemView.findViewById(R.id.imgSelect);
            layoutWorkChart = itemView.findViewById(R.id.layoutWorkChart);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }

    public interface IOnClickWorkChart{
        void onClickItem(Event event);
    }
}
