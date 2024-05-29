package com.example.finalproject.Activities;

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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.ReferencesFB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Sign up activity.
 * signing up a user to the app with email and password.
 */
public class SignUpActivity extends AppCompatActivity {
    private EditText emailET, passwordET;
    private CheckBox conCB;
    private TextView suTV;
    private Button loginBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailET = (EditText) findViewById(R.id.suEmailET);
        passwordET = (EditText) findViewById(R.id.suPasswordET);
        conCB = (CheckBox) findViewById(R.id.suConCB);
        loginBTN = (Button) findViewById(R.id.signupBTN);
        suTV = (TextView) findViewById(R.id.suTV);

        loginBTN.setBackgroundColor(Color.TRANSPARENT);
        loginOption();
    }


    /**
     * Signup.
     * <p>
     * creating a new user with Firebase Authentication using Email and Password.
     * @param view the view
     */
    public void signup(View view) {
            final ProgressDialog pd=ProgressDialog.show(this,"Create Account","Signing Up...",true);
            mAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                if (isValidEmail(emailET.getText().toString())){
                                    ReferencesFB.getUser(mAuth.getCurrentUser());
                                    SharedPreferences settings = getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("stayConnect",conCB.isChecked());
                                    editor.commit();
                                    Intent si = new Intent(SignUpActivity.this, RegisterActivity.class);
                                    startActivity(si);
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                }
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, "There may already be an account with these details.", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }
                    });
    }

    private void loginOption() {
        SpannableString ss = new SpannableString("Don't have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent si = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(si);
            }
        };
        ss.setSpan(span, 24, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        suTV.setText(ss);
        suTV.setMovementMethod(LinkMovementMethod.getInstance());
    }
}