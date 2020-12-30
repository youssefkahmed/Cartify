package com.ecommerce.cartify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.cartify.Models.Customer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    String name, username, email, password,  gender, birthDate, job;
    final Calendar myCalendar = Calendar.getInstance();
    final DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Grabbing View Items
        EditText nameTxt = (EditText)findViewById(R.id.name_txt);
        EditText usernameTxt = (EditText)findViewById(R.id.username_txt);
        EditText emailTxt = (EditText)findViewById(R.id.email_txt);
        EditText passwordTxt = (EditText)findViewById(R.id.password_txt);
        EditText birthdateTxt = (EditText)findViewById(R.id.birthdate_txt);
        EditText jobTxt = (EditText)findViewById(R.id.job_txt);
        RadioGroup genderRadio = (RadioGroup)findViewById(R.id.gender_radio_group);
        Button registerBtn = (Button)findViewById(R.id.register_btn);
        TextView loginTxt = (TextView)findViewById(R.id.login_txt);

        // Adding OnClick Listeners
            // Building Date Picker
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

            // Showing Date Picker
        birthdateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                Toast.makeText(getApplicationContext(), "YOOOOO", Toast.LENGTH_LONG).show();
            }
        });

            // Registering The User
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameTxt.getText().toString();
                username = usernameTxt.getText().toString();
                email = emailTxt.getText().toString();
                password = passwordTxt.getText().toString();
                birthDate = birthdateTxt.getText().toString();
                gender = genderRadio.getCheckedRadioButtonId() == R.id.male_radio_btn ? "Male" : "Female";
                job = jobTxt.getText().toString();

                Customer customer = new Customer(name, username, email, password, gender, birthDate, job);
                dbHelper.registerCustomer(customer);

                nameTxt.setText("");
                usernameTxt.setText("");
                emailTxt.setText("");
                passwordTxt.setText("");
                birthdateTxt.setText("");
                genderRadio.clearCheck();
                jobTxt.setText("");

                Toast.makeText(getApplicationContext(), "Customer Registered Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

            // Navigating back to Login Page
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void updateLabel(){
        EditText birthdateTxt = (EditText)findViewById(R.id.birthdate_txt);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        birthdateTxt.setText(sdf.format(myCalendar.getTime()));
    }
}