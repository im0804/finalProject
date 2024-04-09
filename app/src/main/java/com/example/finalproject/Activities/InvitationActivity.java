package com.example.finalproject.Activities;

import static com.example.finalproject.Activities.LoginActivity.Uid;
import static com.example.finalproject.Activities.LoginActivity.userFB;
import static com.example.finalproject.ReferencesFB.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class InvitationActivity extends AppCompatActivity {
    EditText distanceEt;
    RadioButton RB1, RB2, RB3, RB4, RB5;
    Button startBTN;
    TextView clearTV, dateTV;
    //final Calendar myCalendar= Calendar.getInstance();
    Calendar calNow, calSet;
    String timeFormatStart, dateFormat, dateFrormatFB, userName, userAddress, userCity, key;
    int distance, year, month, day;
    boolean level1, level2, level3, level4, level5;
    InviteClass ic;
    UsersClass user;
    boolean dateChoose = false, startButton = false, validDate = true, validTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        distanceEt = (EditText) findViewById(R.id.distanceEt);
        RB1 = (RadioButton) findViewById(R.id.RB1);
        RB2 = (RadioButton) findViewById(R.id.RB2);
        RB3 = (RadioButton) findViewById(R.id.RB3);
        RB4 = (RadioButton) findViewById(R.id.RB4);
        RB5 = (RadioButton) findViewById(R.id.RB5);
        startBTN = (Button) findViewById(R.id.startTimeBTN);
        clearTV = (TextView) findViewById(R.id.clearTV);
        dateTV = (TextView) findViewById(R.id.dateTV);

        calNow = Calendar.getInstance();

        Query query = refUsers
                .orderByChild("uid")
                .equalTo(Uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if (dS.exists()) {
                    for (DataSnapshot data : dS.getChildren()) {
                        user = data.getValue(UsersClass.class);
                        userName = user.getUserName();
                        userAddress = user.getAddress();
                        userCity = user.getCity();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InvitationActivity.this, "users query didnt work", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        SpannableString ss = new SpannableString("clear");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                RB1.setChecked(false);
                RB2.setChecked(false);
                RB3.setChecked(false);
                RB4.setChecked(false);
                RB5.setChecked(false);
            }
        };
        ss.setSpan(span, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        clearTV.setText(ss);
        clearTV.setMovementMethod(LinkMovementMethod.getInstance());
        super.onStart();
    }

    private void showDatePickerDialog() {
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
                        } else {
                            // Do something with the selected date
                            // For example, you can display it or use it for further processing
                            dateFormat = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            dateFrormatFB = String.format("%02d%02d%04d", dayOfMonth, monthOfYear+1, year);
                            dateChoose = true;
                            validTime = false;
                            dateTV.setText(dateFormat);
                            startBTN.setText("start time");
                        }
                    }
                }, year, month, day);

        // Set the minimum date to today's date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    public void createBtn(View view) {
        if (dateChoose && startButton && validDate && validTime) {
            level1 = RB1.isChecked();
            level2 = RB2.isChecked();
            level3 = RB3.isChecked();
            level4 = RB4.isChecked();
            level5 = RB5.isChecked();
            if (level1 || level2 || level3 || level4 || level5){
                if (!distanceEt.getText().toString().equals("")){
                    distance = Integer.parseInt(distanceEt.getText().toString());
                    key = dateFrormatFB+timeFormatStart;
                    ic = new InviteClass(Uid, userName, userAddress, userCity, dateFormat, timeFormatStart, key,
                            level1, level2, level3, level4, level5, distance);
                    refInvites.child(Uid).child(dateFrormatFB+timeFormatStart).setValue(ic);
                    Intent si = new Intent(this, MainActivity.class);
                    //si.putExtra("date format", dateFrormatFB);
                    startActivity(si);
                }
                else Toast.makeText(this, "please enter distance", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(this, "please click level", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "please check that all fields are right", Toast.LENGTH_LONG).show();
        }
    }

    public void startBTN(View view) {
        if (dateChoose){
            startButton = true;
            openTimePickerDialog(true);
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

            if (calSet.getTime().compareTo(calNow.getTime()) == 0) {
                if (calSet.compareTo(calNow) >= 0) { //// check whats the problem
                    Toast.makeText(InvitationActivity.this, "please select a valid hour.", Toast.LENGTH_SHORT).show();
                    validTime = false;
                    startBTN.setText("start time");
                }
            }
            else {
                validTime = true;
            }


            if (dateChoose && validDate){
                if (startButton && validTime){
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