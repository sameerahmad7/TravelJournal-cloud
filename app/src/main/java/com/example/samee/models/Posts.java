package com.example.samee.models;

import java.util.ArrayList;

public class Posts {
    String tripName,tripType, location;
    ArrayList<String>uris;
public Posts()
{

}
    public Posts(String tripName, String tripType, String location, ArrayList<String> uris) {
        this.tripName = tripName;
        this.tripType = tripType;
        this.location = location;
        this.uris = uris;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getUris() {
        return uris;
    }

    public void setUris(ArrayList<String> uris) {
        this.uris = uris;
    }
}
