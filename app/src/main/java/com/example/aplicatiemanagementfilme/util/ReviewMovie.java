package com.example.aplicatiemanagementfilme.util;

import java.io.Serializable;

public class ReviewMovie implements Serializable {
    private String id;
    private String movieTitle;
    private float userRating;

    public ReviewMovie() {
    }

    public ReviewMovie(String id, String movieTitle, float userRating) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.userRating = userRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    @Override
    public String toString() {
        return "ReviewMovie{" +
                "id='" + id + '\'' +
                ", movieTitle='" + movieTitle + '\'' +
                ", userRating=" + userRating +
                '}';
    }
}
