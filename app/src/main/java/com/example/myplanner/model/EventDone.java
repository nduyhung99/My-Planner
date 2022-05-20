package com.example.myplanner.model;

public class EventDone {
    long idEvent;
    String dateCompleted;
    long timeStart;

    public EventDone(long idEvent, String dateCompleted, long timeStart){
        this.idEvent = idEvent;
        this.dateCompleted = dateCompleted;
        this.timeStart = timeStart;
    }

    public EventDone(){
    }

    public long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(long idEvent) {
        this.idEvent = idEvent;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }
}
