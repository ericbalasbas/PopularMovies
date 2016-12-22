/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 * Use intent to send movie ID. Query movie DB again to get details.
 * Even though we have all of the data from the initial query, in Project Part 2 we will have to make
 * another query anyway.
 */
public class MovieDetailFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String MOVIE_ID = "MOVIE_ID";
    private int mPage;
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private String MovieId;

    protected static Movie movie;

    /** Required empty public constructor */
    public MovieDetailFragment() { }

    public static MovieDetailFragment newInstance(int page, String movieId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(MOVIE_ID, movieId);
        MovieDetailFragment fragment = new MovieDetailFragment();
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

        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            movie = savedInstanceState.getParcelable("movie");
        }

        // Inflate the layout for this fragment
        // View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        View rootView = DetailPagerAdapter.getTabView(mPage, getContext());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Context context = this.getContext();
        // http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        // cannot use getView in onCreate(), onCreateView() methods of the fragment

        View rootView = getView().findViewById(R.id.fragment_movie_detail);

        if (MovieDb.isOnline(getActivity())) { // if network is online, get movie detail
            if (movie == null || movie.id != Integer.parseInt(MovieId)) {
                updateMovieDetail(MovieId, movie, context, rootView);

            } else {
                // if movie has not changed, do not query for movie details again
                // update text and image views from movie object
                updateMovieDetailViews(movie, getView().findViewById(R.id.fragment_movie_detail), this.getContext());
            }
        }
        else {
            CharSequence text = "Cannot connect to internet. Please turn off airplane mode or turn on wifi. ";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        // check if movie is in Favorites, and set FavoriteButton
        ToggleButton favoriteButton = (ToggleButton) getView().findViewById(R.id.favorite_button);
        final FavoriteDbHelper dbHelper = new FavoriteDbHelper(getContext());

        favoriteButton.setChecked(Favorite.findFavorite(dbHelper.getReadableDatabase(), Integer.parseInt(MovieId)));

        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Favorite.addFavorite(dbHelper.getWritableDatabase(),
                                         Integer.parseInt(MovieId),
                                         movie.title,
                                         movie.poster_path);
                } else {
                    // The toggle is disabled
                    Favorite.deleteFavorite(dbHelper.getWritableDatabase(), Integer.parseInt(MovieId));
                }
            }
        });

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void updateMovieDetail(String vMovieId, Movie vMovie, Context vContext, View vRootView) {
        FetchMovieTask movieTask = new FetchMovieTask(vMovieId, vMovie, vContext, vRootView);
        movieTask.execute();
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

}
