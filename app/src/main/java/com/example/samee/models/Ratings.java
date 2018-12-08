package com.example.samee.models;

import java.util.ArrayList;
import java.util.List;

public class Ratings {
    String user,rating,review;
    ArrayList<String> uris;

    public Ratings()
{

}
    public Ratings(String user, String rating,String review,ArrayList<String>uris) {
        this.user = user;
        this.rating = rating;
        this.review=review;
        this.uris=uris;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public ArrayList<String> getUris() {
        return uris;
    }

    public void setUris(ArrayList<String> uris) {
        this.uris = uris;
    }
}
