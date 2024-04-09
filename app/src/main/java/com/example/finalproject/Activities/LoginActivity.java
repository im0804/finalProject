package com.example.finalproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.finalproject.ReferencesFB.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The type Login activity.
 *
 * @author inbar menahem
 * @version 1
 * @since 25 /12/2023 the opening activity for the user to sign up/in in authentication.
 */
public class LoginActivity extends AppCompatActivity {
    EditText emailET, passwordET;
    CheckBox conCB;
    TextView regorlogTV, regTV;
    Button logorregBTN;
    public static String Uid;
    public static FirebaseUser userFB;
    boolean stayConnected, registered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        conCB = (CheckBox) findViewById(R.id.conCB);
        regorlogTV = (TextView) findViewById(R.id.regorlogTV);
        logorregBTN = (Button) findViewById(R.id.logorregBTN);
        regTV = (TextView) findViewById(R.id.regTV);
        stayConnected = false;
        registered = false;
        logoption();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect",false);
        Intent si = new Intent(LoginActivity.this, MainActivity.class);
        if (mAuth.getCurrentUser() != null && isChecked) {
            userFB = mAuth.getCurrentUser();
            stayConnected = true;
            registered = true;
            startActivity(si);
        }
    }

    /**
     * the method checks whether the user is already registered.
     * if so then it checks if the check button is checked and moves to the main activity
     * if not then it saves the user in fireBase authentication and moves to register.
     *
     * @param view the view
     */
    public void login(View view) {
        if (emailET.getText().toString().equals("") || passwordET.getText().toString().equals("")){
            Toast.makeText(this, "please fill in all fields", Toast.LENGTH_LONG).show();
        }
        else {
            if (registered){
                final ProgressDialog pd=ProgressDialog.show(this,"Login","Connecting...",true);
                mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    if (isValidEmail(emailET.getText().toString())){
                                        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=settings.edit();
                                        editor.putBoolean("stayConnect",conCB.isChecked());
                                        editor.commit();
                                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                        Intent si = new Intent(LoginActivity.this, MainActivity.class);
                                        userFB = mAuth.getCurrentUser();
                                        Uid = userFB.getUid();
                                        si.putExtra("Uid",Uid);
                                        startActivity(si);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "please enter a valid email.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
            else{
                mAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (isValidEmail(emailET.getText().toString())){
                                SharedPreferences settings = getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("stayConnect",conCB.isChecked());
                                editor.commit();
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                Intent si = new Intent(LoginActivity.this, RegisterActivity.class);
                                userFB = mAuth.getCurrentUser();
                                Uid = userFB.getUid();
                                si.putExtra("Uid",Uid);
                                startActivity(si);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "e-mail or password are wrong!!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "please enter a valid email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    private void registerOption() {
        regorlogTV.setText("Login");
        logorregBTN.setText("Login");
        registered = true;
        SpannableString ss = new SpannableString("Don't have an account?  Register here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                regorlogTV.setText("Register");
                logorregBTN.setText("Register");
                registered=false;
                logoption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        regTV.setText(ss);
        regTV.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void logoption() {
        regorlogTV.setText("Register");
        logorregBTN.setText("Register");
        registered=false;
        SpannableString ss = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                regorlogTV.setText("Login");
                logorregBTN.setText("Login");
                registered = true;
                registerOption();
            }
        };
        ss.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        regTV.setText(ss);
        regTV.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
            Intent si = new Intent(this, JoinAsCoachActivity.class);
            startActivity(si);
        }
        else if (str.equals("login")){
        }
        else if (str.equals("coachAct")) {
            Intent si = new Intent(this, CoachActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }
}