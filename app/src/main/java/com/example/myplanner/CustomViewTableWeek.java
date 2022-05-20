package com.example.myplanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myplanner.model.Event;

import java.util.ArrayList;
import java.util.List;

public class CustomViewTableWeek extends View {
    private Paint mLinePaint;
    private Paint mTextPaint;

    private float mFirstX = 100;
    private float mFirstY = 100;
    private float maxHeight=1000;
    private float maxWidth=1000;
    private float mSizeOfText = 80;
    private float spaceSize = 10;
    private RectF mArcBounds = new RectF();
    private List<Event> listEvent = new ArrayList<>();

    Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    Paint paint = new Paint();

    public void reDraw(List<Event> list){
        this.listEvent = list;
        invalidate();
    }

    public CustomViewTableWeek(Context context) {
        super(context);
        initPaints();
    }

    public CustomViewTableWeek(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public CustomViewTableWeek(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    public CustomViewTableWeek(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaints();
    }

    private void initPaints() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
//        mLinePaint.setStrokeWidth(2 * getResources().getDisplayMetrics().density);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStrokeCap(Paint.Cap.SQUARE);

        mTextPaint= new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(26);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(w,h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mFirstX = (w-mSizeOfText-spaceSize);
        mFirstY = h / 24f;
        maxHeight = h;
        maxWidth = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i=0;i<23;i++){
            canvas.drawLine(mSizeOfText,(maxHeight/24)*(i+1),maxWidth,(maxHeight/24)*(i+1),mLinePaint);
            if (i+1<10){
                canvas.drawText("0"+(i+1)+":00",4,mFirstY*(i+1)+4,mTextPaint);
            }else {
                canvas.drawText((i+1)+":00",4,mFirstY*(i+1)+4,mTextPaint);
            }
        }

        canvas.drawLine(mSizeOfText+spaceSize,0,spaceSize+mSizeOfText,maxHeight,mLinePaint);
        for (int i=0; i<6; i++){
            canvas.drawLine(mFirstX/7*(i+1)+(mSizeOfText+spaceSize),0,mFirstX/7*(i+1)+(mSizeOfText+spaceSize),maxHeight,mLinePaint);
        }
    }
}
