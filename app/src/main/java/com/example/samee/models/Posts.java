package com.example.samee.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Posts implements Serializable{
    String tripName,tripType, location;
    ArrayList<String>uris;
public Posts()
{

}
    public Posts(java.lang.String tripName, java.lang.String tripType, java.lang.String location, ArrayList<java.lang.String> uris) {
        this.tripName = tripName;
        this.tripType = tripType;
        this.location = location;
        this.uris = uris;
    }

    public java.lang.String getTripName() {
        return tripName;
    }

    public void setTripName(java.lang.String tripName) {
        this.tripName = tripName;
    }

    public java.lang.String getTripType() {
        return tripType;
    }

    public void setTripType(java.lang.String tripType) {
        this.tripType = tripType;
    }

    public java.lang.String getLocation() {
        return location;
    }

    public void setLocation(java.lang.String location) {
        this.location = location;
    }

    public ArrayList<java.lang.String> getUris() {
        return uris;
    }

    public void setUris(ArrayList<java.lang.String> uris) {
        this.uris = uris;
    }
}
