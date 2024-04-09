package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.LoginActivity.Uid;
import static com.example.finalproject.ReferencesFB.refInvites;
import static com.example.finalproject.ReferencesFB.refNotPlayed;
import static com.example.finalproject.ReferencesFB.refUsers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Adapters.CustomAdapterCM;
import com.example.finalproject.Adapters.CustomAdapterInvites;
import com.example.finalproject.Adapters.CustomAdapterUserInvites;
import com.example.finalproject.Objs.InviteClass;
import com.example.finalproject.Objs.MatchClass;
import com.example.finalproject.R;
import com.example.finalproject.RegisterActivity;
import com.example.finalproject.Objs.UsersClass;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author inbar menahem
 * @version 1
 * @since 24/12/2023
 * the main activity for the user.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, View.OnCreateContextMenuListener {
    ListView friendsSearchLV, closeMatchesLV, invitesLV, userInvitesLV;
    String userName, userCity, winner;
    Intent si;
    UsersClass user;
    InviteClass invite;
    MatchClass match;
    CustomAdapterInvites customAdapterInvites;
    CustomAdapterUserInvites customAdapterUserInvites;
    CustomAdapterCM customAdapterCM;
    ArrayList<InviteClass> arrInvites, userArrInvites;
    ArrayList<MatchClass> arrMatches, arrHistory;
    ArrayList<UsersClass> arrUsers;
    ArrayList<String> arrAddresses;
    Button btnReminder;

    AlertDialog.Builder adb;

    LocationRequest locationRequest;
    double distance;
    int userDis, pos,counterDP = 0;
    boolean clicked = false;

    Calendar calNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity","Activity started");

        //friendsSearchLV = (ListView) findViewById(R.id.friendsSearchLV);
        closeMatchesLV = (ListView) findViewById(R.id.closeMatchesLV);
        invitesLV = (ListView) findViewById(R.id.invitesLV);
        userInvitesLV = (ListView) findViewById(R.id.userInvitesLV);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        arrInvites = new ArrayList<InviteClass>();
        userArrInvites = new ArrayList<InviteClass>();
        arrMatches = new ArrayList<MatchClass>();
        arrHistory = new ArrayList<MatchClass>();
        arrUsers = new ArrayList<UsersClass>();
        arrAddresses = new ArrayList<String>();

        customAdapterInvites = new CustomAdapterInvites(MainActivity.this, arrInvites);
        invitesLV.setAdapter(customAdapterInvites);

        customAdapterUserInvites = new CustomAdapterUserInvites(MainActivity.this, userArrInvites);
        userInvitesLV.setAdapter(customAdapterUserInvites);

        customAdapterCM = new CustomAdapterCM(MainActivity.this, arrMatches);
        closeMatchesLV.setAdapter(customAdapterCM);

        adb = new AlertDialog.Builder(this);

        userInvitesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        invitesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        closeMatchesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        userInvitesLV.setOnCreateContextMenuListener(this);
        invitesLV.setOnItemLongClickListener(this);
        closeMatchesLV.setOnItemLongClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()){
                    DataSnapshot dS = tsk.getResult();
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        if(Uid.equals(user.getUid())){
                            userDis = user.getDistance() * 1000;
                            userName = user.getUserName();
                            userCity = user.getCity();
                            Toast.makeText(MainActivity.this, Uid, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        refInvites.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()){
                    DataSnapshot dS = tsk.getResult();
                    arrInvites.clear();
                    userArrInvites.clear();
                    calNow = Calendar.getInstance();
                    for (DataSnapshot data : dS.getChildren()){
                        for (DataSnapshot secChild : data.getChildren()) {
                            invite = secChild.getValue(InviteClass.class);
                            if(!passedDate(invite.getKey(), calNow)){
                                if (Uid.equals(invite.getUid())){
                                    userArrInvites.add(invite);
                                }
                                else {
                                    arrInvites.add(invite);
                                }
                            }
                            else {
                                refInvites.child(invite.getUid())
                                        .child(invite.getKey())
                                        .removeValue();
                            }
                        }
                    }
                    customAdapterUserInvites.notifyDataSetChanged();
                    customAdapterInvites.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refInvites.addValueEventListener(velInvite);
        refNotPlayed.addValueEventListener(velCM);
    }

    ValueEventListener velInvite = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            refInvites.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> tsk) {
                    if (tsk.isSuccessful()){
                        DataSnapshot dS = tsk.getResult();
                        arrInvites.clear();
                        userArrInvites.clear();
                        for (DataSnapshot data : dS.getChildren()){
                            for (DataSnapshot secChild : data.getChildren()) {
                                if (Uid.equals(secChild.getValue(InviteClass.class).getUid())){
                                    invite = secChild.getValue(InviteClass.class);
                                    userArrInvites.add(invite);
                                }
                                else {
                                    invite = secChild.getValue(InviteClass.class);
                                    arrInvites.add(invite);
                                }
                            }
                        }
                        customAdapterUserInvites.notifyDataSetChanged();
                        customAdapterInvites.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener velCM = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            refNotPlayed.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                    if (tsk.isSuccessful()){
                        DataSnapshot dS = tsk.getResult();
                        arrMatches.clear();
                        calNow = Calendar.getInstance();
                        for (DataSnapshot data : dS.getChildren()){
                            for (DataSnapshot secChild : data.getChildren()) {
                                match = secChild.getValue(MatchClass.class);
                                if (!passedDate(match.getKey(), calNow)) {
                                    if (Uid.equals(match.getUidInvited()))
                                        arrMatches.add(match);
                                    if (Uid.equals(match.getUidInviter()))
                                        arrMatches.add(match);
                                }
                                else counterDP++;
                                //Toast.makeText(MainActivity.this, match.getKey(), Toast.LENGTH_LONG).show();
                            }
                        }
                        customAdapterCM.notifyDataSetChanged();
                        btnReminder.setText(counterDP);
                    }
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

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

    /**
     * Add a game method.
     *
     * @param view the view
     * @return opens the invitation activity.
     */
    public void addAGame(View view) {
        si = new Intent(this, InvitationActivity.class);
        startActivity(si);
    }

    public void remind(View view) {
        if (counterDP == 0){
            Toast.makeText(MainActivity.this, "you don't have passed meetings", Toast.LENGTH_LONG).show();
        }
        else {
            Intent si = new Intent(this, ReminderActivity.class);
            startActivityForResult(si, 1);
        }
    }

    @Override
    protected void onActivityResult(int source, int good, @Nullable Intent data_back) {
        super.onActivityResult(source, good, data_back);
        if (source == 1) {
            if (good == RESULT_OK) {
                counterDP--;
                btnReminder.setText(counterDP);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        refNotPlayed.removeEventListener(velCM);
        refInvites.removeEventListener(velInvite);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.invitesLV) {
            adb.setTitle("find a match!");
            adb.setMessage("do you want to play against " + arrInvites.get(position).getUserName() + "?");
            adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MatchClass match = new MatchClass(arrInvites.get(position).getUid(), arrInvites.get(position).getUserName(), Uid, userName, arrInvites.get(position).getDate(), arrInvites.get(position).getStartTime(), arrInvites.get(position).getKey(), null);
                    refNotPlayed.child(Uid)
                            .child(arrInvites.get(position).getKey())
                            .setValue(match);
                    refInvites.child(arrInvites.get(position).getUid())
                            .child(arrInvites.get(position).getKey())
                            .removeValue();
                    arrInvites.remove(position);
                    arrMatches.add(match);
                    customAdapterUserInvites.notifyDataSetChanged();
                    customAdapterInvites.notifyDataSetChanged();
                    customAdapterCM.notifyDataSetChanged();
                }
            });
            adb.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }

        if (parent.getId() == R.id.userInvitesLV) {
            pos = position;

        }

        if (parent.getId() == R.id.closeMatchesLV) {
            adb.setTitle("delete meeting");
            adb.setCancelable(false);
            adb.setMessage("are you sure you want to delete this meeting?");
            adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refNotPlayed.child(arrMatches.get(position).getUidInvited())
                            .child(arrMatches.get(position).getKey())
                            .removeValue();
                    customAdapterCM.notifyDataSetChanged();
                }
            });
            adb.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
            /*calNow = Calendar.getInstance();
                if (passedDate(arrMatches.get(position).getKey(), calNow)) {
                    final String[] arrwinner = {arrInvites.get(position).getUserName(), userName};
                    adb.setTitle("game, set & match!");
                    adb.setCancelable(false);
                    final EditText score = new EditText(this);
                    score.setHint("enter score");
                    adb.setView(score);
                    adb.setItems(arrwinner, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            winner = arrwinner[which];
                            clicked = true;
                        }
                    });
                    adb.setPositiveButton("done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (clicked) {
                                if (!score.getText().toString().isEmpty()) {
                                    EndMatchClass endMatch = new EndMatchClass(score.getText().toString(), winner);
                                    arrMatches.get(position).setEndMatch(endMatch);
                                    arrHistory.add(arrMatches.get(position));
                                    arrMatches.remove(position);
                                    customAdapterCM.notifyDataSetChanged();
                                    clicked = false;
                                } else {
                                    Toast.makeText(MainActivity.this, "please enter score", Toast.LENGTH_LONG).show();
                                }
                            } else
                                Toast.makeText(MainActivity.this, "please choose a winner", Toast.LENGTH_SHORT).show();
                        }
                    });
                    adb.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog ad = adb.create();
                    ad.show();
                }*/
            }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Actions");
        menu.add("delete");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String i = item.getTitle().toString();
        if (i.equals("delete")) {
            refInvites.child(Uid).child(userArrInvites.get(pos).getKey()).removeValue();
            userArrInvites.remove(pos);
            customAdapterUserInvites.notifyDataSetChanged();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String str = item.getTitle().toString();
        if (str.equals("register")){
            si = new Intent(this, RegisterActivity.class);
            startActivity(si);
        }
        else if (str.equals("main")){
        }
        else if (str.equals("profile")){
            Intent si = new Intent(this, ProfileActivity.class);
            startActivity(si);
        }
        else if (str.equals("coach profile")){
            Intent si = new Intent(this, CoachActivity.class);
            startActivity(si);
        }
        else if (str.equals("join as coach")){
            Intent si = new Intent(this, JoinAsCoachActivity.class);
            startActivity(si);
        } else if (str.equals("login")) {
            Intent si = new Intent(this, LoginActivity.class);
            startActivity(si);
        }
        else if (str.equals("coachAct")) {
            Intent si = new Intent(this, CoachActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }
}