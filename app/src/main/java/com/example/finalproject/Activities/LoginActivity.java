package com.example.finalproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.finalproject.ReferencesFB.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Objs.UsersClass;
import com.example.finalproject.R;
import com.example.finalproject.ReferencesFB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;


/**
 * The type Login activity.
 *
 * @author inbar menahem
 * @version 1
 * @since 25 /12/2023 the opening activity for the user to sign up/in in authentication.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText emailET, passwordET;
    private CheckBox conCB;
    private TextView regTV;
    private Button loginBTN;

    UsersClass user;
    Intent si;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        conCB = (CheckBox) findViewById(R.id.conCB);
        loginBTN = (Button) findViewById(R.id.loginBTN);
        regTV = (TextView) findViewById(R.id.regTV);

        loginBTN.setBackgroundColor(Color.TRANSPARENT);
        signUpOption();
    }

    /**
     * the method checks whether the user is already registered.
     * if so then it checks if the 'stay connected' button is checked and moves to the main activity
     * if not then it saves the user in fireBase authentication and moves to register.
     *
     * @param view the view
     */
    public void login(View view) {
        if (emailET.getText().toString().equals("") || passwordET.getText().toString().equals("")){
            Toast.makeText(this, "please fill in all fields", Toast.LENGTH_LONG).show();
        } else if (!isValidEmail(emailET.getText().toString())){
            Toast.makeText(LoginActivity.this, "please enter a valid email", Toast.LENGTH_LONG).show();
        } else {
            final ProgressDialog pd=ProgressDialog.show(this,"Logging Account","Logging in...",true);
            mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                ReferencesFB.getUser(mAuth.getCurrentUser());
                                //checks if the user has finished registration or not
                                refUsers.child(Uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> tsk) {
                                        if (tsk.isSuccessful()) {
                                            user = tsk.getResult().getValue(UsersClass.class);
                                            if (user == null){
                                                Toast.makeText(LoginActivity.this, "please finish registering", Toast.LENGTH_LONG).show();
                                                si = new Intent(LoginActivity.this, RegisterActivity.class);
                                            }
                                            else{
                                                SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = settings.edit();
                                                editor.putBoolean("stayConnect", conCB.isChecked());
                                                editor.commit();
                                                si = new Intent(LoginActivity.this, MainActivity.class);
                                            }
                                            startActivity(si);
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Email or Password is wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * signUp on click method.
     * this method moves the user to Sign Up Activity when he clicks the 'sign up here' link.
     */
    private void signUpOption() {
        SpannableString ss = new SpannableString("Don't have an account?  Sign Up here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                si = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(si);
            }
        };
        ss.setSpan(span, 24, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        regTV.setText(ss);
        regTV.setMovementMethod(LinkMovementMethod.getInstance());
    }

}