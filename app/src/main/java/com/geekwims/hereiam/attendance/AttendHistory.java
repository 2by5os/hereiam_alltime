package com.geekwims.hereiam.attendance;


import java.util.ArrayList;
import java.util.List;

public class AttendHistory {
    private List<AttendInfo> attendInfos = new ArrayList<>();
    private Semester semester;

    public List<AttendInfo> getAttendInfos() {
        return attendInfos;
    }

    public void setAttendInfos(List<AttendInfo> attendInfos) {
        this.attendInfos = attendInfos;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "AttendHistory{" +
                "attendInfos=" + attendInfos +
                ", semester=" + semester +
                '}';
    }
}
