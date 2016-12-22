/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric on 12/21/2016.
 */

public class FetchFavoriteListTask extends AsyncTask<String, Void, List<Movie>>  {

    private final String LOG_TAG = FetchFavoriteListTask.class.getSimpleName();
    private Context context;
    private MovieListAdapter movieListAdapter;

    FetchFavoriteListTask(Context vContext, MovieListAdapter vMovieListAdapter) {
        super();
        context = vContext;
        movieListAdapter = vMovieListAdapter;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        FavoriteDbHelper favoriteDbHelper = new FavoriteDbHelper(context);

        return Favorite.getFavoriteList(favoriteDbHelper.getReadableDatabase());
    }

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
