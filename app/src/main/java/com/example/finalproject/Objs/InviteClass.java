package com.example.finalproject.Objs;

public class InviteClass {
    private String Uid;
    private String userName;
    private String address;
    private String city;
    private String date;
    private String startTime;
    private String key;
    private boolean level1, level2, level3, level4;

    public InviteClass(){}
    public InviteClass(String Uid, String userName, String address, String city, String date, String start, String key, boolean level1, boolean level2, boolean level3, boolean level4){
        this.Uid = Uid;
        this.userName = userName;
        this.address = address;
        this.city = city;
        this.date = date;
        this.startTime = start;
        this.key = key;
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isLevel1() {
        return level1;
    }

    public void setLevel1(boolean level1) {
        this.level1 = level1;
    }

    public boolean isLevel2() {
        return level2;
    }

    public void setLevel2(boolean level2) {
        this.level2 = level2;
    }

    public boolean isLevel3() {
        return level3;
    }

    public void setLevel3(boolean level3) {
        this.level3 = level3;
    }

    public boolean isLevel4() {
        return level4;
    }

    public void setLevel4(boolean level4) {
        this.level4 = level4;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
