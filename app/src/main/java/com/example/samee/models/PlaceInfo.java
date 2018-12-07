package com.example.samee.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by samee on 4/19/2018.
 */

public class PlaceInfo {
    public PlaceInfo(String name,String address,String phoneNumber, String id,String attribution, Uri uri, LatLng latLng, float rating) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.attribution = attribution;
        this.uri = uri;
        this.latLng = latLng;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", attribution='" + attribution + '\'' +
                ", uri=" + uri +
                ", latLng=" + latLng +
                ", rating=" + rating +
                '}';
    }

    public PlaceInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private String name,address,phoneNumber,id,attribution;
    private Uri uri;
    private LatLng latLng;
    private float rating;


}
