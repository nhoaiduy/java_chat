package com.example.chat.model;

public class Media{
    private String cid;
    private String mid;
    private String uid;
    private String src;
    private String type;

    public Media() {
    }

    public Media(String cid, String mid, String uid, String src, String type) {
        this.cid = cid;
        this.mid = mid;
        this.uid = uid;
        this.src = src;
        this.type = type;
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

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
