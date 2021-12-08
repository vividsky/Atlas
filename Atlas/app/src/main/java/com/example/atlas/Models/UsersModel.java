package com.example.atlas.Models;

public class UsersModel {

    private final String id;
    private final String email;
    private final String contact;
    private String password;
    private String name;
    // male -> 0, female -> 1
    private String gender;
    private String address;
    private String alternateContact;
    // type -> SP or NoS
    private String type;

    public UsersModel(String id, String email, String contact, String password) {
        this.id = id;
        this.email = email;
        this.contact = contact;
        this.password = password;
    }

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

    public void setType(String type) {
        this.type = type;
    }

    // getters
    public String getUserId() {
        return id;
    }

    public String getEmail() {
        return email;
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

    public String getType() {
        return type;
    }

}
