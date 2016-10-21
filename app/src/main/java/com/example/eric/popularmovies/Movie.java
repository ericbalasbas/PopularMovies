package com.example.eric.popularmovies;

public class Movie {
    int id;
    String poster_path, title;
    Double vote_average, popularity;

    public Movie(int vId, String vTitle, String vPoster_path, Double vVote_average, Double vPopularity)
    {
        // this.versionName = vName;
        this.id = vId;
        this.title = vTitle;
        this.poster_path = vPoster_path;
        this.vote_average = vVote_average;
        this.popularity = vPopularity;
    }
}
