package com.example.coen390_groupproject_bearcare.Model;

public class Time {
    private int hour;
    private int minute;

    public Time(){}
    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        // Formatting needed: Cause we wanna show pm/am but we only get 24 hour time.
        int formattedHour;
        String phase;
        String extraZero = "";
        if(hour == 0) {
            formattedHour = 12;
            phase = "AM";
        } else if(hour > 12){
            formattedHour = hour-12;
            phase = "PM";
        } else if(hour == 12){
            formattedHour = hour;
            phase = "PM";
        } else {
            formattedHour = hour;
            phase = "AM";
        }
        if(minute < 10){
            extraZero = "0";
        }
        return formattedHour +":"+ extraZero + minute + phase;
    }
}
