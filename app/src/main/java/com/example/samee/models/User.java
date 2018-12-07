package com.example.samee.models;

public class User {
    public User(String name,String city, String dob,String country,String number) {
        this.name = name;
        this.city = city;
        this.dob = dob;
        this.country = country;
        this.number=number;
    }


    String name;
    String city;
    String dob;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    String country;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    String number;

}
