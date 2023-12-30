package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.finalproject.LoginActivity.userFB;
import static com.example.finalproject.ReferencesFB.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * @author		inbar menahem
 * @version	    1
 * @since		24/12/2023
 * the main activity for the user.
 */
public class MainActivity extends AppCompatActivity {
    ListView friendsSearchLV, closeMatchesLV, matchesHistoryLV;
    String Uid;
    Intent si;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        friendsSearchLV = (ListView) findViewById(R.id.friendsSearchLV);
        closeMatchesLV = (ListView) findViewById(R.id.closeMatchesLV);
        matchesHistoryLV = (ListView) findViewById(R.id.matchesHistoryLV);

        Uid = userFB.getUid();
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