/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;


/**
 * Class to store movie trailer information from the Movie DB API.
 * https://developers.themoviedb.org/3/movies/get-movie-videos
 */

class MovieTrailer {
    int movieId;
    String id, key, name, site, size, type;


    MovieTrailer(String vId, int vMovieId, String vKey, String vName, String vSite, String vSize,
                 String vType) {
        this.id = vId;
        this.movieId = vMovieId;
        this.key = vKey;
        this.name = vName;
        this.site = vSite;
        this.size = vSize;
        this.type = vType;
    }

}