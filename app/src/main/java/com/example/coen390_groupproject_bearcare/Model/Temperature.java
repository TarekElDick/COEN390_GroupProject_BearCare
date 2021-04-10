package com.example.coen390_groupproject_bearcare.Model;

import android.util.Log;

import java.util.Date;

public class Temperature {

    private double temp;
    private long day;
    private long month;
    private String date;

    public Temperature(){}

    public Temperature(double temp, String date) {

        this.date = date;
        this.temp = temp;
        this.day =  new Date().getDate();
        this.month = new Date().getMonth()+1;

    }

    @Override
    public String toString() {
        return "Temperature{" +
                ", temperature=" + temp +
                ", date='" + date +
                ", day='" + day +
                ", day='" + month +
                '}';
    }


    public void print(){
        Log.d("Temperature Object", "temp: "+ temp +" , date: "+ date);
    }

    public double getTemp() {
        return temp;
    }

    public String getDate() {
        return date;
    }

    public long getDay() {
        return day;
    }

    public long getMonth() {
        return month;
    }


}
