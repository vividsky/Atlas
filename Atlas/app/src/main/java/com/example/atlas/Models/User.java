package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String id;
    private String email;
    private String contact;
    private String name;
    private String gender;
    private String address;
    private String alternateContact;
    private String profile;
    private ArrayList<String> starredUsers;

    public User(String id, String email, String contact) {
        this.id = id;
        this.email = email;
        this.contact = contact;
        this.starredUsers = new ArrayList<>();
    }
    public User() {}

    // getters

    public String getEmail() {
        return email;
    }

    public String getProfile() {
        return profile;
    }

    public String getContact() {
        return contact;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getAlternateContact() {
        return alternateContact;
    }

    public ArrayList<String> getStarredUsers() {
        return starredUsers;
    }
}

