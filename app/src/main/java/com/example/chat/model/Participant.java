package com.example.chat.model;

import java.io.Serializable;

public class Participant implements Serializable {
    String nickname;
    String uid;

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    Boolean delete;

    public Participant(){

    }
    public Participant(String nickname, String uid, Boolean delete){
        this.nickname = nickname;
        this.uid = uid;
        this.delete = delete;
    }

    public String getNickname(){ return nickname;}
    public void setNickname(String nickname) {this.nickname = nickname;}
    public String getUID(){return uid;}
    public void setUID(String uID) {this.uid = uID;}

    @Override
    public String toString(){
        return "Participants{" +
                "nickName='" + nickname + '\'' +
                "uID='" + uid + '\'' + "}";
    }

}