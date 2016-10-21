package com.example.eric.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.ConnectivityManager;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {

    public MovieAdapter movieAdapter;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isOnline()) {  // if network is online, get movie list
            updateMovieList();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // final String LOG_TAG = MainActivityFragment.class.getSimpleName();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        // is back slash in post_path messing with data?
        // sample movie for testing
        // (int vId, String vTitle, String vPoster_path, Double vVote_average, Double vPopularity)
        Movie m = new Movie(333484,"The Magnificent Seven", "/z6BP8yLwck8mN9dtdYKkZ4XGa3D.jpg", 4.59, 32.373914);
        movieAdapter.add(m);

        // Log.v(LOG_TAG, "onCreateView: movieAdapter.getCount " + movieAdapter.getCount());

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void updateMovieList() {
        FetchMoviesTask movieTask = new FetchMoviesTask();
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // String location = prefs.getString(getString(R.string.pref_location_key),
        //        getString(R.string.pref_location_default));
        movieTask.execute();
    }


    // from http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    // Only tests if network connection works, not if internet connection works.

    // To test internet connection, can ping a site (not recommended, some networks disable ping)
    // or create a test connection and test if that works:
    // http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out/39766506#39766506
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            // Log.v(LOG_TAG, "doInBackground ");

            try {

                // catch IOException already catches MalformedURLException, no need to test for
                // null url strings here
                URL url = new URL(buildMoviesUri().toString());
                Log.v(LOG_TAG, "doInBackground: url: " + url);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                // https://developer.android.com/reference/java/lang/StringBuilder.html

                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                // Log.v(LOG_TAG, "doInBackground: moviesJsonStr: " + moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                movieAdapter.clear();
                // NOTE: for Honeycomb and above, can use addAll method instead of for loop
                // *** this updates GridView adapter with new forecast data
                for(Movie movie : result) {
                    // Log.v(LOG_TAG, "onPostExecute: add movie " + movie.title);
                    // Log.v(LOG_TAG, "onPostExecute: add movie " + movie.poster_path);
                    movieAdapter.add(movie);
                }
                // movieAdapter.notifyDataSetChanged();


                //http://stackoverflow.com/questions/16338281/custom-adapter-getview-method-is-not-called
                // to verify elements, use
                // Log.v(LOG_TAG, "onPostExecute: movieAdapter.GetCount() " + movieAdapter.getCount());
                // returns 20
            }
        }

        private Uri buildMoviesUri() {

            // Construct Uri for query to TheMovieDB.org API
            // https://www.themoviedb.org/documentation/api

            final String language = "en-US";

            // example: https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/popular?";
            final String LANGUAGE_PARAM = "language";
            final String API_KEY_PARAM = "api_key";

            try {
                return Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(LANGUAGE_PARAM, language)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
            } catch (UnsupportedOperationException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
        }

        // add movie data to MovieAdapter
        private List<Movie> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {
            // The Movie DB popular movies query returns a page number, and results array

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_ID = "id";
            final String MDB_TITLE = "title";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_VOTE_AVG = "vote_average";
            final String MDB_POPULARITY = "popularity";

            JSONObject movieQueryResults = new JSONObject(moviesJsonStr);
            JSONArray movieArray = movieQueryResults.getJSONArray(MDB_RESULTS);

            List<Movie> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movieJson = movieArray.getJSONObject(i);
                Movie movie = new Movie(movieJson.getInt(MDB_ID),
                                        movieJson.getString(MDB_TITLE),
                                        movieJson.getString(MDB_POSTER_PATH),
                                        movieJson.getDouble(MDB_VOTE_AVG),
                                        movieJson.getDouble(MDB_POPULARITY));

                results.add(movie);
            }

            return results;
        }
    }
}

