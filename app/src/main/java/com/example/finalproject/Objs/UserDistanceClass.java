package com.example.finalproject.Objs;

public class UserDistanceClass {
    private UsersClass user;
    private Float distance;

    public UserDistanceClass(){}
    public UserDistanceClass(UsersClass user, Float distance) {
        this.user = user;
        this.distance = distance;
    }

    public UsersClass getUser() {
        return user;
    }

    public void setUser(UsersClass user) {
        this.user = user;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}
