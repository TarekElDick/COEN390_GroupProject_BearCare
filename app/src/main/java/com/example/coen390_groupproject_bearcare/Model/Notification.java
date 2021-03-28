package com.example.coen390_groupproject_bearcare.Model;

public class Notification {

    private String parentId;
    private String employeeId;
    private String childId;
    private String childName;
    private String notificationTitle;
    private String NotificationDescription;
    private Date date;
    private Time time;

    public Notification(){}
    public Notification(String parentId, String employeeId, String childId, String childName, String notificationTitle, String notificationDescription, Date date, Time time) {
        this.parentId = parentId;
        this.employeeId = employeeId;
        this.childId = childId;
        this.childName = childName;
        this.notificationTitle = notificationTitle;
        NotificationDescription = notificationDescription;
        this.date = date;
        this.time = time;
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

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return NotificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        NotificationDescription = notificationDescription;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
