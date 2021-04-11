package com.example.coen390_groupproject_bearcare.Model;

import android.widget.Switch;

public class Questionnaire {


    private String  zero;
    private String one;
    private String two;
    private String three;
    private String four;
    private String five;
    private String six;
    private String seven;
    private String eight;
    private String nine;
    private String ten;
    private String eleven;
    private String twelve;
    private String thirteen;
    private String fourteen;
    private String fifteen;
    private String sixteen;
    private String Date;
    private int day;

    private static final String[] questions = {
            "Cough (new or aggravated)",
            "Fever (rectal temperature of 38.5°C or higher",
            "Sudden loss of smell (without nasal congestion)",
            "Difficulty Breathing",
            "Muscle pain (generalised - not related to physical effort)",
            "Intense fatigue",
            "Major loss of appetite",
            "Sore throat",
            "Runny nose",
            "Vomiting or Diarrhea",
            "Does any of the household members have any of the symptoms listed above?",
            "Have you given your child any medication in the last 24 hours to reduce fevers?",
            "Has your child received a positive COVID-19 diagnosis?",
            "Is your child waiting for the results of a COVID-19 test?",
            "Did you, or someone living with the child receive a positive COVID-19 test?",
            "Are you, or someone living with the child waiting for the results of a COVID-19 test?",
            "Did the child, or someone living with the child travel outside of Canada in the last 14 days?"
    };


    public static String getQuestion(int number) {
        return questions[number];
    }

    public String getQuestions(String number){
        switch(number){

            case "zero":
                return questions[0];
            case "one":
                return questions[1];
            case "two":
                return questions[2];
            case "three":
                return questions[3];
            case "four":
                return questions[4];
            case "five":
                return questions[5];
            case "six":
                return questions[6];
            case "seven":
                return questions[7];
            case "eight":
                return questions[8];
            case "nine":
                return questions[9];
            case "ten":
                return questions[10];
            case "eleven":
                return questions[11];
            case "twelve":
                return questions[12];
            case "thirteen":
                return questions[13];
            case "fourteen":
                return questions[14];
            case "fifteen":
                return questions[15];
            case "sixteen":
                return questions[16];
            default:
                return "Error retrieving question";
        }
    }

    //Getters and Setters

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getZero() {
        return zero;
    }

    public void setZero(String zero) {
        this.zero = zero;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public String getThree() {
        return three;
    }

    public void setThree(String three) {
        this.three = three;
    }

    public String getFour() {
        return four;
    }

    public void setFour(String four) {
        this.four = four;
    }

    public String getFive() {
        return five;
    }

    public void setFive(String five) {
        this.five = five;
    }

    public String getSix() {
        return six;
    }

    public void setSix(String six) {
        this.six = six;
    }

    public String getSeven() {
        return seven;
    }

    public void setSeven(String seven) {
        this.seven = seven;
    }

    public String getEight() {
        return eight;
    }

    public void setEight(String eight) {
        this.eight = eight;
    }

    public String getNine() {
        return nine;
    }

    public void setNine(String nine) {
        this.nine = nine;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEleven() {
        return eleven;
    }

    public void setEleven(String eleven) {
        this.eleven = eleven;
    }

    public String getTwelve() {
        return twelve;
    }

    public void setTwelve(String twelve) {
        this.twelve = twelve;
    }

    public String getThirteen() {
        return thirteen;
    }

    public void setThirteen(String thirteen) {
        this.thirteen = thirteen;
    }

    public String getFourteen() {
        return fourteen;
    }

    public void setFourteen(String fourteen) {
        this.fourteen = fourteen;
    }

    public String getFifteen() {
        return fifteen;
    }

    public void setFifteen(String fifteen) {
        this.fifteen = fifteen;
    }

    public String getSixteen() {
        return sixteen;
    }

    public void setSixteen(String sixteen) {
        this.sixteen = sixteen;
    }
}
