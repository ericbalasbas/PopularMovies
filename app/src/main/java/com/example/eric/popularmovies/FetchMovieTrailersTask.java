/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Fetch list of trailers for a movie in background thread, parse results and load list into movieTrailerListAdapter.
 */

class FetchMovieTrailersTask extends AsyncTask<String, Void, ArrayList<MovieTrailer>> {
    private final String LOG_TAG = FetchMovieTrailersTask.class.getSimpleName();
    private String MovieId;
    private MovieTrailerListAdapter movieTrailerListAdapter;

    FetchMovieTrailersTask(String vMovieId, MovieTrailerListAdapter vMovieTrailerListAdapter) {
        super();
        // set activity
        MovieId = vMovieId;
        movieTrailerListAdapter = vMovieTrailerListAdapter;
    }

    @Override
    protected ArrayList<MovieTrailer> doInBackground(String... params) {
        // Will contain the raw JSON response as a string.
        String movieTrailersJsonStr;

        try {
            // catch IOException already catches MalformedURLException, no need to test for
            // null url strings here

            URL url = new URL(MovieDb.buildMovieTrailerUri(MovieId).toString());

            movieTrailersJsonStr = MovieDb.getJson(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        }

        try {
            return MovieDb.getMovieTrailersDataFromJson(movieTrailersJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set movie object to be result, and update all view fields with new movie details.
     * @param result - movie object
     */
    @Override
    protected void onPostExecute(ArrayList<MovieTrailer> result) {
        if (result != null) {
            // need to assign result, otherwise onSaveInstanceState does not work
            TrailersFragment.movieTrailers = result; // = new ArrayList<>(result);
            movieTrailerListAdapter.clear();
            movieTrailerListAdapter.addAll(result);
        }
    }


}
