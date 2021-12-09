package com.example.atlas.Models;

public class User {

    private String id;
    private String email;
    private String contact;
    private String password;
    private String name;
    private String gender;
    private String address;
    private String alternateContact;
    private String profile;

    public User(String id, String email, String contact, String password) {
        this.id = id;
        this.email = email;
        this.contact = contact;
        this.password = password;
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
    public void setProfile(String profile) { this.profile = profile;}

    // getters
    public String getUserId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getProfile() {
        return profile;
    }


    public String getContact() {
        return contact;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
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
}

