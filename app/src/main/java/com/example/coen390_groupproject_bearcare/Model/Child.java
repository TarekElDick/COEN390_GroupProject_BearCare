package com.example.coen390_groupproject_bearcare.Model;

public class Child {

    private long id;
    private long parentId;
    private long employeeId;
    private String fullName;
    private Date birthday;

    public Child(long id, long parentId, long employeeId, String fullName, Date birthday) {
        this.id = id;
        this.parentId = parentId;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Child{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", employeeId=" + employeeId +
                ", fullName='" + fullName + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
