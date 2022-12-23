package com.example.chat.model;


import java.io.Serializable;

public class FriendRequest implements Serializable {
    private String fullName;
    private String image;
    private String uid;
    private int status;

    public FriendRequest(String fullName, String image, String uid, int status){
        this.setFullName(fullName);
        this.setImage(image);
        this.setId(uid);
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return uid;
    }

    public void setId(String uid) {
        this.uid = uid;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }


}
