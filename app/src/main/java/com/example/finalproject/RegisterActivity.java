package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

public class RegisterActivity extends AppCompatActivity {
    EditText fullNameET, userNameET, distanceET, ageET, addressET, yearsOfPlayET;
    Switch genderSW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}