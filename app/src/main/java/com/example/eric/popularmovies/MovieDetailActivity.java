/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;


public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener{

    /**
     * Set Activity layout.
     * @param savedInstanceState - app settings
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
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
