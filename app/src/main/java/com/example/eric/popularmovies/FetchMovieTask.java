/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;


import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
/**
 * Fetch movie details in background thread, parse results and load list into a Movie object.
 */

class FetchMovieTask extends AsyncTask<String, Void, Movie> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private Context context;
    private View rootView;
    private String MovieId;
    private Movie movie;

    FetchMovieTask(String vMovieId, Movie vMovie, Context vContext, View vRootView) {
        this.MovieId = vMovieId;
        this.movie = vMovie;
        this.context = vContext;
        this.rootView = vRootView;
    }

    /**
     * Get movie details from the Movie DB, return Movie object.
     * @param params - none used
     * @return Movie object
     */
    @Override
    protected Movie doInBackground(String... params) {
        // Contains the raw JSON response as a string.
        String moviesJsonStr;

        try {
            // catch IOException already catches MalformedURLException, no need to test for
            // null url strings here
            URL url = new URL(MovieDb.buildMovieDetailUri(MovieId).toString());

            moviesJsonStr = MovieDb.getJson(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        }

        try {
            return MovieDb.getMovieDetailDataFromJson(moviesJsonStr);
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
    protected void onPostExecute(Movie result) {
        if (result != null) {

            movie = result;

            // need to assign result, otherwise onSaveInstanceState does not work
            MovieDetailFragment.movie = result;

            MovieDetailFragment.updateMovieDetailViews(movie, rootView, context);
        }
    }
}