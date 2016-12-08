/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Class to store movie information from the Movie DB API.
 * Currently use three queries to the Movie DB:
 * https://developers.themoviedb.org/3/movies/get-popular-movies
 * https://developers.themoviedb.org/3/movies/get-top-rated-movies
 * https://developers.themoviedb.org/3/movies/get-movie-details
 */
class Movie implements Parcelable {
    int id;
    String poster_path, title, overview;
    Double vote_average, popularity;
    Date release_date;

    @Override
    public int describeContents() {
        return 0;
    }

    public Movie(Parcel in) {
        this.id = in.readInt();
        this.poster_path = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.vote_average = in.readDouble();
        this.popularity = in.readDouble();
        this.release_date = new Date(in.readLong()); // create date from Long
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(poster_path);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeDouble(vote_average);
        dest.writeDouble(popularity);
        dest.writeLong(release_date.getTime()); // save date as long
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    Movie(int vId, String vTitle, String vPoster_path)
    {
        this(vId, vTitle, vPoster_path, "", new Date(), 0.0, 0.0);
    }
    Movie(int vId, String vTitle, String vPoster_path, String vOverview, Date vRelease_date,
                 Double vVote_average, Double vPopularity)
    {
        this.id = vId;
        this.title = vTitle;
        this.poster_path = vPoster_path;
        this.overview = vOverview;
        this.release_date = vRelease_date;
        this.vote_average = vVote_average;
        this.popularity = vPopularity;
    }
}
