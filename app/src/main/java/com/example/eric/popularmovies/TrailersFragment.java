/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 * Use intent to send movie ID. Query movie DB again to get details.
 * Even though we have all of the data from the initial query, in Project Part 2 we will have to make
 * another query anyway.
 */

public class TrailersFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String MOVIE_ID = "MOVIE_ID";
    private int mPage;
    private final String LOG_TAG = TrailersFragment.class.getSimpleName();
    private String MovieId;

    protected static ArrayList<MovieTrailer> movieTrailers;
    protected MovieTrailerListAdapter movieTrailerListAdapter;

    /** Required empty public constructor */
    public TrailersFragment() { }

    public static TrailersFragment newInstance(int page, String movieId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(MOVIE_ID, movieId);
        TrailersFragment fragment = new TrailersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPage = getArguments().getInt(ARG_PAGE);
        MovieId = getArguments().getString(MOVIE_ID);
        Log.v(LOG_TAG, "onCreateView: MovieId: " + MovieId);

        if (savedInstanceState != null && savedInstanceState.containsKey("trailerList")) {
            movieTrailers = savedInstanceState.getParcelableArrayList("trailerList");
        } else {
            movieTrailers = new ArrayList<MovieTrailer>();
        }

        // Inflate the layout for this fragment
        View rootView = DetailPagerAdapter.getTabView(mPage, getContext());

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            MovieId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Get a reference to the Trailers ListView, and attach this adapter to it.
        movieTrailerListAdapter = new MovieTrailerListAdapter(getActivity(), movieTrailers);
        ListView listView = (ListView) rootView.findViewById(R.id.fragment_movie_trailers);
        listView.setAdapter(movieTrailerListAdapter);

        // Start Youtube intent when clicking on trailer item
        // http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (movieTrailerListAdapter.getItem(position) != null) {
                    Uri uri = MovieDb.buildYoutubeUri(movieTrailerListAdapter.getItem(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();

        Context context = this.getContext();
        // http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        // cannot use getView in onCreate(), onCreateView() methods of the fragment

        if (MovieDb.isOnline(getActivity())) { // if network is online, get movie detail
            if (movieTrailers == null || movieTrailers.size() == 0 || movieTrailers.get(0).movieId != Integer.parseInt(MovieId)) {
                updateMovieTrailers(MovieId, movieTrailerListAdapter);
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
    }




    private void updateMovieTrailers(String vMovieId, MovieTrailerListAdapter vMovieTrailerListAdapter) {
        FetchMovieTrailersTask movieTrailersTask = new FetchMovieTrailersTask(vMovieId, vMovieTrailerListAdapter);
        movieTrailersTask.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieTrailers != null && !movieTrailers.isEmpty()) {
            outState.putParcelableArrayList("trailerList", movieTrailers);
        }

        super.onSaveInstanceState(outState);
    }
}
