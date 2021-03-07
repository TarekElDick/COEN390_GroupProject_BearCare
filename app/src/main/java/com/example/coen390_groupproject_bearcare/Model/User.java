package com.example.coen390_groupproject_bearcare.Model;

public class User {

    public String fullName, emailAddress, phoneNumber;
    boolean isEmployee;

    public User(){}

    public User(String fullName, String emailAddress, String phoneNumber, boolean isEmployee){
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.isEmployee = isEmployee;
    }

}
