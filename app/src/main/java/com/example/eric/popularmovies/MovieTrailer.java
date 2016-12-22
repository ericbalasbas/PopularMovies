/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to store movie trailer information from the Movie DB API.
 * https://developers.themoviedb.org/3/movies/get-movie-videos
 */

class MovieTrailer implements Parcelable {
    int movieId;
    String id, key, name, site, size, type;

    @Override
    public int describeContents() {
        return 0;
    }

    public MovieTrailer (Parcel in) {
        this.id = in.readString();
        this.movieId = in.readInt();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readString();
        this.type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(movieId);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeString(size);
        dest.writeString(type);
    }

    public static final Parcelable.Creator<MovieTrailer> CREATOR = new Parcelable.Creator<MovieTrailer>() {
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };


    MovieTrailer(String vId, int vMovieId, String vKey, String vName, String vSite, String vSize,
                 String vType) {
        this.id = vId;
        this.movieId = vMovieId;
        this.key = vKey;
        this.name = vName;
        this.site = vSite;
        this.size = vSize;
        this.type = vType;
    }

}
