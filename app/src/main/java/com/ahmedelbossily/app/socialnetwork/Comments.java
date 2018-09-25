package com.ahmedelbossily.app.socialnetwork;

public class Comments {

    private String uid, username, comment, date, time;

    public Comments() {
    }

    public Comments(String uid, String username, String comment, String date, String time) {
        this.uid = uid;
        this.username = username;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
