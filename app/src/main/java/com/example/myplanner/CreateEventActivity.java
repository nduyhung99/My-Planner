package com.example.myplanner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.myplanner.database.MyPlannerDatabase;
import com.example.myplanner.model.Event;
import com.example.myplanner.notification.MyBroadcastReceiver;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {
    public static final int maxRepeatDay = 365, maxRepeatWeek = 120, maxRepeatMonth = 48, maxRepeatYear = 12;
    private TextView txtSave, txtCancel, txtDateStart, txtTimeStart, txtDateEnd, txtTimeEnd, txtRepeatUntil, txtTitleActivity;
    private ImageView imgBack, imgTick;
    private EditText edtWorkName, edtDescription;
    private TextInputLayout tilRepeat, tilNotification;
    private LinearLayout layoutRepeatUntil, layoutError;
    private SwitchCompat switchNotification;
    private CardView cardColor;
    private Date date, dateStart, dateEnd;
    private Calendar calendarStart, calendarEnd, calendarSettingEndDate;
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    ActivityResultLauncher<Intent> activityResultLauncher;

    private ImageView imgVoice;
    private AutoCompleteTextView autotxtRepeat, autotxtNotification;
    SimpleDateFormat formatDay = new SimpleDateFormat("dd");
    SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
    SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
    SimpleDateFormat formatHour = new SimpleDateFormat("HH");
    SimpleDateFormat formatMinute = new SimpleDateFormat("mm");

    SimpleDateFormat formatDayName = new SimpleDateFormat("EEEE");
    int currentPicker=0;
    int timeRepeat = 5;
    int saveFirst = 0;
    private String mission = "";
    private Event eventReceive;
    ArrayAdapter arrayAdapterRepeat;
    String language="";

    MyPlannerDatabase myPlannerDatabase;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.gray1), false, false, CreateEventActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
        language = sharedPreferences.getString("language","");
        if (!language.equals("")){
            MainActivity.setLanguage(this,language);
        }
        setContentView(R.layout.activity_create_event);
        addControls();

        autotxtRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String repeatUntil = charSequence.toString().trim();
                if (repeatUntil.contains(getString(R.string.every_week))){
                    if (timeRepeat>60){
                        timeRepeat = 60;
                    }
                }else if (repeatUntil.contains(getString(R.string.every_month))){
                    if (timeRepeat>48){
                        timeRepeat = 48;
                    }
                }else if (repeatUntil.contains(getString(R.string.every_year))){
                    if (timeRepeat>12){
                        timeRepeat = 12;
                    }
                }
                if (txtRepeatUntil.getText().toString().contains(getString(R.string.times))){
                    txtRepeatUntil.setText(timeRepeat+" "+getString(R.string.times));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edtWorkName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (saveFirst==1){
                    if (charSequence.toString().trim().equals("")){
                        layoutError.setVisibility(View.VISIBLE);
                    }else {
                        layoutError.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWork();
            }
        });

        imgTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWork();
            }
        });

        layoutRepeatUntil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lol = txtRepeatUntil.getText().toString();
                showBottomSheetRepeatUntil(lol);
            }
        });

        autotxtRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals(getString(R.string.no_repeat))){
                    layoutRepeatUntil.setVisibility(View.GONE);
                }else {
                    layoutRepeatUntil.setVisibility(View.VISIBLE);
                    String lol = txtRepeatUntil.getText().toString();
                    showBottomSheetRepeatUntil(lol);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    tilNotification.setVisibility(View.VISIBLE);
                }else {
                    tilNotification.setVisibility(View.INVISIBLE);
                }
            }
        });

        txtTimeEnd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                currentPicker = 4;
                showTimePickerDialog();
            }
        });

        txtTimeStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                currentPicker = 3;
                showTimePickerDialog();
            }
        });

        txtDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPicker = 2;
                showDatePickerDialog("date_work");
            }
        });

        txtDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPicker = 1;
                showDatePickerDialog("date_work");
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    Intent intentData = result.getData();
                    ArrayList<String> lol = intentData
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edtWorkName.setText(lol.get(0));
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imgVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void saveWork() {
        String title = edtWorkName.getText().toString().trim();
        if (title.equals("")){
            layoutError.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Bounce).duration(100).repeat(1).playOn(layoutError);
            saveFirst = 1;
            Toast.makeText(this, getString(R.string.name_work_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        String description = edtDescription.getText().toString();
        long dateStart = calendarStart.getTime().getTime();
        long dateEnd = calendarEnd.getTime().getTime();
        long idEvent = System.currentTimeMillis();
        long dateEndORTimeRepeat = 0;
        int notification = 0, repeat=0, typeRepeat = 0;

        typeRepeat = getTimeRepeat();
        if (layoutRepeatUntil.getVisibility()==View.VISIBLE){
            repeat = getRepeat();
            if (typeRepeat==0){
                dateEndORTimeRepeat=0;
            }else if (typeRepeat==1){
                dateEndORTimeRepeat = calendarSettingEndDate.getTime().getTime();
            }else if (typeRepeat==2){
                dateEndORTimeRepeat = timeRepeat;
            }
        }
        if (tilNotification.getVisibility()==View.VISIBLE){
            notification = getTimeNotification();
        }
//        Events(IdEvent LONG,Event VARCHAR(200), Description VARCHAR(1000), TimeStart LONG, TimeEnd LONG, Notification INTEGER, Repeat INTEGER, DateEndORTimeRepeat LONG, Done INTEGER)

//        txtTest.setText(idEvent+"\n"+title+"\n"+description+"\n"+formatDate.format(dateStart)+"\n"+formatDate.format(dateEnd)+"\n"+notification+"\n"+repeat+"\n"+dateEndORTimeRepeat);

        if (mission.equals("repair")){
            long id = Long.parseLong(eventReceive.getIdEvent());
            myPlannerDatabase.queryData("delete from Events where IdEvent = "+id+"");
            myPlannerDatabase.queryData("insert into Events values("+id+",'"+title+"','"+description+"',"+dateStart+","+dateEnd+","+notification+","+repeat+","+dateEndORTimeRepeat+",0)");
            if (notification!=0){
                createNotification(eventReceive.getIdEvent(),dateStart,notification,title,description,getApplicationContext());
            }else {
                cancelNotification(eventReceive.getIdEvent());
            }
            Intent resultIntent = new Intent();
            setResult(RESULT_OK,resultIntent);
            finish();
            Toast.makeText(CreateEventActivity.this, getString(R.string.repeair_successfully), Toast.LENGTH_SHORT).show();
        }else {
            myPlannerDatabase.queryData("insert into Events values("+idEvent+",'"+title+"','"+description+"',"+dateStart+","+dateEnd+","+notification+","+repeat+","+dateEndORTimeRepeat+",0)");
            if (notification!=0){
                createNotification(String.valueOf(idEvent),dateStart,notification,title,description,getApplicationContext());
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("idEvent",idEvent);
            setResult(RESULT_OK,resultIntent);
            finish();
            Toast.makeText(CreateEventActivity.this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelNotification(String idEvent) {
        int requestCode = Integer.parseInt(idEvent.substring(0,idEvent.length()-3));
        Intent intent = new Intent(this,MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void createNotification(String idEvent, long dateStart, int notification,String title, String description, Context context) {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        int requestCode = Integer.valueOf(idEvent.substring(0,idEvent.length()-3));
        intent.putExtra("idEvent",idEvent);
        intent.putExtra("description",description);
        intent.putExtra("dtStart",String.valueOf(dateStart));
        intent.putExtra("title",title);
        // todo: chuyen sang service chay ngam
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long timeWakeup = dateStart;
        switch (notification){
            case 1:
                timeWakeup = dateStart;
                break;
            case 2:
                timeWakeup = dateStart - (5*60*1000);
                break;
            case 3:
                timeWakeup = dateStart - (15*60*1000);
                break;
            case 4:
                timeWakeup = dateStart - (30*60*1000);
                break;
            default:
                break;
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP,timeWakeup,pendingIntent);
    }

    private int getTimeRepeat() {
        String timeRepeat = txtRepeatUntil.getText().toString();
        if (timeRepeat.equals(getString(R.string.no_end_date))){
            return 0;
        }else if (timeRepeat.contains("/")){
            return 1;
        }else if (timeRepeat.contains(getString(R.string.times))){
            return 2;
        }
        return 0;
    }

    private int getRepeat() {
        String repeatUntil = autotxtRepeat.getText().toString();
        if (repeatUntil.equals(getString(R.string.every_day))){
            return 1;
        }else if (repeatUntil.contains(getString(R.string.every_week))){
            return 2;
        }else if (repeatUntil.contains(getString(R.string.every_month))){
            return 3;
        }else if (repeatUntil.contains(getString(R.string.every_year))){
            return 4;
        }
        return 0;
    }

    private int getTimeNotification() {
        String notification = autotxtNotification.getText().toString();
        if (notification.equals(getString(R.string.at_time))){
            return 1;
        }else if (notification.equals(getString(R.string.before_5_minutes))){
            return 2;
        }else if (notification.equals(getString(R.string.before_15_minutes))){
            return 3;
        }else if (notification.equals(getString(R.string.before_30_minutes))){
            return 4;
        }
        return 0;
    }

    private void showBottomSheetRepeatUntil(String lol) {
        View viewBottomSheetDialog = getLayoutInflater().inflate(R.layout.bottom_sheet_repeat,null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreateEventActivity.this,R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(viewBottomSheetDialog);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        RadioGroup radGroup = viewBottomSheetDialog.findViewById(R.id.radGroup);
        RadioButton radNoEndDate = viewBottomSheetDialog.findViewById(R.id.radNoEndDate);
        RadioButton radEndDateSetting = viewBottomSheetDialog.findViewById(R.id.radEndDateSetting);
        RadioButton radTimeSetting = viewBottomSheetDialog.findViewById(R.id.radTimeSetting);
        TextView txtDateEnd = viewBottomSheetDialog.findViewById(R.id.txtDateEnd);
        LinearLayout layoutTimeSetting = viewBottomSheetDialog.findViewById(R.id.layoutTimeSetting);
        EditText edtTimeSetting = viewBottomSheetDialog.findViewById(R.id.edtTimeSetting);
        TextView txtCancel = viewBottomSheetDialog.findViewById(R.id.txtCancel);
        TextView txtOk = viewBottomSheetDialog.findViewById(R.id.txtOk);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (calendarSettingEndDate==null || calendarSettingEndDate.getTime().getTime()<calendarStart.getTime().getTime()){
            calendarSettingEndDate = (Calendar) calendarStart.clone();
            calendarSettingEndDate.add(Calendar.DAY_OF_MONTH,4);
        }
        txtDateEnd.setText(formatDate.format(calendarSettingEndDate.getTime()));

        txtDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                showDatePickerDialog("date_repeat");
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (radGroup.getCheckedRadioButtonId()){
                    case R.id.radNoEndDate:
                        txtRepeatUntil.setText(getString(R.string.no_end_date));
                        break;
                    case R.id.radEndDateSetting:
                        txtRepeatUntil.setText(formatDate.format(calendarSettingEndDate.getTime()));
                        break;
                    case R.id.radTimeSetting:
                        int repeat = Integer.parseInt(edtTimeSetting.getText().toString());
                        int rep = getRepeat();
                        setTimeRepeat(rep,repeat);
                        txtRepeatUntil.setText(timeRepeat+" "+getString(R.string.times));
                        break;
                    default:
                        break;
                }
                bottomSheetDialog.dismiss();
            }
        });

        radTimeSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    layoutTimeSetting.setVisibility(View.VISIBLE);
                    edtTimeSetting.setText(String.valueOf(timeRepeat));
                    edtTimeSetting.requestFocus();
//                    edtTimeSetting.setCursorVisible(true);
                    edtTimeSetting.selectAll();
                    edtTimeSetting.setSelectAllOnFocus(true);
                    txtDateEnd.setText(formatDate.format(calendarSettingEndDate.getTime()));
                    imm.showSoftInput(edtTimeSetting, InputMethodManager.SHOW_IMPLICIT);
                }else {
                    layoutTimeSetting.setVisibility(View.GONE);
//                    edtTimeSetting.setCursorVisible(false);
                    imm.hideSoftInputFromWindow(edtTimeSetting.getWindowToken(), 0);
                }
            }
        });

        radEndDateSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    txtDateEnd.setVisibility(View.VISIBLE);
                }else {
                    txtDateEnd.setVisibility(View.GONE);
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        if (lol.contains(getString(R.string.no_end_date))){
            radNoEndDate.setChecked(true);
            txtDateEnd.setVisibility(View.GONE);
            layoutTimeSetting.setVisibility(View.GONE);
        }else if (lol.contains(getString(R.string.times))){
            radTimeSetting.setChecked(true);
            txtDateEnd.setVisibility(View.GONE);
            layoutTimeSetting.setVisibility(View.VISIBLE);
        }else {
            radEndDateSetting.setChecked(true);
            layoutTimeSetting.setVisibility(View.GONE);
            txtDateEnd.setVisibility(View.VISIBLE);
        }
        bottomSheetDialog.show();
    }

    private void setTimeRepeat(int rep, int repeat) {
        if (repeat!=0){
            timeRepeat = repeat;
        }
        switch (rep){
            case 1:
                if (repeat>365){
                    timeRepeat = maxRepeatDay;
                }
                break;
            case 2:
                if (repeat>120){
                    timeRepeat = maxRepeatWeek;
                }
                break;
            case 3:
                if (repeat>48){
                    timeRepeat = maxRepeatMonth;
                }
                break;
            case 4:
                if (repeat>12){
                    timeRepeat = maxRepeatYear;
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimePickerDialog() {
        Dialog dialog=new Dialog(CreateEventActivity.this);
        dialog.setContentView(R.layout.dialog_time_picker);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TimePicker timePicker = dialog.findViewById(R.id.timePicker);
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtOk = dialog.findViewById(R.id.txtOk);
        timePicker.setIs24HourView(true);

        if (currentPicker==3){
            timePicker.setHour(Integer.parseInt(formatHour.format(calendarStart.getTime())));
            timePicker.setMinute(Integer.parseInt(formatMinute.format(calendarStart.getTime())));
        }else if (currentPicker==4){
            timePicker.setHour(Integer.parseInt(formatHour.format(calendarEnd.getTime())));
            timePicker.setMinute(Integer.parseInt(formatMinute.format(calendarEnd.getTime())));
        }

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                if (currentPicker==3){
                    calendarStart.set(Calendar.HOUR_OF_DAY,hour);
                    calendarStart.set(Calendar.MINUTE,minute);
                    txtTimeStart.setText(formatTime.format(calendarStart.getTime()));
                    if (calendarStart.getTime().getTime() > calendarEnd.getTime().getTime()){
                        calendarEnd.set(Calendar.HOUR_OF_DAY,hour);
                        calendarEnd.set(Calendar.MINUTE,minute+30);
                        txtTimeEnd.setText(formatTime.format(calendarEnd.getTime()));
                        txtDateEnd.setText(formatDate.format(calendarEnd.getTime()));
                    }
                }else if (currentPicker==4){
                    calendarEnd.set(Calendar.HOUR_OF_DAY,hour);
                    calendarEnd.set(Calendar.MINUTE,minute);
                    txtTimeEnd.setText(formatTime.format(calendarEnd.getTime()));
                    if (calendarEnd.getTime().getTime()<calendarStart.getTime().getTime()){
                        calendarEnd.add(Calendar.DAY_OF_MONTH,1);
                        txtDateEnd.setText(formatDate.format(calendarEnd.getTime()));
                        Toast.makeText(CreateEventActivity.this, getString(R.string.set_date_end_to_next_day), Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDatePickerDialog(String type) {
        Dialog dialog=new Dialog(CreateEventActivity.this);
        dialog.setContentView(R.layout.dialog_date_picker);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtOk = dialog.findViewById(R.id.txtOk);

        if (type.equals("date_work")){
            if (currentPicker==1){
                Date current = calendarStart.getTime();
                datePicker.updateDate(Integer.parseInt(formatYear.format(current)), Integer.parseInt(formatMonth.format(current))-1, Integer.parseInt(formatDay.format(current)));
            }else if (currentPicker==2){
                Date current = calendarEnd.getTime();
                datePicker.updateDate(Integer.parseInt(formatYear.format(current)), Integer.parseInt(formatMonth.format(current))-1, Integer.parseInt(formatDay.format(current)));
            }
        }else if (type.equals("date_repeat")){
            Date current = calendarSettingEndDate.getTime();
            datePicker.updateDate(Integer.parseInt(formatYear.format(current)), Integer.parseInt(formatMonth.format(current))-1, Integer.parseInt(formatDay.format(current)));
        }

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("date_work")){
                    if (currentPicker==1){
                        if (txtRepeatUntil.getText().toString().contains("/")){
                            Toast.makeText(CreateEventActivity.this, "Ngày lặp lại không thể trước ngày bắt đầu!", Toast.LENGTH_SHORT).show();
                        }else {
                            calendarStart.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                            calendarStart.set(Calendar.MONTH, datePicker.getMonth());
                            calendarStart.set(Calendar.YEAR, datePicker.getYear());
                            txtDateStart.setText(formatDate.format(calendarStart.getTime()));
                            setAdapterAutotxtRepeat(calendarStart.getTime());
                            if (calendarStart.getTime().getTime()>calendarEnd.getTime().getTime()){
                                calendarEnd.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                                calendarEnd.set(Calendar.MONTH, datePicker.getMonth());
                                calendarEnd.set(Calendar.YEAR, datePicker.getYear());
                                txtDateEnd.setText(formatDate.format(calendarEnd.getTime()));
                            }
                        }
                    }else if (currentPicker==2){
                        Calendar tempCalendar = (Calendar) calendarEnd.clone();
                        tempCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        tempCalendar.set(Calendar.MONTH, datePicker.getMonth());
                        tempCalendar.set(Calendar.YEAR, datePicker.getYear());
                        if (calendarStart.getTime().getTime()>tempCalendar.getTime().getTime()){
                            Toast.makeText(CreateEventActivity.this, "Ngày kết thúc không thể trước ngày bắt đầu!", Toast.LENGTH_SHORT).show();
                        }else {
                            calendarEnd = (Calendar) tempCalendar.clone();
                            txtDateEnd.setText(formatDate.format(calendarEnd.getTime()));
                        }
                    }
                }else if (type.equals("date_repeat")){
                    Calendar tempCalendar = (Calendar) calendarSettingEndDate.clone();
                    tempCalendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
                    tempCalendar.set(Calendar.MONTH,datePicker.getMonth());
                    tempCalendar.set(Calendar.YEAR,datePicker.getYear());
                    if (tempCalendar.getTime().getTime()<calendarStart.getTime().getTime()){
                        Toast.makeText(CreateEventActivity.this, "Ngày lặp lại không thể trước ngày bắt đầu!", Toast.LENGTH_SHORT).show();
                    }else {
                        calendarSettingEndDate = (Calendar) tempCalendar.clone();
                        txtRepeatUntil.setText(formatDate.format(calendarSettingEndDate.getTime()));
                    }
                }
                dialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addControls() {
        txtSave = findViewById(R.id.txtSave);
        txtCancel = findViewById(R.id.txtCancel);
        txtDateStart = findViewById(R.id.txtDateStart);
        txtTimeStart = findViewById(R.id.txtTimeStart);
        txtDateEnd = findViewById(R.id.txtDateEnd);
        txtTimeEnd = findViewById(R.id.txtTimeEnd);

        imgBack = findViewById(R.id.imgBack);
        imgTick = findViewById(R.id.imgTick);
        edtWorkName = findViewById(R.id.edtWorkName);
        edtDescription = findViewById(R.id.edtDescription);
        tilRepeat = findViewById(R.id.tilRepeat);
        tilNotification = findViewById(R.id.tilNotification);
        layoutRepeatUntil = findViewById(R.id.layoutRepeatUntil);
        switchNotification = findViewById(R.id.switchNotification);
        cardColor = findViewById(R.id.cardColor);
        imgVoice = findViewById(R.id.imgVoice);
        autotxtRepeat = findViewById(R.id.autotxtRepeat);
        autotxtNotification = findViewById(R.id.autotxtNotification);
        txtRepeatUntil = findViewById(R.id.txtRepeatUntil);

        layoutError = findViewById(R.id.layoutError);
        txtTitleActivity = findViewById(R.id.txtTitleActivity);
        myPlannerDatabase = new MyPlannerDatabase(getApplicationContext());
        mission = getIntent().getStringExtra("MISSION");

        if (mission.equals("repair")){
            txtTitleActivity.setText(getString(R.string.repair_work));
            eventReceive = (Event) getIntent().getSerializableExtra("EVENT");
            date = new Date(eventReceive.getDtStart());
            edtWorkName.setText(eventReceive.getTitle());
            edtDescription.setText(eventReceive.getDescription());

            String dayAndMonth = DateFormat.getDateInstance(DateFormat.LONG).format(date);
            String[] options = {getString(R.string.no_repeat),getString(R.string.every_day),getString(R.string.every_week)+"("+formatDayName.format(date)+")",
                    getString(R.string.every_month)+"("+getString(R.string.day)+" "+formatDay.format(date)+")",getString(R.string.every_year)+"("+dayAndMonth.substring(0,dayAndMonth.length()-6)+")"};

            Cursor cursor = myPlannerDatabase.getData("SELECT * FROM Events WHERE IdEvent = "+eventReceive.getIdEvent()+"");
            if (cursor!=null){
                cursor.moveToFirst();
                long timeStartEvent = cursor.getLong(3);
                long timeEndEvent = cursor.getLong(4);
                long dateEndORTimeRepeat = cursor.getLong(7);
                setTextStartAndEndRepair(timeStartEvent,timeEndEvent);
                if (dateEndORTimeRepeat==0){
                    txtRepeatUntil.setText(getString(R.string.no_end_date));
                }else if (0<dateEndORTimeRepeat && dateEndORTimeRepeat<1000){
                    txtRepeatUntil.setText(String.valueOf(dateEndORTimeRepeat)+" "+getString(R.string.times));
                    timeRepeat = Integer.parseInt(String.valueOf(dateEndORTimeRepeat));
                }else {
                    txtRepeatUntil.setText(formatDate.format(dateEndORTimeRepeat));
                    calendarSettingEndDate = Calendar.getInstance();
                    calendarSettingEndDate.setTime(new Date(dateEndORTimeRepeat));
                }
            }
            if (eventReceive.getNotification()!=0){
                switchNotification.setChecked(true);
                tilNotification.setVisibility(View.VISIBLE);
                switch (eventReceive.getNotification()){
                    case 1:
                        autotxtNotification.setText(getString(R.string.at_time));
                        break;
                    case 2:
                        autotxtNotification.setText(getString(R.string.before_5_minutes));
                        break;
                    case 3:
                        autotxtNotification.setText(getString(R.string.before_15_minutes));
                        break;
                    case 4:
                        autotxtNotification.setText(getString(R.string.before_30_minutes));
                        break;
                    default:
                        break;
                }
            }else {
                autotxtNotification.setText(getString(R.string.at_time));
            }

            if (eventReceive.getRepeat()==0){
                autotxtRepeat.setText(getString(R.string.no_repeat));
            }else {
                switch (eventReceive.getRepeat()){
                    case 1:
                        autotxtRepeat.setText(options[1]);
                        break;
                    case 2:
                        autotxtRepeat.setText(options[2]);
                        break;
                    case 3:
                        autotxtRepeat.setText(options[3]);
                        break;
                    case 4:
                        autotxtRepeat.setText(options[4]);
                        break;
                    default:
                        break;
                }
                layoutRepeatUntil.setVisibility(View.VISIBLE);
            }
        }else {
            txtTitleActivity.setText(getString(R.string.new_work));

            Date tempDate = (Date) getIntent().getSerializableExtra("DATE");
            Calendar mCalendar = Calendar.getInstance();
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY),minute = mCalendar.get(Calendar.MINUTE), second = mCalendar.get(Calendar.SECOND), millisecond = mCalendar.get(Calendar.MILLISECOND);

            mCalendar.setTime(tempDate);
            mCalendar.set(Calendar.HOUR_OF_DAY,hour);
            mCalendar.set(Calendar.MINUTE,minute);
            mCalendar.set(Calendar.SECOND,0);
            mCalendar.set(Calendar.MILLISECOND,2);
            date = mCalendar.getTime();
            autotxtRepeat.setText(getString(R.string.no_repeat));
            autotxtNotification.setText(getString(R.string.at_time));
            setTextStartAndEnd(date);
        }

        setAdapterAutotxtRepeat(date);
        String[] options = {getString(R.string.at_time),getString(R.string.before_5_minutes),getString(R.string.before_15_minutes),getString(R.string.before_30_minutes)};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.item_options,options);
        autotxtNotification.setAdapter(arrayAdapter);
    }

    private void setTextStartAndEndRepair(long timeStartEvent, long timeEndEvent) {
        calendarStart = Calendar.getInstance();
        calendarStart.setTime(new Date(timeStartEvent));
        String strTimeStart = formatTime.format(calendarStart.getTime());
        String strDateStart = formatDate.format(calendarStart.getTime());
        txtDateStart.setText(strDateStart);
        txtTimeStart.setText(strTimeStart);
        dateStart = calendarStart.getTime();

        calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(new Date(timeEndEvent));
        String strTimeEnd = formatTime.format(calendarEnd.getTime());
        String strDateEnd = formatDate.format(calendarEnd.getTime());
        txtDateEnd.setText(strDateEnd);
        txtTimeEnd.setText(strTimeEnd);
        dateEnd = calendarEnd.getTime();
    }

    private void setAdapterAutotxtRepeat(Date date) {
        String dayAndMonth = DateFormat.getDateInstance(DateFormat.LONG).format(date);
        String[] options = {getString(R.string.no_repeat),getString(R.string.every_day),getString(R.string.every_week)+"("+formatDayName.format(date)+")",
                getString(R.string.every_month)+"("+getString(R.string.day)+" "+formatDay.format(date)+")",getString(R.string.every_year)+"("+dayAndMonth.substring(0,dayAndMonth.length()-6)+")"};
        arrayAdapterRepeat = new ArrayAdapter(this,R.layout.item_options,options);

        String option = autotxtRepeat.getText().toString();
        if (option.contains(getString(R.string.no_repeat))){
            autotxtRepeat.setText(options[0]);
        }else if (option.contains(getString(R.string.every_day))){
            autotxtRepeat.setText(options[1]);
        }else if (option.contains(getString(R.string.every_week))){
            autotxtRepeat.setText(options[2]);
        }else if (option.contains(getString(R.string.every_month))){
            autotxtRepeat.setText(options[3]);
        }else if (option.contains(getString(R.string.every_year))){
            autotxtRepeat.setText(options[4]);
        }

        autotxtRepeat.setAdapter(arrayAdapterRepeat);
    }

    private void setTextStartAndEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 2);

        int modulo = calendar.get(Calendar.MINUTE) % 30;
        if(modulo > 0) {

            calendar.add(Calendar.MINUTE, -modulo);
        }

        calendar.add(Calendar.MINUTE, 30);
        String strTimeStart = formatTime.format(calendar.getTime());
        String strDateStart = formatDate.format(calendar.getTime());
        txtTimeStart.setText(strTimeStart);
        txtDateStart.setText(strDateStart);
        dateStart = calendar.getTime();
        calendarStart = (Calendar) calendar.clone();

        calendar.add(Calendar.MINUTE,30);
        String strTimeEnd = formatTime.format(calendar.getTime());
        String strDateEnd = formatDate.format(calendar.getTime());
        txtTimeEnd.setText(strTimeEnd);
        txtDateEnd.setText(strDateEnd);
        dateEnd = calendar.getTime();
        calendarEnd = (Calendar) calendar.clone();
    }

    private void setTextDateAndTime() {
        
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something…");
        try {
            activityResultLauncher.launch(intent);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void clickHideKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}