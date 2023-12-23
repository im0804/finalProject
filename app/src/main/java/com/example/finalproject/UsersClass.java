package com.example.finalproject;

public class UsersClass {
    String Uid;
    String fullName;
    String userName;
    int age;
    String gender;
    int level;
    int ratingLevel;
    String address;
    String city;
    int yearsOfPlay;
    int distance;
    boolean isCoach = false;

    public UsersClass(String uid, String fullName, String userName, int age, String gender, String address, String city, int level, int ratingLevel, int yearsOfPlay, int distance) {
        this.Uid = uid;
        this.fullName = fullName;
        this.userName = userName;
        this.age = age;
        this.gender = gender;
        this.level = level;
        this.ratingLevel = ratingLevel;
        this.city = city;
        this.address = address;
        this.yearsOfPlay = yearsOfPlay;
        this.distance = distance;
        this.isCoach = false;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRatingLevel() {
        return ratingLevel;
    }

    public void setRatingLevel(int ratingLevel) {
        this.ratingLevel = ratingLevel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getYearsOfPlay() {
        return yearsOfPlay;
    }

    public void setYearsOfPlay(int yearsOfPlay) {
        this.yearsOfPlay = yearsOfPlay;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isCoach() {
        return isCoach;
    }

    public void setIsCoach(boolean isCoach) {
        this.isCoach = isCoach;
    }
}
