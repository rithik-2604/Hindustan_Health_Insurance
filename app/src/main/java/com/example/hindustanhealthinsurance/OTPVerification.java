package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {

    private EditText otp_1, otp_2, otp_3, otp_4, otp_5, otp_6;
    private String otp_back_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otpverification);

        otp_1 = findViewById(R.id.otp_1);
        otp_2 = findViewById(R.id.otp_2);
        otp_3 = findViewById(R.id.otp_3);
        otp_4 = findViewById(R.id.otp_4);
        otp_5 = findViewById(R.id.otp_5);
        otp_6 = findViewById(R.id.otp_6);

        TextView mobile = findViewById(R.id.mobile_number);
        Button submit = findViewById(R.id.submit);
        ProgressBar progressBar = findViewById(R.id.verifying_otp);

        String number = "+91-" + getIntent().getStringExtra(OTPInput.MSG);
        mobile.setText(number);

        otp_back_end = getIntent().getStringExtra("otp");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setValidation() == 1) {
                    String enteredOTP = otp_1.getText().toString() +
                                        otp_2.getText().toString() +
                                        otp_3.getText().toString() +
                                        otp_4.getText().toString() +
                                        otp_5.getText().toString() +
                                        otp_6.getText().toString();

                    if (otp_back_end != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otp_back_end, enteredOTP);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        submit.setVisibility(View.VISIBLE);

                                        if (task.isSuccessful()) {
                                            Toast.makeText(OTPVerification.this, "OTP VERIFIED!!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(OTPVerification.this, "Logged in Successfully!!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(OTPVerification.this, UserDashboard.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else {
                                            Toast.makeText(OTPVerification.this, "Enter the correct OTP", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(OTPVerification.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        OTPnext();

        findViewById(R.id.otp_resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra(OTPInput.MSG),
                        60,
                        TimeUnit.SECONDS,
                        OTPVerification.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPVerification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otp_back_end = newOTP;
                                Toast.makeText(OTPVerification.this, "OTP sended Succesfully!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void OTPnext() {
        otp_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp_2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp_3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp_4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp_5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    otp_6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private int setValidation() {
        int valid_flag = 1;

        otp_1.setError(null);
        otp_2.setError(null);
        otp_3.setError(null);
        otp_4.setError(null);
        otp_5.setError(null);
        otp_6.setError(null);

        if (otp_1.getText().toString().trim().equalsIgnoreCase("")) {
            otp_1.setError(getResources().getString(R.string.otp_error));
            valid_flag = 0;
        }
        if (otp_2.getText().toString().trim().equalsIgnoreCase("")) {
            otp_2.setError(getResources().getString(R.string.otp_error));
            valid_flag = 0;
        }
        if (otp_3.getText().toString().trim().equalsIgnoreCase("")) {
            otp_3.setError(getResources().getString(R.string.otp_error));
            valid_flag = 0;
        }
        if (otp_4.getText().toString().trim().equalsIgnoreCase("")) {
            otp_4.setError(getResources().getString(R.string.otp_error));
            valid_flag = 0;
        }
        if (otp_5.getText().toString().trim().equalsIgnoreCase("")) {
            otp_5.setError(getResources().getString(R.string.otp_error));
            valid_flag = 0;
        }
        if (otp_6.getText().toString().trim().equalsIgnoreCase("")) {
            otp_6.setError(getResources().getString(R.string.otp_error));
            valid_flag = 0;
        }

        return valid_flag;
    }
}