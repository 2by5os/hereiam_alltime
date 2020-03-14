package com.geekwims.hereiam.db;

import android.provider.BaseColumns;
import android.util.Log;

import java.util.Date;

public class BatteryLog {
    private static final String TAG = "BatteryLog";

    private int beginTime = 0;
    private int endTime = 0;
    private int beginLevel = 0;
    private int endLevel = 0;


    public BatteryLog() {
    }

    public BatteryLog(int beginTime, int endTime, int beginLevel, int endLevel) {
        Log.d(TAG, beginTime + " " + endTime + " " + beginLevel + " " + endLevel);

        this.beginTime = beginTime;
        this.endTime = endTime;
        this.beginLevel = beginLevel;
        this.endLevel = endLevel;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getBeginLevel() {
        return beginLevel;
    }

    public void setBeginLevel(int beginLevel) {
        this.beginLevel = beginLevel;
    }

    public int getEndLevel() {
        return endLevel;
    }

    public void setEndLevel(int endLevel) {
        this.endLevel = endLevel;
    }
}
