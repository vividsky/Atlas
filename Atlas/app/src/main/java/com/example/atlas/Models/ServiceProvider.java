package com.example.atlas.Models;

import java.io.Serializable;

public class ServiceProvider implements Serializable {

    public String speciality;
    public int experience;
    public int expectedWage;
    public String vehicleOwned;
    public String id;
    public User userDetails;

    public ServiceProvider() {
    }

    public ServiceProvider(String id, String speciality, int experience, int expectedWage, String vehicleOwned, User userDetails) {
        this.id = id;
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

    public int getExperience() {
        return experience;
    }

    public int getExpectedWage() {
        return expectedWage;
    }

    public String getVehicleOwned() {
        return vehicleOwned;
    }

    public User getUserDetails() {
        return userDetails;
    }


}
