package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class JoinAsCoachActivity extends AppCompatActivity {
    EditText yearsOfCoachingEt, desET;
    UsersClass user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_as_coach);

        yearsOfCoachingEt = (EditText) findViewById(R.id.yearsOfCoachingET);
        desET = (EditText) findViewById(R.id.desEt);

        user.setIsCoach(true);
    }

    public void finish(View view) {
        if ((yearsOfCoachingEt.getText().toString().equals("")) || (desET.getText().toString().equals(""))){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        }
        else {
            finish();
        }
    }
}