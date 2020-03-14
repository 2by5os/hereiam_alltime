package com.geekwims.hereiam.response.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by suyoungkim on 2016. 11. 15..
 *
 * the response of timetable
 */

public class ClassSchedule {
    public final static int MON = 0;
    public final static int TUE = 1;
    public final static int WED = 2;
    public final static int THU = 3;
    public final static int FRI = 4;
    public final static int SAT = 5;
    public final static int SUN = 6;

    public String[][] schedule = new String[7][24];

    public String[][] getSchedule() {
        return schedule;
    }

    public void setSchedule(int day, int time, String value) {
        schedule[day][time] = value;
    }

    public static int convertToDay(String strDay) {
        int result = -1;

        switch (strDay) {
            case "MON" :
                result = MON;
                break;
            case "TUE" :
                result = TUE;
                break;
            case "WED" :
                result = WED;
                break;
            case "THU" :
                result = THU;
                break;
            case "FRI" :
                result = FRI;
                break;
            case "SAT" :
                result = SAT;
                break;
            case "SUN" :
                result = SUN;
                break;
        }

        return result;
    }

    @Override
    public String toString() {
        return "ClassSchedule{" +
                "schedule=" + Arrays.toString(schedule) +
                '}';
    }

}
