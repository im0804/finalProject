package com.example.finalproject.Objs;

import java.util.ArrayList;

public class EndMatchClass {
    private ArrayList<String> score;
    private String winner;

    public EndMatchClass(){}
    public EndMatchClass(ArrayList<String> score, String winner){
        this.score = score;
        this.winner = winner;
    }

    public ArrayList<String> getScore() {
        return score;
    }

    public void setScore(ArrayList<String> score) {
        this.score = score;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
