package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.MainActivity.currentUser;
import static com.example.finalproject.ReferencesFB.*;
import static com.example.finalproject.Activities.MainActivity.currentLoc;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.finalproject.Adapters.CustomAdapterCoach;
import com.example.finalproject.Adapters.CustomAdapterInvites;
import com.example.finalproject.Objs.UserDistanceClass;
import com.example.finalproject.R;
import com.example.finalproject.Objs.UsersClass;
import com.example.finalproject.ReferencesFB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * activity for coach user.
 * shows all users with coach attribute.
 */
public class CoachActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView closeToYouLV;
    private Button btnJoin, sortBTN;

    boolean orderClass = false;
    int position;

    Intent si;
    UsersClass user;
    UserDistanceClass distance;

    ArrayList<UserDistanceClass> arrDistance, arrSorted;
    ArrayList<UsersClass> arrUsers;
    CustomAdapterCoach customAdapterCoach;

    AlertDialog.Builder adb;
    Location tempLoc;

    public static boolean cameFromCoach = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);

        closeToYouLV = (ListView) findViewById(R.id.closeToYouLV);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setBackgroundColor(Color.TRANSPARENT);
        sortBTN = (Button) findViewById(R.id.sortBTN);
        sortBTN.setBackgroundColor(Color.TRANSPARENT);
        arrDistance = new ArrayList<UserDistanceClass>();
        arrSorted = new ArrayList<UserDistanceClass>();
        arrUsers = new ArrayList<UsersClass>();


        customAdapterCoach = new CustomAdapterCoach(CoachActivity.this, arrDistance);
        closeToYouLV.setAdapter(customAdapterCoach);
        closeToYouLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        closeToYouLV.setOnCreateContextMenuListener(this);

        //reading all users with coach attribute and showing them in array list
        //also, it calculates their distance from the current user and puts it into array list
        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()) {
                    DataSnapshot dS = tsk.getResult();
                    tempLoc = new Location("temp location");
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        if (user.isCoach() && !user.getUid().equals(Uid)){
                            tempLoc.setLatitude(user.getAddLatitude());
                            tempLoc.setLongitude(user.getAddLongitude());
                            Log.i("distance", currentLoc.distanceTo(tempLoc)+"");
                            Log.i("temp long", tempLoc.getLongitude()+"");
                            Log.i("temp lat", tempLoc.getLatitude()+"");
                            Log.i("current long", currentLoc.getLongitude()+"");
                            Log.i("current lat", currentLoc.getLatitude()+"");
                            distance = new UserDistanceClass(user, currentLoc.distanceTo(tempLoc));
                            arrDistance.add(distance);
                        }
                    }
                    customAdapterCoach.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * on click sort method
     * <p>
     * @param    view the view
     * sorts the coach users by distance from the current user
     */
    public void sortBTN(View view) {
        if (orderClass) {
            arrDistance.sort((o1, o2)
                    -> o1.getDistance().compareTo(
                    o2.getDistance()));
            sortBTN.setText("Closest ↑");
        } else {
            arrDistance.sort((o1, o2)
                    -> o2.getDistance().compareTo(
                    o1.getDistance()));
            sortBTN.setText("Closest ↓");
        }
        orderClass = !orderClass;
        customAdapterCoach.notifyDataSetChanged();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Actions");
        menu.add("see profile");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String i = item.getTitle().toString();
        if (i.equals("see profile")) {
            Intent si = new Intent(CoachActivity.this, ProfileActivity.class);
            si.putExtra("coachUid", arrDistance.get(position).getUser().getUid());
            cameFromCoach = true;
            startActivityForResult(si,150);
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
        else if(str.equals("Log Out")){
            adb = new AlertDialog.Builder(this);
            adb.setTitle("are you sure you want to log out?");
            adb.setMessage("logging out will not remove all your data");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putBoolean("stayConnected", false);
                    editor.commit();
                    CoachActivity.this.startActivity(new Intent(CoachActivity.this, OpeningActivity.class));
                    currentUser = null;
                }
            });
            adb.setNegativeButton("No", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        return super.onOptionsItemSelected(item);
    }
}