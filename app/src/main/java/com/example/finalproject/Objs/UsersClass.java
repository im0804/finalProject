package com.example.finalproject.Objs;

import android.location.Location;

import java.util.Locale;

public class UsersClass {
    private String Uid;
    private String fullName;
    private String userName;
    private int age;
    private String gender;
    private int level;
    private int ratingLevel;
    private String addressName;
    private double addLatitude;
    private double addLongitude;
    private String city;
    private int yearsOfPlay;
    private int distance;
    private boolean isCoach;
    private CoachUserClass userCoach;

    public UsersClass(){}
    public UsersClass(String uid, String fullName, String userName, int age, String gender, String addressName,double addLatitude, double addLongitude, String city, int level, int ratingLevel, int yearsOfPlay, int distance) {
        this.Uid = uid;
        this.fullName = fullName;
        this.userName = userName;
        this.age = age;
        this.gender = gender;
        this.level = level;
        this.ratingLevel = ratingLevel;
        this.city = city;
        this.addressName = addressName;
        this.addLatitude = addLatitude;
        this.addLongitude = addLongitude;
        this.yearsOfPlay = yearsOfPlay;
        this.distance = distance;
        this.isCoach = false;
    }
    public UsersClass(UsersClass user, CoachUserClass userCoach1){
        this.Uid = user.Uid;
        this.fullName = user.fullName;
        this.userName = user.userName;
        this.age = user.age;
        this.gender = user.gender;
        this.level = user.level;
        this.ratingLevel = user.ratingLevel;
        this.city = user.city;
        this.addressName = user.addressName;
        //this.addressLocation = user.addressLocation;
        this.yearsOfPlay = user.yearsOfPlay;
        this.distance = user.distance;
        this.isCoach = true;
        userCoach = new CoachUserClass(userCoach1.getYearsOfCoaching(), userCoach1.getCoachType(), userCoach1.getDescription());
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

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public double getAddLatitude() {
        return addLatitude;
    }

    public void setAddLatitude(double addLatitude) {
        this.addLatitude = addLatitude;
    }

    public double getAddLongitude() {
        return addLongitude;
    }

    public void setAddLongitude(double addLongitude) {
        this.addLongitude = addLongitude;
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
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isCoach() {
        return isCoach;
    }

    public void setCoach(boolean coach) {
        isCoach = coach;
    }
    public boolean getIsCoach() {
        return isCoach;
    }

    public void setIsCoach(boolean isCoach) {
        this.isCoach = isCoach;
    }

    public CoachUserClass getUserCoach() {
        return userCoach;
    }

    public void setUserCoach(CoachUserClass userCoach) {
        this.userCoach = userCoach;
    }
}
