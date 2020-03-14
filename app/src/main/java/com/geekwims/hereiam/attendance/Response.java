package com.geekwims.hereiam.attendance;

/**
 * response for attendance
 */

public class Response {
    private AttendStatus attendStatus = AttendStatus.FAIL;

    public Response() {
    }

    public Response(AttendStatus fail) {

    }

    public AttendStatus getAttendStatus() {
        return attendStatus;
    }

    public void setAttendStatus(AttendStatus attendStatus) {
        this.attendStatus = attendStatus;
    }

    public enum AttendStatus {
        ATTENDANCE, LATENESS, ABSENCE, FAIL, ALREADY
    }

    @Override
    public String toString() {
        return "Response{" +
                "attendStatus=" + attendStatus +
                '}';
    }
}
