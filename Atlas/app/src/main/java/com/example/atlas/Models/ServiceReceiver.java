package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceReceiver implements Serializable {

    public User userDetails;
    public ArrayList<String> requirements;

    public ServiceReceiver() {}

    public ServiceReceiver(ArrayList<String> requirements, User userDetails) {
        this.requirements = requirements;
        this.userDetails = userDetails;
    }

    public ArrayList<String> getRequirements() {
        return requirements;
    }

    public User getUserDetails() {
        return userDetails;
    }

}


