/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;

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

            if (rootView == null) {
                rootView = LayoutInflater.from(context).inflate(
                        R.layout.fragment_movie_detail, null, false);
            }

            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);

            Uri uri = MovieDb.buildPosterUri(movie.poster_path);
            Picasso.with(context)
                    .load(uri)
                    .placeholder(R.drawable.place_holder_185x277)
                    .error(R.drawable.error_185x277)
                    .into(imageView);

            DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
            NumberFormat numberFormat = NumberFormat.getNumberInstance();

            TextView titleView = (TextView) rootView.findViewById(R.id.title);
            titleView.setText(movie.title);
            TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
            releaseDateView.setText(format.format(movie.release_date));
            TextView voteAverageView = (TextView) rootView.findViewById(R.id.vote_average);
            voteAverageView.setText(numberFormat.format(movie.vote_average));
            TextView overviewView = (TextView) rootView.findViewById(R.id.overview);
            overviewView.setText(movie.overview);

        }
    }
}