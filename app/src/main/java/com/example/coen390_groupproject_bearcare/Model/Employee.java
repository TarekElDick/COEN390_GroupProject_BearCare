package com.example.coen390_groupproject_bearcare.Model;

public class Employee {

    private long id;
    private long directorId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;

    public Employee(long id,long directorId, String fullName, String email, String phoneNumber, String password) {
        this.id = id;
        this.directorId = directorId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", directorId=" + directorId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getDirectorId() { return directorId;  }

    public void setDirectorId(long directorId) { this.directorId = directorId; }
}
