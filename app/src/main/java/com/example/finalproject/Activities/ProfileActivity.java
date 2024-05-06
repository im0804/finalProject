package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.LoginActivity.Uid;
import static com.example.finalproject.RegisterActivity.user;
import static com.example.finalproject.Adapters.CustomAdapterHistory.score;
import static com.example.finalproject.ReferencesFB.*;
//import static com.example.finalproject.RegisterActivity.imagesRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Adapters.CustomAdapterHistory;
import com.example.finalproject.Objs.InviteClass;
import com.example.finalproject.Objs.MatchClass;
import com.example.finalproject.R;
import com.example.finalproject.RegisterActivity;
import com.example.finalproject.Objs.UsersClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.CheckedInputStream;

/**
 * @author		inbar menahem
 * @version	    1
 * @since		24/12/2023
 * the profile activity of the user.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    TextView fullNameTV, userNameTV, distanceTV, ageTV, cityTV,genderTV, yearsOfPlayTV, yearsOfCoachingTV, coachTypeTV, coachDesTV, titleTV;
    ListView historyMatchesLV;
    ImageView pfpIV;
    LinearLayout coachLayout;
    Intent si;
    MatchClass history;
    StorageReference imageRef;
    ArrayList<MatchClass> arrHistory;
    CustomAdapterHistory historyCA;
    AlertDialog.Builder adb;

    public static String uid;



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
        coachLayout.setVisibility(coachLayout.GONE);
        titleTV = (TextView) findViewById(R.id.titleTV);

        arrHistory = new ArrayList<MatchClass>();
        historyCA = new CustomAdapterHistory(this, arrHistory);
        historyMatchesLV.setAdapter(historyCA);
        historyMatchesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        historyMatchesLV.setOnItemLongClickListener(this);

        //Uid = userFB.getUid();

    }

    @Override
    protected void onStart() {
        super.onStart();
        userProfile(Uid);

    }

    ValueEventListener velHistory = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            arrHistory.clear();
            for (DataSnapshot data : snapshot.getChildren()){
                for (DataSnapshot secChild : data.getChildren()) {
                    history = secChild.getValue(MatchClass.class);
                    if(history.getUidInviter().equals(uid)){
                        arrHistory.add(history);
                    }
                    else if (history.getUidInvited().equals(uid)){
                        arrHistory.add(history);
                    }
                }
            }
            historyCA.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void edit(View view) {
        Intent si = new Intent(this, RegisterActivity.class);
        si.putExtra("from profile", 1);
        startActivity(si);
    }

    /**
     * show photo method
     * <p>
     *
     * @param	-
     * @return	download the uploaded photo from firebase.
     */
    public void showPhoto(String uid) throws IOException {
        //final ProgressDialog pd = ProgressDialog.show(this, "Image download", "downloading...", true);

        final File localFile = File.createTempFile(uid,"jpg");
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //pd.dismiss();
                Toast.makeText(ProfileActivity.this, "Image download success", Toast.LENGTH_LONG).show();
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                pfpIV.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //pd.dismiss();
                Toast.makeText(ProfileActivity.this, "Image download failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
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
    public void userProfile(String id){
        uid = id;
        imageRef = imagesRef.child(uid);
        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                if (tsk.isSuccessful()) {
                    DataSnapshot dS = tsk.getResult();
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        if (user.getUid().equals(uid)) {
                            fullNameTV.setText("full name: " + '\n' + user.getFullName());
                            userNameTV.setText("user name: " + '\n' + user.getUserName());
                            distanceTV.setText("distance: " + '\n' + user.getDistance());
                            ageTV.setText("age: " + user.getAge());
                            genderTV.setText("gender: " + user.getGender());
                            cityTV.setText("city: " + user.getCity());
                            yearsOfPlayTV.setText("years of coaching: " + '\n' + user.getYearsOfPlay());
                            //boolean isCoach = user.getIsCoach();
                            try {
                                showPhoto(uid);
                            } catch (IOException e) {
                                Toast.makeText(ProfileActivity.this, "Image failed", Toast.LENGTH_LONG).show();
                            }
                            if (user.getIsCoach()) {
                                coachLayout.setVisibility(coachLayout.VISIBLE);
                                coachTypeTV.setText("coach type: " + '\n' + user.getUserCoach().getCoachType());
                                coachDesTV.setText("coach description: " + '\n' + user.getUserCoach().getDescription());
                                yearsOfCoachingTV.setText("years of coaching: " + '\n' + user.getUserCoach().getYearsOfCoaching());
                            }
                            titleTV.setText(user.getUserName());
                        }
                    }
                }
            }
        });
        refPlayed.addValueEventListener(velHistory);
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
            Intent si = new Intent(this, JoinAsCoachActivity.class);
            startActivity(si);
        }
        else if (str.equals("login")) {
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