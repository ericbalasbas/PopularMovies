/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.provider.BaseColumns;

/**
 * This class describes the table and column names for the favorites database.
 */

public final class FavoriteContract {


    // Inner class that defines the table contents
    public static class FavoriteEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "favorite";
        // public static final String _ID = "_ID";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
    }

}
