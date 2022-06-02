package com.example.myplanner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplanner.adapter.EventFromGmailAdapter;
import com.example.myplanner.database.MyPlannerDatabase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SettingActivity extends AppCompatActivity {
    public static final int PERMS_REQUEST_CODE = 1, RESULT_SETTINGS = 123;
    public static final String KEY_EMAIL_OFF = "KEY_EMAIL_OFF";
    private LinearLayout layoutNotificationSettings, layoutSelectLanguage;
    private TextView txtEventFromGmail;
    private RelativeLayout layoutSettingsEventFromGmail;
    private ImageView imgPrevious, imgBack;
    private RecyclerView rcvSettingsEventFromGmail;
    Context context;
    Resources resources;
    String language = "";
    EventFromGmailAdapter adapter;
    List<String> listAccount = new ArrayList<>();
    List<String> listShowOffGmail = new ArrayList<>();
    List<String> listShowOffGmailChange = new ArrayList<>();
    MyPlannerDatabase myPlannerDatabase;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.orange1), true, false, SettingActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
        language = sharedPreferences.getString("language","");
        if (!language.equals("")){
            MainActivity.setLanguage(this,language);
        }
        context = LocaleHelper.setLocale(SettingActivity.this, "en");
        resources = context.getResources();

        setContentView(R.layout.activity_setting);
        layoutSelectLanguage = findViewById(R.id.layoutSelectLanguage);
        layoutNotificationSettings = findViewById(R.id.layoutNotificationSettings);
        txtEventFromGmail = findViewById(R.id.txtEventsFromGmail);
        layoutSettingsEventFromGmail = findViewById(R.id.layoutSettingsEventFromGmail);
        imgPrevious = findViewById(R.id.imgPrevious);
        rcvSettingsEventFromGmail = findViewById(R.id.rcvSettingsEventFromGmail);
        imgBack = findViewById(R.id.imgBack);

        myPlannerDatabase = new MyPlannerDatabase(this);

        adapter = new EventFromGmailAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rcvSettingsEventFromGmail.setLayoutManager(linearLayoutManager);
        listAccount = getAllAccountGoogle();
        listShowOffGmail = getAllAccountShowOff();
        listShowOffGmailChange = getAllAccountShowOff();
        adapter.setData(listAccount,listShowOffGmail);
        rcvSettingsEventFromGmail.setAdapter(adapter);

        adapter.setiOnSwitchChange(new EventFromGmailAdapter.IOnSwitchChange() {
            @Override
            public void onChange(String account, boolean checked) {
                SharedPreferences sharedPreferences1 = getSharedPreferences(KEY_EMAIL_OFF,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences1.edit();
                if (!checked){
                    listShowOffGmailChange.add(account);
                    String strSave = getStringFromList(listShowOffGmailChange);
                    editor.putString("mail_off",strSave);
                    editor.apply();
                }else {
                    listShowOffGmailChange.remove(account);
                    String strSave = getStringFromList(listShowOffGmailChange);
                    editor.putString("mail_off",strSave);
                    editor.apply();
                }
            }
        });

        imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutSettingsEventFromGmail.setVisibility(View.GONE);
            }
        });

        txtEventFromGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutSettingsEventFromGmail.setVisibility(View.VISIBLE);
            }
        });

        layoutSelectLanguage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showLanguageDialog();
            }
        });

        layoutNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                }
                startActivity(intent);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private String getStringFromList(List<String> list) {
        String delim = "/";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < list.size() - 1)
        {
            sb.append(list.get(i));
            sb.append(delim);
            i++;
        }
        sb.append(list.get(i));
        return sb.toString();
    }

    @NonNull
    private List<String> getAllAccountShowOff() {
        List<String> list;
        SharedPreferences sharedPreferences = getSharedPreferences(KEY_EMAIL_OFF,MODE_PRIVATE);
        String emails = sharedPreferences.getString("mail_off","");
        list = new ArrayList<>(Arrays.asList(emails.split("/")));
        return list;
    }

    private List<String> getAllAccountGoogle() {
        List<String> list = new ArrayList<>();
//        AccountManager manager = AccountManager.get(context);
//        Account[] accounts = manager.getAccounts();
//
//        for (Account account : accounts) {
//            list.add(account.name);
//        }

//        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
//        Account[] accounts = AccountManager.get(this).getAccounts();
//        for (Account account : accounts) {
//            if (gmailPattern.matcher(account.name).matches()) {
//                list.add(account.name);
//            }
//        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = {CalendarContract.Events.ACCOUNT_NAME};

        Cursor cursor = contentResolver.query(uri,projection,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(projection[0]));
                if (name.contains("@")){
                    if (!list.contains(name)){
                        list.add(name);
                    }
                }
            }
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showLanguageDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_language_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        RadioButton radVietnamese = dialog.findViewById(R.id.radVietnamese);
        RadioButton radEnglish = dialog.findViewById(R.id.radEnglish);
        TextView txtClose = dialog.findViewById(R.id.txtClose);


        LocaleListCompat lol = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        String localeLanguage = lol.get(0).toString();
        SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
        String language = sharedPreferences.getString("language","");
        if (language.equals("")){
            if (localeLanguage.contains("en")){
                radEnglish.setChecked(true);
            }else if (localeLanguage.contains("vi")){
                radVietnamese.setChecked(true);
            }else {
                radEnglish.setChecked(true);
            }
        }else {
            if (language.equals("en")){
                radEnglish.setChecked(true);
            }else {
                radVietnamese.setChecked(true);
            }
        }

        radVietnamese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("language","vi");
                    editor.commit();
                    radEnglish.setChecked(false);

                    Intent refresh = new Intent(SettingActivity.this, MainActivity.class);
                    finish();
                    startActivity(refresh);
                }
            }
        });

        radEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    SharedPreferences sharedPreferences = getSharedPreferences("KEY_LANGUAGE",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("language","en");
                    editor.commit();
                    radVietnamese.setChecked(false);

                    Intent refresh = new Intent(SettingActivity.this, MainActivity.class);
                    finish();
                    startActivity(refresh);
                }
            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (layoutSettingsEventFromGmail.getVisibility()==View.VISIBLE){
            layoutSettingsEventFromGmail.setVisibility(View.GONE);
        }else {
            Intent resultIntent = new Intent();
            int temp = 0;
            if (listShowOffGmail.size()!=listShowOffGmailChange.size()){
                temp=1;
            }else {
                for (String string : listShowOffGmailChange){
                    if (!listShowOffGmail.contains(string)){
                        temp=1;
                    }
                }
            }
            if (temp==1){
                setResult(RESULT_SETTINGS,resultIntent);
                finish();
            }else {
                setResult(RESULT_CANCELED,resultIntent);
                finish();
            }

//            super.onBackPressed();
        }
    }
}