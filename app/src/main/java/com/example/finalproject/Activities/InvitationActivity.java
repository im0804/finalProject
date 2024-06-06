package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.MainActivity.userAddress;
import static com.example.finalproject.Activities.MainActivity.userCity;
import static com.example.finalproject.Activities.MainActivity.userName;
import static com.example.finalproject.ReferencesFB.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.finalproject.Objs.InviteClass;
import com.example.finalproject.R;
import com.example.finalproject.Objs.UsersClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Invitation activity.
 * here the user create an invitation
 */
public class InvitationActivity extends AppCompatActivity {
    private RadioButton RB1, RB2, RB3, RB4;
    private Button startBTN, createBTN;
    private TextView clearTV, dateTV;

    int year, month, day;
    boolean level1, level2, level3, level4;
    boolean dateChoose = false, validTime = true, isSameDay = false;
    String timeFormatStart, dateFormat, dateFrormatFB, key;

    InviteClass ic;
    Calendar calNow, calSet;
    Intent gi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        RB1 = (RadioButton) findViewById(R.id.RB1);
        RB2 = (RadioButton) findViewById(R.id.RB2);
        RB3 = (RadioButton) findViewById(R.id.RB3);
        RB4 = (RadioButton) findViewById(R.id.RB4);
        startBTN = (Button) findViewById(R.id.startTimeBTN);
        createBTN = (Button) findViewById(R.id.createBTN);
        clearTV = (TextView) findViewById(R.id.clearTV);
        dateTV = (TextView) findViewById(R.id.dateTV);

        startBTN.setBackgroundColor(Color.TRANSPARENT);
        startBTN.setClickable(false);
        createBTN.setBackgroundColor(Color.TRANSPARENT);

        calNow = Calendar.getInstance();
        gi = getIntent();

        //initialize first look of activity layout
        SpannableString ss = new SpannableString("clear");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                RB1.setChecked(false);
                RB2.setChecked(false);
                RB3.setChecked(false);
                RB4.setChecked(false);
            }
        };
        ss.setSpan(span, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        clearTV.setText(ss);
        clearTV.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    protected void onStart() {
        // adding click listener for date TextView
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        //checking if the user came from edit invitation or '+' button
        if (gi.getStringExtra("uidEditKey") != null) {
            ProgressDialog pd = ProgressDialog.show(this, "invite info", "downloading...");
            refInvites.child(Uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> tsk) {
                    if (tsk.isSuccessful()) {
                        DataSnapshot dS = tsk.getResult();
                        for (DataSnapshot data : dS.getChildren()) {
                            ic = data.getValue(InviteClass.class);
                            startBTN.setText(ic.getStartTime());
                            if (ic.isLevel1())
                                RB1.setChecked(true);
                            if (ic.isLevel2())
                                RB2.setChecked(true);
                            if (ic.isLevel3())
                                RB3.setChecked(true);
                            if (ic.isLevel4())
                                RB4.setChecked(true);
                            dateTV.setText(ic.getDate());
                            dateTV.setEnabled(false);
                        }
                        pd.dismiss();
                    }
                }
            });
        }
        super.onStart();
    }

    private void showDatePickerDialog() {
        //opens a date picker dialog and re-setting time
        startBTN.setClickable(false);
        startBTN.setText("Start Time");
        validTime = false;
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog with a minimum date set to today's date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Check if the selected date is not in the past
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);
                        Calendar currentDate = Calendar.getInstance();
                        if (selectedDate.before(currentDate)) {
                            // Show a toast indicating that the selected date is invalid
                            Toast.makeText(InvitationActivity.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                        } else if (selectedDate.equals(currentDate)) {
                            isSameDay = true;
                            dateFormat = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            dateFrormatFB = String.format("%04d%02d%02d", year, monthOfYear+1, dayOfMonth);;
                            dateChoose = true;
                            startBTN.setClickable(true);
                            dateTV.setText(dateFormat);
                            startBTN.setText("start time");
                            } else {
                                // saving the chosen date
                                dateFormat = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dateFrormatFB = String.format("%04d%02d%02d", year, monthOfYear+1, dayOfMonth);
                                dateChoose = true;
                                startBTN.setClickable(true);
                                dateTV.setText(dateFormat);
                                startBTN.setText("start time");
                                isSameDay = false;
                            }
                    }
                }, year, month, day);

        // Set the minimum date to today's date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    /**
     * On Click method Create btn.
     *
     * this method checks if all the fields are full and valid
     * it saves all the information into an object and into the database
     * after it's done, the user returns to Main Activity
     * @param view the view
     */
    public void createBtn(View view) {
        //checks fields validity
        if (dateChoose && validTime) {
            level1 = RB1.isChecked();
            level2 = RB2.isChecked();
            level3 = RB3.isChecked();
            level4 = RB4.isChecked();
            if (level1 || level2 || level3 || level4){
                if (gi.getStringExtra("uidEditKey") != null){
                    key = gi.getStringExtra("uidEditKey");
                    ic.setStartTime(timeFormatStart);
                    ic.setLevel1(level1);
                    ic.setLevel2(level2);
                    ic.setLevel3(level3);
                    ic.setLevel4(level4);
                    setResult(RESULT_OK, gi);
                }
                else {
                    key = dateFrormatFB + timeFormatStart;
                    ic = new InviteClass(Uid, userName, userAddress, userCity, dateFormat, timeFormatStart, key,
                            level1, level2, level3, level4);
                }
                refInvites.child(Uid).child(key).setValue(ic);
                finish();
            }
            else Toast.makeText(this, "please click level", Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(this, "please check that all fields are right", Toast.LENGTH_LONG).show();
    }

    /**
     * On Click method Start btn.
     *
     * this method opens a clock so that the user can choose a time for the invitation
     * @param view the view
     */
    public void startBTN(View view) {
        if (dateChoose){
            openTimePickerDialog(true);
        }
        else{
            Toast.makeText(InvitationActivity.this, "please choose a date before choosing the time", Toast.LENGTH_LONG).show();
        }
    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Choose time");
        timePickerDialog.show();
    }

    /**
     * On time set listener.
     */
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        /**
         * onTimeSet method
         * <p> Return the time of day picked by the user
         * </p>
         *
         * @param view the time picker view that triggered the method
         * @param hourOfDay the hour the user picked
         * @param minute the minute the user picked
         */
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.YEAR, year);
            calSet.set(Calendar.MONTH, month);
            calSet.set(Calendar.DAY_OF_MONTH, day);
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            // checks if the chosen time is not in the past only if the chosen date is the current day
            if (isSameDay){
                long end = calNow.getTimeInMillis();
                long start = calSet.getTimeInMillis();
                if (TimeUnit.MILLISECONDS.toSeconds(end - start) > 0){
                    Toast.makeText(InvitationActivity.this, "please select a valid time.", Toast.LENGTH_SHORT).show();
                    startBTN.setText("start time");
                    validTime = false;
                }
                else validTime = true;
            }
            else {
                validTime = true;
            }

            if (dateChoose){
                if (validTime){
                    timeFormatStart = String.format("%02d:%02d", hourOfDay, minute);
                    startBTN.setText(timeFormatStart);
                }
                else {
                    Toast.makeText(InvitationActivity.this, "please choose a valid time", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(InvitationActivity.this, "please choose a date", Toast.LENGTH_SHORT).show();
            }
        }
    };
}