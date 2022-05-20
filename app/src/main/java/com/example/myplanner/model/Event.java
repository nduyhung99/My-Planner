package com.example.myplanner.model;

import java.io.Serializable;

public class Event implements Serializable {
    String idEvent;
    String title;
    long dtStart;
    long dtEnd;
    String description;
    String type;
    boolean done;
    String calendarDisplayName;
    int notification;
    int repeat;
    int colorEvent;
    int visibleDate;
//    String[] attachments;

    public Event() {
    }

    public Event(String idEvent, String title, long dtStart, long dtEnd, String description, String type, boolean done, String calendarDisplayName, int notification, int repeat, int colorEvent, int visibleDate) {
        this.idEvent = idEvent;
        this.title = title;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
        this.description = description;
        this.type = type;
        this.done = done;
        this.calendarDisplayName = calendarDisplayName;
        this.notification = notification;
        this.repeat = repeat;
        this.colorEvent = colorEvent;
        this.visibleDate = visibleDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDtStart() {
        return dtStart;
    }

    public void setDtStart(long dtStart) {
        this.dtStart = dtStart;
    }

    public long getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(long dtEnd) {
        this.dtEnd = dtEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getCalendarDisplayName() {
        return calendarDisplayName;
    }

    public void setCalendarDisplayName(String calendarDisplayName) {
        this.calendarDisplayName = calendarDisplayName;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getVisibleDate() {
        return visibleDate;
    }

    public void setVisibleDate(int visibleDate) {
        this.visibleDate = visibleDate;
    }

    public int getColorEvent() {
        return colorEvent;
    }

    public void setColorEvent(int colorEvent) {
        this.colorEvent = colorEvent;
    }
}
