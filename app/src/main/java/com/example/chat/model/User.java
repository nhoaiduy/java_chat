package com.example.chat.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class User {




    String uid;
    String dob;
    String email;
    String fullName;
    String gender;
    String image;
    String phone;
    @Nullable
    ArrayList<Friend> friends;
    @Nullable
    ArrayList<FriendRequest> requests;
    String status;
    public User(){

    }

    public User(String uid, String dob, String email, String fullName, String gender, String image, String phone, ArrayList<Friend> friends, ArrayList<FriendRequest> requests, String status) {
        this.uid = uid;
        this.dob = dob;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.image = image;
        this.phone = phone;
        this.friends = friends;
        this.requests = requests;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "dob=" + dob +
                "email=" + email +
                "fullName=" + fullName +
                "gender=" + gender +
                "image=" + image +
                "phone=" + phone + "}";
    }
}
