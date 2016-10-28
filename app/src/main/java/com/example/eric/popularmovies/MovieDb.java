package com.example.eric.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


class MovieDb {
    private static final String LOG_TAG = MovieDb.class.getSimpleName();

    MovieDb() {  }


    /*
     *
     * @return boolean
     *
     * from http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * Only tests if network connection works, not if internet connection works.

     * To test internet connection, can ping a site (not recommended, some networks disable ping)
     * or create a test connection and test if that works:
     * http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out/39766506#39766506
     */
    static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    static String getJson(URL queryUrl) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the request to TheMovieDB, and open the connection
            urlConnection = (HttpURLConnection) queryUrl.openConnection();
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
            // TODO: change to StringBuilder
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
            return buffer.toString();
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
    }
    /***
     *
     * @param poster_path - from Movie DB API
     * @return uri for movie poster for use in Picasso.load(android.net.Uri uri)
     *
     * to construct url for movie poster
     * base url: http://image.tmdb.org/t/p/
     * size: "w92", "w154", "w185" (recommended), "w342", "w500", "w780", or "original"
     * poster_path
     * for example: http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
     *
     */

    static Uri buildPosterUri(String poster_path) {

        final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_PARAM = "w185";

        try {
            return Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(SIZE_PARAM)
                    .appendPath(poster_path)
                    .build();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }


    static Uri buildMovieListUri(String vSortOrder) {

        // Construct Uri for query to TheMovieDB.org API
        // https://www.themoviedb.org/documentation/api

        // example: https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US
        // final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/popular?";
        // final String POPULAR_PATH = "popular";
        // final String TOP_RATED_PATH = "top_rated";
        // final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated?";
        final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
        final String LANGUAGE_PARAM = "language";
        final String API_KEY_PARAM = "api_key";
        final String language = "en-US";

        try {
            return Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(vSortOrder)
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }


    static Uri buildMovieDetailUri(String MovieId) {

        // Construct Uri for query to TheMovieDB.org API
        // https://www.themoviedb.org/documentation/api

        final String language = "en-US";

        // https://api.themoviedb.org/3/movie/{movie_id}?api_key=<<api_key>>&language=en-US
        final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String LANGUAGE_PARAM = "language";
        final String API_KEY_PARAM = "api_key";

        try {
            return Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(MovieId)
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }



    static List<Movie> getMovieListDataFromJson(String moviesJsonStr)
            throws JSONException {
        // The Movie DB popular movies query returns a page number, and results array

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_POSTER_PATH = "poster_path";


        JSONObject movieQueryResults = new JSONObject(moviesJsonStr);
        JSONArray movieArray = movieQueryResults.getJSONArray(MDB_RESULTS);

        List<Movie> results = new ArrayList<>();

        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movieJson = movieArray.getJSONObject(i);

            Movie movie = new Movie(movieJson.getInt(MDB_ID),
                    movieJson.getString(MDB_TITLE),
                    movieJson.getString(MDB_POSTER_PATH).replaceAll("/", ""));
            // remove all slashes

            results.add(movie);
        }

        return results;
    }

    static Movie getMovieDetailDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date"; // string in format "2016-09-14"
        final String MDB_VOTE_AVG = "vote_average";
        final String MDB_POPULARITY = "popularity";

        JSONObject movieJson = new JSONObject(moviesJsonStr);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date release_date = new Date();

        try {
            release_date = format.parse(movieJson.getString(MDB_RELEASE_DATE));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        Movie results = new Movie(movieJson.getInt(MDB_ID),
                movieJson.getString(MDB_TITLE),
                movieJson.getString(MDB_POSTER_PATH).replaceAll("/", ""), // remove all slashes
                movieJson.getString(MDB_OVERVIEW),
                release_date,
                movieJson.getDouble(MDB_VOTE_AVG),
                movieJson.getDouble(MDB_POPULARITY));

        return results;
    }


}
