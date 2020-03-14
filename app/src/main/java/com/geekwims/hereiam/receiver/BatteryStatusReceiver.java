package com.geekwims.hereiam.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.geekwims.hereiam.db.DatabaseHelper;

public class BatteryStatusReceiver extends BroadcastReceiver {

    private static final String TAG = BatteryStatusReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) : " + intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));

        // 현재 충전 중인지 확인
        if (0 >= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
            Log.d(TAG, "intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) : " + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
            int intBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);


            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            if (databaseHelper != null)
                databaseHelper.insert(System.currentTimeMillis(), System.currentTimeMillis(), intBatteryLevel, intBatteryLevel);
            context.unregisterReceiver(this);
        }
    }
}