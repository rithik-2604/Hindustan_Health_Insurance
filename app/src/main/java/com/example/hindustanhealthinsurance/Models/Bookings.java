package com.example.hindustanhealthinsurance.Models;

import androidx.annotation.NonNull;

public class Bookings {
    private String cName;
    private String cNo;
    private String cEmail;
    private String pt;
    //By Default: 1 Term Year

    private String appliedDate;
    private String expiryDate;

    private String gender;
    private String bmi;
    private String children;
    private String smoker;
    private String region;
    private String cost;

    public Bookings() {}

    public Bookings(String cName, String cNo, String cEmail, String pt, String appliedDate, String expiryDate, String gender, String bmi, String children, String smoker, String region, String cost) {
        this.cName = cName;
        this.cNo = cNo;
        this.cEmail = cEmail;
        this.pt = pt;
        this.appliedDate = appliedDate;
        this.expiryDate = expiryDate;
        this.gender = gender;
        this.bmi = bmi;
        this.children = children;
        this.smoker = smoker;
        this.region = region;
        this.cost = cost;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcNo() {
        return cNo;
    }

    public void setcNo(String cNo) {
        this.cNo = cNo;
    }

    public String getcEmail() {
        return cEmail;
    }

    public void setcEmail(String cEmail) {
        this.cEmail = cEmail;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getSmoker() {
        return smoker;
    }

    public void setSmoker(String smoker) {
        this.smoker = smoker;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}