package com.example.finalproject;

public class EndMatchClass {
    String score;
    String winner;

    public EndMatchClass(){}
    public EndMatchClass(String score, String winner){
        this.score = score;
        this.winner = winner;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
