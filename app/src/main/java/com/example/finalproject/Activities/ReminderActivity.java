package com.example.finalproject.Activities;

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
    RadioButton winner1RB, winner2RB;
    ArrayList<MatchClass> arrPassed, arrHistory;
    ArrayList<String> score;
    Calendar calNow;
    MatchClass match;
    EndMatchClass endMatch = new EndMatchClass();
    String winner;
    AlertDialog.Builder adb;
    Intent gi;
    String userNameInviter, userNameInvited, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        uid1TV = (TextView) findViewById(R.id.uid1TV);
        uid2TV = (TextView) findViewById(R.id.uid2TV);
        title1TV = (TextView) findViewById(R.id.title1TV);
        winner1RB = (RadioButton) findViewById(R.id.winner1RB);
        winner2RB = (RadioButton) findViewById(R.id.winner2RB);
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

        arrPassed = new ArrayList<MatchClass>();
        arrHistory = new ArrayList<MatchClass>();
        adb = new AlertDialog.Builder(this);
        gi = getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refNotPlayed.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()) {
                    DataSnapshot dS = tsk.getResult();
                    arrPassed.clear();
                    calNow = Calendar.getInstance();
                    for (DataSnapshot data : dS.getChildren()) {
                        for (DataSnapshot secChild : data.getChildren()) {
                            match = secChild.getValue(MatchClass.class);
                            if (passedDate(match.getKey(), calNow)) {
                                arrPassed.add(match);
                            }
                        }
                    }
                }
            }
        });
        match = arrPassed.get(0);
        userNameInviter = match.getUserNameInviter();
        userNameInvited = match.getUserNameInvited();
        date = match.getDate();

    if (!arrPassed.isEmpty()) {
        title1TV.setText(date + " "+ match.getUserNameInviter()+" VS "+ match.getUserNameInvited());
        uid1TV.setText(match.getUserNameInviter());
        uid2TV.setText(match.getUserNameInvited());
        winner1RB.setText(match.getUserNameInviter());
        winner2RB.setText(match.getUserNameInvited());

        }
    }

    public boolean passedDate(String dateString, Calendar calNow) {
        long end = calNow.getTimeInMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHH:mm");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        }
        catch(ParseException e){
            throw new RuntimeException(e);
        }
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(date);
        long start = calStart.getTimeInMillis();
        if (TimeUnit.MILLISECONDS.toDays(end - start) > 0){
            return true;
        }
        return false;
    }

    public void done(View view) {
        if (!winner1RB.isChecked() && !winner2RB.isChecked())
            Toast.makeText(ReminderActivity.this, "please choose a winner", Toast.LENGTH_LONG).show();
        else {
            if (!winner1RB.isChecked()) winner = userNameInviter;
            else winner = userNameInvited;

            if (set11ET.getText().toString().isEmpty() || set12ET.getText().toString().isEmpty()) {
                Toast.makeText(ReminderActivity.this, "fill at least one set", Toast.LENGTH_LONG).show();
            } else {
                if ((!set11ET.getText().toString().isEmpty() && set12ET.getText().toString().isEmpty()) || (set11ET.getText().toString().isEmpty() && !set12ET.getText().toString().isEmpty()))
                    Toast.makeText(ReminderActivity.this, "complete the sec half of the set", Toast.LENGTH_LONG).show();
                else {
                    score.add(set11ET.getText().toString());
                    score.add(set12ET.getText().toString());
                    if ((!set21ET.getText().toString().isEmpty() && set22ET.getText().toString().isEmpty()) || (set21ET.getText().toString().isEmpty() && !set22ET.getText().toString().isEmpty()))
                        Toast.makeText(ReminderActivity.this, "complete the sec half of the set", Toast.LENGTH_LONG).show();
                    else if (!set21ET.getText().toString().isEmpty() && !set22ET.getText().toString().isEmpty()) {
                        score.add(set21ET.getText().toString());
                        score.add(set22ET.getText().toString());
                    }

                    if ((!set31ET.getText().toString().isEmpty() && set32ET.getText().toString().isEmpty()) || (set31ET.getText().toString().isEmpty() && !set32ET.getText().toString().isEmpty()))
                        Toast.makeText(ReminderActivity.this, "complete the sec half of the set", Toast.LENGTH_LONG).show();
                    else if (!set31ET.getText().toString().isEmpty() && !set32ET.getText().toString().isEmpty()) {
                        score.add(set31ET.getText().toString());
                        score.add(set32ET.getText().toString());
                    }

                    if ((!set41ET.getText().toString().isEmpty() && set42ET.getText().toString().isEmpty()) || (set41ET.getText().toString().isEmpty() && !set42ET.getText().toString().isEmpty()))
                        Toast.makeText(ReminderActivity.this, "complete the sec half of the set", Toast.LENGTH_LONG).show();
                    else if (!set41ET.getText().toString().isEmpty() && !set42ET.getText().toString().isEmpty()) {
                        score.add(set41ET.getText().toString());
                        score.add(set42ET.getText().toString());
                    }

                    if ((!set51ET.getText().toString().isEmpty() && set52ET.getText().toString().isEmpty()) || (set51ET.getText().toString().isEmpty() && !set52ET.getText().toString().isEmpty()))
                        Toast.makeText(ReminderActivity.this, "complete the sec half of the set", Toast.LENGTH_LONG).show();
                    else if (!set51ET.getText().toString().isEmpty() && !set52ET.getText().toString().isEmpty()) {
                        score.add(set51ET.getText().toString());
                        score.add(set52ET.getText().toString());
                    }

                    refNotPlayed.child(match.getUidInvited())
                            .child(match.getKey())
                            .removeValue();
                    endMatch.setScore(score);
                    endMatch.setWinner(winner);
                    match.setEndMatch(endMatch);
                    refPlayed.child(match.getUidInvited())
                            .child(match.getKey())
                            .setValue(match);
                    setResult(RESULT_OK, gi);
                    finish();
                }
            }
        }
    }

    public void delete(View view) {
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
}