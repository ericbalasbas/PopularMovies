/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.eric.popularmovies.FavoriteContract.FavoriteEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * Static functions to find, add, and delete Favorite rows from the SQLite database.
 */

class Favorite {

    private static final String LOG_TAG = Favorite.class.getSimpleName();

    static boolean findFavorite(SQLiteDatabase db, int vMovieId) {
        Cursor result;
        int rows;
        String query = String.format("SELECT _ID FROM %s WHERE _ID = %s",
                                      FavoriteEntry.TABLE_NAME,
                                      Integer.toString(vMovieId));

        result = db.rawQuery(query, new String[] {});
        rows = result.getCount();
        result.close();

        if (rows == 0) {
            return false;
        } else {
            return true;
        }
    }


    // SQLiteDatabase.insertOrThrow(String table, String nullColumnHack, ContentValues values)
    static void addFavorite(SQLiteDatabase db, int vMovieId, String vTitle, String vPosterPath) {
        ContentValues values = new ContentValues();
        values.put(FavoriteEntry._ID, vMovieId);
        values.put(FavoriteEntry.COLUMN_TITLE, vTitle);
        values.put(FavoriteEntry.COLUMN_POSTER_PATH, vPosterPath);

        db.insertOrThrow(FavoriteEntry.TABLE_NAME, null, values);
    }



    static void deleteFavorite(SQLiteDatabase db, int vMovieId) {
        String whereClause = String.format("%s = ?", FavoriteEntry._ID);
        db.delete(FavoriteEntry.TABLE_NAME, whereClause, new String[]{Integer.toString(vMovieId)});
    }



    static List<Movie> getFavoriteList(SQLiteDatabase db) {
        // FavoriteDbHelper favoriteDbHelper = new FavoriteDbHelper(context);
        List<Movie> result = new ArrayList<Movie>();
        Cursor cursor;
        String query = String.format("SELECT _ID, %s, %s FROM %s",
                FavoriteEntry.COLUMN_TITLE,
                FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteEntry.TABLE_NAME);

        cursor = db.rawQuery(query, new String[] {});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            // add each Movie to list
            for (int i=0;i<cursor.getCount();i=i+1) {
                result.add(new Movie(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)));
                cursor.moveToNext();
            }
        }

        cursor.close();

        return result;
    }
}
