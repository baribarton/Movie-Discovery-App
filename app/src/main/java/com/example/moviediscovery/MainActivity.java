package com.example.moviediscovery;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "f879d096ee67e768d6d9f09ca12e0ae9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MovieFetcher().execute();
    }

    private class MovieFetcher extends AsyncTask<Void, Void, MovieResultsPage> {

        @Override
        protected MovieResultsPage doInBackground(Void... voids) {
            Discover discover = new Discover().page(1).primaryReleaseYear(2019);
            return new TmdbApi(API_KEY).getDiscover().getDiscover(discover);
        }

        @Override
        protected void onPostExecute(MovieResultsPage movieResultsPage) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("movieResultsPage", movieResultsPage);

            RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
            recyclerViewFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, recyclerViewFragment, "ViewPager");
            fragmentTransaction.commit();

            super.onPostExecute(movieResultsPage);
        }
    }
}
