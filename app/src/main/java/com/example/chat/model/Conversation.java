package com.example.chat.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Conversation implements Serializable {
    private String cadmin;
    private String cname;
    private String image;
    private String type;
    private String cid;
    private ArrayList<Participant> participants;


    public Conversation(){

    }

    public Conversation(String cid, String cadmin, String cname, String image, String type, ArrayList<Participant> participants){
        this.setCid(cid);
        this.setCAdmin(cadmin);
        this.setCName(cname);
        this.setImage(image);
        this.setType(type);
        this.setParticipants(participants);
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCAdmin() {
        return cadmin;
    }

    public void setCAdmin(String cAdmin) {
        this.cadmin = cAdmin;
    }

    public String getCName() {
        return cname;
    }

    public void setCName(String cName) {
        this.cname = cName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
}