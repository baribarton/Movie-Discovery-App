package com.example.moviediscovery;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.Locale;

import info.movito.themoviedbapi.TmdbApi;
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
        if (colorTheme == 0) {
            colorTheme = getResources().getColor(R.color.colorPrimaryDark);
        }

        // Keep AsyncTask from leaking or causing crashes
        setRetainInstance(true);

        MovieDb movie = (MovieDb) getArguments().getSerializable(MovieDetailsContainer.MOVIE_BUNDLE_KEY);

        if (movie == null) {
            Toast.makeText(view.getContext(), Utils.GENERAL_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check network
        Context context = view.getContext();
        if (!Utils.isNetworkAvailable(context)) {
            Toast.makeText(context, Utils.CONNECTIVITY_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch movies
        new MovieDetailsFetcher(getActivity(), movie, colorTheme).execute();
        setInitialTextViews(movie, view, colorTheme);
        setStaticTextViewsColor(view, colorTheme);

    }

    /**
     * AsyncTask to fetch the movie details. This would probably be better combined with a
     * broadcast receiver so that there aren't any issues when this activity is recreated (such as
     * on orientation change)
     */
    private static class MovieDetailsFetcher extends AsyncTask<Void, Void, MovieDb> {

        private WeakReference<Activity> weakReferenceActivity;
        private MovieDb movie;
        private int colorTheme;

        /**
         * Constructor
         *
         * @param activity   The parent Activity
         * @param movie      the movie to fetch details for
         * @param colorTheme the color theme
         */
        public MovieDetailsFetcher(Activity activity, MovieDb movie, int colorTheme) {
            weakReferenceActivity = new WeakReference<>(activity);
            this.movie = movie;
            this.colorTheme = colorTheme;
        }

        @Override
        protected MovieDb doInBackground(Void... voids) {

            // Just return the movies that were retrieved if something went wrong during API call

            try {
                return new TmdbApi(MoviesListFragment.API_KEY)
                        .getMovies()
                        .getMovie(movie.getId(), "en-US", null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieDb movie) {
            TextView revenueTextView = weakReferenceActivity.get().findViewById(R.id.revenue_date_text);
            TextView runtimeTextView = weakReferenceActivity.get().findViewById(R.id.runtime_date_text);

            revenueTextView.setText(String.format(Locale.ENGLISH, "$%d", movie.getRevenue()));
            revenueTextView.setTextColor(colorTheme);

            runtimeTextView.setText(String.format(Locale.ENGLISH, "%d minutes", movie.getRuntime()));
            runtimeTextView.setTextColor(colorTheme);
        }
    }

    /**
     * Sets the text and color for the textviews that don't require an API call to getDetails
     *
     * @param view       The view for this fragment
     * @param colorTheme The color theme
     */
    private void setInitialTextViews(MovieDb movie, View view, int colorTheme) {
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
        TextView revenueStaticTextView = view.findViewById(R.id.revenue_static_text);
        TextView runtimeStaticTextView = view.findViewById(R.id.runtime_static_text);

        titleStaticTextView.setTextColor(colorTheme);
        overviewStaticTextView.setTextColor(colorTheme);
        releaseDateStaticTextView.setTextColor(colorTheme);
        revenueStaticTextView.setTextColor(colorTheme);
        runtimeStaticTextView.setTextColor(colorTheme);
    }
}
