/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fragment that contains the grid view of movies.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public MovieListAdapter movieListAdapter;
    protected static ArrayList<Movie> movieList;
    protected static boolean sortOrderChanged = true;

    /** Required empty public constructor */
    public MainActivityFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Load GridView with list of Movie objects from movieListAdapter.
     * @param inflater - default
     * @param container - default
     * @param savedInstanceState - app settings
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey("movieList")) {
            movieList = savedInstanceState.getParcelable("movieList");
        }

        if (movieList != null && !sortOrderChanged) {
            movieListAdapter = new MovieListAdapter(getActivity(), movieList);
        }
        else {
            movieListAdapter = new MovieListAdapter(getActivity(), new ArrayList<Movie>());
        }

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieListAdapter);

        /**
         * Start movie detail activity when grid view item is clicked.
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (movieListAdapter.getItem(position) != null) {
                    String MovieId = Integer.toString(movieListAdapter.getItem(position).id);

                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, MovieId);
                    startActivity(intent);
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    /**
     * Query the Movie DB for movie list if there is network access, otherwise load toast to warn user.
     */
    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // NOTE: values in strings.xml for pref_sort_order_key must match the Movie DB API
        String sortOrder = prefs.getString(getContext().getString(R.string.pref_sort_order_key), getContext().getString(R.string.pref_most_popular));

        if (MovieDb.isOnline(getActivity())) {  // if network is online, get movie list
            if (movieList == null || sortOrderChanged || sortOrder.equals("favorite")) {
                updateMovieList(sortOrder);
                sortOrderChanged = false;
            }
        }
        else {
            Context context = this.getContext();
            CharSequence text = "Cannot connect to internet. Please turn off airplane mode or turn on wifi. ";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    private void updateMovieList(String sortOrder) {
        // if sortOrder is "favorite" always query database
        if (sortOrder.equals(getContext().getString(R.string.pref_favorite))) {
            FetchFavoriteListTask favoriteListTask = new FetchFavoriteListTask(getContext(), movieListAdapter);
            favoriteListTask.execute();
        } else {
            FetchMovieListTask movieTask = new FetchMovieListTask(getActivity(), getContext(), movieListAdapter,
                                                                  sortOrder);
            movieTask.execute();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieList != null && !movieList.isEmpty()) {
            outState.putParcelableArrayList("movieList", movieList);
        }

        super.onSaveInstanceState(outState);
    }
}
