package com.ecommerce.cartify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Grabbing View Items
        EditText usernameTxt = (EditText)findViewById(R.id.login_username_txt);
        EditText passwordTxt = (EditText)findViewById(R.id.login_password_txt);
        TextView registerTxt = (TextView)findViewById(R.id.login_txt);
        Button loginBtn = (Button)findViewById(R.id.login_btn);

        // Adding OnClick Listeners
            // Logging In
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean login = dbHelper.checkLogin(usernameTxt.getText().toString(), passwordTxt.getText().toString());
                if(login) {
                    Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                    finish();
                    startActivity(i);
                }
                else
                    Toast.makeText(MainActivity.this, "NOT Logged In Successfully", Toast.LENGTH_SHORT).show();
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