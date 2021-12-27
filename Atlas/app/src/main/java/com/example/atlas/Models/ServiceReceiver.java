package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ServiceReceiver implements Serializable {

    public User userDetails;
    public ArrayList<String> requirements;
    public String id;

    public ServiceReceiver() {}

    public ServiceReceiver(String id, ArrayList<String> requirements, User userDetails) {
        this.id = id;
        this.requirements = requirements;
        this.userDetails = userDetails;
    }

    public ArrayList<String> getRequirements() {
        return requirements;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public String getId() {
        return id;
    }


}


