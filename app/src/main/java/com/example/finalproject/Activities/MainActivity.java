package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.LoginActivity.Uid;
import static com.example.finalproject.ReferencesFB.REQUEST_CODE_INVITE;
import static com.example.finalproject.ReferencesFB.REQUEST_CODE_REMINDER;
import static com.example.finalproject.ReferencesFB.refInvites;
import static com.example.finalproject.ReferencesFB.refNotPlayed;
import static com.example.finalproject.ReferencesFB.refUsers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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
import com.example.finalproject.RegisterActivity;
import com.example.finalproject.Objs.UsersClass;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
    ArrayList<Double> arrLatlngs;
    ArrayList<String> arrUids;
    public static ArrayList<MatchClass> arrPassed;
    public static String userName, userCity, userAddress;
    public static int userDis = 0;
    Button btnReminder;

    AlertDialog.Builder adb;

    LocationRequest locationRequest;
    double distance;
    int pos,counterDP = 0;
    boolean clicked = false;

    Calendar calNow;
    ProgressDialog pd;
    Location currentLoc = new Location("current Location");
    Location addressLoc = new Location("address Location");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity","Activity started");

        //friendsSearchLV = (ListView) findViewById(R.id.friendsSearchLV);
        closeMatchesLV = (ListView) findViewById(R.id.closeMatchesLV);
        invitesLV = (ListView) findViewById(R.id.invitesLV);
        userInvitesLV = (ListView) findViewById(R.id.userInvitesLV);
        btnReminder = (Button) findViewById(R.id.btnReminder);

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
        arrPassed = new ArrayList<MatchClass>();
        arrLatlngs = new ArrayList<Double>();
        arrUids = new ArrayList<String>();

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

        refNotPlayed.addValueEventListener(velCM);

    }

    @Override
    protected void onStart() {
        super.onStart();
        counterDP = 0;
        pd = ProgressDialog.show(this,"downloading data","downloading... \n it might take a minute",true);

        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()){
                    DataSnapshot dS = tsk.getResult();
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        if (user.getUid().equals(Uid)){
                            userDis = user.getDistance() * 1000;
                            userName = user.getUserName();
                            userCity = user.getCity();
                            userAddress = user.getAddressName();
                        }
                        else {
                            arrUids.add(user.getUid());
                            arrLatlngs.add(user.getAddLatitude());
                            arrLatlngs.add(user.getAddLongitude());
                        }
                    }
                }
                else {
                    Log.e("Firebase Error", "Error getting data", tsk.getException());
                }
            }
        });

        getCurrentLocation();
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
                                    withinRange(currentLoc, arrLatlngs);
                                    if (arrUids.contains(invite.getUid())) {
                                        arrInvites.add(invite);
                                    }
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
        pd.dismiss();
    }

    ValueEventListener velCM = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd = ProgressDialog.show(MainActivity.this,"downloading data","downloading... \n it might take a minute",true);
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
                    } else{
                        counterDP++;
                        arrPassed.add(match);
                    }
                }
            }
            customAdapterCM.notifyDataSetChanged();
            btnReminder.setText(""+counterDP);
            pd.dismiss();
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
        if (TimeUnit.MILLISECONDS.toSeconds(end - start) > 0){
            return true;
        }
        return false;
    }

    public void withinRange(Location currentLocation, ArrayList<Double> arrLatlngs){
        for (int i = 0; i<arrLatlngs.size(); i+=2){
            addressLoc.setLatitude(arrLatlngs.get(i));
            addressLoc.setLongitude(arrLatlngs.get(i+1));
            if (currentLocation.distanceTo(addressLoc) > distance){
                arrUids.remove(i);
            }
        }
    }

    public void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() >0){
                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        currentLoc.setLongitude(longitude);
                                        currentLoc.setLatitude(latitude);
                                    }
                                }
                            }, Looper.getMainLooper());
                }
                else {
                    turnOnGPS();
                }
            }
            else {
                requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

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

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

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
     * @param view the view
     * @return opens the invitation activity.
     */
    public void addAGame(View view) {
        si = new Intent(this, InvitationActivity.class);
        startActivityForResult(si, REQUEST_CODE_INVITE);
    }

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

        if (requestCode == REQUEST_CODE_REMINDER  && resultCode  == RESULT_OK) {
            counterDP--;
            btnReminder.setText(""+counterDP);
        }

        if (requestCode == REQUEST_CODE_INVITE && resultCode == RESULT_OK){
            refInvites.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> tsk) {
                    pd = ProgressDialog.show(MainActivity.this,"downloading data","downloading... \n it might take a minute",true);
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
                    pd.dismiss();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        refNotPlayed.removeEventListener(velCM);}

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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

        if (parent.getId() == R.id.userInvitesLV) {
            pos = position;

        }

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