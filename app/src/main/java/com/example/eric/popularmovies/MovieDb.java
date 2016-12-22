/*
 * Copyright (c) 2016. Eric Balasbas
 */

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


/**
 * Class of static methods used to connect to the Movie DB, and parse query results.
 */
class MovieDb {
    private static final String LOG_TAG = MovieDb.class.getSimpleName();

    MovieDb() {  }


    /**
     * Test if network connection is on
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

    /**
     * Query the Movie DB and return the results as a JSON string.
     * @param queryUrl - url
     * @return String
     */
    static String getJson(URL queryUrl) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Must use finally clause because try-with-resources statement not supported in Android API 15
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
            StringBuilder buffer = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
                buffer.append("\n");
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
            return "";
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
     * Construct Uri required to get movie poster from the Movie DB.
     * @param poster_path - from Movie DB API
     * @return Uri for movie poster for use in Picasso.load(android.net.Uri uri)
     *
     * to construct url for movie poster
     * base url: http://image.tmdb.org/t/p/
     * size: "w92", "w154", "w185" (recommended), "w342", "w500", "w780", or "original"
     * poster_path: /nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
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

    /**
     * Construct Uri to get movie list from the Movie DB.
     * @param vSortOrder - must match Get Popular Movies or Get Top Rated Movies command from the Movie DB API.
     * @return Uri
     *
     * The Movie DB API documentation:
     * Get Popular Movies command: https://developers.themoviedb.org/3/movies/get-popular-movies
     * Get Top Rated Movies command: https://developers.themoviedb.org/3/movies/get-top-rated-movies
     *
     * Example: https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US
     * Example: example: https://api.themoviedb.org/3/movie/top_rated?api_key=<<api_key>>&language=en-US
     */
    static Uri buildMovieListUri(String vSortOrder) {
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

    /**
     * Construct Uri to get movie details from the Movie DB.
     * @param MovieId - movie_id field from the Movie DB
     * @return Uri
     *
     * The Movie DB API documentation:
     * Get Movie Details: https://developers.themoviedb.org/3/movies/get-movie-details
     *
     * Example: https://api.themoviedb.org/3/movie/{movie_id}?api_key=<<api_key>>&language=en-US
     */
    static Uri buildMovieDetailUri(String MovieId) {
        final String language = "en-US";
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

    /**
     * Construct Uri to get movie trailers from the Movie DB.
     * @param MovieId - movie_id field from the Movie DB
     * @return Uri
     *
     * The Movie DB API documentation:
     * Get Movie Videos: https://developers.themoviedb.org/3/movies/get-movie-videos
     *
     * Example: https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=<<api_key>>&language=en-US
     */
    static Uri buildMovieTrailerUri(String MovieId) {
        final String language = "en-US";
        final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String VIDEOS_PARAM = "videos";
        final String LANGUAGE_PARAM = "language";
        final String API_KEY_PARAM = "api_key";

        try {
            return Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(MovieId)
                    .appendPath(VIDEOS_PARAM)
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }

    /**
     * Construct Uri to get movie reviews from the Movie DB.
     * @param MovieId - movie_id field from the Movie DB
     * @return Uri
     *
     * The Movie DB API documentation:
     * Get Movie Reviews: https://developers.themoviedb.org/3/movies/get-movie-reviews
     *
     * Example: https://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key=<<api_key>>&language=en-US
     */
    static Uri buildReviewsUri(String MovieId) {
        final String language = "en-US";
        final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String REVIEWS_PARAM = "reviews";
        final String LANGUAGE_PARAM = "language";
        final String API_KEY_PARAM = "api_key";

        try {
            return Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(MovieId)
                    .appendPath(REVIEWS_PARAM)
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }

    /**
     * Return results from the Movie DB query JSON string as a list of Movie objects.
     * @param moviesJsonStr - movie list string in JSON format
     * @return List<Movie>
     * @throws JSONException
     */
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
                    movieJson.getString(MDB_POSTER_PATH).replaceAll("/", "")); // remove all slashes

            results.add(movie);
        }

        return results;
    }

    /**
     * Return Movie object from Get Movie Details query.
     * @param moviesJsonStr - movie details in JSON format
     * @return Movie
     * @throws JSONException
     */
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

        // release_date field in Movie DB is in the format "yyyy-MM-dd", there is no locale for this field
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date release_date = new Date();

        try {
            release_date = format.parse(movieJson.getString(MDB_RELEASE_DATE));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        return new Movie(movieJson.getInt(MDB_ID),
                movieJson.getString(MDB_TITLE),
                movieJson.getString(MDB_POSTER_PATH).replaceAll("/", ""), // remove all slashes
                movieJson.getString(MDB_OVERVIEW),
                release_date,
                movieJson.getDouble(MDB_VOTE_AVG),
                movieJson.getDouble(MDB_POPULARITY));
    }


    /**
     * Return results from the Movie DB query JSON string as a list of MovieTrailer objects.
     * @param movieTrailersJsonStr - movie list string in JSON format
     * @return List<MovieTrailer>
     * @throws JSONException
     */
    static ArrayList<MovieTrailer> getMovieTrailersDataFromJson(String movieTrailersJsonStr)
            throws JSONException {

        // The Movie DB get movie videos query returns a movie id, and results array

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_ID = "id";
        final String MDB_KEY = "key";
        final String MDB_NAME = "name";
        final String MDB_SITE = "site";
        final String MDB_SIZE = "size";
        final String MDB_TYPE = "type";

// String vId, int vMovieId, String vKey, String vName, String vSite, String vSize, String vType

        JSONObject movieQueryResults = new JSONObject(movieTrailersJsonStr);
        JSONArray movieArray = movieQueryResults.getJSONArray(MDB_RESULTS);
        int movieId = movieQueryResults.getInt(MDB_ID);

        ArrayList<MovieTrailer> results = new ArrayList<>();

        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movieJson = movieArray.getJSONObject(i);

            MovieTrailer trailer = new MovieTrailer(movieJson.getString(MDB_ID),
                    movieId,
                    movieJson.getString(MDB_KEY),
                    movieJson.getString(MDB_NAME),
                    movieJson.getString(MDB_SITE),
                    movieJson.getString(MDB_SIZE),
                    movieJson.getString(MDB_TYPE));

            results.add(trailer);
        }

        return results;
    }

    /**
     * Return results from the Movie DB query JSON string as a list of Review objects.
     * @param reviewsJsonStr - movie list string in JSON format
     * @return List<Review>
     * @throws JSONException
     */
    static ArrayList<Review> getReviewsDataFromJson(String reviewsJsonStr)
            throws JSONException {

        // The Movie DB get movie reviews query returns a movie id, and results array

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_ID = "id";
        final String MDB_AUTHOR = "author";
        final String MDB_CONTENT = "content";
        final String MDB_URL = "url";

        JSONObject movieQueryResults = new JSONObject(reviewsJsonStr);
        int movieId = movieQueryResults.getInt(MDB_ID);
        JSONArray reviewArray = movieQueryResults.getJSONArray(MDB_RESULTS);

        ArrayList<Review> results = new ArrayList<>();

        for(int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewJson = reviewArray.getJSONObject(i);

            Review review = new Review(reviewJson.getString(MDB_ID),
                    movieId,
                    reviewJson.getString(MDB_AUTHOR),
                    reviewJson.getString(MDB_CONTENT),
                    reviewJson.getString(MDB_URL));

            results.add(review);
        }

        return results;
    }

    static Uri buildYoutubeUri(MovieTrailer vMovieTrailer) {
        final String BASE_URL = "http://www.youtube.com/watch";
        final String KEY_PARAM = "v";

        try {
            return Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, vMovieTrailer.key)
                    .build();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }


    }
}
