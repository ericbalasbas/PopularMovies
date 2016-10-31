/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import java.util.Date;

/**
 * Class to store movie information from the Movie DB API.
 * Currently use three queries to the Movie DB:
 * https://developers.themoviedb.org/3/movies/get-popular-movies
 * https://developers.themoviedb.org/3/movies/get-top-rated-movies
 * https://developers.themoviedb.org/3/movies/get-movie-details
 */
class Movie {
    int id;
    String poster_path, title, overview;
    Double vote_average, popularity;
    Date release_date;

    Movie(int vId, String vTitle, String vPoster_path)
    {
        this(vId, vTitle, vPoster_path, "", new Date(), 0.0, 0.0);
    }
    Movie(int vId, String vTitle, String vPoster_path, String vOverview, Date vRelease_date,
                 Double vVote_average, Double vPopularity)
    {
        this.id = vId;
        this.title = vTitle;
        this.poster_path = vPoster_path;
        this.overview = vOverview;
        this.release_date = vRelease_date;
        this.vote_average = vVote_average;
        this.popularity = vPopularity;
    }
}
