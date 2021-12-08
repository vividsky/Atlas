package com.example.atlas.Models;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class ServiceProviderModel {
    ArrayList<String> specialities;
    int experience;
    int expectedWage;
    boolean vehicleOwned;
    User userDetails;

    public ServiceProviderModel(ArrayList<String> specialities, int experience, int expectedWage, boolean vehicleOwned, User userDetails) {
        this.specialities = specialities;
        this.experience = experience;
        this.expectedWage = expectedWage;
        this.vehicleOwned = vehicleOwned;
        this.userDetails = userDetails;
    }
}
