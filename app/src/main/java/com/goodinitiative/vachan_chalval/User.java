package com.goodinitiative.vachan_chalval;

/**
 * Created by mahesh on 11/8/2017.
 */

public class User {
    String name,contact,address;
    String profileurl;

    public User() {
    }

    public User(String name, String contact, String address, String profileurl) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.profileurl = profileurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }
}
