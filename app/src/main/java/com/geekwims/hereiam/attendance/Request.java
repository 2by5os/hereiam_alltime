package com.geekwims.hereiam.attendance;

import com.geekwims.hereiam.response.course.CourseInfo;

/**
 * request for attendance to device(RPi)
 */

public class Request {
    private String studentNum;
    private CourseInfo courseInfo;

    public Request(String studentNum, CourseInfo courseInfo) {
        this.studentNum = studentNum;
        this.courseInfo = courseInfo;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public CourseInfo getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(CourseInfo courseInfo) {
        this.courseInfo = courseInfo;
    }
}
