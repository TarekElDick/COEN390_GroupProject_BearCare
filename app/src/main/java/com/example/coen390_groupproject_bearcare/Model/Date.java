package com.example.coen390_groupproject_bearcare.Model;

public class Date {

    // We might want to use Date.util java class which already has all these functions and more !.

    private int day;
    private int month;
    private int year;

    public Date(){}
    public Date(int day, int month, int year) {

        if( day <= 31 && day >= 1) {
            this.day = day;
        } else {
            this.day = -1;
        }
        if(month <= 12 && month >= 1) {
            this.month = month;
        }else{
            this.day = -1;

        }
        if(year <= 2021 && year >= 1900){
            this.year = year;
        }else{
            this.year = -1;
        }

    }

    @Override
    public String toString() {
        return "Day=" + day +
                ", Month=" + month +
                ", Year=" + year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
