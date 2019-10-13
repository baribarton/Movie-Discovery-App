package com.example.moviediscovery;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends AppCompatActivity {

    /*
        I would put an API_KEY here in a real product for security reasons but since this is just
        an example, I will leave it here. also, these API keys are limited based on IP Address, so
        the security impact is low
     */
    public static final String API_KEY = "f879d096ee67e768d6d9f09ca12e0ae9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list_container);
        new MovieFetcher().execute();
        setFloatingActionButtonOnClick();
    }

    private void setFloatingActionButtonOnClick() {
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Toast.makeText(MainActivity.this, year + ", " + monthOfYear + ", " + dayOfMonth, Toast.LENGTH_LONG).show();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
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
            fragmentTransaction.replace(R.id.movies_list_fragment, recyclerViewFragment, "ViewPager");
            fragmentTransaction.commit();

            super.onPostExecute(movieResultsPage);
        }
    }
}
