package com.example.finalproject.Objs;

public class CoachUserClass{
    private int yearsOfCoaching;
    private String coachType;

    private CoachUserClass(){}

    public CoachUserClass(int yearsOfCoaching, String coachType){
        this.yearsOfCoaching = yearsOfCoaching;
        this.coachType = coachType;
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

}
