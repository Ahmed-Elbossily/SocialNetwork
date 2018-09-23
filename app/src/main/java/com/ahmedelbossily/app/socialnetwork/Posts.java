package com.ahmedelbossily.app.socialnetwork;

public class Posts {

    private String uid;
    private String fullname;
    private String description;
    private String profileImage;
    private String postImage;
    private String date;
    private String time;

    public Posts() {
    }

    public Posts(String uid, String fullname, String description, String profileImage, String postImage, String date, String time) {
        this.uid = uid;
        this.fullname = fullname;
        this.description = description;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.date = date;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
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
