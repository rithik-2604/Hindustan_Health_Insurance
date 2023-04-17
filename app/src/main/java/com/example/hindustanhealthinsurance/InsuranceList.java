package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hindustanhealthinsurance.Models.Bookings;
import com.example.hindustanhealthinsurance.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InsuranceList extends AppCompatActivity {
    public static final String MSG = "com.example.hindustanhealthinsurance.InsuranceList";

    FirebaseAuth auth;
    FirebaseDatabase database;

    private CardView plan_1;
    private CardView plan_2;
    private CardView plan_3;
    private CardView plan_4;
    private CardView plan_5;

    private ImageView dropdown;
    private ImageView go_back;

    User u;
    Bookings b;

    private List<String> plansList;

    private int flagCheckList = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_insurance_list);

        initObjects();

        dropdown.setOnClickListener(view -> {
            if (flagCheckList == 0) {
                plan_1.setVisibility(View.GONE);
                plan_2.setVisibility(View.GONE);
                plan_3.setVisibility(View.GONE);
                plan_4.setVisibility(View.GONE);
                plan_5.setVisibility(View.GONE);

                flagCheckList = 1;

                dropdown.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
            }
            else {
                plan_1.setVisibility(View.VISIBLE);
                plan_2.setVisibility(View.VISIBLE);
                plan_3.setVisibility(View.VISIBLE);
                plan_4.setVisibility(View.VISIBLE);
                plan_5.setVisibility(View.VISIBLE);

                flagCheckList = 0;

                dropdown.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
            }
        });

        plan_1.setOnClickListener(view -> planSelect("1"));
        plan_2.setOnClickListener(view -> planSelect("2"));
        plan_3.setOnClickListener(view -> planSelect("3"));
        plan_4.setOnClickListener(view -> planSelect("5"));
        plan_5.setOnClickListener(view -> planSelect("10"));

        go_back.setOnClickListener(view -> startActivity(new Intent(InsuranceList.this, UserDashboard.class)));
    }

    private void initObjects() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        plan_1 = findViewById(R.id.plan_1);
        plan_2 = findViewById(R.id.plan_2);
        plan_3 = findViewById(R.id.plan_3);
        plan_4 = findViewById(R.id.plan_4);
        plan_5 = findViewById(R.id.plan_5);

        dropdown = findViewById(R.id.dropdown);
        go_back = findViewById(R.id.go_back);

        u = new User();
        b = new Bookings();

        final FirebaseUser currentUser = auth.getCurrentUser();

        assert currentUser != null;
        u.setUid(currentUser.getUid());
        u.setEmail(currentUser.getEmail());

        if (u.getUid() != null) {
            plansList = new ArrayList<>();
            database.getReference().child("Bookings").child(u.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String cPlan = ds.child("pt").getValue(String.class);
                            plansList.add(cPlan);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        plan_1.setVisibility(View.GONE);
        plan_2.setVisibility(View.GONE);
        plan_3.setVisibility(View.GONE);
        plan_4.setVisibility(View.GONE);
        plan_5.setVisibility(View.GONE);
    }

    private void planSelect(String s) {
        if (!plansList.contains(s)) {
            Intent toIntent = new Intent(InsuranceList.this, ApplyInsurance.class);
            toIntent.putExtra(MSG, s);
            startActivity(toIntent);
        }
        else {
            Toast.makeText(InsuranceList.this, "The plan has been purchased already", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InsuranceList.this, UserDashboard.class));
    }
}