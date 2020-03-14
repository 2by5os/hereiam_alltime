package com.geekwims.hereiam.autocheck;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.receiver.AlarmReceiver;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.response.course.NextCourseInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Calendar;

import static com.geekwims.hereiam.receiver.AlarmReceiver.END_TIME;
import static com.geekwims.hereiam.receiver.AlarmReceiver.KEY_COUNT;
import static com.geekwims.hereiam.receiver.AlarmReceiver.PREFS_NAME;
import static com.geekwims.hereiam.receiver.AlarmReceiver.TOTAL_COUNT;

public class AutoCheckManager {
    private static final String TAG = "AutoCheckManager";

    private Activity mActivity;
    private AlarmManager _am;

    public AutoCheckManager(Activity activity) {
        mActivity = activity;
        _am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
    }

    public void onRegist(NextCourseInfo nextCourseInfo)
    {
        Log.i("MainActivity | onRegist", "|" + "========= regist" + "|");

        Intent intent = new Intent("com.geekwims.hereiam.receiver");
        Log.i("MainActivity | onRegist", nextCourseInfo.toString());

        CourseInfo courseInfo = nextCourseInfo.getCourseInfo();

//        intent.putExtra(AlarmReceiver.INTENT_KEY, (Serializable) courseInfo);
        intent.putExtra("getCourseId", courseInfo.getCourseId());
        intent.putExtra("getCourseName", courseInfo.getCourseName());
        intent.putExtra("getDay", courseInfo.getDay());
        intent.putExtra("getDeviceAddr", courseInfo.getDeviceAddr());
        intent.putExtra("getEndTime", courseInfo.getEndTime());
        intent.putExtra("getProfessor", courseInfo.getProfessor());
        intent.putExtra("getRoom", courseInfo.getRoom());
        intent.putExtra("getTime", courseInfo.getTime());

        if (intent.hasExtra(AlarmReceiver.INTENT_KEY)) {
            Log.i(TAG, "************ extra is set *****************");
        } else {
            Log.i(TAG, "----------------  not set...");
        }
        int totalCount = (nextCourseInfo.getCourseInfo().getEndTime() - nextCourseInfo.getCourseInfo().getTime()) * 2;

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_WEEK, courseInfo.getDay() + 1); // 요일 설정
        cal.set(Calendar.HOUR_OF_DAY, courseInfo.getTime()); // 시간 설정
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        PendingIntent pIntent = PendingIntent.getBroadcast(mActivity.getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i("MainActivity | onRegist", "|" + "set alarm at " + cal.getTimeInMillis() + "|");

        long interval =  30 * 1000;// 출석 체크 간격

        // 출결 카운팅 초기화 후 진행
        SharedPreferences values = mActivity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = values.edit();
        editor.putInt(KEY_COUNT, 0);
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.DAY_OF_WEEK, courseInfo.getDay() + 1);
        endTime.set(Calendar.HOUR_OF_DAY, courseInfo.getEndTime());
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);
        editor.putLong(END_TIME, endTime.getTimeInMillis());
        editor.putInt(TOTAL_COUNT, totalCount);
        editor.apply();


        Log.i("MainActivity | onRegist", "|" + "repeating...." + "|");
        _am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, pIntent);
    }

    public void onUnregist()
    {
        Log.i("MainActivity|onUnregist", "|" + "========= unregist" + "|");
        Intent intent = new Intent(mActivity.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(mActivity.getApplicationContext(), 1, intent, 0);

        _am.cancel(pIntent);
    }

}
