package com.example.myplanner.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.icu.text.DateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myplanner.R;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventOfDayRcvMonthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Event> mListEvent;
    private Context mContext;
    private List<EventDone> mListEventDone;
    IOnClickEventOfDay onItemClickListener;
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd MMMM, yyyy");
    SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

    public void setOnItemClickListener(IOnClickEventOfDay listener){
        this.onItemClickListener = listener;
    }

    public EventOfDayRcvMonthAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Event> list, List<EventDone> listDone){
        this.mListEvent = list;
        this.mListEventDone = listDone;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_device_rcv_month,parent,false);
            return new DeviceEventViewHolder(view);
        }else if (viewType==1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_user_rcv_month,parent,false);
            return new UserEventViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = mListEvent.get(position);
        String title = event.getTitle();
        if (title==null || title.equals("")){
            title = mContext.getString(R.string.work);
        }
        if (holder.getItemViewType()==0){
            DeviceEventViewHolder deviceEventViewHolder = (DeviceEventViewHolder) holder;
            deviceEventViewHolder.txtTitleEvent.setText(title);

            deviceEventViewHolder.layoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener!=null){
                        onItemClickListener.onItemClick(event);
                    }
                }
            });

            //**************************************** SET TEXT CHO TEXTVIEW THỜI GIAN DIỄN RA CÔNG VIỆC
            String start = formatTime.format(event.getDtStart());
            String end = formatTime.format(event.getDtEnd());
            String startDay = formatDate.format(event.getDtStart());
            String endDay = formatDate.format(event.getDtEnd());
            String yearStart = formatYear.format(event.getDtStart());
            String yearEnd = formatYear.format(event.getDtEnd());
            String date;
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(event.getDtStart());
            }else {
                date = formatDate.format(event.getDtStart());
            }

            if (startDay.equals(endDay)){
                if (start.equals(end)){
                    deviceEventViewHolder.txtDurationEvent.setText(date);
                }else {
                    deviceEventViewHolder.txtDurationEvent.setText(start+" - "+end);
                }
            }else {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    String dateEnd = DateFormat.getDateInstance(DateFormat.MEDIUM).format(event.getDtEnd());
                    if (yearStart.equals(yearEnd)){
                        deviceEventViewHolder.txtDurationEvent.setText(start+" "+date.substring(0,date.length()-6)+" - "+end +" "+dateEnd.substring(0,dateEnd.length()-6));
                    }else {
                        deviceEventViewHolder.txtDurationEvent.setText(start+" "+date+" - "+end +" "+dateEnd);
                    }
                }else {
                    if (yearStart.equals(yearEnd)){
                        deviceEventViewHolder.txtDurationEvent.setText(start+" "+startDay.substring(0,startDay.length()-6)+" - "+end +" "+endDay.substring(0,endDay.length()-6));
                    }else {
                        deviceEventViewHolder.txtDurationEvent.setText(start+" "+startDay+" - "+end +" "+endDay);
                    }
                }
            }

            //********************************
            if (event.getIdEvent().equals("2")){
                deviceEventViewHolder.imgEvent.setColorFilter(ContextCompat.getColor(mContext, R.color.orange1), android.graphics.PorterDuff.Mode.MULTIPLY);
            }else {
                deviceEventViewHolder.imgEvent.setColorFilter(ContextCompat.getColor(mContext, R.color.teal_700), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            if (!event.getCalendarDisplayName().contains("gmail.com")){
                deviceEventViewHolder.imgEvent.setImageResource(R.drawable.ic_event_device);
            }else {
                deviceEventViewHolder.imgEvent.setImageResource(R.drawable.ic_event_acount);
            }

        }else {
            UserEventViewHolder userEventViewHolder = (UserEventViewHolder) holder;
            userEventViewHolder.txtTitleEvent.setText(title);

            if (event.isDone()){
                userEventViewHolder.imgDone.setVisibility(View.VISIBLE);
                userEventViewHolder.txtTitleEvent.setPaintFlags(userEventViewHolder.txtTitleEvent.getPaintFlags() | (~Paint.LINEAR_TEXT_FLAG));
            }else {
                userEventViewHolder.imgDone.setVisibility(View.GONE);
                userEventViewHolder.txtTitleEvent.setPaintFlags(userEventViewHolder.txtTitleEvent.getPaintFlags() & (~Paint.LINEAR_TEXT_FLAG));
            }

//            userEventViewHolder.imgDone.setVisibility(event.isDone() ? View.VISIBLE : View.GONE);
            //**************************************** SET TEXT CHO TEXTVIEW THỜI GIAN DIỄN RA CÔNG VIỆC
            String start = formatTime.format(event.getDtStart());
            String end = formatTime.format(event.getDtEnd());
            String startDay = formatDate.format(event.getDtStart());
            String endDay = formatDate.format(event.getDtEnd());
            String yearStart = formatYear.format(event.getDtStart());
            String yearEnd = formatYear.format(event.getDtEnd());
            String date;
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(event.getDtStart());
            }else {
                date = formatDate.format(event.getDtStart());
            }

            if (startDay.equals(endDay)){
                if (start.equals(end)){
                    userEventViewHolder.txtDurationEvent.setText(date);
                }else {
                    userEventViewHolder.txtDurationEvent.setText(start+" - "+end);
                }
            }else {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    String dateEnd = DateFormat.getDateInstance(DateFormat.MEDIUM).format(event.getDtEnd());
                    if (yearStart.equals(yearEnd)){
                        userEventViewHolder.txtDurationEvent.setText(start+" "+date.substring(0,date.length()-6)+" - "+end +" "+dateEnd.substring(0,dateEnd.length()-6));
                    }else {
                        userEventViewHolder.txtDurationEvent.setText(start+" "+date+" - "+end +" "+dateEnd);
                    }
                }else {
                    if (yearStart.equals(yearEnd)){
                        userEventViewHolder.txtDurationEvent.setText(start+" "+startDay.substring(0,startDay.length()-6)+" - "+end +" "+endDay.substring(0,endDay.length()-6));
                    }else {
                        userEventViewHolder.txtDurationEvent.setText(start+" "+startDay+" - "+end +" "+endDay);
                    }
                }
            }

            //********************************

            userEventViewHolder.layoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener!=null){
                        onItemClickListener.onItemClick(event);
                    }
                }
            });

            userEventViewHolder.layoutCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    event.setDone(!event.isDone());
//                    userEventViewHolder.imgDone.setVisibility(event.isDone() ? View.VISIBLE : View.GONE);
                    if (event.isDone()){
                        userEventViewHolder.imgDone.setVisibility(View.VISIBLE);
                        userEventViewHolder.txtTitleEvent.setPaintFlags(userEventViewHolder.txtTitleEvent.getPaintFlags() | (~Paint.LINEAR_TEXT_FLAG));
                    }else {
                        userEventViewHolder.imgDone.setVisibility(View.GONE);
                        userEventViewHolder.txtTitleEvent.setPaintFlags(0);
                    }
                    onItemClickListener.onCheckItemClick(event);
                }
            });
            userEventViewHolder.setIsRecyclable(false);
        }
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
        if (mListEvent.get(position).getType().equals("device")){
            return 0;
        }else {
            return 1;
        }
    }

    public class DeviceEventViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitleEvent,txtDurationEvent;
        private ImageView imgEvent;
        private RelativeLayout layoutContainer;

        public DeviceEventViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitleEvent = itemView.findViewById(R.id.txtTitleEvent);
            txtDurationEvent = itemView.findViewById(R.id.txtDurationEvent);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            imgEvent = itemView.findViewById(R.id.imgEvent);
        }
    }

    public class UserEventViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitleEvent,txtDurationEvent;
        private ImageView imgDone;
        private RelativeLayout layoutContainer, layoutCheck;

        public UserEventViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitleEvent = itemView.findViewById(R.id.txtTitleEvent);
            txtDurationEvent = itemView.findViewById(R.id.txtDurationEvent);
            imgDone = itemView.findViewById(R.id.imgDone);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            layoutCheck = itemView.findViewById(R.id.layoutCheck);
        }
    }

    public interface IOnClickEventOfDay{
        void onItemClick(Event event);
        void onCheckItemClick(Event event);
    }
}
