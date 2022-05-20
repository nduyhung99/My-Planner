package com.example.myplanner.model;

import java.io.Serializable;
import java.util.Date;

public class DateOfCalendar implements Serializable {
    Date date;
    String plan;

    public DateOfCalendar() {
    }

    public DateOfCalendar(Date date, String plan) {
        this.date = date;
        this.plan = plan;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

}
