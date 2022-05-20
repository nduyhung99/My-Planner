package com.example.myplanner.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myplanner.R;
import com.example.myplanner.model.Event;
import com.example.myplanner.model.EventDone;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListWorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Date currentDate;
    private List<Event> mListEvent;
    private List<EventDone> mListDone;
    SimpleDateFormat formatNumberOfDay = new SimpleDateFormat("dd");
    SimpleDateFormat formatDay = new SimpleDateFormat("EEE");
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    private IOnClickEventAndWorkListener iOnClickEventAndWorkListener;

    public void setIOnClickEventAndWorkListener(IOnClickEventAndWorkListener listener){
        this.iOnClickEventAndWorkListener = listener;
    }

    public ListWorkAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<Event> list, List<EventDone> listDone, Date date){
        this.mListEvent = list;
        this.mListDone = listDone;
        this.currentDate = date;
        notifyDataSetChanged();
    }

    public void reload(){
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title_month,parent,false);
            return new TitleMonthViewHolder(view);
        }else if (viewType==1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_period,parent,false);
            return new PeriodViewHolder(view);
        }else if (viewType==2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_and_work,parent,false);
            return new EventAndWorkViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int viewType = holder.getItemViewType();
        Event event = mListEvent.get(position);
        if (viewType == 0){
            TitleMonthViewHolder titleMonthViewHolder = (TitleMonthViewHolder) holder;
            titleMonthViewHolder.txtMonth.setText(event.getTitle());
            int idDrawable = getIdDrawable(event.getRepeat());
            Glide.with(titleMonthViewHolder.imgMonth.getContext()).load(idDrawable)
                    .override(1000,300)
                    .dontAnimate()
                    .into(titleMonthViewHolder.imgMonth);
            titleMonthViewHolder.setIsRecyclable(false);
        }else if (viewType == 1){
            PeriodViewHolder periodViewHolder = (PeriodViewHolder) holder;
            if (formatNumberOfDay.format(event.getDtStart()).equals(formatNumberOfDay.format(event.getDtEnd()))){
                String dateEnd = DateFormat.getDateInstance().format(event.getDtEnd());
                periodViewHolder.txtPeriod.setText(formatNumberOfDay.format(event.getDtStart())+" - "+dateEnd.substring(0,dateEnd.length()-6));
            }else {
                String dateStart = DateFormat.getDateInstance().format(event.getDtStart());
                String dateEnd = DateFormat.getDateInstance().format(event.getDtEnd());
                periodViewHolder.txtPeriod.setText(dateStart.substring(0,dateStart.length()-6)+" - "+dateEnd.substring(0,dateEnd.length()-6));
            }
            periodViewHolder.setIsRecyclable(false);
        }else {
            EventAndWorkViewHolder eventAndWorkViewHolder = (EventAndWorkViewHolder) holder;
            if (event.getType().equals("device")){
                eventAndWorkViewHolder.layoutCheck.setVisibility(View.GONE);
                if (event.getIdEvent().equals("2")){
                    eventAndWorkViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_device_2);
                }else {
                    eventAndWorkViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_device_3);
                }
            }else {
                eventAndWorkViewHolder.layoutCheck.setVisibility(View.VISIBLE);
                eventAndWorkViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_user);
            }

//            ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
//                    .toBuilder()
//                    .setAllCorners(CornerFamily.ROUNDED,16)
//                    .build();
//
//            MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
//            ViewCompat.setBackground(eventAndWorkViewHolder.layoutContainer,shapeDrawable);
//
//            if (event.getType().equals("device")){
//                int[][] states = new int[][] {
//                        new int[] { android.R.attr.state_hovered}, // hovered
//                };
//
//                int[] colors = new int[] {event.getColorEvent()};
//                ColorStateList myColorList = new ColorStateList(states, colors);
//                shapeDrawable.setFillColor(myColorList);
//                shapeDrawable.setState(states[0]);
//
//                eventAndWorkViewHolder.layoutCheck.setVisibility(View.GONE);
////                shapeDrawable.setFillColor(ContextCompat.getColorStateList(mContext,R.color.blue1));
//            }else {
//                eventAndWorkViewHolder.layoutCheck.setVisibility(View.VISIBLE);
//                shapeDrawable.setFillColor(ContextCompat.getColorStateList(mContext,R.color.blue1));
//            }

            if (event.getType().equals("user")){
                String time = formatTime.format(event.getDtStart());
                eventAndWorkViewHolder.txtTitle.setText(time+ " " +event.getTitle());
            }else {
                eventAndWorkViewHolder.txtTitle.setText(event.getTitle());
            }

            if (event.getVisibleDate()==1){
                eventAndWorkViewHolder.txtDay.setText(formatDay.format(event.getDtStart()));
                eventAndWorkViewHolder.txtNumberOfDay.setText(formatNumberOfDay.format(event.getDtStart()));
                eventAndWorkViewHolder.layoutDate.setVisibility(View.VISIBLE);
                if (formatDate.format(currentDate).equals(formatDate.format(event.getDtStart()))){
                    eventAndWorkViewHolder.txtDay.setTextColor(mContext.getResources().getColor(R.color.blue1));
                    eventAndWorkViewHolder.txtNumberOfDay.setTextColor(Color.WHITE);
                    eventAndWorkViewHolder.txtNumberOfDay.setBackgroundResource(R.drawable.background_current_day);
                    if (event.getType().equals("current_day")){
                        eventAndWorkViewHolder.txtHaveNoWork.setVisibility(View.VISIBLE);
                        eventAndWorkViewHolder.layoutContainer.setVisibility(View.GONE);
                    }else {
                        eventAndWorkViewHolder.txtHaveNoWork.setVisibility(View.GONE);
                        eventAndWorkViewHolder.layoutContainer.setVisibility(View.VISIBLE);
                    }
                }
            }else {
                eventAndWorkViewHolder.layoutDate.setVisibility(View.INVISIBLE);
            }

            if (event.getIdEvent().equals("6")){
                eventAndWorkViewHolder.imgBirthday.setVisibility(View.VISIBLE);
                eventAndWorkViewHolder.layoutContainer.setBackgroundResource(R.drawable.background_event_and_work_device_1);
            }

            if (event.isDone()){
                eventAndWorkViewHolder.imgDone.setVisibility(View.VISIBLE);
                eventAndWorkViewHolder.txtTitle.setPaintFlags(eventAndWorkViewHolder.txtTitle.getPaintFlags() | (~Paint.LINEAR_TEXT_FLAG));
            }else {
                eventAndWorkViewHolder.imgDone.setVisibility(View.GONE);
                eventAndWorkViewHolder.txtTitle.setPaintFlags(0);
            }

            eventAndWorkViewHolder.layoutCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    event.setDone(!event.isDone());
//                    userEventViewHolder.imgDone.setVisibility(event.isDone() ? View.VISIBLE : View.GONE);
                    if (event.isDone()){
                        eventAndWorkViewHolder.imgDone.setVisibility(View.VISIBLE);
                        eventAndWorkViewHolder.txtTitle.setPaintFlags(eventAndWorkViewHolder.txtTitle.getPaintFlags() | (~Paint.LINEAR_TEXT_FLAG));
                    }else {
                        eventAndWorkViewHolder.imgDone.setVisibility(View.GONE);
                        eventAndWorkViewHolder.txtTitle.setPaintFlags(0);
                    }
                    iOnClickEventAndWorkListener.onClickCheckItem(event, position);
                }
            });

            eventAndWorkViewHolder.layoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iOnClickEventAndWorkListener.onClickItemToDetails(event);
                }
            });

            eventAndWorkViewHolder.txtHaveNoWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iOnClickEventAndWorkListener.onClickItemToCreate(event);
                }
            });

            eventAndWorkViewHolder.setIsRecyclable(false);
        }
        holder.setIsRecyclable(false);
    }

    private int getIdDrawable(int notification) {
        switch (notification){
            case 1:
                return R.drawable.bkg_01_january;
            case 2:
                return R.drawable.bkg_02_february;
            case 3:
                return R.drawable.bkg_03_march;
            case 4:
                return R.drawable.bkg_04_april;
            case 5:
                return R.drawable.bkg_05_may;
            case 6:
                return R.drawable.bkg_06_june;
            case 7:
                return R.drawable.bkg_07_july;
            case 8:
                return R.drawable.bkg_08_august;
            case 9:
                return R.drawable.bkg_09_september;
            case 10:
                return R.drawable.bkg_10_october;
            case 11:
                return R.drawable.bkg_11_november;
            case 12:
                return R.drawable.bkg_12_december;
            default:
                return R.drawable.bkg_01_january;
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
        String type = mListEvent.get(position).getType();
        if (type.equals("title_month")){
            return 0;
        }else if (type.equals("period")){
            return 1;
        }else{
            return 2;
        }
    }

    public class PeriodViewHolder extends RecyclerView.ViewHolder{
        private TextView txtPeriod;

        public PeriodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPeriod = itemView.findViewById(R.id.txtPeriod);
        }
    }

    public class TitleMonthViewHolder extends RecyclerView.ViewHolder{
        private TextView txtMonth;
        private ImageView imgMonth;

        public TitleMonthViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMonth = itemView.findViewById(R.id.txtMonth);
            imgMonth = itemView.findViewById(R.id.imgMonth);
        }
    }

    public class EventAndWorkViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout layoutContainer;
        private RelativeLayout layoutCheck, layoutDate;
        private ImageView imgDone, imgNotDone, imgBirthday;
        private TextView txtTitle, txtDay, txtNumberOfDay, txtHaveNoWork;

        public EventAndWorkViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            layoutCheck = itemView.findViewById(R.id.layoutCheck);
            imgDone = itemView.findViewById(R.id.imgDone);
            imgNotDone = itemView.findViewById(R.id.imgNotDone);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            layoutDate = itemView.findViewById(R.id.layoutDate);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtNumberOfDay = itemView.findViewById(R.id.txtNumberOfDay);
            txtHaveNoWork = itemView.findViewById(R.id.txtHaveNoWork);
            imgBirthday = itemView.findViewById(R.id.imgBirthday);
        }
    }

    public interface IOnClickEventAndWorkListener{
        void onClickCheckItem(Event event, int position);
        void onClickItemToDetails(Event event);
        void onClickItemToCreate(Event event);
    }

}
