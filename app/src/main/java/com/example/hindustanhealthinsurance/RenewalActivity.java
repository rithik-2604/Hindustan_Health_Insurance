package com.example.hindustanhealthinsurance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class RenewalActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> list;
    ListView renewalListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_renewal);

        initObjects();

        renewalListView.setOnItemClickListener((adapterView, view, i, l) -> {
            String type = list.get(i).get("cost");
            assert type != null;
            String pt = type.substring((type.indexOf('.') + 2), (type.lastIndexOf('l'))).trim();

            Intent toIntent = new Intent(RenewalActivity.this, RenewalPlanDetails.class);
            toIntent.putExtra("pType", pt);
            startActivity(toIntent);
        });
    }


    public void onBackPressed() {
        startActivity(new Intent(RenewalActivity.this, UserDashboard.class));
        finish();
    }

    private void initObjects() {
        renewalListView = findViewById(R.id.renewalListView);
        list = new ArrayList<>();


        Intent fromIntent = getIntent();

        StringTokenizer st1 = new StringTokenizer(fromIntent.getStringExtra("rpts"), "/");
        StringTokenizer st2 = new StringTokenizer(fromIntent.getStringExtra("reds"), "/");
        while((st1.hasMoreTokens()) && (st2.hasMoreTokens())) {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("cost", "Plan Type: " + "Rs. " + st1.nextToken() + " lakh(s)");
            hm.put("expiry", "Expiry Date: " + st2.nextToken());

            list.add(hm);
        }

        String[] from = {"cost", "expiry"};
        int[] to = {R.id.plan_cost, R.id.plan_expiry};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.layout_plans, from, to);
        renewalListView.setAdapter(simpleAdapter);
    }
}