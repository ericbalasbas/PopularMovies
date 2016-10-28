package com.example.eric.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {

    public MovieListAdapter movieListAdapter;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        if (MovieDb.isOnline(getActivity())) {  // if network is online, get movie list
            updateMovieList();
        }
        else {
            Context context = this.getContext();
            CharSequence text = "Cannot connect to internet. Please turn off airplane mode or turn on wifi. ";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String LOG_TAG = MainActivityFragment.class.getSimpleName();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieListAdapter = new MovieListAdapter(getActivity(), new ArrayList<Movie>());

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieListAdapter);

        // TODO: Refactor into MainActivity
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String MovieId = Integer.toString(movieListAdapter.getItem(position).id);

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, MovieId);
                Log.v(LOG_TAG, "onItemClick: movieId " + MovieId);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void updateMovieList() {
        FetchMovieListTask movieTask = new FetchMovieListTask();
        movieTask.execute();
    }


    public class FetchMovieListTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                // NOTE: values in strings.xml for pref_sort_order_key must match the Movie DB API
                String sortOrder = prefs.getString(getString(R.string.pref_sort_order_key),
                        getString(R.string.pref_most_popular));
                Log.v(LOG_TAG, "doInBackground: sortOrder: " + sortOrder);
                // sortOrder = "popular";

                // catch IOException already catches MalformedURLException, no need to test for
                // null url strings here

                URL url = new URL(MovieDb.buildMovieListUri(sortOrder).toString());
                Log.v(LOG_TAG, "doInBackground: url: " + url.toString());

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

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                movieListAdapter.clear();
                // NOTE: for Honeycomb and above, can use addAll method instead of for loop
                // *** this updates GridView adapter with new movie data
                // for(Movie movie : result) {
                //    movieListAdapter.add(movie);
                //}
                movieListAdapter.addAll(result);
            }
        }

    }
}

