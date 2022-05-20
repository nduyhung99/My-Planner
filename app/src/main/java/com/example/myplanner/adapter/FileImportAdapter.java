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

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class FileImportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<File> mListFile;
    Context mContext;
    int selectedPosition = -1;
    IFileImportOnclick iFileImportOnclick;

    public FileImportAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<File> list){
        this.mListFile = list;
        notifyDataSetChanged();
    }

    public void setiFileImportOnclick(IFileImportOnclick listener){
        this.iFileImportOnclick = listener;
    }

    public void selected(int position){
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_import,parent,false);
        return new FileImportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        File file = mListFile.get(position);
        FileImportViewHolder fileImportViewHolder = (FileImportViewHolder) holder;
        fileImportViewHolder.txtPath.setText(file.getAbsolutePath());
        fileImportViewHolder.txtFileName.setText(file.getName());
        if (selectedPosition == position){
            fileImportViewHolder.imgSelectFile.setVisibility(View.VISIBLE);
        }else {
            fileImportViewHolder.imgSelectFile.setVisibility(View.GONE);
        }
        fileImportViewHolder.layoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iFileImportOnclick!=null){
                    iFileImportOnclick.onClickItem(file,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListFile!=null){
            return mListFile.size();
        }
        return 0;
    }

    class FileImportViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout layoutContainer;
        TextView txtPath, txtFileName;
        ImageView imgSelectFile;

        public FileImportViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            txtPath = itemView.findViewById(R.id.txtPath);
            txtFileName = itemView.findViewById(R.id.txtFileName);
            imgSelectFile = itemView.findViewById(R.id.imgSelectFile);
        }
    }

    public interface IFileImportOnclick{
        void onClickItem(File file,int position);
    }
}
