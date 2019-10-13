package com.example.moviediscovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * This is the adapter for the RecyclerView
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageButtonHolder> {
    public static final String BASE_POSTER_PATH = "https://image.tmdb.org/t/p/original";
    private List<MovieDb> movies;
    private Context context;

    /**
     * Constructor
     *
     * @param movies  The list of movies
     * @param context The current context
     */
    public RecyclerViewAdapter(List<MovieDb> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageButtonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ImageButtonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageButtonHolder holder, int position) {
        MovieDb movie = movies.get(position);

        // Get display metrics for resizing
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        Picasso.get()
                .load(BASE_POSTER_PATH + movie.getPosterPath())
                .resize(width / MoviesListFragment.COLUMNS, (int) ((width / MoviesListFragment.COLUMNS) * 1.5))     //1.5 is arbitrary
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.moviePosterImageButton);

        holder.moviePosterImageButton.setOnClickListener(v -> {
            Context context = holder.moviePosterImageButton.getContext();
            Intent intent = new Intent(context, MovieDetailsContainer.class);
            intent.putExtra("MOVIE", movies.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * The ViewHolder for the recyclerView
     */
    static class ImageButtonHolder extends RecyclerView.ViewHolder {
        public ImageButton moviePosterImageButton;

        public ImageButtonHolder(View view) {
            super(view);
            moviePosterImageButton = view.findViewById(R.id.moviePoster);
        }
    }

    /**
     * Setter for movies.
     *
     * @param movies the new movies list
     */
    public void setMovies(List<MovieDb> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
