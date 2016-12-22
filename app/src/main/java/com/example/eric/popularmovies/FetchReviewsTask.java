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
 * Fetch list of reviews for a movie in background thread, parse results and load list into reviewListAdapter.
 */

class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private String MovieId;
    private ReviewListAdapter ReviewListAdapter;

    FetchReviewsTask(String vMovieId, ReviewListAdapter vReviewListAdapter) {
        super();

        MovieId = vMovieId;
        ReviewListAdapter = vReviewListAdapter;
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {
        // Will contain the raw JSON response as a string.
        String ReviewsJsonStr;

        try {
            // catch IOException already catches MalformedURLException, no need to test for
            // null url strings here

            URL url = new URL(MovieDb.buildReviewsUri(MovieId).toString());

            ReviewsJsonStr = MovieDb.getJson(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        }

        try {
            return MovieDb.getReviewsDataFromJson(ReviewsJsonStr);
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
    protected void onPostExecute(ArrayList<Review> result) {
        if (result != null) {
            // need to assign result, otherwise onSaveInstanceState does not work

            ReviewsFragment.reviews = result;
            ReviewListAdapter.clear();
            ReviewListAdapter.addAll(result);
        }
    }


}
