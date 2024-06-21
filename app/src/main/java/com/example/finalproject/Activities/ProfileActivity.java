package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.CoachActivity.cameFromCoach;
import static com.example.finalproject.Activities.MainActivity.arrUsers;
import static com.example.finalproject.Activities.MainActivity.currentUser;
import static com.example.finalproject.ReferencesFB.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Adapters.CustomAdapterHistory;
import com.example.finalproject.Objs.MatchClass;
import com.example.finalproject.Objs.UsersClass;
import com.example.finalproject.R;
import com.example.finalproject.ReferencesFB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The type Profile activity.
 *
 * @author inbar menahem
 * @version 1
 * @since 24 /12/2023 the profile activity of the user.
 */
public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private TextView fullNameTV, userNameTV, distanceTV, ageTV, cityTV,genderTV, yearsOfPlayTV, yearsOfCoachingTV, coachTypeTV, coachDesTV, titleTV;
    private ListView historyMatchesLV;
    private ImageView pfpIV;
    private LinearLayout coachLayout, layout;
    private Button editBTN, editCoachBTN;

    MatchClass history;
    Intent gi;

    ArrayList<MatchClass> arrHistory;
    CustomAdapterHistory historyCA;

    AlertDialog.Builder adb;
    ProgressDialog pd;

    StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullNameTV = (TextView) findViewById(R.id.fullNameTV);
        userNameTV = (TextView) findViewById(R.id.userNameTV);
        distanceTV = (TextView) findViewById(R.id.distanceTV);
        ageTV = (TextView) findViewById(R.id.ageTV);
        genderTV = (TextView) findViewById(R.id.genderTV);
        cityTV = (TextView) findViewById(R.id.cityTV);
        yearsOfPlayTV = (TextView) findViewById(R.id.yearsOfPlayTV);
        yearsOfCoachingTV = (TextView) findViewById(R.id.yearsOfCoachingTV);
        coachTypeTV = (TextView) findViewById(R.id.coachTypeTV);
        historyMatchesLV = (ListView) findViewById(R.id.historyMatchesLV);
        pfpIV = (ImageView) findViewById(R.id.pfpIV);
        coachLayout = (LinearLayout) findViewById(R.id.coachLayout);
        layout = (LinearLayout) findViewById(R.id.allLayout);
        coachLayout.setVisibility(coachLayout.GONE);
        titleTV = (TextView) findViewById(R.id.titleTV);
        editCoachBTN = (Button) findViewById(R.id.editCoach);
        editCoachBTN.setBackgroundColor(Color.rgb(193,219,180));
        editBTN = (Button) findViewById(R.id.editBTN);
        editBTN.setBackgroundColor(Color.TRANSPARENT);

        arrHistory = new ArrayList<MatchClass>();
        historyCA = new CustomAdapterHistory(this, arrHistory);
        historyMatchesLV.setAdapter(historyCA);
        historyMatchesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        pd = new ProgressDialog(ProfileActivity.this);
        pd.setTitle("image download");
        pd.setMessage("loading...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //checks if the user came from coach activity to see other coaches profile
        if (cameFromCoach){
            historyMatchesLV.setOnItemClickListener(null);
            editCoachBTN.setVisibility(View.INVISIBLE);
            editBTN.setVisibility(View.INVISIBLE);
            gi = getIntent();
            Uid = gi.getStringExtra("coachUid");
            cameFromCoach = false;
            userProfile(Uid);
        }
        else {
            historyMatchesLV.setOnItemClickListener(this);
            ReferencesFB.getUser(mAuth.getCurrentUser());
            userProfile(Uid);
        }
    }

    /**
     * On Click method Edit.
     *
     * this method moves the user to Register Activity to change his details.
     * @param view the view
     */
    public void edit(View view) {
        Intent si = new Intent(this, RegisterActivity.class);
        si.putExtra("from profile", 1);
        startActivity(si);
    }

    /**
     * On Click method coach edit.
     *
     * this method moves the user to JoinAsCoach Activity to change his details.
     * @param view the view
     */
    public void editCoach(View view) {
        Intent si = new Intent(this, JoinAsCoachActivity.class);
        si.putExtra("from profile", 1);
        startActivityForResult(si, -1);
    }

    /**
     * show photo method
     * this method downloads the uploaded photo from Firebase Storage and shows it to the user.
     *
     * @throws IOException the io exception
     */
    public void showPhoto() throws IOException {
        final File localFile = File.createTempFile(Uid,"jpg");
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                pfpIV.setImageBitmap(bitmap);
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                pfpIV.setImageResource(R.drawable.pfp);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        // changing the profile to the chosen user' profile
        if (arrHistory.get(position).getUidInviter().equals(Uid)){
            adb.setTitle(arrHistory.get(position).getUserNameInvited() + "'s profile");
            adb.setMessage("do you want to see "+ arrHistory.get(position).getUserNameInvited() + "'s profile?");
            adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            adb.setNegativeButton("no", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        else {
            adb.setTitle(arrHistory.get(position).getUserNameInviter() + "'s profile");
            adb.setMessage("do you want to see "+ arrHistory.get(position).getUserNameInviter() + "'s profile?");
            adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userProfile(arrHistory.get(position).getUidInviter());
                    editBTN.setVisibility(View.INVISIBLE);
                    editCoachBTN.setVisibility(View.INVISIBLE);
                    dialog.cancel();
                }
            });
            adb.setNegativeButton("no", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * User profile method .
     * this method update all fields with user's information
     *
     * @param id chosen user Uid
     */
    public void userProfile(String id){
        imageRef = imagesRef.child(id);

        for (UsersClass user : arrUsers) {
            if (user.getUid().equals(id)) {
                fullNameTV.setText("full name: " + '\n' + user.getFullName());
                userNameTV.setText("user name: " + '\n' + user.getUserName());
                distanceTV.setText("distance: " + '\n' + user.getDistance());
                ageTV.setText("age: " + user.getAge());
                genderTV.setText("gender: " + user.getGender());
                cityTV.setText("city: " + user.getCity());
                yearsOfPlayTV.setText("years of playing: " + '\n' + user.getYearsOfPlay());
                try {
                    showPhoto();
                } catch (IOException e) {
                    Toast.makeText(ProfileActivity.this, "Image failed", Toast.LENGTH_LONG).show();
                }
                if (user.isCoach()) {
                    layout.setBackgroundResource(R.drawable.coachprofile);
                    coachLayout.setVisibility(coachLayout.VISIBLE);
                    coachTypeTV.setText("coach type: " + '\n' + user.getUserCoach().getCoachType());
                    yearsOfCoachingTV.setText("years of coaching: " + '\n' + user.getUserCoach().getYearsOfCoaching());
                }
                titleTV.setText(user.getUserName());
                history(id);
            }
        }
    }

    /**
     * History method.
     * this method get from database the matches history of the player
     *
     * @param id chosen user Uid
     */
    public void history(String id){
        refPlayed.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()) {
                    DataSnapshot dS = tsk.getResult();
                    for (DataSnapshot data : dS.getChildren()) {
                        for (DataSnapshot secChild : data.getChildren()) {
                            history = secChild.getValue(MatchClass.class);
                            if(history.getUidInviter().equals(id)){
                                arrHistory.add(history);
                            }
                            else if (history.getUidInvited().equals(id)){
                                arrHistory.add(history);
                            }
                        }
                    }
                    historyCA.notifyDataSetChanged();
                }
            }
        });
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
                    ProfileActivity.this.startActivity(new Intent(ProfileActivity.this, OpeningActivity.class));
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