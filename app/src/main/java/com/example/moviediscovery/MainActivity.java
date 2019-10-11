package com.example.moviediscovery;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.TextView;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "f879d096ee67e768d6d9f09ca12e0ae9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new MovieFetcher().execute();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(new ViewPagerFragment(), "ViewPager");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class MovieFetcher extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Void... voids) {
            TmdbMovies movies = new TmdbApi(API_KEY).getMovies();
            return movies.getMovie(5353, "en");
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
//            TextView textView = findViewById(R.id.text1);
//            textView.setText(movieDb.getTitle() + ": " + movieDb.getOverview());
            super.onPostExecute(movieDb);
        }
    }
}
