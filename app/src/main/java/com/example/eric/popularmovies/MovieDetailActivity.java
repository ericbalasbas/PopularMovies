/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

// TODO: Send bundle as argument to start tabs (save on db requests?)

public class MovieDetailActivity extends AppCompatActivity
        implements MovieDetailFragment.OnFragmentInteractionListener,
        TrailersFragment.OnFragmentInteractionListener,
        ReviewsFragment.OnFragmentInteractionListener {

    private String MovieId;

    /**
     * Set Activity layout.
     * @param savedInstanceState - app settings
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            MovieId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        setContentView(R.layout.activity_movie_detail);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        // https://github.com/codepath/android_guides/wiki/Google-Play-Style-Tabs-using-TabLayout
        ViewPager viewPager = (ViewPager) findViewById(R.id.movie_detail_viewpager);

        DetailPagerAdapter pagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), MovieId);

        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.movie_detail_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        // to give each tab a custom view
        // https://github.com/codepath/android_guides/wiki/Google-Play-Style-Tabs-using-TabLayout
        // for (int i = 0; i < tabLayout.getTabCount(); i++) {
        //     TabLayout.Tab tab = tabLayout.getTabAt(i);
        //     tab.setCustomView(pagerAdapter.getTabView(i));
        // }
    }

    /**
     * Inflate the standard options menu.
     * @param menu - standard options menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        // TODO: start SettingsActivity here
        return true;
    }

    /**
     *
     * @param uri
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
    @Override
    public void onFragmentInteraction(Uri uri) { }

}
