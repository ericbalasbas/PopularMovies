/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetch list of movies in background thread, parse results and load list into movieListAdapter.
 */

class FetchMovieListTask extends AsyncTask<String, Void, List<Movie>> {
    private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();
    private Activity activity;
    private Context context;
    private MovieListAdapter movieListAdapter;

    FetchMovieListTask(Activity vActivity, Context vContext, MovieListAdapter vMovieListAdapter) {
        super();
        // set activity, set movieListAdapter
        activity = vActivity;
        context = vContext;
        movieListAdapter = vMovieListAdapter;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        // Will contain the raw JSON response as a string.
        String moviesJsonStr;

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

            // NOTE: values in strings.xml for pref_sort_order_key must match the Movie DB API
            String sortOrder = prefs.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_most_popular));

            // catch IOException already catches MalformedURLException, no need to test for
            // null url strings here

            URL url = new URL(MovieDb.buildMovieListUri(sortOrder).toString());

            moviesJsonStr = MovieDb.getJson(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        }

        try {
            return MovieDb.getMovieListDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add list of movies to movieListAdapter in UI thread, after query has finished.
     * @param result - list of movies to add to movieListAdapter
     */
    @Override
    protected void onPostExecute(List<Movie> result) {
        if (result != null) {
            movieListAdapter.clear();
            movieListAdapter.addAll(result);
            // Save result to MainActivityFragment.movieList
            MainActivityFragment.movieList = new ArrayList<>(result);
        }
    }

}