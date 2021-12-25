package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceProvider implements Serializable {

    public String speciality;
    public String experience;
    public String expectedWage;
    public String  vehicleOwned;
    public User userDetails;

    public ServiceProvider() {}

    public ServiceProvider(String speciality, String experience, String expectedWage, String vehicleOwned, User userDetails) {
        this.speciality = speciality;
        this.experience = experience;
        this.expectedWage = expectedWage;
        this.vehicleOwned = vehicleOwned;
        this.userDetails = userDetails;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getExperience() {
        return experience;
    }

    public String getExpectedWage() {
        return expectedWage;
    }

    public String getVehicleOwned() {
        return vehicleOwned;
    }

    public User getUserDetails() {
        return userDetails;
    }


}
