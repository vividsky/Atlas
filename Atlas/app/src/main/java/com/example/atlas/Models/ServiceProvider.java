package com.example.atlas.Models;

import java.util.ArrayList;

public class ServiceProvider {
    public ArrayList<String> specialities;
    public String experience;
    public String expectedWage;
    public String  vehicleOwned;
    public User userDetails;

    public ServiceProvider(ArrayList<String> specialities, String experience, String expectedWage, String vehicleOwned, User userDetails) {
        this.specialities = specialities;
        this.experience = experience;
        this.expectedWage = expectedWage;
        this.vehicleOwned = vehicleOwned;
        this.userDetails = userDetails;
    }
    public ServiceProvider() {}
}
