package com.example.finalproject.Objs;

public class MessageClass {
    private String date;
    private String sendingUid;
    private String mssgContent;

    public MessageClass() {
    }

    public MessageClass(String date, String sendingUid, String mssgContent) {
        this.date = date;
        this.sendingUid = sendingUid;
        this.mssgContent = mssgContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSendingUid() {
        return sendingUid;
    }

    public void setSendingUid(String sendingUid) {
        this.sendingUid = sendingUid;
    }

    public String getMssgContent() {
        return mssgContent;
    }

    public void setMssgContent(String mssgContent) {
        this.mssgContent = mssgContent;
    }
}