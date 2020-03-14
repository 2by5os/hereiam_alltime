package com.geekwims.hereiam.attendance;


import com.geekwims.hereiam.constant.AttendStatus;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendInfo {
    private int id;
    private AttendStatus status;
    private Timestamp attendAt;
    private int week;
    private String strDate;
    private String strTime;

    public AttendInfo() {
    }

    public AttendInfo(AttendStatus status, long attendAt, int week) {
        this.id = 0;
        this.status = status;
        this.attendAt = new Timestamp(attendAt);
        this.week = week;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AttendStatus getStatus() {
        return status;
    }

    public void setStatus(AttendStatus status) {
        this.status = status;
    }

    public Timestamp getAttendAt() {
        return attendAt;
    }

    public void setAttendAt(Timestamp attendAt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

        long ms = attendAt.getTime();

        this.strDate = dateFormat.format(new Date(ms));
        this.strTime = timeFormat.format(new Date(ms));
        this.attendAt = attendAt;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getStrDate() {
        return strDate;
    }

    public String getStrTime() {
        return strTime;
    }

    @Override
    public String toString() {
        return "AttendInfo{" +
                "id=" + id +
                ", status=" + status +
                ", attendAt=" + attendAt +
                ", week=" + week +
                '}';
    }
}
