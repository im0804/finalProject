package com.example.finalproject;

import static com.example.finalproject.LoginActivity.userFB;
import static com.example.finalproject.ReferencesFB.REQUEST_CAMERA_PERMISSION;
import static com.example.finalproject.ReferencesFB.REQUEST_IMAGE_CAPTURE;
import static com.example.finalproject.ReferencesFB.REQUEST_PICK_IMAGE;
import static com.example.finalproject.ReferencesFB.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION;
import static com.example.finalproject.ReferencesFB.refUsers;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

/**
 * @author		inbar menahem
 * @version	    1
 * @since		25/12/2023
 * activity for coach user.
 */
public class CoachActivity extends AppCompatActivity {
    ListView searchCoachLV, closeToYouLV;
    Intent si;
    String Uid;
    UsersClass user;
    AlertDialog.Builder adb;
    boolean isCoach;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);

        searchCoachLV = (ListView) findViewById(R.id.searchCoachlV);
        closeToYouLV = (ListView) findViewById(R.id.closeToYouLV);

        Uid = userFB.getUid();
        Query query = refUsers
                .orderByChild("uid")
                .equalTo(Uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if (dS.exists()) {
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        isCoach = user.isCoach;

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CoachActivity.this, "on cancelled", Toast.LENGTH_LONG).show();
            }
        });

        adb = new AlertDialog.Builder(this);
    }

    /**
     * on click join method
     * <p>
     *
     * @param    view the view
     * @return   moves the user to the sign in as a coach activity.
     */
    public void join(View view) {
        if (isCoach){
            adb.setTitle("already joined");
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
        if (str.equals("register")){
            Intent si = new Intent(this, RegisterActivity.class);
            startActivity(si);
        }
        else if (str.equals("main")){
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
        else if (str.equals("join as coach")){
        }
        else if (str.equals("login")) {
            Intent si = new Intent(this, LoginActivity.class);
            startActivity(si);
        } else if (str.equals("coachAct")) {
        }

        return super.onOptionsItemSelected(item);
    }

}