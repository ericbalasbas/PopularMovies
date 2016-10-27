package com.example.eric.popularmovies;

import android.content.Context;
import android.content.Intent;
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

        if (MovieDb.isOnline(getActivity())) {  // if network is online, get movie detail
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


    private void updateMovieDetail(Context context, View rootView) {

        FetchMovieTask movieTask = new FetchMovieTask(context, rootView);
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // String location = prefs.getString(getString(R.string.pref_location_key),
        //        getString(R.string.pref_location_default));
        movieTask.execute();
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
            // Contains the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // catch IOException already catches MalformedURLException, no need to test for
                // null url strings here
                URL url = new URL(MovieDb.buildMovieDetailUri(MovieId).toString());

                moviesJsonStr = MovieDb.getJson(url);
                Log.v(LOG_TAG, "doInBackground: moviesJsonStr " + moviesJsonStr); // json string looks good here
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

                Uri uri = MovieDb.buildPosterUri(movie.poster_path);
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

    }
}
