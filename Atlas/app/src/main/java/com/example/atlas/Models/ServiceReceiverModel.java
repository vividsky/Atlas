package com.example.atlas.Models;

import java.util.ArrayList;

public class ServiceReceiverModel {
    public UsersModel userDetails;
    public ArrayList<String> requirements;

    public ServiceReceiverModel(ArrayList<String> requirements, UsersModel userDetails) {
        this.requirements = requirements;
        this.userDetails = userDetails;
    }

    public ArrayList<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(ArrayList<String> requirements) {
        this.requirements = requirements;
    }
}


