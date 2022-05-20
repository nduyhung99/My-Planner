package com.example.myplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyPlannerDatabase extends SQLiteOpenHelper {
    private static String name = "my_planner.sqlite";
    private static int version = 1;

    public MyPlannerDatabase(@Nullable Context context) {
        super(context, name, null, version);
    }

    // truy van khong tra ket qua: CREATE, INSERT, DELETE, UPDATE...
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //truy van co tra ket qua:SELECT
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Events(IdEvent LONG PRIMARY KEY,Event VARCHAR(200), Description VARCHAR(1000), TimeStart LONG, TimeEnd LONG, Notification INTEGER, Repeat INTEGER, DateEndORTimeRepeat LONG, Done INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS EventsDone(IdEvent LONG, DateCompleted CHAR(50), LONG TimeStart)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i<2){
            updateToVersion2(sqLiteDatabase);
        }
    }

    private void updateToVersion2(SQLiteDatabase db) {
        // thực hiện update database khi cần thiết
    }
}
