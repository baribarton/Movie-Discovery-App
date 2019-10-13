package com.example.moviediscovery;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list_container);

        if (!Utils.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(MainActivity.this, "You must be connected to the internet", Toast.LENGTH_LONG).show();
            ProgressBar progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            return;
        }

        MoviesListFragment moviesListFragment = new MoviesListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.movies_list_fragment, moviesListFragment, "MoviesList");
        fragmentTransaction.commit();
    }
}
