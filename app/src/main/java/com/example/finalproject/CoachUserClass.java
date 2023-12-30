package com.example.finalproject;

public class CoachUserClass{
    int yearsOfCoaching;
    String coachType;
    String description;

    public CoachUserClass(){}

    public CoachUserClass(int yearsOfCoaching, String coachType, String description){
        this.yearsOfCoaching = yearsOfCoaching;
        this.coachType = coachType;
        this.description = description;
    }

    public int getYearsOfCoaching() {
        return yearsOfCoaching;
    }

    public void setYearsOfPlay(int yearsOfPlay) {
        this.yearsOfCoaching = yearsOfPlay;
    }

    public String getCoachType() {
        return coachType;
    }

    public void setCoachType(String coachType) {
        this.coachType = coachType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
