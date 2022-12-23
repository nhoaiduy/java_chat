package com.example.chat.model;

import java.io.Serializable;

public class Message implements Serializable {
    String mid;
    long order;
    String cid;
    String uid;
    String mdes;
    String mdate;
    String mtype;

    public Message(String mid, int order, String cid, String uid, String mdes, String mdate, String mtype) {
        this.mid = mid;
        this.order = order;
        this.cid = cid;
        this.uid = uid;
        this.mdes = mdes;
        this.mdate = mdate;
        this.mtype = mtype;
    }

    public Message() {
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMdes() {
        return mdes;
    }

    public void setMdes(String mdes) {
        this.mdes = mdes;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }
}
