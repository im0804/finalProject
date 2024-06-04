package com.example.finalproject.Activities;

import static com.example.finalproject.ReferencesFB.*;
import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalproject.Adapters.CustomAdapterCM;
import com.example.finalproject.Adapters.CustomAdapterInvites;
import com.example.finalproject.Adapters.CustomAdapterUserInvites;
import com.example.finalproject.Objs.InviteClass;
import com.example.finalproject.Objs.MatchClass;
import com.example.finalproject.R;
import com.example.finalproject.ReferencesFB;
import com.example.finalproject.Objs.UsersClass;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * created by Inbar Menahem on 24/12/2023
 * Main Activity
 * <p>
 * display invites, user invite and close matches
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, View.OnCreateContextMenuListener {
    private ListView invitesLV, userInvitesLV, closeMatchesLV;
    private Button btnReminder, btnAddInvite;

    int pos, counterDP = 0;

    UsersClass user;
    InviteClass invite;
    MatchClass match;

    Intent si;
    AlertDialog.Builder adb;
    Calendar calNow;
    ProgressDialog pd;

    CustomAdapterInvites customAdapterInvites;
    CustomAdapterUserInvites customAdapterUserInvites;
    CustomAdapterCM customAdapterCM;

    ArrayList<InviteClass> arrInvites, userArrInvites;
    ArrayList<MatchClass> arrMatches, arrHistory;
    ArrayList<String> arrAddresses;
    ArrayList<Double> arrLatlngs;
    ArrayList<Float> arrDistance;
    ArrayList<String> arrUids;
    public static ArrayList<UsersClass> arrUsers;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationClient;
    Location addressLoc = new Location("address Location");
    CancellationTokenSource cancellationTokenSource;
    Task<Location> currentLocationTask;

    public static ArrayList<MatchClass> arrPassed;
    public static String userName, userCity, userAddress;
    public static int userDis = 0;
    public static UsersClass currentUser;
    public static Location currentLoc = new Location("current Location");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        closeMatchesLV = (ListView) findViewById(R.id.closeMatchesLV);
        invitesLV = (ListView) findViewById(R.id.invitesLV);
        userInvitesLV = (ListView) findViewById(R.id.userInvitesLV);
        btnReminder = (Button) findViewById(R.id.btnReminder);
        btnAddInvite = (Button) findViewById(R.id.btnAddInvite);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        btnAddInvite.setBackgroundColor(Color.TRANSPARENT);
        btnReminder.setBackgroundColor(Color.TRANSPARENT);

        arrInvites = new ArrayList<InviteClass>();
        userArrInvites = new ArrayList<InviteClass>();
        arrMatches = new ArrayList<MatchClass>();
        arrHistory = new ArrayList<MatchClass>();
        arrAddresses = new ArrayList<String>();
        arrPassed = new ArrayList<MatchClass>();
        arrLatlngs = new ArrayList<Double>();
        arrDistance = new ArrayList<Float>();
        arrUids = new ArrayList<String>();
        arrUsers = new ArrayList<UsersClass>();

        ReferencesFB.getUser(mAuth.getCurrentUser());
        user = new UsersClass();

        customAdapterInvites = new CustomAdapterInvites(MainActivity.this, arrInvites);
        invitesLV.setAdapter(customAdapterInvites);

        customAdapterUserInvites = new CustomAdapterUserInvites(MainActivity.this, userArrInvites);
        userInvitesLV.setAdapter(customAdapterUserInvites);

        customAdapterCM = new CustomAdapterCM(MainActivity.this, arrMatches);
        closeMatchesLV.setAdapter(customAdapterCM);

        userInvitesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        invitesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        closeMatchesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        userInvitesLV.setOnCreateContextMenuListener(this);
        invitesLV.setOnItemLongClickListener(this);
        closeMatchesLV.setOnItemLongClickListener(this);

        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("image download");
        pd.setMessage("loading...");
        pd.setCancelable(false);

        // Location permissions
        cancellationTokenSource = new CancellationTokenSource();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                if (!isGPSEnabled()) {
                    turnOnGPS();
                }
            }
        }

        // reading user information from database
        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()) {
                    DataSnapshot dS = tsk.getResult();
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        arrUsers.add(user);
                        if (user.getUid().equals(Uid)) {
                            userDis = user.getDistance() * 1000;
                            userName = user.getUserName();
                            userCity = user.getCity();
                            userAddress = user.getAddressName();
                            currentUser = user;
                        } else {
                            arrUids.add(user.getUid());
                            arrLatlngs.add(user.getAddLatitude());
                            arrLatlngs.add(user.getAddLongitude());
                        }
                    }
                } else {
                    Log.e("Firebase Error", "Error getting data", tsk.getException());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        counterDP = 0;

        // reading invites from database
        refInvites.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()) {
                    DataSnapshot dS = tsk.getResult();
                    arrInvites.clear();
                    userArrInvites.clear();
                    calNow = Calendar.getInstance();
                    for (DataSnapshot data : dS.getChildren()) {
                        for (DataSnapshot secChild : data.getChildren()) {
                            invite = secChild.getValue(InviteClass.class);
                            if (!passedDate(invite.getKey(), calNow)) {
                                if (Uid.equals(invite.getUid())) {
                                    userArrInvites.add(invite);
                                } else {
                                    arrInvites.add(invite);
                                }
                            } else {
                                refInvites.child(invite.getUid())
                                        .child(invite.getKey())
                                        .removeValue();
                            }
                        }
                    }
                    customAdapterUserInvites.notifyDataSetChanged();
                    customAdapterInvites.notifyDataSetChanged();

                    // sort invites by Location and distance
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    currentLocationTask = fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken());
                    currentLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            currentLoc.setLatitude(location.getLatitude());
                            currentLoc.setLongitude(location.getLongitude());
                            Log.i("main location", location.getLatitude()+"");
                            Log.i("main location", location.getLongitude()+"");
                            for (int i = 0; i < arrInvites.size(); i++) {
                                withinRange(currentLoc, arrLatlngs);
                            }
                            customAdapterInvites.notifyDataSetChanged();
                        }
                    });

                    for (int i = 0; i < arrInvites.size(); i++) {
                        if (currentUser.getLevel() == 1){
                            if (!arrInvites.get(i).isLevel1()){
                                arrInvites.remove(i);
                            }
                        } else if (currentUser.getLevel() == 2){
                            if (!arrInvites.get(i).isLevel2()){
                                arrInvites.remove(i);
                            }
                        } else if (currentUser.getLevel() == 3){
                            if (!arrInvites.get(i).isLevel3()){
                                arrInvites.remove(i);
                            }
                        } else if (currentUser.getLevel() == 4){
                            if (!arrInvites.get(i).isLevel4()){
                                arrInvites.remove(i);
                            }
                        }
                    }
                }
            }
        });

        // reading close matches from database
        refNotPlayed.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();
                    arrMatches.clear();
                    counterDP = 0;
                    calNow = Calendar.getInstance();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        for (DataSnapshot secChild : data.getChildren()) {
                            match = secChild.getValue(MatchClass.class);
                            if (!passedDate(match.getKey(), calNow)) {
                                if (Uid.equals(match.getUidInvited()))
                                    arrMatches.add(match);
                                if (Uid.equals(match.getUidInviter()))
                                    arrMatches.add(match);
                            } else {
                                if (Uid.equals(match.getUidInvited())){
                                    counterDP++;
                                    arrPassed.add(match);
                                }

                                if (Uid.equals(match.getUidInviter())){
                                    counterDP++;
                                    arrPassed.add(match);
                                }
                            }
                        }
                    }
                    customAdapterCM.notifyDataSetChanged();

                    //checks if close match date has passed
                    for (int i = 0; i < arrMatches.size(); i++){
                        if (passedDate(arrMatches.get(i).getKey(), calNow)) {
                            counterDP++;
                            arrPassed.add(arrMatches.get(i));
                            arrMatches.remove(i);
                        }
                    }
                    btnReminder.setText(""+counterDP);
                    customAdapterCM.notifyDataSetChanged();
                    pd.dismiss();
                }
            }
        });
    }

    /**
     * method Passed date boolean.
     *
     * this method check if invitation date is before current date.
     * @param dateString invitation date
     * @param calNow     current date
     *
     */
    public boolean passedDate(String dateString, Calendar calNow) {
        long end = calNow.getTimeInMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH:mm");
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
        if (TimeUnit.MILLISECONDS.toSeconds(end - start) > 0){
            return true;
        }
        return false;
    }

    /**
     * method Within range.
     *
     * this method check if invites are within range of user's distance choice.
     * @param currentLocation user's current location
     * @param arrLatlngs      an array of addresses by latitude and longitude.
     */
    public void withinRange(Location currentLocation, ArrayList<Double> arrLatlngs){
        for (int i = 0; i<arrLatlngs.size(); i+=2){
            addressLoc.setLatitude(arrLatlngs.get(i));
            addressLoc.setLongitude(arrLatlngs.get(i+1));
            Log.i("distance: ", currentLocation.distanceTo(addressLoc)+"");
            if (currentLocation.distanceTo(addressLoc) > userDis){
                Iterator<InviteClass> iterator = arrInvites.iterator();
                while (iterator.hasNext()) {
                    invite = iterator.next();
                    if (invite.getUid().equals(arrUids.get(i/2))) {
                        iterator.remove();
                    }
                }
            }
            else {
                arrDistance.add(currentLocation.distanceTo(addressLoc));
            }
        }
    }

    /**
     * method turn on GPS.
     *
     * this method check if phone's GPS is on, if not it turns it on.
     */
    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    /**
     * method is GPS enabled.
     *
     * this method check if permissions for GPS are given.
     */
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    /**
     * method request permissions result.
     *
     * this method check if location permissions are given.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if (!isGPSEnabled()) {
                            //getCurrentLocation();
                            turnOnGPS();
                        }
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /**
     * Add a game method.
     *
     * moves the user to Invitation Activity
     * @param view the view
     */
    public void addAGame(View view) {
        si = new Intent(this, InvitationActivity.class);
        startActivityForResult(si, REQUEST_CODE_INVITE);
    }

    /**
     * Remind.
     *
     * moves the user to Reminder Activity
     * @param view the view
     */
    public void remind(View view) {
        if (counterDP == 0){
            btnReminder.setText("0");
            Toast.makeText(MainActivity.this, "you don't have passed meetings", Toast.LENGTH_LONG).show();
        }
        else {
            Intent si = new Intent(this, ReminderActivity.class);
            startActivityForResult(si, REQUEST_CODE_REMINDER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // shows the number of passed matches.
        if (requestCode == REQUEST_CODE_REMINDER  && resultCode  == RESULT_OK) {
            counterDP--;
            btnReminder.setText(""+counterDP);
        }

        //checks if the invitation has been done
        if (requestCode == REQUEST_CODE_INVITE && resultCode == RESULT_OK){

            // reading invites from database again after an new invitation is added
            refInvites.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> tsk) {
                    if (tsk.isSuccessful()){
                        DataSnapshot dS = tsk.getResult();
                        arrInvites.clear();
                        userArrInvites.clear();
                        for (DataSnapshot data : dS.getChildren()){
                            for (DataSnapshot secChild : data.getChildren()) {
                                invite = secChild.getValue(InviteClass.class);
                                if (Uid.equals(secChild.getValue(InviteClass.class).getUid())){
                                    userArrInvites.add(invite);
                                }
                                else {
                                    withinRange(currentLoc, arrLatlngs);
                                    if (arrUids.contains(invite.getUid())){
                                        arrInvites.add(invite);
                                    }
                                }
                            }
                        }
                        customAdapterUserInvites.notifyDataSetChanged();
                        customAdapterInvites.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //gives the user an option to join an invitation
        if (parent.getId() == R.id.invitesLV) {
            adb = new AlertDialog.Builder(this);
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

        // saves the chosen item position so that the user can delete his invitation in context menu
        if (parent.getId() == R.id.userInvitesLV) {
            pos = position;

        }

        //gives the user an option to delete a close match.
        if (parent.getId() == R.id.closeMatchesLV) {
            adb = new AlertDialog.Builder(this);
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
            }
        return true;
    }

    // disables the back button
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Actions");
        menu.add("Delete");
        menu.add("Edit");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String i = item.getTitle().toString();
        if (i.equals("Delete")) {
            refInvites.child(Uid).child(userArrInvites.get(pos).getKey()).removeValue();
            userArrInvites.remove(pos);
            customAdapterUserInvites.notifyDataSetChanged();
        }
        if (i.equals("Edit")) {
            Intent si = new Intent(MainActivity.this, InvitationActivity.class);
            si.putExtra("uidEditKey", userArrInvites.get(pos).getKey());
            startActivity(si);
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
                    MainActivity.this.startActivity(new Intent(MainActivity.this, OpeningActivity.class));
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