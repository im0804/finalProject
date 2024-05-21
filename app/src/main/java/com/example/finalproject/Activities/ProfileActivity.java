package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.MainActivity.currentUser;
import static com.example.finalproject.ReferencesFB.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.finalproject.R;
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
public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private TextView fullNameTV, userNameTV, distanceTV, ageTV, cityTV,genderTV, yearsOfPlayTV, yearsOfCoachingTV, coachTypeTV, coachDesTV, titleTV;
    private ListView historyMatchesLV;
    private ImageView pfpIV;
    private LinearLayout coachLayout, layout;
    private Button editBTN;

    MatchClass history;

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
        coachDesTV = (TextView) findViewById(R.id.coachDesTV);
        historyMatchesLV = (ListView) findViewById(R.id.historyMatchesLV);
        pfpIV = (ImageView) findViewById(R.id.pfpIV);
        coachLayout = (LinearLayout) findViewById(R.id.coachLayout);
        layout = (LinearLayout) findViewById(R.id.allLayout);
        coachLayout.setVisibility(coachLayout.GONE);
        titleTV = (TextView) findViewById(R.id.titleTV);
        editBTN = (Button) findViewById(R.id.editBTN);
        editBTN.setBackgroundColor(Color.TRANSPARENT);

        arrHistory = new ArrayList<MatchClass>();
        historyCA = new CustomAdapterHistory(this, arrHistory);
        historyMatchesLV.setAdapter(historyCA);
        historyMatchesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        historyMatchesLV.setOnItemLongClickListener(this);

        pd = new ProgressDialog(ProfileActivity.this);
        pd.setTitle("image download");
        pd.setMessage("loading...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userProfile(Uid);

    }

    /**
     * On Click method Edit.
     *
     * this method moves the user to Profile Activity to change his details.
     * @param view the view
     */
    public void edit(View view) {
        Intent si = new Intent(this, RegisterActivity.class);
        si.putExtra("from profile", 1);
        startActivity(si);
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
                Toast.makeText(ProfileActivity.this, "Image download failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        // changing the profile to the chosen user' profile
        if (arrHistory.get(position).getUidInviter().equals(Uid)){
            adb.setTitle(arrHistory.get(position).getUserNameInvited() + "'s profile");
            adb.setMessage("do you want to see "+ arrHistory.get(position).getUserNameInvited() + "'s profile?");
            adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userProfile(arrHistory.get(position).getUidInvited());
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
                    editBTN.setVisibility(View.GONE);
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
        return true;
    }

    /**
     * User profile method .
     * this method update all fields with user's information
     *
     * @param id chosen user Uid
     */
    public void userProfile(String id){
        imageRef = imagesRef.child(id);

        fullNameTV.setText("full name: " + '\n' + currentUser.getFullName());
        userNameTV.setText("user name: " + '\n' + currentUser.getUserName());
        distanceTV.setText("distance: " + '\n' + currentUser.getDistance());
        ageTV.setText("age: " + currentUser.getAge());
        genderTV.setText("gender: " + currentUser.getGender());
        cityTV.setText("city: " + currentUser.getCity());
        yearsOfPlayTV.setText("years of playing: " + '\n' + currentUser.getYearsOfPlay());
        try {
            showPhoto();
        } catch (IOException e) {
            Toast.makeText(ProfileActivity.this, "Image failed", Toast.LENGTH_LONG).show();
        }
        if (currentUser.isCoach()) {
            layout.setBackgroundResource(R.drawable.profilecoach);
            coachLayout.setVisibility(coachLayout.VISIBLE);
            coachTypeTV.setText("coach type: " + '\n' + currentUser.getUserCoach().getCoachType());
            coachDesTV.setText("coach description: " + '\n' + currentUser.getUserCoach().getDescription());
            yearsOfCoachingTV.setText("years of coaching: " + '\n' + currentUser.getUserCoach().getYearsOfCoaching());
        }
        titleTV.setText(currentUser.getUserName());
        history(id);
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
        return super.onOptionsItemSelected(item);
    }
}