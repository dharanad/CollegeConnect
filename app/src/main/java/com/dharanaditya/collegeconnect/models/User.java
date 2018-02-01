/*
 * Copyright (c) 2018 Dharan Aditya <dharan.aditya@gmail.com>
 */

package com.dharanaditya.collegeconnect.models;

/**
 * Created by dharanaditya on 31/01/18.
 */

public class User {
    private String firstName;
    private String lastName;
    private String collegeID;
    private String userType;
    private String email;
    private String phoneNumber;

    public User() {
    }

    public User(String firstName, String lastName, String collegeID, String userType, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.collegeID = collegeID;
        this.userType = userType;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCollegeID() {
        return collegeID;
    }

    public void setCollegeID(String collegeID) {
        this.collegeID = collegeID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", collegeID='" + collegeID + '\'' +
                ", userType='" + userType + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
