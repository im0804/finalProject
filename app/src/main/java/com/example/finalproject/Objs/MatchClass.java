package com.example.finalproject.Objs;

import com.example.finalproject.Objs.EndMatchClass;

public class MatchClass {
    private String uidInviter;
    private String uidInvited;
    private String userNameInviter;
    private String userNameInvited;
    private String date;
    private String hour;
    private String key;
    //String location;
    private EndMatchClass endMatch;

    public MatchClass(){}
    public MatchClass(String uidInviter, String userNameInviter, String uidInvited, String userNameInvited, String date, String hour, String key, EndMatchClass endMatch){
        this.uidInviter = uidInviter;
        this.uidInvited = uidInvited;
        this.userNameInviter = userNameInviter;
        this.userNameInvited = userNameInvited;
        this.date = date;
        this.hour = hour;
        this.key = key;
        //this.location = location;
        this.endMatch = endMatch;
    }




    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUidInviter() {
        return uidInviter;
    }

    public void setUidInviter(String uidInviter) {
        this.uidInviter = uidInviter;
    }

    public String getUidInvited() {
        return uidInvited;
    }

    public void setUidInvited(String uidInvited) {
        this.uidInvited = uidInvited;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    /*public String getLocation() {
        return location;
    }*/

    /*public void setLocation(String location) {
        this.location = location;
    }*/

    public EndMatchClass getEndMatch() {
        return endMatch;
    }

    public void setEndMatch(EndMatchClass endMatch) {
        this.endMatch = endMatch;
    }

    public String getUserNameInviter() {
        return userNameInviter;
    }

    public void setUserNameInviter(String userNameInviter) {
        this.userNameInviter = userNameInviter;
    }

    public String getUserNameInvited() {
        return userNameInvited;
    }

    public void setUserNameInvited(String userNameInvited) {
        this.userNameInvited = userNameInvited;
    }
}
