package com.ecommerce.cartify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.cartify.Helpers.FirebaseHelper;
import com.ecommerce.cartify.Models.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    // View Variables
    EditText nameTxt;
    EditText usernameTxt;
    EditText emailTxt;
    EditText passwordTxt;
    EditText birthdateTxt;
    EditText jobTxt;
    RadioGroup genderRadio;
    Button registerBtn;
    TextView loginTxt;

    // Customer Variables
    String name, username, email, password,  gender, birthDate, job;
    final Calendar myCalendar = Calendar.getInstance();

    // SQLiteOpenHelper Database
    //final DbHelper dbHelper = new DbHelper(this);

    // Firebase Database
    final FirebaseHelper fbHelper = new FirebaseHelper();

    public RegisterActivity() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Grabbing View Items
        nameTxt = (EditText)findViewById(R.id.name_txt);
        usernameTxt = (EditText)findViewById(R.id.username_txt);
        emailTxt = (EditText)findViewById(R.id.email_txt);
        passwordTxt = (EditText)findViewById(R.id.password_txt);
        birthdateTxt = (EditText)findViewById(R.id.birthdate_txt);
        jobTxt = (EditText)findViewById(R.id.job_txt);
        genderRadio = (RadioGroup)findViewById(R.id.gender_radio_group);
        registerBtn = (Button)findViewById(R.id.register_btn);
        loginTxt = (TextView)findViewById(R.id.login_txt);

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
                new DatePickerDialog(RegisterActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
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

                if(name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                        birthDate.isEmpty() || gender.isEmpty() || job.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please Fill All Required Fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Customer customer = new Customer(name, username, email, password, gender, birthDate, job);
                registerCustomer(customer);
            }
        });

            // Navigating back to Login Page
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(i);
            }
        });
    }

    private void updateLabel(){
        EditText birthdateTxt = (EditText)findViewById(R.id.birthdate_txt);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        birthdateTxt.setText(sdf.format(myCalendar.getTime()));
    }

    private void registerCustomer(Customer customer){

        // Adding loading bar
        ProgressDialog loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Register Account");
        loadingBar.setMessage("Please wait while your account is being registered");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("customers");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(customer.getUsername()).exists()){
                    boolean emailInUse = false;
                    for(DataSnapshot postSnapshot : snapshot.getChildren()){
                        Customer cust = postSnapshot.getValue(Customer.class);
                        if(cust.getEmail().toLowerCase().equals(customer.getEmail().toLowerCase())){
                            emailInUse = true;
                            break;
                        }
                    }
                    if(emailInUse){
                        loadingBar.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "Email Is Already Registered To Another Account",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    HashMap<String, Object> custMap = new HashMap<>();

                    custMap.put("cust_name", customer.getName());
                    custMap.put("username", customer.getUsername());
                    custMap.put("email", customer.getEmail());
                    custMap.put("password", customer.getPassword());
                    custMap.put("gender", customer.getGender());
                    custMap.put("birth_date", customer.getBirthDate());
                    custMap.put("job", customer.getJob());

                    dbRef.child(customer.getUsername()).setValue(custMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    nameTxt.setText("");
                                    usernameTxt.setText("");
                                    emailTxt.setText("");
                                    passwordTxt.setText("");
                                    birthdateTxt.setText("");
                                    genderRadio.clearCheck();
                                    jobTxt.setText("");

                                    loadingBar.dismiss();
                                    Toast.makeText(getApplicationContext(), "You've Been Registered Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingBar.dismiss();
                                    Toast.makeText(getApplicationContext(), "Connection Error!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                else{
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Username Has To Be Unique!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}