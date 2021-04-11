package com.example.coen390_groupproject_bearcare.Model;

public class Attendance {

    private Date date;
    private boolean currentAttendance;

    public Attendance(){}
    public Attendance(Date date, boolean currentAttendance) {
        this.date = date;
        this.currentAttendance = currentAttendance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCurrentAttendance() {
        return currentAttendance;
    }

    public void setCurrentAttendance(boolean currentAttendance) {
        this.currentAttendance = currentAttendance;
    }
}
