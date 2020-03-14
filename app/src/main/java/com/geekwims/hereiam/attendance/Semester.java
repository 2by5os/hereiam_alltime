package com.geekwims.hereiam.attendance;

import java.sql.Timestamp;

/**
 *
 */
public class Semester {
    private int id;
    private String year;
    private String semester;
    private Timestamp beginAt;
    private Timestamp endAt;

    public Semester() {
    }

    public Semester(int id, String year, String semester, Timestamp beginAt, Timestamp endAt) {
        this.id = id;
        this.year = year;
        this.semester = semester;
        this.beginAt = beginAt;
        this.endAt = endAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Timestamp getBeginAt() {
        return beginAt;
    }

    public void setBeginAt(Timestamp beginAt) {
        this.beginAt = beginAt;
    }

    public Timestamp getEndAt() {
        return endAt;
    }

    public void setEndAt(Timestamp endAt) {
        this.endAt = endAt;
    }
}
