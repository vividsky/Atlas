package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceProvider implements Serializable {
    public ArrayList<String> specialities;
    public String experience;
    public String expectedWage;
    public String  vehicleOwned;
    public User userDetails;

    public ArrayList<String> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(ArrayList<String> specialities) {
        this.specialities = specialities;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getExpectedWage() {
        return expectedWage;
    }

    public void setExpectedWage(String expectedWage) {
        this.expectedWage = expectedWage;
    }

    public String getVehicleOwned() {
        return vehicleOwned;
    }

    public void setVehicleOwned(String vehicleOwned) {
        this.vehicleOwned = vehicleOwned;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User userDetails) {
        this.userDetails = userDetails;
    }

    public ServiceProvider(ArrayList<String> specialities, String experience, String expectedWage, String vehicleOwned, User userDetails) {
        this.specialities = specialities;
        this.experience = experience;
        this.expectedWage = expectedWage;
        this.vehicleOwned = vehicleOwned;
        this.userDetails = userDetails;
    }
    public ServiceProvider() {}
}
