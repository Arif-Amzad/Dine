
package com.arifamzad.dine.patternManagement;

public class MyBorder {
    String name, phone, uid;
    String days_on, paid, status;
    private boolean expanded;

    public MyBorder(String name, String phone, String uid, String days_on, String paid, String status){
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.days_on = days_on;
        this.paid = paid;
        this.status = status;
    }

    public MyBorder(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDays_on() {
        return days_on;
    }

    public void setDays_on(String days_on) {
        this.days_on = days_on;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

}
