package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hindustanhealthinsurance.Models.Bookings;
import com.example.hindustanhealthinsurance.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RenewalPlanDetails extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseDatabase database;

    Intent fromIntent;

    User user;
    Bookings b;

    String planType;

    TextView rc_name, rc_email, rc_phone;
    TextView rc_pt, rc_pc;
    TextView rc_ad, rc_ed;
    TextView rc_bmi, rc_smoke;

    Button rc_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_renewal_plan_details);

        initObjects();
    }

    private void initObjects() {
        rc_name = findViewById(R.id.rc_name);
        rc_email = findViewById(R.id.rc_email);
        rc_phone = findViewById(R.id.rc_phone);

        rc_pt = findViewById(R.id.rc_pt);
        rc_pc = findViewById(R.id.rc_pc);

        rc_bmi = findViewById(R.id.rc_bmi);
        rc_smoke = findViewById(R.id.rc_smoke);

        rc_ad = findViewById(R.id.rc_ad);
        rc_ed = findViewById(R.id.rc_ed);

        rc_btn = findViewById(R.id.rc_btn);
        rc_btn.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        fromIntent = getIntent();
        planType = fromIntent.getStringExtra("pType");

        user = new User();
        b = new Bookings();

        final FirebaseUser currentUser = auth.getCurrentUser();

        assert currentUser != null;
        user.setUid(currentUser.getUid());
        user.setEmail(currentUser.getEmail());

        if ((user.getUid() != null) && ((planType.equals("1")) || (planType.equals("2")) || (planType.equals("3")) || (planType.equals("5")) || (planType.equals("10")))) {
            DatabaseReference bookingDb = database.getReference().child("Bookings").child(user.getUid()).child(planType);
            bookingDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    b = snapshot.getValue(Bookings.class);
                    assert b != null;

                    Bookings currentBooking = b;
                    fillDetails(currentBooking);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void fillDetails(Bookings b) {
        rc_name.setText(rc_name.getText() + " " + b.getcName());
        rc_email.setText(rc_email.getText() + " " +  b.getcEmail());
        rc_phone.setText(rc_phone.getText() + " " + b.getcNo());

        rc_pt.setText(rc_pt.getText() + " " + b.getPt() + " lakh(s)");
        rc_pc.setText(rc_pc.getText() + " " + b.getCost());

        rc_ad.setText(rc_ad.getText() + " " + b.getAppliedDate());
        rc_bmi.setText(rc_bmi.getText() + " " + b.getBmi());
        rc_smoke.setText(rc_smoke.getText() + " " + b.getSmoker());
        rc_ed.setText(rc_ed.getText() + " " + b.getExpiryDate());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RenewalPlanDetails.this, UserDashboard.class));
    }

    @Override
    public void onClick(View view) {
        String expiryDate = b.getExpiryDate();
        int year = Integer.parseInt(expiryDate.substring(expiryDate.lastIndexOf('-') + 1)) + 1;

        expiryDate = expiryDate.substring(0, expiryDate.lastIndexOf('-') + 1) + year;

        b.setExpiryDate(expiryDate);

        DatabaseReference bookingDb = database.getReference().child("Bookings").child(user.getUid()).child(planType);
        bookingDb.setValue(b);

        sendMail();
        Intent toIntent = new Intent(RenewalPlanDetails.this, UserDashboard.class);
        toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toIntent);
    }

    private void sendMail() {
        String subject, email_message;
        subject = "INSURANCE POLICY RENEWAL DETAILS";
        email_message = "Customer Name: " + b.getcName() + "\n" +
                "Contact Number: +91 " + b.getcNo() + "\n" +
                "EmailID: " + b.getcEmail() + "\n" +
                "Plan Type: Rs. " + b.getPt() + " lakh(s)" + "\n" +
                "Your Insurance policy has been successfully Renewed!";
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, b.getcEmail(), subject, email_message);
        try {
            javaMailAPI.execute();
            Toast.makeText(RenewalPlanDetails.this, "Email send to the " + b.getcName() + " successfully!!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(RenewalPlanDetails.this, "Error occurred sending to the email", Toast.LENGTH_SHORT).show();
        }
    }
}