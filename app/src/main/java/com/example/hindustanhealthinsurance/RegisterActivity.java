package com.example.hindustanhealthinsurance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hindustanhealthinsurance.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText first_name, last_name, email, password, confirm_password, phone_number;
    EditText address_1, address_2, city, pincode;
    Button register_btn, state_btn;
    TextView existing_user, state_select;

    String emailTo, subject, message;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        final int[] checkedItem = {-1};

        first_name = findViewById(R.id.FirstName);
        last_name = findViewById(R.id.LastName);
        email = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        confirm_password = findViewById(R.id.cPassword);
        phone_number = findViewById(R.id.PhoneNumber);

        address_1 = findViewById(R.id.address_1);
        address_2 = findViewById(R.id.address_2);
        city = findViewById(R.id.City);
        state_btn = findViewById(R.id.state_btn);
        state_select = findViewById(R.id.state);
        pincode = findViewById(R.id.PinCode);


        register_btn = findViewById(R.id.register_btn);
        existing_user = findViewById(R.id.existing_user);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");

        state_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
                alertDialog.setTitle("Select Your State");

                final String[] statesList = {"Andhra Pradesh", "Arunachal Pradesh", "Assam",
                                            "Bihar",
                                            "Chhattisgarh",
                                            "Goa", "Gujarat",
                                            "Haryana", "Himachal Pradesh",
                                            "Jharkhand",
                                            "Karnataka", "Kerala",
                                            "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
                                            "Nagaland",
                                            "Odisha",
                                            "Punjab",
                                            "Rajasthan",
                                            "Sikkim",
                                            "Tamil Nadu", "Telangana", "Tripura",
                                            "Uttarakhand", "Uttar Pradesh",
                                            "West Bengal"};

                alertDialog.setSingleChoiceItems(statesList, checkedItem[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkedItem[0] = i;
                        state_select.setText("State: " + statesList[i]);
                        dialogInterface.dismiss();
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog customAlertDialog = alertDialog.create();
                customAlertDialog.show();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setValidation() == 1) {
                    Toast.makeText(RegisterActivity.this, "Account Information entered correctly", Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword
                            (email.getText().toString().trim(), password.getText().toString().trim()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                User user = new User(first_name.getText().toString().trim(),
                                                    last_name.getText().toString().trim(),
                                                    email.getText().toString().trim(),
                                                    password.getText().toString().trim(),
                                                    phone_number.getText().toString().trim(),
                                                    address_1.getText().toString().trim(),
                                                    address_2.getText().toString().trim(),
                                                    city.getText().toString().trim(),
                                                    state_select.getText().toString().trim().substring(7),
                                                    pincode.getText().toString().trim());

                                String id = task.getResult().getUser().getUid();
                                database.getReference().child("Users").child(id).setValue(user);

                                Toast.makeText(RegisterActivity.this, "User Created Successfully!!", Toast.LENGTH_SHORT).show();
                                sendEmail();
                                startActivity(new Intent(RegisterActivity.this, UserDashboard.class));
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(RegisterActivity.this, "Account Information didn't entered properly", Toast.LENGTH_SHORT).show();
            }
        });

        existing_user.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void sendEmail() {
        emailTo = email.getText().toString().trim();
        subject = "Registration Successful!!";
        message = "Thank you for filling out our sign up form." + "\n" +
                  "We are glad that you joined us." + "\n" +
                  "Have a nice day" + "\n" +
                  "Respectfully," + "\n" +
                  "HINDUSTAN HEALTH INSURANCE";

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, emailTo, subject, message);
        try {
            javaMailAPI.execute();
            Toast.makeText(RegisterActivity.this, "Email send to the user successfully!!", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            Toast.makeText(RegisterActivity.this, "Error occured sending to the email", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, StartActivity.class));
        finish();
    }

    private int setValidation() {
        int valid_flag = 1;

        first_name.setError(null);
        last_name.setError(null);
        email.setError(null);
        password.setError(null);
        confirm_password.setError(null);
        phone_number.setError(null);

        address_1.setError(null);
        city.setError(null);
        state_select.setError(null);
        pincode.setError(null);


        if (first_name.getText().toString().trim().equalsIgnoreCase("")) {
            first_name.setError(getResources().getString(R.string.fn_valid));
            valid_flag = 0;
        }

        if (last_name.getText().toString().trim().equalsIgnoreCase("")) {
            last_name.setError(getResources().getString(R.string.ln_valid));
            valid_flag = 0;
        }

        if (email.getText().toString().trim().equalsIgnoreCase("")) {
            email.setError(getResources().getString(R.string.e_valid));
            valid_flag = 0;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            email.setError(getResources().getString(R.string.e_valid));
            valid_flag = 0;
        }


        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(password.getText().toString().trim());

        StringBuilder sb = new StringBuilder();
        char[] chars = password.getText().toString().toCharArray();

        int flag = -1;

        if (password.getText().toString().trim().equalsIgnoreCase("")) {
            password.setError(getResources().getString(R.string.p_valid));
            valid_flag = 0;
        }
        else if (password.getText().toString().trim().length() < 8) {
            password.setError(getResources().getString(R.string.p_valid));
            valid_flag = 0;
        }
        else if (!m.find()) {
            password.setError(getResources().getString(R.string.p_valid));
            valid_flag = 0;
        }
        else {
            for (char c : chars) {
                if (Character.isDigit(c))
                    flag = 1;
            }

            if (flag == -1) {
                password.setError(getResources().getString(R.string.p_valid));
                valid_flag = 0;
            }
        }


        if (!confirm_password.getText().toString().trim().equals(password.getText().toString().trim())) {
            confirm_password.setError(getResources().getString(R.string.cp_valid));
            valid_flag = 0;
        }

        if (phone_number.getText().toString().trim().equalsIgnoreCase("")) {
            phone_number.setError(getResources().getString(R.string.pn_valid));
            valid_flag = 0;
        }
        else if (phone_number.getText().toString().trim().length() != 10) {
            phone_number.setError(getResources().getString(R.string.pn_valid));
            valid_flag = 0;
        }

        if (address_1.getText().toString().trim().equalsIgnoreCase("")) {
            address_1.setError(getResources().getString(R.string.address_1_valid));
            valid_flag = 0;
        }

        if (city.getText().toString().trim().equalsIgnoreCase("")) {
            city.setError(getResources().getString(R.string.city_valid));
            valid_flag = 0;
        }

        if (state_select.getText().toString().trim().length() <= 7) {
            state_select.setError(getResources().getString(R.string.state_valid));
            valid_flag = 0;
        }

        if (pincode.getText().toString().trim().equalsIgnoreCase("")) {
            pincode.setError(getResources().getString(R.string.pin_code_valid));
            valid_flag = 0;
        }
        else if (pincode.getText().toString().trim().length() != 6) {
            pincode.setError(getResources().getString(R.string.pin_code_valid));
            valid_flag = 0;
        }

        return valid_flag;
    }
}