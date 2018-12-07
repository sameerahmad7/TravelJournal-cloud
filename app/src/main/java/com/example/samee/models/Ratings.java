package com.example.samee.models;

public class Ratings {
    String user,rating;
public Ratings()
{

}
    public Ratings(String user, String rating) {
        this.user = user;
        this.rating = rating;
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
}
