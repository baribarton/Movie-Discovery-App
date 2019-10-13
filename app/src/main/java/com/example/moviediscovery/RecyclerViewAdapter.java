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

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TextViewHolder> {
    public static final String BASE_POSTER_PATH = "https://image.tmdb.org/t/p/original";
    private MovieResultsPage movieResultsPage;
    private int resultsCount;
    private Context context;

    public RecyclerViewAdapter(MovieResultsPage movieResultsPage, int resultsCount, Context context) {
        this.movieResultsPage = movieResultsPage;
        this.resultsCount = resultsCount;
        this.context = context;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        MovieDb movie = movieResultsPage.getResults().get(position);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        Picasso.get()
                .load(BASE_POSTER_PATH + movie.getPosterPath())
                .resize(width / RecyclerViewFragment.COLUMNS, (int) ((width / RecyclerViewFragment.COLUMNS) * 1.5))
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.moviePosterImageButton);

        holder.moviePosterImageButton.setOnClickListener(v -> {
            Context context = holder.moviePosterImageButton.getContext();
            Intent intent = new Intent(context, MovieDetailsContainer.class);
            intent.putExtra("MOVIE", movieResultsPage.getResults().get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resultsCount;
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageButton moviePosterImageButton;

        public TextViewHolder(View view) {
            super(view);
            moviePosterImageButton = view.findViewById(R.id.moviePoster);
        }
    }
}
