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
import java.util.List;

/**
 * Created by eric on 12/13/2016.
 */

class FetchMovieTrailersTask extends AsyncTask<String, Void, List<MovieTrailer>> {
    private final String LOG_TAG = FetchMovieTrailersTask.class.getSimpleName();
    private String MovieId;
    private Context context;
    private View rootView;

    FetchMovieTrailersTask(String vMovieId, Context vContext, View vRootView) {
        super();
        // set activity
        MovieId = vMovieId;
        context = vContext;
        rootView = vRootView;
    }

    @Override
    protected List<MovieTrailer> doInBackground(String... params) {
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
    protected void onPostExecute(List<MovieTrailer> result) {
        if (result != null) {
            // need to assign result, otherwise onSaveInstanceState does not work
            MovieDetailFragment.movieTrailers = result;

            MovieTrailer movieTrailer = MovieTrailer.getTrailer(result);

            MovieDetailFragment.updateMovieDetailTrailer(movieTrailer, rootView, context);
        }
    }


}
