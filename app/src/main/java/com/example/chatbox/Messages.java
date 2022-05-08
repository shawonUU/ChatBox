package com.example.chatbox;

public class Messages {
    String from,message,typ,datetime,status,mid,statusdatetime;

    public Messages(){}

    public Messages(String from, String message, String typ, String datetime, String status,String mid,String statusdatetime) {
        this.from = from;
        this.message = message;
        this.typ = typ;
        this.datetime = datetime;
        this.status = status;
        this.mid = mid;
        this.statusdatetime = statusdatetime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getStatusdatetime() {
        return statusdatetime;
    }

    public void setStatusdatetime(String statusdatetime) {
        this.statusdatetime = statusdatetime;
    }
}
