package com.example.moviediscovery;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Container activity for displaying the details about a movie
 */
public class MovieDetailsContainer extends AppCompatActivity {
    private static final String TAG = MovieDetailsContainer.class.getName();
    private Toolbar toolbar;
    private ImageView appBarImage;
    private ProgressBar containerProgressBar;
    private MovieDb movie;
    private int colorTheme;

    public static final String MOVIE_BUNDLE_KEY = "MOVIE";
    public static final String COLOR_THEME_KEY = "COLOR_THEME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_container);

        toolbar = findViewById(R.id.movieDetailsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        movie = (MovieDb) getIntent().getExtras().getSerializable("MOVIE");
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(movie.getTitle());

        appBarImage = findViewById(R.id.app_bar_image);

        //TODO if movie can't be retrieved then display an error message

        // Get an image to display on the AppBar
        String movieImagePath = movie.getBackdropPath();
        if (movieImagePath == null) {
            movieImagePath = movie.getPosterPath();
        }

        containerProgressBar = findViewById(R.id.containerProgressBar);

        Target target = createTarget();

        // Keep strong reference to target to prevent garbage collection ( see Picasso.into(Target) )
        appBarImage.setTag(target);

        Picasso.get()
                .load(RecyclerViewAdapter.BASE_POSTER_PATH + movieImagePath)
                .into(target);
    }

    private void createMovieDetailsFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MOVIE_BUNDLE_KEY, movie);
        bundle.putInt(COLOR_THEME_KEY, colorTheme);

        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
        movieDetailsFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.movie_details_fragment, movieDetailsFragment, "MovieDetailsFragment");
        fragmentTransaction.commit();
    }

    /**
     * Creates the target view for the image to be loaded into
     *
     * @return The target view
     */
    private Target createTarget() {
        return new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                appBarImage.setImageBitmap(bitmap);

                //generate a unique color from the image
                Palette.from(bitmap)
                        .generate(palette -> {
                            int color = getResources().getColor(R.color.colorPrimaryDark);
                            if (palette != null) {
                                color = getColor(palette);
                            }
                            setColorTheme(color);
                            createMovieDetailsFragment();
                        });

                containerProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                appBarImage.setImageDrawable(errorDrawable);
                containerProgressBar.setVisibility(View.GONE);
                Toast.makeText(MovieDetailsContainer.this, "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                appBarImage.setImageDrawable(placeHolderDrawable);
            }
        };
    }

    /**
     * Sets the color theme. Also sets all relevant views and their components to the color theme
     * that is passed
     *
     * @param colorTheme the new color theme
     */
    private void setColorTheme(int colorTheme) {
        this.colorTheme = colorTheme;
        setStatusBarColor(colorTheme);
        toolbar.setBackgroundColor(colorTheme);
        setScrimColor(colorTheme);
    }

    /**
     * Changes the status bar color of the current window
     */
    private void setStatusBarColor(int newColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(newColor);
        }
    }

    /**
     * Sets the collapsing toolbar scrim color
     *
     * @param color the color of the scrim
     */
    private void setScrimColor(int color) {
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(color);
    }

    /**
     * Get a color to be used for the color theme
     *
     * @param palette The palette object
     * @return The color that will be used for the color theme
     */
    private int getColor(Palette palette) {
        int defaultColor = getResources().getColor(R.color.colorPrimaryDark);
        int color = palette.getDarkVibrantColor(defaultColor);

        // This is the order of the colors to get. DarkVibrantColor is the most desired, so it's first
        int[] colors = {palette.getDarkMutedColor(defaultColor), palette.getMutedColor(defaultColor),
                palette.getVibrantColor(defaultColor), palette.getLightMutedColor(defaultColor),
                palette.getLightVibrantColor(defaultColor)};

        // Get one of the palette colors from most desirable to least desirable
        for (int newColor : colors) {
            if (color != defaultColor) {
                break;
            }
            color = newColor;
        }

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        // Make the color a little brighter
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);

        return Color.HSVToColor(hsv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
