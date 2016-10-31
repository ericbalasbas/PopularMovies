/*
 * Copyright (c) 2016. Eric Balasbas
 */

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

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * Use intent to send movie ID. Query movie DB again to get details.
 * Even though we have all of the data from the initial query, in Project Part 2 we will have to make
 * another query anyway.
 */
public class MovieDetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String MovieId;

    public Movie movie;

    /** Required empty public constructor */
    public MovieDetailFragment() {   }


    @Override
    public void onStart() {
        super.onStart();

        Context context = this.getContext();
        // http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        // cannot use getView in onCreate(), onCreateView() methods of the fragment

        View rootView = getView().findViewById(R.id.activity_movie_detail);

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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            MovieId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

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
        void onFragmentInteraction(Uri uri);
    }


    private void updateMovieDetail(Context context, View rootView) {
        FetchMovieTask movieTask = new FetchMovieTask(context, rootView);
        movieTask.execute();
    }


    class FetchMovieTask extends AsyncTask<String, Void, Movie> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private Context context;
        private View rootView;

        FetchMovieTask(Context context, View rootView) {
            this.context = context;
            this.rootView = rootView;
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
                    rootView = LayoutInflater.from(getContext()).inflate(
                                R.layout.fragment_movie_detail, null, false);
                }

                ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);

                Uri uri = MovieDb.buildPosterUri(movie.poster_path);
                Picasso.with(context)
                        .load(uri)
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
}
