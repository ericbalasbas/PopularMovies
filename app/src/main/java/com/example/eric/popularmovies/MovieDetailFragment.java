/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;


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

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private String MovieId;

    protected static Movie movie;
    protected static List<MovieTrailer> movieTrailers;

    /** Required empty public constructor */
    public MovieDetailFragment() { }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            movie = savedInstanceState.getParcelable("movie");
        }

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
    public void onStart() {
        super.onStart();

        Context context = this.getContext();
        // http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        // cannot use getView in onCreate(), onCreateView() methods of the fragment

        View rootView = getView().findViewById(R.id.activity_movie_detail);

        if (MovieDb.isOnline(getActivity())) { // if network is online, get movie detail
            if (movie == null || movie.id != Integer.parseInt(MovieId)) {
                updateMovieDetail(MovieId, movie, context, rootView);
            } else {
                // if movie has not changed, do not query for movie details again
                // update text and image views from movie object
                updateMovieDetailViews(movie, getView().findViewById(R.id.activity_movie_detail), this.getContext());
                updateMovieDetailTrailer(MovieTrailer.getTrailer(movieTrailers), getView().findViewById(R.id.activity_movie_detail), this.getContext());
            }
        }
        else {
            CharSequence text = "Cannot connect to internet. Please turn off airplane mode or turn on wifi. ";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
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


    private void updateMovieDetail(String vMovieId, Movie vMovie, Context vContext, View vRootView) {
        FetchMovieTask movieTask = new FetchMovieTask(vMovieId, vMovie, vContext, vRootView);
        movieTask.execute();

        FetchMovieTrailersTask movieTrailersTask = new FetchMovieTrailersTask(vMovieId, vContext, vRootView);
        movieTrailersTask.execute();
    }

    public static void updateMovieDetailViews(Movie vMovie, View vRootView, Context vContext) {
        if (vRootView == null) {
            vRootView = LayoutInflater.from(vContext).inflate(
                    R.layout.fragment_movie_detail, null, false);
        }

        ImageView imageView = (ImageView) vRootView.findViewById(R.id.movie_poster);

        Uri uri = MovieDb.buildPosterUri(vMovie.poster_path);
        Picasso.with(vContext)
                .load(uri)
                .placeholder(R.drawable.place_holder_185x277)
                .error(R.drawable.error_185x277)
                .into(imageView);

        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();

        TextView titleView = (TextView) vRootView.findViewById(R.id.title);
        titleView.setText(vMovie.title);
        TextView releaseDateView = (TextView) vRootView.findViewById(R.id.release_date);
        releaseDateView.setText(format.format(vMovie.release_date));
        TextView voteAverageView = (TextView) vRootView.findViewById(R.id.vote_average);
        voteAverageView.setText(numberFormat.format(vMovie.vote_average));
        TextView overviewView = (TextView) vRootView.findViewById(R.id.overview);
        overviewView.setText(vMovie.overview);
    }

    public static void updateMovieDetailTrailer(MovieTrailer vMovieTrailer, View vRootView, Context vContext) {
        if (vRootView == null) {
            vRootView = LayoutInflater.from(vContext).inflate(
                    R.layout.fragment_movie_detail, null, false);
        }

        String frameVideo = String.format("<html><body>%s<br><iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/%s\" frameborder=\"0\" allowfullscreen></iframe></body></html>",
                                            vMovieTrailer.name,
                                            vMovieTrailer.key);

        WebView trailer = (WebView) vRootView.findViewById(R.id.trailer);
        trailer.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
                return false; // TODO: test if true
            }
        });
        WebSettings webSettings = trailer.getSettings();
        webSettings.setJavaScriptEnabled(true);
        trailer.loadData(frameVideo, "text/html", "utf-8");


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

}
