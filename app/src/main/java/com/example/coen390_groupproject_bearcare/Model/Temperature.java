package com.example.coen390_groupproject_bearcare.Model;

public class Temperature {

    public double temp;
    public String name;
    public String date;

    public Temperature(){}
    public Temperature(String name, double temp, String date) {

        this.name = name;
        this.temp = temp;
        this.date = date;

    }

    @Override
    public String toString() {
        return "Temperature{" +
                "name=" + name +
                ", temperature=" + temp +
                ", date='" + date +
                '}';
    }
}
