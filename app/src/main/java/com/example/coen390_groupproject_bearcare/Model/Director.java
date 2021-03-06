package com.example.coen390_groupproject_bearcare.Model;

public class Director {

    private long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;

    public Director(long id, String fullName, String email, String phoneNumber, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

}
