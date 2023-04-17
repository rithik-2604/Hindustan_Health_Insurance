package com.example.hindustanhealthinsurance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hindustanhealthinsurance.Models.Bookings;

import java.util.ArrayList;

public class PlanListAdapter extends ArrayAdapter<Bookings> {
    Context pContext;
    int pResource;

    public PlanListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Bookings> objects) {
        super(context, resource, objects);
        pContext = context;
        pResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String cName, cNo, cEmail, pt;
        String appliedDate, expiryDate;
        String gender, bmi, children, smoker, region, cost;

        cName = getItem(position).getcName();
        cNo = getItem(position).getcNo();
        cEmail = getItem(position).getcEmail();
        pt = getItem(position).getPt();

        appliedDate = getItem(position).getAppliedDate();
        expiryDate = getItem(position).getExpiryDate();

        gender = getItem(position).getGender();
        bmi = getItem(position).getBmi();
        children = getItem(position).getChildren();
        smoker = getItem(position).getSmoker();
        region = getItem(position).getRegion();
        cost = getItem(position).getCost();

        Bookings b = new Bookings(cName, cNo, cEmail, pt,
                appliedDate, expiryDate,
                gender, bmi, children, smoker, region, cost);


        LayoutInflater inflater = LayoutInflater.from(pContext);
        convertView = inflater.inflate(pResource, parent, false);

        TextView pType = (TextView) convertView.findViewById(R.id.plan_cost);
        TextView eDate = (TextView) convertView.findViewById(R.id.expiry_date);

        pType.setText(pt);
        eDate.setText(expiryDate);

        return convertView;
    }
}