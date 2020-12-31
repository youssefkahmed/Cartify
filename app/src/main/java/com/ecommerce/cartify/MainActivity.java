package com.ecommerce.cartify;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Db Access var
    final DbHelper dbHelper = new DbHelper(this);

    // Shared Preferences vars
    public static final String PREFS_NAME = "CartifyPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checking Shared Preferences for any saved username & password
        /*SharedPreferences mPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = mPref.getString(PREF_USERNAME, null);
        String password = mPref.getString(PREF_PASSWORD, null);

        if (username == null || password == null) {
            Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
            i.putExtra("username", username);
            finish();
            startActivity(i);
        }*/

        // Grabbing View Items
        EditText usernameTxt = (EditText)findViewById(R.id.login_username_txt);
        EditText passwordTxt = (EditText)findViewById(R.id.login_password_txt);
        TextView registerTxt = (TextView)findViewById(R.id.register_txt);
        TextView forgotPassTxt = (TextView)findViewById(R.id.forgot_pwd_txt);
        Button loginBtn = (Button)findViewById(R.id.login_btn);
        CheckBox rememberMeCB = (CheckBox)findViewById(R.id.remember_me_checkBox);

        // Adding OnClick Listeners
            // Logging In
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameTxt.getText().toString();
                String password =  passwordTxt.getText().toString();

                boolean login = dbHelper.checkLogin(username, password);
                if(login) {
                    // Setting up intent
                    Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                    i.putExtra("username", username);

                    // Setting up Shared Preferences
                    if(rememberMeCB.isChecked()){
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .edit()
                                .putString(PREF_USERNAME, username)
                                .putString(PREF_PASSWORD, password)
                                .apply();
                    }

                    finish();
                    startActivity(i);
                }
                else
                    Toast.makeText(MainActivity.this, "NOT Logged In Successfully", Toast.LENGTH_SHORT).show();
            }
        });

            // Forgot Password
        forgotPassTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View builderView = getLayoutInflater().inflate(R.layout.dialog_forgot_pass, null);
                builder.setView(builderView)
                        .setMessage("Enter your email")
                        .setTitle("Forgot Password")
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String custEmail = ((EditText)builderView.findViewById(R.id.forgotPass_email_txt)).getText().toString();
                                boolean result = dbHelper.resetPassword(custEmail);

                                if(!result)
                                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Check Your Email!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

            // Navigating to Register Page
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}