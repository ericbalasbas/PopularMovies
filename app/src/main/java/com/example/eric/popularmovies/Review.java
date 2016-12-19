/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

/**
 * Class to store movie review information from the Movie DB API.
 * https://developers.themoviedb.org/3/movies/get-movie-reviews
 */


public class Review {
    int movieId;
    String id, author, content, url;


    Review(String vId, int vMovieId, String vAuthor, String vContent, String vUrl) {
        this.id = vId;
        this.movieId = vMovieId;
        this.author = vAuthor;
        this.content = vContent;
        this.url = vUrl;
    }

}
