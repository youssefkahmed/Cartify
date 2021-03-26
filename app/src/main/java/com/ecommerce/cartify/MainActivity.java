package com.ecommerce.cartify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.ecommerce.cartify.Helpers.FirebaseHelper;
import com.ecommerce.cartify.Models.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // Db Access var
    final DbHelper dbHelper = new DbHelper(this);
    final FirebaseHelper fbHelper = new FirebaseHelper();

    // View Variables
    EditText usernameTxt;
    EditText passwordTxt;
    TextView registerTxt;
    TextView forgotPassTxt;
    Button loginBtn;
    CheckBox rememberMeCB;

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
        usernameTxt = (EditText)findViewById(R.id.login_username_txt);
        passwordTxt = (EditText)findViewById(R.id.login_password_txt);
        registerTxt = (TextView)findViewById(R.id.register_txt);
        forgotPassTxt = (TextView)findViewById(R.id.forgot_pwd_txt);
        loginBtn = (Button)findViewById(R.id.login_btn);
        rememberMeCB = (CheckBox)findViewById(R.id.remember_me_checkBox);

        // Adding OnClick Listeners
            // Logging In
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameTxt.getText().toString().toLowerCase();
                String password =  passwordTxt.getText().toString();

                checkLogin(username, password);
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
                                boolean result = dbHelper.resetPassword(custEmail, MainActivity.this);

                                if(!result)
                                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
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
                finish();
                startActivity(i);
            }
        });
    }


    // Helper Functions
    public void checkLogin(String username, String password){

        // Adding loading bar
        ProgressDialog loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Logging In");
        loadingBar.setMessage("Please wait while you're being logged in.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("customers");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Customer customer = null;
                if(snapshot.child(username).exists())
                    customer = snapshot.child(username).getValue(Customer.class);

                if(username.equals("") || customer == null){
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "This Username Does Not Exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!customer.getPassword().equals(password)) {
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Password Is Incorrect!", Toast.LENGTH_SHORT).show();
                    return;
                }

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

                loadingBar.dismiss();
                finish();
                startActivity(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}