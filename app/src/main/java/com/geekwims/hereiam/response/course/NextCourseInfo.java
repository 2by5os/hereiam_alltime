package com.geekwims.hereiam.response.course;

import java.io.Serializable;


public class NextCourseInfo implements Serializable {
    private CourseInfo courseInfo = null;
    private STATUS status = STATUS.NONE;

    public enum STATUS {
        NONE("NONE"), CUR("CUR"), NEXT("NEXT");

        STATUS(String status) {
        }
    }

    public NextCourseInfo() {

    }

    public STATUS getStatus() {
        return status;
    }

    public CourseInfo getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(CourseInfo courseInfo) {
        this.courseInfo = courseInfo;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NextCourseInfo{" +
                "courseInfo=" + courseInfo +
                ", status=" + status +
                '}';
    }
}