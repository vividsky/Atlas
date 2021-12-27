package com.example.atlas.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String id;
    private String email;
    private String contact;
    private String password;
    private String name;
    private String gender;
    private String address;
    private String alternateContact;
    private String profile;
    private ArrayList<String> starredUsers;

    public User(String id, String email, String contact, String password) {
        this.id = id;
        this.email = email;
        this.contact = contact;
        this.password = password;
        this.starredUsers = new ArrayList<>();
    }
    public User() {}

    //    setters
    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAlternateContact(String alternateContact) {
        this.alternateContact = alternateContact;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

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

