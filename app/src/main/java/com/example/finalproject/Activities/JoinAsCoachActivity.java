package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.LoginActivity.Uid;
import static com.example.finalproject.Activities.LoginActivity.userFB;
import static com.example.finalproject.RegisterActivity.user;
import static com.example.finalproject.ReferencesFB.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.finalproject.Objs.CoachUserClass;
import com.example.finalproject.R;
import com.example.finalproject.RegisterActivity;
import com.example.finalproject.Objs.UsersClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * @author		inbar menahem
 * @version	    1
 * @since		25/12/2023
 * activity for new coaches.
 */
public class JoinAsCoachActivity extends AppCompatActivity {
    // להוסיף בדיקה לרדיו בטן לבדוק על המספר עליו לחץ
    EditText yearsOfCoachingEt, desET;
    RadioButton begCRB, groupsRB, competitiveRB, allRB;
    String coachType;
    CoachUserClass userCoach;
    //UsersClass newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_as_coach);

        yearsOfCoachingEt = (EditText) findViewById(R.id.yearsOfCoachingET);
        desET = (EditText) findViewById(R.id.desEt);
        begCRB = (RadioButton) findViewById(R.id.begCRB);
        groupsRB = (RadioButton) findViewById(R.id.groupsRB);
        competitiveRB = (RadioButton) findViewById(R.id.competitiveRB);
        allRB = (RadioButton) findViewById(R.id.allRB);

        Query query = refUsers
                .orderByChild("uid")
                .equalTo(Uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if (dS.exists()) {
                    for(DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(JoinAsCoachActivity.this, "on cancelled" , Toast.LENGTH_LONG).show();
            }
        });


    }

    /**
     * event listener method
     * <p>
     *
     * @return	checks for changes and set the text in the texr views.
     */


    /**
     * on click finish method
     * <p>
     *
     * @param    view the view
     * @return   update the user's attributes and upload them to firebase.
     */
    public void finishBTN(View view) {
        if ((yearsOfCoachingEt.getText().toString().equals("")) || (desET.getText().toString().equals(""))){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        }
        else {
            if (groupsRB.isChecked()){
                coachType = "groups";
            } else if (begCRB.isChecked()) {
                coachType = "beginners";
            } else if (competitiveRB.isChecked()) {
                coachType = "competitive";
            } else if (allRB.isChecked()) {
                coachType = "all levels";
            }
            user.setIsCoach(true);
            userCoach = new CoachUserClass(Integer.parseInt(yearsOfCoachingEt.getText().toString()), coachType, desET.getText().toString());
            user = new UsersClass(user, userCoach);
            refUsers.child(Uid).setValue(user);

            finish();
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
        }
        else if (str.equals("coachAct")) {
            Intent si = new Intent(this, CoachActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }


}