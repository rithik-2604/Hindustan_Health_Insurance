package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPInput extends AppCompatActivity {
    public static final String MSG = "com.example.hindustanhealthinsurance.OTPInput";
    private EditText phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otpinput);

        phone_number = findViewById(R.id.PhoneNumber);
        Button getOtp = findViewById(R.id.get_otp);
        ProgressBar progressBar = findViewById(R.id.sending_otp);

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setValidation() == 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    getOtp.setVisibility(View.INVISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + phone_number.getText().toString().trim(),
                            60,
                            TimeUnit.SECONDS,
                            OTPInput.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBar.setVisibility(View.GONE);
                                    getOtp.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.GONE);
                                    getOtp.setVisibility(View.VISIBLE);
                                    Toast.makeText(OTPInput.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBar.setVisibility(View.GONE);
                                    getOtp.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(OTPInput.this, OTPVerification.class);
                                    intent.putExtra(MSG, phone_number.getText().toString().trim());
                                    intent.putExtra("otp", s);
                                    startActivity(intent);
                                }
                            }
                    );
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private int setValidation() {
        int valid_flag = 1;

        phone_number.setError(null);

        if (phone_number.getText().toString().trim().equalsIgnoreCase("")) {
            phone_number.setError(getResources().getString(R.string.pn_valid));
            valid_flag = 0;
        }
        else if (phone_number.getText().toString().trim().length() != 10) {
            phone_number.setError(getResources().getString(R.string.pn_valid));
            valid_flag = 0;
        }

        return valid_flag;
    }
}