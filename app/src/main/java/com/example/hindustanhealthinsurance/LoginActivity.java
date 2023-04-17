package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hindustanhealthinsurance.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Button btn_login;
        TextView register;

        email = findViewById(R.id.Username);
        password = findViewById(R.id.Password);

        btn_login = findViewById(R.id.login_btn);
        register = findViewById(R.id.newuser_txt);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setValidation() == 1) {
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this, UserDashboard.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        register.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, StartActivity.class));
        finish();
    }

    private int setValidation() {
        int valid_flag = 1;

        email.setError(null);
        password.setError(null);

        if (email.getText().toString().trim().equalsIgnoreCase("")) {
            email.setError(getResources().getString(R.string.e_login));
            valid_flag = 0;
        }

        if (password.getText().toString().trim().equalsIgnoreCase("")) {
            password.setError(getResources().getString(R.string.p_login));
            valid_flag = 0;
        }

        return valid_flag;
    }
}