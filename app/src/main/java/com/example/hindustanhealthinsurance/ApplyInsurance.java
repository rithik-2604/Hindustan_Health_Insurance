package com.example.hindustanhealthinsurance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApplyInsurance extends AppCompatActivity {

    //Attributes
    public static final String MSG = "com.example.hindustanhealthinsurance.ApplyInsurance";
    private static final String url = "https://hindustanhealthinsurance.herokuapp.com/predict";

    private RadioGroup groupPlan;
    private RadioButton plan_1;
    private RadioButton plan_2;
    private RadioButton plan_3;
    private RadioButton plan_4;
    private RadioButton plan_5;

    private EditText dob;

    private RadioGroup groupGender;
    private RadioButton male;
    private RadioButton female;

    private EditText bmi;

    private RadioGroup groupSmoker;
    private RadioButton smoker_yes;
    private RadioButton smoker_no;

    private RadioGroup groupRegion;
    private RadioButton north_east;
    private RadioButton north_west;
    private RadioButton south_east;
    private RadioButton south_west;

    private String message;

    private Calendar calendar;
    private int g = -1, s = -1, r = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_apply_insurance);

        //Initializing all the fields with theirs corresponding ID's
        groupPlan = findViewById(R.id.plan_opted);
        plan_1 = findViewById(R.id.plan_1);
        plan_2 = findViewById(R.id.plan_2);
        plan_3 = findViewById(R.id.plan_3);
        plan_4 = findViewById(R.id.plan_4);
        plan_5 = findViewById(R.id.plan_5);

        dob = findViewById(R.id.dob);

        groupGender = findViewById(R.id.gender);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        bmi = findViewById(R.id.bmi);

        groupSmoker = findViewById(R.id.smoker);
        smoker_yes = findViewById(R.id.smoke_yes);
        smoker_no = findViewById(R.id.smoke_no);

        groupRegion = findViewById(R.id.region);
        north_east = findViewById(R.id.north_east);
        north_west = findViewById(R.id.north_west);
        south_east = findViewById(R.id.south_east);
        south_west = findViewById(R.id.south_west);

        Button premium_cost = findViewById(R.id.premium_cost);

        calendar = Calendar.getInstance();

        Intent fromIntent = getIntent();
        message = fromIntent.getStringExtra(InsuranceList.MSG);

        if (message.equalsIgnoreCase("1"))
            plan_1.setChecked(true);
        else if (message.equalsIgnoreCase("2"))
            plan_2.setChecked(true);
        else if (message.equalsIgnoreCase("3"))
            plan_3.setChecked(true);
        else if (message.equalsIgnoreCase("5"))
            plan_4.setChecked(true);
        else if (message.equalsIgnoreCase("10"))
            plan_5.setChecked(true);


        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabel();
        };

        dob.setOnClickListener(view -> new DatePickerDialog(ApplyInsurance.this, date,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        premium_cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setValidation() == 1) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String insuranceCost = jsonObject.getString("Insurance Cost");

                                        String plan = message;
                                        Intent book_intent = new Intent(ApplyInsurance.this, BookPlan.class);
                                        book_intent.putExtra("Plan Type", plan);
                                        book_intent.putExtra("Date of Birth", dob.getText().toString());
                                        book_intent.putExtra("Gender", "" + g);
                                        book_intent.putExtra("Smoker", "" + s);
                                        book_intent.putExtra("BMI", bmi.getText().toString().trim());
                                        book_intent.putExtra("Children", "0");
                                        book_intent.putExtra("Region", "" + r);
                                        book_intent.putExtra("Insurance Cost", insuranceCost);
                                        startActivity(book_intent);
                                    }
                                    catch(JSONException e) {
                                        Toast.makeText(ApplyInsurance.this, "Oops! Server Error, Please try again", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(ApplyInsurance.this, InsuranceList.class));
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ApplyInsurance.this, "Oops! Server Error, Please try again", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ApplyInsurance.this, InsuranceList.class));
                                }
                            }) {

                      @Override
                      protected Map<String, String> getParams() {
                          Map<String, String> params = new HashMap<String, String>();
                          RadioButton gender, smoke, region;

                          Calendar todayDate = Calendar.getInstance();
                          params.put("age", String.valueOf(todayDate.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)));

                          gender = findViewById(groupGender.getCheckedRadioButtonId());
                          if (gender.getText().toString().equalsIgnoreCase("MALE")) {
                              g = 0;
                          }
                          else if (gender.getText().toString().equalsIgnoreCase("FEMALE")) {
                              g = 1;
                          }
                          params.put("sex", String.valueOf(g));

                          params.put("bmi", bmi.getText().toString().trim());
                          params.put("children", "0");

                          smoke = findViewById(groupSmoker.getCheckedRadioButtonId());
                          if (smoke.getText().toString().equalsIgnoreCase("YES")) {
                              s = 0;
                          }
                          else if (smoke.getText().toString().equalsIgnoreCase("NO")) {
                              s = 1;
                          }
                          params.put("smoker", String.valueOf(s));

                          region = findViewById(groupRegion.getCheckedRadioButtonId());
                          if (region.getText().toString().equalsIgnoreCase("SOUTH-EAST")) {
                              r = 0;
                          }
                          else if (region.getText().toString().equalsIgnoreCase("SOUTH-WEST")) {
                              r = 1;
                          }
                          else if (region.getText().toString().equalsIgnoreCase("NORTH-EAST")) {
                              r = 2;
                          }
                          else if (region.getText().toString().equalsIgnoreCase("NORTH-WEST")) {
                              r = 3;
                          }
                          params.put("region", String.valueOf(r));

                          return params;
                      }
                    };

                    RequestQueue queue = Volley.newRequestQueue(ApplyInsurance.this);
                    queue.add(stringRequest);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ApplyInsurance.this, UserDashboard.class));
    }

    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(dateFormat.format(calendar.getTime()));
    }

    private int setValidation() {
        int valid_flag = 1;
        int flag_plan = 0, flag_gender = 0, flag_smoker = 0, flag_region = 0;

        //Initially, all the fields have NO errors
        plan_1.setError(null);
        plan_2.setError(null);
        plan_3.setError(null);
        plan_4.setError(null);
        plan_5.setError(null);

        dob.setError(null);

        male.setError(null);
        female.setError(null);

        bmi.setError(null);

        smoker_yes.setError(null);
        smoker_no.setError(null);

        north_east.setError(null);
        north_west.setError(null);
        south_east.setError(null);
        south_west.setError(null);

        flag_plan = groupPlan.getCheckedRadioButtonId();
        if (flag_plan == -1) {
            plan_1.setError(getResources().getString(R.string.radio_button_error));
            plan_2.setError(getResources().getString(R.string.radio_button_error));
            plan_3.setError(getResources().getString(R.string.radio_button_error));
            plan_4.setError(getResources().getString(R.string.radio_button_error));
            plan_5.setError(getResources().getString(R.string.radio_button_error));
            valid_flag = 0;
        }

        if (dob.getText().toString().trim().equalsIgnoreCase("")) {
            dob.setError(getResources().getString(R.string.error_dob));
            valid_flag = 0;
        }

        flag_gender = groupGender.getCheckedRadioButtonId();
        if (flag_gender == -1) {
            male.setError(getResources().getString(R.string.radio_button_error));
            female.setError(getResources().getString(R.string.radio_button_error));
            valid_flag = 0;
        }

        if (bmi.getText().toString().trim().equalsIgnoreCase("")) {
            bmi.setError(getResources().getString(R.string.error_bmi));
            valid_flag = 0;
        }

        flag_smoker = groupSmoker.getCheckedRadioButtonId();
        if (flag_smoker == -1) {
            smoker_yes.setError(getResources().getString(R.string.radio_button_error));
            smoker_no.setError(getResources().getString(R.string.radio_button_error));
            valid_flag = 0;
        }

        flag_region = groupRegion.getCheckedRadioButtonId();
        if (flag_region == -1) {
            north_east.setError(getResources().getString(R.string.radio_button_error));
            north_west.setError(getResources().getString(R.string.radio_button_error));
            south_east.setError(getResources().getString(R.string.radio_button_error));
            south_west.setError(getResources().getString(R.string.radio_button_error));
            valid_flag = 0;
        }

        return valid_flag;
    }
}