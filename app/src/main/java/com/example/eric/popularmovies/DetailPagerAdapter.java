/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;


/**
 * Created by eric on 12/16/2016.
 */

class DetailPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Details", "Trailers", "Reviews" };
    private String movieId;

    DetailPagerAdapter(FragmentManager fm, String vMovieId) {
        super(fm);
        this.movieId = vMovieId;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        // http://stackoverflow.com/questions/16149778/how-do-i-use-fragmentpageradapter-to-have-tabs-with-different-content

        switch (position) {
            case 0:
                fragment = MovieDetailFragment.newInstance(position, movieId);
                break;
            case 1:
                fragment = TrailersFragment.newInstance(position, movieId);
                break;
            case 2:
                fragment = ReviewsFragment.newInstance(position, movieId);
                break;
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    static View getTabView(int position, Context context) {
        // https://github.com/codepath/android_guides/wiki/Google-Play-Style-Tabs-using-TabLayout
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView

        View view;
        switch (position) {
            case 0: view = LayoutInflater.from(context).inflate(R.layout.fragment_movie_detail, null);
                break;
            case 1: view = LayoutInflater.from(context).inflate(R.layout.fragment_movie_trailers, null);
                break;
            case 2: view = LayoutInflater.from(context).inflate(R.layout.fragment_reviews, null);
                break;
            default: view = null;
                break;
        }
        return view;
    }
}
