package com.example.finalproject;

import static com.example.finalproject.LoginActivity.userFB;
import static com.example.finalproject.ReferencesFB.*;
//import static com.example.finalproject.RegisterActivity.imagesRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
/**
 * @author		inbar menahem
 * @version	    1
 * @since		24/12/2023
 * the profile activity of the user.
 */

public class ProfileActivity extends AppCompatActivity {
    TextView fullNameTV, userNameTV, distanceTV, ageTV, cityTV,genderTV, yearsOfPlayTV, yearsOfCoachingTV, coachTypeTV, coachDesTV, titleTV;
    ListView historyMatchesLV;
    ImageView pfpIV;
    LinearLayout coachLayout;
    Intent si;
    String Uid;
    UsersClass user;

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
        coachLayout.setVisibility(coachLayout.GONE);
        titleTV = (TextView) findViewById(R.id.titleTV);

        Uid = userFB.getUid();
        imageRef = imagesRef.child(Uid);
        Query query = refUsers
                .orderByChild("uid")
                .equalTo(Uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if (dS.exists()) {
                    for(DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        fullNameTV.setText("full name: "+'\n'+user.getFullName());
                        userNameTV.setText("user name: "+'\n'+user.getUserName());
                        distanceTV.setText("distance: "+'\n'+user.getDistance());
                        ageTV.setText("age: "+user.getAge());
                        genderTV.setText("gender: "+user.getGender());
                        cityTV.setText("city: "+user.getCity());
                        yearsOfPlayTV.setText("years of coaching: "+'\n'+user.getYearsOfPlay());
                        //boolean isCoach = user.getIsCoach();
                        try {
                            showPhoto();
                        }
                        catch (IOException e) {
                            Toast.makeText(ProfileActivity.this, "Image failed", Toast.LENGTH_LONG).show();
                        }
                        if (user.isCoach)
                        {
                            coachLayout.setVisibility(coachLayout.VISIBLE);
                            coachTypeTV.setText("coach type: "+'\n'+user.userCoach.coachType);
                            coachDesTV.setText("coach description: "+'\n'+user.userCoach.description);
                            yearsOfCoachingTV.setText("years of coaching: "+'\n'+user.userCoach.getYearsOfCoaching());
                        }
                        titleTV.setText(user.getUserName());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "on cancelled" , Toast.LENGTH_LONG).show();
            }
        });

    }

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
    public void showPhoto() throws IOException {
        //final ProgressDialog pd = ProgressDialog.show(this, "Image download", "downloading...", true);

        final File localFile = File.createTempFile(Uid,"jpg");
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