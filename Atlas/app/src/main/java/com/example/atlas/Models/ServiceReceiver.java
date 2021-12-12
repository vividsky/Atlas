package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceReceiver implements Serializable {
    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User userDetails) {
        this.userDetails = userDetails;
    }

    public User userDetails;
    public ArrayList<String> requirements;

    public ServiceReceiver(ArrayList<String> requirements, User userDetails) {
        this.requirements = requirements;
        this.userDetails = userDetails;
    }
    public ServiceReceiver() {}

    public ArrayList<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(ArrayList<String> requirements) {
        this.requirements = requirements;
    }
}


