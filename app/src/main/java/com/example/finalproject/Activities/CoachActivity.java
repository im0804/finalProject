package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.MainActivity.currentUser;
import static com.example.finalproject.ReferencesFB.*;
import static com.example.finalproject.Activities.MainActivity.currentLoc;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.finalproject.Adapters.CustomAdapterCoach;
import com.example.finalproject.Adapters.CustomAdapterInvites;
import com.example.finalproject.R;
import com.example.finalproject.Objs.UsersClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * activity for coach user.
 * shows all the coach users.
 */
public class CoachActivity extends AppCompatActivity {
    private ListView closeToYouLV;
    private Button btnJoin;
    Intent si;
    UsersClass user;

    ArrayList<Float> arrDistance;
    ArrayList<UsersClass> arrUsers;
    CustomAdapterCoach customAdapterCoach;

    AlertDialog.Builder adb;
    Location tempLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);

        closeToYouLV = (ListView) findViewById(R.id.closeToYouLV);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setBackgroundColor(Color.TRANSPARENT);
        arrDistance = new ArrayList<Float>();
        arrUsers = new ArrayList<UsersClass>();

        customAdapterCoach = new CustomAdapterCoach(CoachActivity.this, arrUsers);
        closeToYouLV.setAdapter(customAdapterCoach);

        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()) {
                    DataSnapshot dS = tsk.getResult();
                    tempLoc = new Location("temp location");
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        if (user.isCoach() && !user.getUid().equals(Uid)){
                            arrUsers.add(user);
                            tempLoc.setLatitude(user.getAddLatitude());
                            tempLoc.setLongitude(user.getAddLongitude());
                            arrDistance.add(currentLoc.distanceTo(tempLoc));
                        }
                    }
                    customAdapterCoach.notifyDataSetChanged();
                }
            }
        });

    }

    public void sortUsersByDistance() {
    }

    /**
     * on click join method
     * <p>
     * @param    view the view
     * moves the user to the sign in as a coach activity.
     * if the user is already a coach, it will show an alert dialog.
     */
    public void join(View view) {
        if (currentUser.isCoach()){
            adb = new AlertDialog.Builder(this);
            adb.setTitle("you've already joined!");
            adb.setMessage("choose what do you want to do");
            adb.setPositiveButton("go back to main screen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    si = new Intent(CoachActivity.this, MainActivity.class);
                    startActivity(si);
                }
            });
            adb.setNegativeButton("Stay", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        else {
            si = new Intent(CoachActivity.this, JoinAsCoachActivity.class);
            startActivity(si);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String str = item.getTitle().toString();
        if (str.equals("main")){
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        else if (str.equals("profile")){
            Intent si = new Intent(this, ProfileActivity.class);
            startActivity(si);
        }
        else if (str.equals("coach profile")){
            Intent si = new Intent(this, CoachActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }

}