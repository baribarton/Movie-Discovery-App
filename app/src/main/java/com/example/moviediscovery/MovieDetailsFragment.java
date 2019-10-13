package com.example.moviediscovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Fragment for displaying a movie's details
 */
public class MovieDetailsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_details_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int colorTheme = getArguments().getInt(MovieDetailsContainer.COLOR_THEME_KEY);

        setDynamicTextViews(view, colorTheme);
        setStaticTextViewsColor(view, colorTheme);
    }

    /**
     * Sets the text and color for the textviews that change based on the movie
     *
     * @param view       The view for this fragment
     * @param colorTheme The color theme
     */
    private void setDynamicTextViews(View view, int colorTheme) {
        MovieDb movie = (MovieDb) getArguments().getSerializable(MovieDetailsContainer.MOVIE_BUNDLE_KEY);

        String title = movie.getTitle();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();

        TextView titleTextView = view.findViewById(R.id.title_text);
        TextView overviewTextView = view.findViewById(R.id.overview_text);
        TextView releaseDateTextView = view.findViewById(R.id.release_date_text);

        titleTextView.setText(title);
        titleTextView.setTextColor(colorTheme);

        overviewTextView.setText(overview);
        overviewTextView.setTextColor(colorTheme);

        releaseDateTextView.setText(releaseDate);
        releaseDateTextView.setTextColor(colorTheme);
    }

    /**
     * Sets the text color for the text views that have unchanging text
     *
     * @param view       The view for this fragment
     * @param colorTheme The color theme
     */
    private void setStaticTextViewsColor(View view, int colorTheme) {
        TextView titleStaticTextView = view.findViewById(R.id.title_static_text);
        TextView overviewStaticTextView = view.findViewById(R.id.overview_static_text);
        TextView releaseDateStaticTextView = view.findViewById(R.id.release_date_static_text);

        titleStaticTextView.setTextColor(colorTheme);
        overviewStaticTextView.setTextColor(colorTheme);
        releaseDateStaticTextView.setTextColor(colorTheme);
    }
}
