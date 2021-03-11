package com.example.coen390_groupproject_bearcare.Model;

public class Child {

    private String parentId;
    private String employeeId;
    private String firstName;
    private String lastName;
    private Date birthday;

    public Child(){}
    public Child( String parentId, String employeeId, String firstName, String lastName, Date birthday) {

        this.parentId = parentId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Child{" +
                "parentId=" + parentId +
                ", employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {  return firstName;  }

    public void setFirstName(String firstName) {  this.firstName = firstName;  }

    public String getLastName() {  return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
