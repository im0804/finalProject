package com.example.finalproject.Activities;

import static com.example.finalproject.ReferencesFB.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.example.finalproject.R;
import com.example.finalproject.ReferencesFB;

/**
 * Opening activity.
 * this is the first activity that will be displayed to the user.
 */
public class OpeningActivity extends AppCompatActivity {
    private Button loginBTN, sinupBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        loginBTN = findViewById(R.id.btnLogin);
        sinupBTN = findViewById(R.id.btnSignup);
        loginBTN.setBackgroundColor(Color.TRANSPARENT);
        sinupBTN.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //checks if the user chose to stay connected in his last entry
        SharedPreferences settings = getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect",false);
        Intent si = new Intent(OpeningActivity.this, MainActivity.class);
        if (mAuth.getCurrentUser() != null && isChecked) {
            ReferencesFB.getUser(mAuth.getCurrentUser());
            startActivity(si);
        }
    }

    /**
     * On Click method - Sign up.
     *<p>
     * moves the user to SignUp Activity.
     * @param view the view
     */
    public void signUp(View view) {
        Intent si = new Intent(OpeningActivity.this, SignUpActivity.class);
        startActivity(si);
    }

    /**
     * On Click method - Login.
     *<p>
     *moves the user to Login Activity.
     * @param view the view
     */
    public void login(View view) {
        Intent si = new Intent(OpeningActivity.this, LoginActivity.class);
        startActivity(si);
    }
}