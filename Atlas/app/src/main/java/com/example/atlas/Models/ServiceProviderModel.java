package com.example.atlas.Models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class ServiceProviderModel {
    public ArrayList<String> specialities;
    public String experience;
    public String expectedWage;
    public String  vehicleOwned;
    public UsersModel userDetails;

    public ServiceProviderModel(ArrayList<String> specialities, String experience, String expectedWage, String vehicleOwned, UsersModel userDetails) {
        this.specialities = specialities;
        this.experience = experience;
        this.expectedWage = expectedWage;
        this.vehicleOwned = vehicleOwned;
        this.userDetails = userDetails;
    }
    public ServiceProviderModel() {}
}
