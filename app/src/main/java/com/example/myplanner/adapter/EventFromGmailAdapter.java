package com.example.myplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myplanner.R;

import java.util.List;

public class EventFromGmailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> mListAccount;
    List<String> mListGmailShowOff;
    Context mContext;
    IOnSwitchChange iOnSwitchChange;

    public EventFromGmailAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<String> listAccount, List<String> listGmailShowOf){
        this.mListAccount = listAccount;
        this.mListGmailShowOff = listGmailShowOf;
        notifyDataSetChanged();
    }

    public void setiOnSwitchChange(IOnSwitchChange i){
        this.iOnSwitchChange = i;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_from_gmail,parent,false);
        return new EventFromGmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventFromGmailViewHolder viewHolder = (EventFromGmailViewHolder) holder;
        viewHolder.txtGmail.setText(mListAccount.get(position));
        if (mListGmailShowOff.contains(mListAccount.get(position))){
            viewHolder.switchEventFromGmail.setChecked(false);
        }else {
            viewHolder.switchEventFromGmail.setChecked(true);
        }

        viewHolder.switchEventFromGmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (iOnSwitchChange!=null){
                    iOnSwitchChange.onChange(mListAccount.get(position),b);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListAccount!=null){
            return mListAccount.size();
        }
        return 0;
    }

    class EventFromGmailViewHolder extends RecyclerView.ViewHolder{
        private TextView txtGmail;
        private SwitchCompat switchEventFromGmail;

        public EventFromGmailViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGmail = itemView.findViewById(R.id.txtGmail);
            switchEventFromGmail = itemView.findViewById(R.id.switchEventFromGmail);
        }
    }

    public interface IOnSwitchChange{
        void onChange(String account, boolean checked);
    }
}
