/*
 * Copyright (c) 2016. Eric Balasbas
 */

package com.example.eric.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to store movie review information from the Movie DB API.
 * https://developers.themoviedb.org/3/movies/get-movie-reviews
 */


public class Review  implements Parcelable {
    int movieId;
    String id, author, content, url;

    @Override
    public int describeContents() {
        return 0;
    }

    public Review (Parcel in) {
        this.id = in.readString();
        this.movieId = in.readInt();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(movieId);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


    Review(String vId, int vMovieId, String vAuthor, String vContent, String vUrl) {
        this.id = vId;
        this.movieId = vMovieId;
        this.author = vAuthor;
        this.content = vContent;
        this.url = vUrl;
    }

}
