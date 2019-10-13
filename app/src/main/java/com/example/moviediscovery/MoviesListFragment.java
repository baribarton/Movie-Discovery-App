package com.example.moviediscovery;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MoviesListFragment extends Fragment {
    /*
        I would never put an API_KEY here in a real product for security reasons but since this is just
        an example, I will leave it here. also, these API keys are limited based on IP Address, so
        the security impact is low
     */
    private static final String API_KEY = "f879d096ee67e768d6d9f09ca12e0ae9";

    private static final String MOVIE_RESULTS_PAGES_KEY = "movies";
    public final static int COLUMNS = 2;
    private RecyclerViewAdapter recyclerViewAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), COLUMNS);
        recyclerView.setLayoutManager(layoutManager);

        List<MovieDb> movies = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(movies, getContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        // To keep AsyncTask from causing leaks or crashes
        setRetainInstance(true);

        // Set date
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);

        if (DateRange.getInstance().getStartDate() == null) {
            DateRange.getInstance().setStartDate(simpleDateFormat.format(new Date()));
            DateRange.getInstance().setEndDate(simpleDateFormat.format(new Date()));
        }

        new MovieFetcher(getActivity(), recyclerViewAdapter, DateRange.getInstance()).execute();
        setFloatingActionButtonOnClick();
    }

    /**
     * AsyncTask to fetch movies within a date range.
     */
    private static class MovieFetcher extends AsyncTask<Void, Void, List<MovieDb>> {
        private WeakReference<Activity> activityWeakReference;
        private DateRange dateRange;
        private RecyclerViewAdapter recyclerViewAdapter;

        private static final int MAX_PAGE_COUNT = 5;

        public MovieFetcher(Activity activity, RecyclerViewAdapter recyclerViewAdapter, DateRange dateRange) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.recyclerViewAdapter = recyclerViewAdapter;
            this.dateRange = dateRange;
        }

        @Override
        protected List<MovieDb> doInBackground(Void... voids) {
            Discover initialDiscover = new Discover().
                    page(1).
                    releaseDateGte(dateRange.getStartDate()).
                    releaseDateLte(dateRange.getEndDate()).sortBy("release_date.desc");

            // Get initial movie results page
            MovieResultsPage initialMovieResultsPage = new TmdbApi(API_KEY).getDiscover().getDiscover(initialDiscover);
            List<MovieDb> movies = new ArrayList<>();
            filter(movies, initialMovieResultsPage);

            int pageCount = Math.min(initialMovieResultsPage.getTotalPages(), MAX_PAGE_COUNT);

            // Iterate through each page
            for (int i = 1; i < pageCount; i++) {
                Discover discover = new Discover().
                        page(i).
                        releaseDateGte(dateRange.getStartDate()).
                        releaseDateLte(dateRange.getEndDate());

                MovieResultsPage movieResultsPage = new TmdbApi(API_KEY).getDiscover().getDiscover(discover);
                filter(movies, movieResultsPage);
            }

            sortDescending(movies);
            return movies;
        }

        /**
         * Sort the movies because they can sometimes be out of order
         *
         * @param movies the list of movies
         */
        private void sortDescending(List<MovieDb> movies) {
            Collections.sort(movies, (movie1, movie2) -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    return simpleDateFormat.parse(movie2.getReleaseDate()).compareTo(simpleDateFormat.parse(movie1.getReleaseDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            });
        }

        /**
         * Filters out irrelevant movies such as movies with a primary release date before the user
         * input date range
         *
         * @param movies           The movies list to display
         * @param movieResultsPage The page of movies returned from the endpoint
         */
        private void filter(List<MovieDb> movies, MovieResultsPage movieResultsPage) {
            for (MovieDb movie : movieResultsPage.getResults()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                boolean isValidDate = false;
                try {
                    isValidDate = simpleDateFormat.parse(movie.getReleaseDate()).after(simpleDateFormat.parse(DateRange.getInstance().getStartDate())) ||
                            simpleDateFormat.parse(movie.getReleaseDate()).equals(simpleDateFormat.parse(DateRange.getInstance().getStartDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (isValidDate) {
                    movies.add(movie);
                }
            }
        }

        @Override
        protected void onPostExecute(List<MovieDb> movies) {
            recyclerViewAdapter.setMovies(movies);
            recyclerViewAdapter.notifyDataSetChanged();
            ProgressBar dateProgressBar = activityWeakReference.get().findViewById(R.id.progress_bar);
            dateProgressBar.setVisibility(View.GONE);
            super.onPostExecute(movies);
        }
    }


    /**
     * Sets the FAB on click listener
     */
    private void setFloatingActionButtonOnClick() {
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(v -> {
            ProgressBar dateProgressBar = getActivity().findViewById(R.id.progress_bar);
            dateProgressBar.setVisibility(View.VISIBLE);

            final Calendar now = Calendar.getInstance();

            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH);
            int day = now.get(Calendar.DAY_OF_MONTH);

            if (DateRange.getInstance().getStartDate() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    Date date = simpleDateFormat.parse(DateRange.getInstance().getStartDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(createDatePickerDialogOnClick(), year, month, day);
            datePickerDialog.setOnCancelListener(dialog -> dateProgressBar.setVisibility(View.GONE));
            datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
        });
    }

    /**
     * Creates the DatePickerDialogOnClickListener
     *
     * @return The DatePickerDialogOnClickListener
     */
    private DatePickerDialog.OnDateSetListener createDatePickerDialogOnClick() {
        return (view, yearStart, monthOfYearStart, dayOfMonthStart, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            calendar.set(yearStart, monthOfYearStart, dayOfMonthStart, 0, 0);
            String startDate = simpleDateFormat.format(calendar.getTime());

            calendar.set(yearEnd, monthOfYearEnd, dayOfMonthEnd, 0, 0);
            String endDate = simpleDateFormat.format(calendar.getTime());
            boolean isValidDate = false;

            try {
                isValidDate = simpleDateFormat.parse(startDate).before(simpleDateFormat.parse(endDate)) ||
                        simpleDateFormat.parse(startDate).equals(simpleDateFormat.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isValidDate) {
                DateRange.getInstance().setStartDate(startDate);
                DateRange.getInstance().setEndDate(endDate);
                new MovieFetcher(getActivity(), recyclerViewAdapter, DateRange.getInstance()).execute();
                ProgressBar dateProgressBar = getActivity().findViewById(R.id.progress_bar);
                dateProgressBar.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity(), "Invalid date range", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
