package com.example.finalproject;

public class ChatClass {
    String initiateUser;
    String guestUser;
    String chatName;
    //MessageClass

    public ChatClass(){}
    public ChatClass(String initiateUser, String guestUser, String chatName){
        this.initiateUser = initiateUser;
        this.guestUser = guestUser;
        this.chatName = chatName;
    }

    public String getInitiateUser() {
        return initiateUser;
    }

    public void setInitiateUser(String initiateUser) {
        this.initiateUser = initiateUser;
    }

    public String getGuestUser() {
        return guestUser;
    }

    public void setGuestUser(String guestUser) {
        this.guestUser = guestUser;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
