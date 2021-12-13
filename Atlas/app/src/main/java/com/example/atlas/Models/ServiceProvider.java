package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceProvider implements Serializable {

    public ArrayList<String> specialities;
    public String experience;
    public String expectedWage;
    public String  vehicleOwned;
    public User userDetails;

    public ServiceProvider() {}

    public ServiceProvider(ArrayList<String> specialities, String experience, String expectedWage, String vehicleOwned, User userDetails) {
        this.specialities = specialities;
        this.experience = experience;
        this.expectedWage = expectedWage;
        this.vehicleOwned = vehicleOwned;
        this.userDetails = userDetails;
    }

    public ArrayList<String> getSpecialities() {
        return specialities;
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
