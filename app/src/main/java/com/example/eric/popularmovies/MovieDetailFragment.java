package com.example.eric.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * Use intent to send movie ID. Query movie DB again to get details.
 * Even though we have all of the data from the initial query, in Part 2 we will have to make
 * another query anyway.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private String MovieId;
    public MovieDetailAdapter movieDetailAdapter;
    public Movie movie;

    public MovieDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();

        Context context = this.getContext();
        // http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        // cannot use getView in onCreate(), onCreateView() methods of the fragment

        View rootView = getView().findViewById(R.id.activity_movie_detail);
        if (rootView == null) {
            Log.v(LOG_TAG, "onStart: rootView is null ");
        }

        // TODO: refactor to MovieDb class
        if (isOnline()) {  // if network is online, get movie detail
            updateMovieDetail(context, rootView);
        }
        else {
            CharSequence text = "Cannot connect to internet. Please turn off airplane mode or turn on wifi. ";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        movieDetailAdapter = new MovieDetailAdapter(getActivity(), new ArrayList<Movie>());
        // NOTE: need to use AdapterView.setAdapter here to use an Adapter

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            MovieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.v(LOG_TAG, "onCreateView: MovieId " + MovieId); // MovieId shows up correctly
        }
        // Inflate the layout for this fragment
        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *
     *  Caused by: java.lang.RuntimeException: com.example.eric.popularmovies.MovieDetailActivity@466b5c5 must implement OnFragmentInteractionListener
     */
    public interface OnFragmentInteractionListener {
        // TODO: Implement in MovieDetailActivity. Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // TODO: refactor to MovieDb class
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

    private void updateMovieDetail(Context context, View rootView) {

        FetchMovieTask movieTask = new FetchMovieTask(context, rootView);
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // String location = prefs.getString(getString(R.string.pref_location_key),
        //        getString(R.string.pref_location_default));
        movieTask.execute();
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

    private Uri buildPosterUri(String poster_path) {

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

    public class FetchMovieTask extends AsyncTask<String, Void, Movie> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private Context context;
        private View rootView;

        public FetchMovieTask(Context context, View rootView) {
            this.context = context;
            this.rootView = rootView;
        }

        @Override
        protected Movie doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {

                // TODO: refactor to MovieDb class
                // catch IOException already catches MalformedURLException, no need to test for
                // null url strings here
                URL url = new URL(buildMovieDetailUri(MovieId).toString());
                Log.v(LOG_TAG, "doInBackground: url " + url.toString()); // url looks good here

                // Create the request to TheMovieDB, and open the connection
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
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "doInBackground: moviesJsonStr " + moviesJsonStr); // json string looks good here
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
        protected void onPostExecute(Movie result) {
            if (result != null) {

                movie = result;
                Log.v(LOG_TAG, "onPostExecute: movie title:  " + movie.title);

                if (rootView == null) {
                    Log.v(LOG_TAG, "onPostExecute: rootView is null");

                    rootView = LayoutInflater.from(getContext()).inflate(
                                R.layout.fragment_movie_detail, null, false);

                }

                ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);

                Uri uri = buildPosterUri(movie.poster_path);
                Picasso.with(context)
                        .load(uri)
                        .into(imageView);
                // .placeholder(R.drawable.placeholder)
                // .error(R.drawable.error)

                // TODO: format date for locale
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                TextView titleView = (TextView) rootView.findViewById(R.id.title);
                titleView.setText(movie.title);
                TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
                releaseDateView.setText(format.format(movie.release_date));
                TextView voteAverageView = (TextView) rootView.findViewById(R.id.vote_average);
                // TODO: format vote_average for locale
                voteAverageView.setText(movie.vote_average.toString());
                TextView overviewView = (TextView) rootView.findViewById(R.id.overview);
                overviewView.setText(movie.overview);

            }
        }

        // TODO: refactor into MovieDb class
        private Uri buildMovieDetailUri(String MovieId) {

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

        // add movie data to MovieDetailAdapter
        private Movie getMovieDataFromJson(String moviesJsonStr)
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
}
