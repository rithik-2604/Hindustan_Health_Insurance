package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.hindustanhealthinsurance.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDashboard extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;

    User user;

    private CardView history;
    private CardView group_chat;
    private CardView apply_plan;
    private CardView renewal;
    private CardView logout;

    List<String> planTypes;
    List<String> expiryDates;

    List<String> rPlanTypes;
    List<String> rExpiryDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        initObjects();

        history.setOnClickListener(view -> {
            DatabaseReference bookingsReference = FirebaseDatabase.getInstance().getReference("Bookings").
                    child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            bookingsReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String currentPlan = ds.child("pt").getValue(String.class);

                            DatabaseReference currentBooking = FirebaseDatabase.getInstance().getReference("Bookings").
                                    child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(Objects.requireNonNull(currentPlan));
                            currentBooking.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String pt, expiryDate;
                                    pt = snapshot.child("pt").getValue(String.class);
                                    expiryDate = snapshot.child("expiryDate").getValue(String.class);

                                    planTypes.add(pt);
                                    expiryDates.add(expiryDate);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        StringBuilder pts = new StringBuilder();
                        StringBuilder eds = new StringBuilder();

                        for (int i = 0; i < planTypes.size(); i++) {
                            pts.append(planTypes.get(i)).append("/");
                            eds.append(expiryDates.get(i)).append("/");
                        }

                        if ((pts.toString().length() > 0) && (eds.toString().length() > 0)) {
                            Intent intent = new Intent(UserDashboard.this, HistoryActivity.class);
                            intent.putExtra("pts", pts.toString());
                            intent.putExtra("eds", eds.toString());

                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        renewal.setOnClickListener(view -> {
            DatabaseReference bookingsReference = FirebaseDatabase.getInstance().getReference("Bookings").
                    child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            bookingsReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String currentPlan = ds.child("pt").getValue(String.class);

                            DatabaseReference currentBooking = FirebaseDatabase.getInstance().getReference("Bookings").
                                    child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(Objects.requireNonNull(currentPlan));
                            currentBooking.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String rpt, rexpiryDate;
                                    rpt = snapshot.child("pt").getValue(String.class);
                                    rexpiryDate = snapshot.child("expiryDate").getValue(String.class);

                                    rPlanTypes.add(rpt);
                                    rExpiryDates.add(rexpiryDate);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        StringBuilder rpts = new StringBuilder();
                        StringBuilder reds = new StringBuilder();

                        for (int i = 0; i < rPlanTypes.size(); i++) {
                            rpts.append(rPlanTypes.get(i)).append("/");
                            reds.append(rExpiryDates.get(i)).append("/");
                        }

                        if ((rpts.toString().length() > 0) && (reds.toString().length() > 0)) {
                            Intent intent = new Intent(UserDashboard.this, RenewalActivity.class);
                            intent.putExtra("rpts", rpts.toString());
                            intent.putExtra("reds", reds.toString());

                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        apply_plan.setOnClickListener(view -> startActivity(new Intent(UserDashboard.this, InsuranceList.class)));
        group_chat.setOnClickListener(view -> startActivity(new Intent(UserDashboard.this, GroupChatActivity.class)));


        logout.setOnClickListener(view -> {
            if (user.getUid() != null) {
                auth.signOut();
            }

            Intent intent = new Intent(UserDashboard.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(UserDashboard.this, "Please click LOGOUT to exit", Toast.LENGTH_SHORT).show();
    }

    public void initObjects() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        user = new User();

        final FirebaseUser currentUser = auth.getCurrentUser();

        assert currentUser != null;
        user.setUid(currentUser.getUid());
        user.setEmail(currentUser.getEmail());

        history = findViewById(R.id.history);
        group_chat = findViewById(R.id.group_chat);
        apply_plan = findViewById(R.id.apply_plan);
        renewal = findViewById(R.id.renewal);
        logout = findViewById(R.id.logout);

        planTypes = new ArrayList<>();
        expiryDates = new ArrayList<>();

        rPlanTypes = new ArrayList<>();
        rExpiryDates = new ArrayList<>();
    }
}