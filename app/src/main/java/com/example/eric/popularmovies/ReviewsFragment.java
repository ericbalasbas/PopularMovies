/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 * Use intent to send movie ID. Query movie DB again to get details.
 * Even though we have all of the data from the initial query, in Project Part 2 we will have to make
 * another query anyway.
 */

public class ReviewsFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String MOVIE_ID = "MOVIE_ID";
    private int mPage;
    private final String LOG_TAG = ReviewsFragment.class.getSimpleName();
    private String MovieId;

    protected static ArrayList<Review> reviews;
    protected ReviewListAdapter reviewListAdapter;

    /** Required empty public constructor */
    public ReviewsFragment() { }

    public static ReviewsFragment newInstance(int page, String movieId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(MOVIE_ID, movieId);
        ReviewsFragment fragment = new ReviewsFragment();
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

        if (savedInstanceState != null && savedInstanceState.containsKey("reviews")) {
            reviews = savedInstanceState.getParcelableArrayList("reviews");
        } else {
            reviews = new ArrayList<Review>();
        }

        // Inflate the layout for this fragment
        // View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        View rootView = DetailPagerAdapter.getTabView(mPage, getContext());

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            MovieId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Get a reference to the Trailers ListView, and attach this adapter to it.
        reviewListAdapter = new ReviewListAdapter(getActivity(), reviews);
        ListView listView = (ListView) rootView.findViewById(R.id.fragment_reviews);
        listView.setAdapter(reviewListAdapter);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        Context context = this.getContext();
        // http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        // cannot use getView in onCreate(), onCreateView() methods of the fragment

        if (MovieDb.isOnline(getActivity())) { // if network is online, get movie detail
             if (reviews == null || reviews.size() == 0 || reviews.get(0).movieId != Integer.parseInt(MovieId)) {
                updateReviews(MovieId, reviewListAdapter);
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




    private void updateReviews(String vMovieId, ReviewListAdapter vReviewListAdapter) {
        FetchReviewsTask reviewsTask = new FetchReviewsTask(vMovieId, vReviewListAdapter);
        reviewsTask.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList("reviews", reviews);
        }
        super.onSaveInstanceState(outState);
    }
}
