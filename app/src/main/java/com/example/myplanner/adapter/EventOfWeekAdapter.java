package com.example.myplanner.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
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
import com.example.myplanner.model.EventDone;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventOfWeekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Event> mListEvent;
    private List<EventDone> mListDone;
    private Context mContext;
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    IOnClickEventOfWeekListener iOnClickEventOfWeekListener;

    public EventOfWeekAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<Event> list, List<EventDone> listDone){
        this.mListEvent = list;
        this.mListDone = listDone;
        notifyDataSetChanged();
    }

    public void setIOnClickItemListener(IOnClickEventOfWeekListener listener){
        this.iOnClickEventOfWeekListener = listener;
    }

    public void reload(){
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_of_week,parent,false);
        return new ItemEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = mListEvent.get(position);
        ItemEventViewHolder itemEventViewHolder = (ItemEventViewHolder) holder;

        if (event.getType().equals("device")){
            itemEventViewHolder.layoutCheck.setVisibility(View.GONE);
            if (event.getIdEvent().equals("2")){
                itemEventViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_device_2);
            }else {
                itemEventViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_device_3);
            }
        }else {
            itemEventViewHolder.layoutCheck.setVisibility(View.VISIBLE);
            itemEventViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_user);
        }

        itemEventViewHolder.txtTitle.setText(event.getTitle());
//        itemEventViewHolder.txtTitle.setSelected(true);

        if (event.getIdEvent().equals("6")){
//            itemEventViewHolder.imgBirthday.setVisibility(View.VISIBLE);
            itemEventViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_device_1);
        }

        if (event.getType().equals("user")){
            itemEventViewHolder.layoutContainTime.setVisibility(View.VISIBLE);
            itemEventViewHolder.layoutContainTime2.setVisibility(View.VISIBLE);
            itemEventViewHolder.txtTime.setText(formatTime.format(event.getDtStart()));
            itemEventViewHolder.txtTime2.setText(formatTime.format(event.getDtEnd()));

            if (event.isDone()){
                itemEventViewHolder.txtTitle.setPaintFlags(itemEventViewHolder.txtTitle.getPaintFlags() | (~Paint.LINEAR_TEXT_FLAG));
            }else {
                itemEventViewHolder.txtTitle.setPaintFlags(0);
            }
        }

        itemEventViewHolder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemEventViewHolder.txtTitle.setSelected(itemEventViewHolder.txtTitle.isTextSelectable()?false:true);
            }
        });

        itemEventViewHolder.layoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iOnClickEventOfWeekListener!=null){
                    iOnClickEventOfWeekListener.onClickItem(event);
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

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ItemEventViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout layoutContainer, layoutContainTime, layoutContainTime2;
        private RelativeLayout layoutCheck;
        private ImageView imgCheck, imgBirthday;
        private TextView txtTitle, txtTime, txtTime2;
        public ItemEventViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            layoutCheck = itemView.findViewById(R.id.layoutCheck);
            imgCheck = itemView.findViewById(R.id.imgCheck);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgBirthday = itemView.findViewById(R.id.imgBirthday);
            layoutContainTime = itemView.findViewById(R.id.layoutContainTime);
            layoutContainTime2 = itemView.findViewById(R.id.layoutContainTime2);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTime2 = itemView.findViewById(R.id.txtTime2);
        }
    }

    public interface IOnClickEventOfWeekListener{
        void onClickItem(Event event);
    }
}
