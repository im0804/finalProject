package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.MainActivity.arrPassed;
import static com.example.finalproject.ReferencesFB.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Objs.EndMatchClass;
import com.example.finalproject.Objs.MatchClass;
import com.example.finalproject.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Reminder activity.
 * here the user enters result of a finished match
 */
public class ReminderActivity extends AppCompatActivity {

    private TextView uid1TV, uid2TV, title1TV;
    private EditText set11ET, set12ET, set21ET, set22ET, set31ET, set32ET, set41ET, set42ET, set51ET, set52ET;
    private Button btnDelete, btnAdd, BTNFinish;

    boolean allGood = false;
    int counter = 0, player1Sets=0, player2Sets=0, scoreCounter = 0;
    String userNameInviter, userNameInvited, date, winner;

    MatchClass match;
    EndMatchClass endMatch = new EndMatchClass();

    ArrayList<String> score;

    AlertDialog.Builder adb;
    Intent gi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        uid1TV = (TextView) findViewById(R.id.uid1TV);
        uid2TV = (TextView) findViewById(R.id.uid2TV);
        title1TV = (TextView) findViewById(R.id.title1TV);
        set11ET = (EditText) findViewById(R.id.set11ET);
        set12ET = (EditText) findViewById(R.id.set12ET);
        set21ET = (EditText) findViewById(R.id.set21ET);
        set22ET = (EditText) findViewById(R.id.set22ET);
        set31ET = (EditText) findViewById(R.id.set31ET);
        set32ET = (EditText) findViewById(R.id.set32ET);
        set41ET = (EditText) findViewById(R.id.set41ET);
        set42ET = (EditText) findViewById(R.id.set42ET);
        set51ET = (EditText) findViewById(R.id.set51ET);
        set52ET = (EditText) findViewById(R.id.set52ET);
        btnDelete = (Button) findViewById(R.id.deleteBTN);
        btnAdd = (Button) findViewById(R.id.addBTN);
        BTNFinish = (Button) findViewById(R.id.finishBTN);
        btnDelete.setBackgroundColor(Color.TRANSPARENT);
        btnAdd.setBackgroundColor(Color.TRANSPARENT);
        BTNFinish.setBackgroundColor(Color.TRANSPARENT);

        set21ET.setEnabled(false);
        set22ET.setEnabled(false);
        set31ET.setEnabled(false);
        set32ET.setEnabled(false);
        set41ET.setEnabled(false);
        set42ET.setEnabled(false);
        set51ET.setEnabled(false);
        set52ET.setEnabled(false);

        score = new ArrayList<String>();

        gi = getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //sets the design.
        if (!arrPassed.isEmpty()) {
            match = arrPassed.get(0);
            userNameInviter = match.getUserNameInviter();
            userNameInvited = match.getUserNameInvited();
            date = match.getDate();
            title1TV.setText(date + " "+ match.getUserNameInviter()+" VS "+ match.getUserNameInvited());
            uid1TV.setText(match.getUserNameInviter());
            uid2TV.setText(match.getUserNameInvited());
        }
    }

    /**
     * On Click method Close match.
     *
     * this method checks validity of all fields
     * it counts how many sets each player has
     * if all is good, it saves all the information in database
     * @param view the view
     */
    public void closeMatch(View view) {
        if (score.isEmpty() || scoreCounter == 0) {
            if (checkScore(set11ET, set12ET)){
                score.add(set11ET.getText().toString() + " : " + set12ET.getText().toString());
                if (Integer.parseInt(set11ET.getText().toString()) - Integer.parseInt(set12ET.getText().toString()) > 0){
                    player1Sets++;
                }
                else{
                    player2Sets++;
                }
            }
        }
        if (score.size() == 1 || scoreCounter == 1) {
            if (checkScore(set21ET, set22ET)){
                score.add(set21ET.getText().toString() + " : " + set22ET.getText().toString());
                if (Integer.parseInt(set21ET.getText().toString()) - Integer.parseInt(set22ET.getText().toString()) > 0) {
                    player1Sets++;
                } else {
                    player2Sets++;
                }
            }
        }
        if (score.size() == 2 || scoreCounter == 2) {
            if (checkScore(set31ET, set32ET)){
                score.add(set31ET.getText().toString() + " : " + set32ET.getText().toString());
                if (Integer.parseInt(set31ET.getText().toString()) - Integer.parseInt(set32ET.getText().toString()) > 0) {
                    player1Sets++;
                } else {
                    player2Sets++;
                }
            }
        }
        if (score.size() == 3 || scoreCounter == 3) {
            if (checkScore(set41ET, set42ET)){
                score.add(set41ET.getText().toString() + " : " + set42ET.getText().toString());
                if (Integer.parseInt(set41ET.getText().toString()) - Integer.parseInt(set42ET.getText().toString()) > 0) {
                    player1Sets++;
                } else {
                    player2Sets++;
                }
            }
        }
        if (score.size() == 4 || scoreCounter == 4) {
            if (checkScore(set51ET, set52ET)) {
                score.add(set51ET.getText().toString() + " : " + set52ET.getText().toString());
                if (Integer.parseInt(set51ET.getText().toString()) - Integer.parseInt(set52ET.getText().toString()) > 0) {
                    player1Sets++;
                } else {
                    player2Sets++;
                }

            }
        }

        if (allGood){
            adb = new AlertDialog.Builder(this);
            adb.setTitle("enter score");
            adb.setMessage("are you sure you want to finish entering the score?");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (player1Sets > player2Sets){
                        winner = uid1TV.getText().toString();
                    }
                    else if (player1Sets < player2Sets){
                        winner = uid2TV.getText().toString();
                    }
                    else{
                        winner = "no one - it's a tie";
                    }
                    endMatch.setScore(score);
                    endMatch.setWinner(winner);
                    refNotPlayed.child(match.getUidInvited())
                            .child(match.getKey())
                            .removeValue();
                    match.setEndMatch(endMatch);
                    refPlayed.child(match.getUidInvited())
                            .child(match.getKey())
                            .setValue(match);
                    setResult(RESULT_OK, gi);
                    finish();
                }
            });
            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        else {
            Toast.makeText(ReminderActivity.this, "check if all written sets are valid", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * method Check score.
     *
     * this method checks if added score is valid
     * @param scorePlayer1  player 1's score
     * @param scorePlayer2  player 2's score
     */
    public boolean checkScore(EditText scorePlayer1, EditText scorePlayer2){
        allGood = false;
        if (scorePlayer1.getText().toString().isEmpty() && scorePlayer1.getText().toString().isEmpty()){
            allGood = true;
            return false;
        }
        if (!scorePlayer1.getText().toString().isEmpty() && !scorePlayer2.getText().toString().isEmpty()) {
            if (isLegalScore(scorePlayer1.getText().toString(), scorePlayer2.getText().toString())) {
                allGood = true;
            }
            else {
                Toast.makeText(this, "please enter a valid score", Toast.LENGTH_LONG).show();
                scorePlayer1.setText("");
                scorePlayer2.setText("");
                allGood = false;
            }
        }
        else {
            if ((!scorePlayer1.getText().toString().isEmpty() || !scorePlayer1.getText().toString().isEmpty())){
                Toast.makeText(this, "please enter a valid score", Toast.LENGTH_LONG).show();
                scorePlayer1.setText("");
                scorePlayer2.setText("");
                allGood = false;
            }
        }
        return allGood;
    }

    /**
     * On Click method Add set.
     *
     * this method checks if previous set is valid, if so it opens another set
     * @param view the view
     */
    public void addSet(View view) {
        if (counter == 0) {
            if (checkScore(set11ET, set12ET)){
                set21ET.setEnabled(true);
                set22ET.setEnabled(true);
                counter++;
                scoreCounter++;
            }
            else{
                Toast.makeText(ReminderActivity.this, "please enter a valid score", Toast.LENGTH_SHORT).show();
            }
        }
        else if (counter == 1) {
            if (checkScore(set21ET, set22ET)){
                set31ET.setEnabled(true);
                set32ET.setEnabled(true);
                counter++;
                scoreCounter++;
            }
            else{
                Toast.makeText(ReminderActivity.this, "please enter a valid score", Toast.LENGTH_LONG).show();
            }
        }
        else if (counter == 2) {
            if (checkScore(set31ET, set32ET)){
                set41ET.setEnabled(true);
                set42ET.setEnabled(true);
                counter++;
                scoreCounter++;
            }
            else{
                Toast.makeText(ReminderActivity.this, "please enter a valid score", Toast.LENGTH_LONG).show();
            }
        }
        else if (counter == 3) {
            if (checkScore(set41ET, set42ET)){
                set51ET.setEnabled(true);
                set52ET.setEnabled(true);
                counter++;
                scoreCounter++;
            }
            else{
                Toast.makeText(ReminderActivity.this, "please enter a valid score", Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * On Click method Delete.
     *
     * this method deletes the meeting
     * it opens a dialog that asks if the user is sure
     * if so then it deletes the meeting from database
     * @param view the view
     */
    public void delete(View view) {
        adb = new AlertDialog.Builder(this);
        adb.setTitle("delete meeting");
        adb.setMessage("are you sure you want to delete this meeting?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refNotPlayed.child(match.getUidInvited())
                        .child(match.getKey())
                        .removeValue();
                setResult(RESULT_OK, gi);
                finish();
            }
        });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * Is legal score method.
     *
     * this method checks if the given games are legal
     * @param score3 player 1's game
     * @param score4 player 2's game
     */
    public static boolean isLegalScore(String score3, String  score4) {
        // check if one of them is null
        if ((score3.isEmpty()) || (score4.isEmpty())){
            return false;
        }
        int score1 = Integer.parseInt(score3);
        int score2 = Integer.parseInt(score4);

        // Check if scores are within valid range
        if ((score1 < 0 || score1 > 7) || (score2 < 0 || score2 > 7)) {
            return false;
        }

        // Check if one player has won
        if ((score1 == 6 && score2 < 5) || (score2 == 6 && score1 < 5)) {
            return true;
        }

        // Check if one player has won the tiebreak
        if ((score1 == 7 && score2 == 6) || (score1 == 6 && score2 == 7)) {
            return true;
        }

        // check if one player won with 2 games lead
        if ((score1 == 7 && score2 == 5) || (score1 == 5 && score2 == 7)) {
            return true;
        }

        return false; // All other cases are illegal scores
    }

}