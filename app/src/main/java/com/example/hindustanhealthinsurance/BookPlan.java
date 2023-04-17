package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.Calendar;

public class BookPlan extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;

    Intent fromIntent;

    User user;

    TextView display_processing;
    TextView insurance_cost;
    TextView premium_cost;
    TextView plan_type;
    TextView applied_date;
    TextView expired_date;

    Button book_insurance;

    private String actualCost;

    private String uid, cname, cno;
    private String subject, emailTo, email_message;
    private String pt, bmi, c, ic;
    private String gender, smoker, region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_book_plan);

        initObjects();

        book_insurance.setOnClickListener(view -> {
            uid = user.getUid();
            database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference().child("Users").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        cname = snapshot.child("first_name").getValue(String.class) + snapshot.child("last_name").getValue(String.class);
                        cno = snapshot.child("phone").getValue(String.class);
                        emailTo = snapshot.child("email").getValue(String.class);

                        subject = "INSURANCE BOOKING DETAILS";
                        region = "";

                        if ((fromIntent.getStringExtra("Gender")).equals("1"))
                            gender = "Female";
                        else
                            gender = "Male";

                        if ((fromIntent.getStringExtra("Smoker")).equals("1"))
                            smoker = "No";
                        else
                            smoker = "Yes";

                        switch ((fromIntent.getStringExtra("Region"))) {
                            case "0":
                                region = "South-East";
                                break;
                            case "1":
                                region = "South-West";
                                break;
                            case "2":
                                region = "North-East";
                                break;
                            case "3":
                                region = "North-West";
                                break;
                        }

                        pt = fromIntent.getStringExtra("Plan Type");
                        bmi = fromIntent.getStringExtra("BMI");
                        c = fromIntent.getStringExtra("Children");
                        ic = actualCost.substring(0, actualCost.indexOf('.'));

                        email_message = subject + "\n" +
                                "Customer Name: " + cname + "\n" +
                                "Contact Number: +91 " + cno + "\n" +
                                "EmailID: " + emailTo + "\n" +
                                "Plan Type: Rs. " + pt + " lakh" + "\n" +
                                "Term: 1 YEAR" + "\n" +
                                "Gender: " + gender + "\n" +
                                "BMI: " + bmi + "\n" +
                                "Number Of Children: " + c + "\n" +
                                "Smoking Habit: " + smoker + "\n" +
                                "Area Of Region: " + region + "\n" +
                                "Insurance Premium Cost: " + ic + "\n\n\n" +
                                "Thank You! for your purchase from HINDUSTAN HEALTH INSURANCE";

                        savePlan();
                        Toast.makeText(BookPlan.this, "Booking Successful", Toast.LENGTH_LONG).show();
                        sendEmail();

                        Intent toIntent = new Intent(BookPlan.this, UserDashboard.class);
                        toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(toIntent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookPlan.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void onBackPressed() {
        startActivity(new Intent(BookPlan.this, UserDashboard.class));
    }

    private void initObjects() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        user = new User();

        final FirebaseUser currentUser = auth.getCurrentUser();

        assert currentUser != null;
        user.setUid(currentUser.getUid());
        user.setEmail(currentUser.getEmail());

        fromIntent = getIntent();

        display_processing = findViewById(R.id.display_processing);
        premium_cost = findViewById(R.id.premium_cost);
        insurance_cost = findViewById(R.id.insurance_cost);
        plan_type = findViewById(R.id.plan_type);
        applied_date = findViewById(R.id.applied_date);
        expired_date = findViewById(R.id.expiry_date);

        book_insurance = findViewById(R.id.book_insurance);


        display_processing.setText(getResources().getText(R.string.insurance_details));
        insurance_cost.setVisibility(View.VISIBLE);


        actualCost = fromIntent.getStringExtra("Insurance Cost");
        String cost = "Rs: " + actualCost.substring(0, actualCost.indexOf('.'));
        premium_cost.setText(cost);


        String plan = getResources().getString(R.string.plan_type) + ": Rs. " + fromIntent.getStringExtra("Plan Type") + " lakh";
        plan_type.setText(plan);

        Calendar calendar = Calendar.getInstance();
        String todayDate = getResources().getText(R.string.applied_date) + ": " + calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);

        calendar.add(Calendar.YEAR, 1);
        String expiryDate = getResources().getText(R.string.expiry_date) + ": " + calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);

        applied_date.setText(todayDate);
        expired_date.setText(expiryDate);

        book_insurance.setVisibility(View.VISIBLE);
    }

    private void sendEmail() {
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, emailTo, subject, email_message);
        try {
            javaMailAPI.execute();
            Toast.makeText(BookPlan.this, "Email send to the " + cname + " successfully!!", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(BookPlan.this, "Error occurred sending to the email", Toast.LENGTH_LONG).show();
        }
    }

    private void savePlan() {
        String ad = applied_date.getText().toString();
        String ed = expired_date.getText().toString();

        Bookings bookings = new Bookings(cname, cno, emailTo, pt,
                ad.substring(ad.indexOf(':') + 2), ed.substring(ed.indexOf(':') + 2), gender, bmi, c, smoker, region, ic);
        database.getReference().child("Bookings").child(uid).child(pt).setValue(bookings);
    }
}