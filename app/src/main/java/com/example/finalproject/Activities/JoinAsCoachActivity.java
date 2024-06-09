package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.MainActivity.currentUser;
import static com.example.finalproject.ReferencesFB.Uid;
import static com.example.finalproject.ReferencesFB.refInvites;
import static com.example.finalproject.ReferencesFB.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.finalproject.Objs.CoachUserClass;
import com.example.finalproject.Objs.InviteClass;
import com.example.finalproject.R;
import com.example.finalproject.Objs.UsersClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Join As Coach activity.
 *
 * here the user can join to the coaches community
 */
public class JoinAsCoachActivity extends AppCompatActivity {
    private EditText yearsOfCoachingEt;
    private RadioButton begCRB, groupsRB, competitiveRB, allRB;
    private Button btnFinish;

    String coachType;

    UsersClass user;
    CoachUserClass userCoach;
    Intent profileGI;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_as_coach);

        yearsOfCoachingEt = (EditText) findViewById(R.id.yearsOfCoachingET);
        begCRB = (RadioButton) findViewById(R.id.begCRB);
        groupsRB = (RadioButton) findViewById(R.id.groupsRB);
        competitiveRB = (RadioButton) findViewById(R.id.competitiveRB);
        allRB = (RadioButton) findViewById(R.id.allRB);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        profileGI = getIntent();
        pd = new ProgressDialog(JoinAsCoachActivity.this);
        pd.setTitle("downloading info");
        pd.setMessage("loading...");
        pd.setCancelable(false);
        pd.show();

        btnFinish.setBackgroundColor(Color.TRANSPARENT);
        // checks if the user came from edit coach details in profile or from coach activity
        if (profileGI != null) {
            if (profileGI.getIntExtra("from profile", -1) == 1){
                refUsers.child(Uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> tsk) {
                        if (tsk.isSuccessful()) {
                            DataSnapshot dS = tsk.getResult();
                            user = dS.getValue(UsersClass.class);
                            yearsOfCoachingEt.setText(user.getUserCoach().getYearsOfCoaching()+"");
                            if (user.getUserCoach().getCoachType().equals("groups")) {
                                groupsRB.setChecked(true);
                            } else if (user.getUserCoach().getCoachType().equals("beginners")) {
                                begCRB.setChecked(true);
                            } else if (user.getUserCoach().getCoachType().equals("competitive")) {
                                competitiveRB.setChecked(true);
                            } else if (user.getUserCoach().getCoachType().equals("all levels")) {
                                allRB.setChecked(true);
                            }
                        pd.dismiss();
                        }
                    }
                });
            }
        }
        pd.dismiss();
    }


    /**
     * On Click method Finish
     * this method update the user's object and upload them to firebase.
     * after finishing, the user moves to Main Activity.
     *
     * @param view the view
     */
    public void finishBTN(View view) {
        if ((yearsOfCoachingEt.getText().toString().equals(""))){
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
            currentUser.setCoach(true);
            userCoach = new CoachUserClass(Integer.parseInt(yearsOfCoachingEt.getText().toString()), coachType);
            currentUser = new UsersClass(currentUser, userCoach);
            refUsers.child(Uid).setValue(currentUser);

            Intent intent = new Intent(JoinAsCoachActivity.this, MainActivity.class);
            startActivity(intent);
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