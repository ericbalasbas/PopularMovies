package com.example.eric.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param movies A List of Movie objects to display in a list
     */
    public MovieAdapter(Activity context, List<Movie> movies) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, movies);
    }


    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the movie object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);
        Log.v(LOG_TAG, "getView: position " + position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_image);

        if (movie != null) {
            Uri uri = buildPosterUri(movie.poster_path);
            Picasso.with(this.getContext())
                   .load(uri)
                   .into(imageView);
            // .placeholder(R.drawable.placeholder)
            // .error(R.drawable.error)

        }

        return convertView;
    }

    /***
     *
     * @param poster_path - from Movie DB API
     * @return uri for movie poster for use in Picasso.load(android.net.Uri uri)
     *
     * to construct url for movie poster
     * base url: http://image.tmdb.org/t/p/
     * size: "w92", "w154", "w185" (recommended), "w342", "w500", "w780", or "original"
     * poster_path
     * for example: http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
     *
     */

    private Uri buildPosterUri(String poster_path) {

        final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_PARAM = "w185";

        try {
            return Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(SIZE_PARAM)
                    .appendPath(poster_path)
                    .build();
        } catch (UnsupportedOperationException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }
}
