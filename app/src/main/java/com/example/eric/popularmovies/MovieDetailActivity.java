/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;


public class MovieDetailActivity extends AppCompatActivity {
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
        return true;
    }

}
