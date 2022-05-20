package com.example.myplanner.model;

public class Work {
    long idWork;
    String title;
    String description;
    long timeStart;
    long timeEnd;
    int notification;
    int repeat;
    long dateEndOrTimeRepeat;
    int done;
    String type;

    public Work() {
    }

    public Work(long idWork, String title, String description, long timeStart, long timeEnd, int notification, int repeat, long dateEndOrTimeRepeat, int done, String type) {
        this.idWork = idWork;
        this.title = title;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.notification = notification;
        this.repeat = repeat;
        this.dateEndOrTimeRepeat = dateEndOrTimeRepeat;
        this.done = done;
        this.type = type;
    }

    public long getIdWork() {
        return idWork;
    }

    public void setIdWork(long idWork) {
        this.idWork = idWork;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
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

    public long getDateEndOrTimeRepeat() {
        return dateEndOrTimeRepeat;
    }

    public void setDateEndOrTimeRepeat(long dateEndOrTimeRepeat) {
        this.dateEndOrTimeRepeat = dateEndOrTimeRepeat;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
