package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
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

public class HistoryPlanDetails extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseDatabase database;

    Intent fromIntent;

    User user;
    Bookings b;

    String planType;

    TextView hc_name, hc_email, hc_phone;
    TextView hc_pt, hc_pc;
    TextView hc_bmi, hc_smoke;
    TextView hc_ad, hc_ed;

    Button hc_btn;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history_plan_details);

        initObjects();
    }

    private void initObjects() {
        hc_name = findViewById(R.id.hc_name);
        hc_email = findViewById(R.id.hc_email);
        hc_phone = findViewById(R.id.hc_phone);

        hc_pt = findViewById(R.id.hc_pt);
        hc_pc = findViewById(R.id.hc_pc);

        hc_bmi = findViewById(R.id.hc_bmi);
        hc_smoke = findViewById(R.id.hc_smoke);

        hc_ad = findViewById(R.id.hc_ad);
        hc_ed = findViewById(R.id.hc_ed);

        hc_btn = findViewById(R.id.hc_btn);
        hc_btn.setOnClickListener(this);

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
        hc_name.setText(hc_name.getText() + " " + b.getcName());
        hc_email.setText(hc_email.getText() + " " +  b.getcEmail());
        hc_phone.setText(hc_phone.getText() + " " + b.getcNo());

        hc_pt.setText(hc_pt.getText() + " " + b.getPt() + " lakh(s)");
        hc_pc.setText(hc_pc.getText() + " " + b.getCost());

        hc_ad.setText(hc_ad.getText() + " " + b.getAppliedDate());
        hc_bmi.setText(hc_bmi.getText() + " " + b.getBmi());
        hc_smoke.setText(hc_smoke.getText() + " " + b.getSmoker());
        hc_ed.setText(hc_ed.getText() + " " + b.getExpiryDate());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HistoryPlanDetails.this, UserDashboard.class));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.show();

        Button yes = dialog.findViewById(R.id.btn_yes);
        Button no = dialog.findViewById(R.id.btn_no);

        yes.setOnClickListener(view1 -> {
            dialog.dismiss();

            database.getReference().child("Bookings").child(user.getUid()).child(planType).removeValue();

            sendMail();
            Intent toIntent = new Intent(HistoryPlanDetails.this, UserDashboard.class);
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(toIntent);
        });

        no.setOnClickListener(view1 -> dialog.dismiss());
    }

    private void sendMail() {
        String subject, email_message;
        subject = "INSURANCE POLICY CANCELLATION DETAILS";
        email_message = "Customer Name: " + b.getcName() + "\n" +
                "Contact Number: +91 " + b.getcNo() + "\n" +
                "EmailID: " + b.getcEmail() + "\n" +
                "Plan Type: Rs. " + b.getPt() + " lakh(s)" + "\n" +
                "Cancellation of Insurance Policy has been completed";

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, b.getcEmail(), subject, email_message);
        try {
            javaMailAPI.execute();
            Toast.makeText(HistoryPlanDetails.this, "Email send to the " + b.getcName() +  " successfully!!", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(HistoryPlanDetails.this, "Error occurred sending to the email", Toast.LENGTH_LONG).show();
        }
    }
}