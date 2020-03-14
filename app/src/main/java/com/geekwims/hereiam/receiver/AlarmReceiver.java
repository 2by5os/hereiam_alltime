package com.geekwims.hereiam.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.activity.MainActivity;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.response.course.NextCourseInfo;
import com.geekwims.hereiam.util.AttendBluetoothManager;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "AlarmReceiver";

    public static final String PREFS_NAME = "com.examples.hereiam.PREFS";
    public static final String KEY_COUNT = "ATTEND_CHECK_COUNT";
    public static final String END_TIME = "END_TIME";
    public static final String TOTAL_COUNT = "TOTAL_TIME";

    public static final String INTENT_KEY = "ALARM_INTENT_EXTRA";

    Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "----- onRecevice -------");

         mContext = context;
        SharedPreferences values = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);

        Log.i(TAG, "--------- Intent Action : " + intent.getAction() + " ---------------- ");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Log.d(TAG, "*********** start listing... **************");
            String valueText = "";
            String valueName = "";
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);

                valueText = value == null ? "null" : value.toString();
                valueName = value == null ? "no named" : value.getClass().getName();

                Log.d(TAG, String.format("%s %s (%s)", key,
                        valueText, valueName));
            }
        } else {
            Log.e(TAG, "bundle is null");
        }

        if (!intent.hasExtra(INTENT_KEY)) {
            Log.i(TAG, "----- Intent are not set -------");
        }
        
        int courseId = intent.getIntExtra("getCourseId", 0);
        String courseName = intent.getStringExtra("getCourseName");
        int day = intent.getIntExtra("getDay", 0);
        String deviceAddr = intent.getStringExtra("getDeviceAddr");
        int endTime = intent.getIntExtra("getEndTime", 0);
        String room = intent.getStringExtra("getRoom");
        int time = intent.getIntExtra("getTime", 0);

        CourseInfo courseInfo = new CourseInfo(courseId, courseName, room, day, time, endTime, deviceAddr, null);

        Log.i(TAG, "----- bluetooth connection -------");

        AttendBluetoothManager attendBluetoothManager = new AttendBluetoothManager(context, courseInfo);

//        attendBluetoothManager.connect("00:11:22:33:AA:BB", false);
        attendBluetoothManager.connect(courseInfo.getDeviceAddr(), false);

    }

}
