package com.example.finalproject;

public class InviteClass {
    String Uid;
    String userName;
    String address;
    String city;
    String date;
    String startTime;
    String key;
    boolean level1, level2, level3, level4, level5;
    int distance;

    public InviteClass(){}
    public InviteClass(String Uid, String userName, String address, String city, String date, String start, String key, boolean level1, boolean level2, boolean level3, boolean level4, boolean level5, int distance){
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
        this.level5 = level5;
        this.distance = distance;
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

    public boolean isLevel5() {
        return level5;
    }

    public void setLevel5(boolean level5) {
        this.level5 = level5;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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
