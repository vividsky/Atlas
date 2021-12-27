package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class ServiceProvider implements Serializable {

    public String speciality;
    public String experience;
    public String expectedWage;
    public String  vehicleOwned;
    public String id;
    public User userDetails;

    public ServiceProvider() {}

    public ServiceProvider(String id, String speciality, String experience, String expectedWage, String vehicleOwned, User userDetails) {
        this.id  = id;
        this.speciality = speciality;
        this.experience = experience;
        this.expectedWage = expectedWage;
        this.vehicleOwned = vehicleOwned;
        this.userDetails = userDetails;
    }

    public String getId() {
        return id;
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
