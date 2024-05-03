package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.MainActivity.arrPassed;
import static com.example.finalproject.ReferencesFB.refNotPlayed;
import static com.example.finalproject.ReferencesFB.refPlayed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Objs.EndMatchClass;
import com.example.finalproject.Objs.MatchClass;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReminderActivity extends AppCompatActivity {

    TextView uid1TV, uid2TV, title1TV;
    EditText set11ET, set12ET, set21ET, set22ET, set31ET, set32ET, set41ET, set42ET, set51ET, set52ET;
    ArrayList<MatchClass> arrHistory;
    ArrayList<String> score;
    MatchClass match;
    EndMatchClass endMatch = new EndMatchClass();
    String winner;
    AlertDialog.Builder adb;
    Intent gi;
    String userNameInviter, userNameInvited, date;
    boolean allGood = false, finishBTN = false, isEmpty = false;
    int counter = 0, player1Sets=0, player2Sets=0;



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

        set21ET.setEnabled(false);
        set22ET.setEnabled(false);
        set31ET.setEnabled(false);
        set32ET.setEnabled(false);
        set41ET.setEnabled(false);
        set42ET.setEnabled(false);
        set51ET.setEnabled(false);
        set52ET.setEnabled(false);


        score = new ArrayList<String>();

        arrHistory = new ArrayList<MatchClass>();
        gi = getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        match = arrPassed.get(0);
        userNameInviter = match.getUserNameInviter();
        userNameInvited = match.getUserNameInvited();
        date = match.getDate();

    if (!arrPassed.isEmpty()) {
        title1TV.setText(date + " "+ match.getUserNameInviter()+" VS "+ match.getUserNameInvited());
        uid1TV.setText(match.getUserNameInviter());
        uid2TV.setText(match.getUserNameInvited());

        }
    }

    public void closeMatch(View view) {
        finishBTN = true;
        if (score.isEmpty()) {
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
        if (score.size() == 1) {
            if (checkScore(set21ET, set22ET)){
                if (!isEmpty) {
                    score.add(set21ET.getText().toString() + " : " + set22ET.getText().toString());
                    if (Integer.parseInt(set21ET.getText().toString()) - Integer.parseInt(set22ET.getText().toString()) > 0) {
                        player1Sets++;
                    } else {
                        player2Sets++;
                    }
                }
            }
        }
        if (score.size() == 2) {
            if (checkScore(set31ET, set32ET)){
                if (!isEmpty) {
                    score.add(set31ET.getText().toString() + " : " + set32ET.getText().toString());
                    if (Integer.parseInt(set31ET.getText().toString()) - Integer.parseInt(set32ET.getText().toString()) > 0) {
                        player1Sets++;
                    } else {
                        player2Sets++;
                    }
                }
            }
        }
        if (score.size() == 3) {
            if (checkScore(set41ET, set42ET)){
                if (!isEmpty) {
                    score.add(set41ET.getText().toString() + " : " + set42ET.getText().toString());
                    if (Integer.parseInt(set41ET.getText().toString()) - Integer.parseInt(set42ET.getText().toString()) > 0) {
                        player1Sets++;
                    } else {
                        player2Sets++;
                    }
                }
            }
        }
        if (score.size() == 4) {
            if (checkScore(set51ET, set52ET)) {
                if (!isEmpty) {
                    score.add(set51ET.getText().toString() + " : " + set52ET.getText().toString());
                    if (Integer.parseInt(set51ET.getText().toString()) - Integer.parseInt(set52ET.getText().toString()) > 0) {
                        player1Sets++;
                    } else {
                        player2Sets++;
                    }
                }
            }
        }

        if (allGood){
            if (player1Sets > player2Sets){
                winner = uid1TV.getText().toString();
            }
            else {
                winner = uid2TV.getText().toString();
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
        else {
            Toast.makeText(this, "something is wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkScore(EditText scorePlayer1, EditText scorePlayer2){
        allGood = false;
        if (!scorePlayer1.getText().toString().isEmpty() && !scorePlayer2.getText().toString().isEmpty()) {
            if (isLegalScore(scorePlayer1.getText().toString(), scorePlayer2.getText().toString())) {
                allGood = true;
            }
            else {
                Toast.makeText(this, "please enter a valid score", Toast.LENGTH_SHORT).show();
                scorePlayer1.setText("");
                scorePlayer2.setText("");
                allGood = false;
            }
        }
        else {
            if ((!scorePlayer1.getText().toString().isEmpty() || !scorePlayer1.getText().toString().isEmpty())){
                Toast.makeText(this, "please enter a valid score", Toast.LENGTH_SHORT).show();
                scorePlayer1.setText("");
                scorePlayer2.setText("");
                allGood = false;
            }

            if (scorePlayer1.getText().toString().isEmpty() && scorePlayer1.getText().toString().isEmpty()){
                allGood = true;
                isEmpty = true;
            }
        }
        return allGood;
    }

    public void addSet(View view) {
        if (counter == 0) {
            if (checkScore(set11ET, set12ET)){
                set21ET.setEnabled(true);
                set22ET.setEnabled(true);
            }
        }
        if (counter == 1) {
            if (checkScore(set21ET, set22ET)){
                set31ET.setEnabled(true);
                set32ET.setEnabled(true);
            }
        }
        if (counter == 2) {
            if (checkScore(set31ET, set32ET)){
                set41ET.setEnabled(true);
                set42ET.setEnabled(true);
            }
        }
        if (counter == 3) {
            if (checkScore(set41ET, set42ET)){
                set51ET.setEnabled(true);
                set52ET.setEnabled(true);
            }
        }

        counter++;
    }

    public void delete(View view) {
        adb = new AlertDialog.Builder(this);
        adb.setTitle("delete meeting");
        adb.setMessage("are you sure you want to delete this meeting?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // check how to remove the value, using the inviter or rhe invited
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