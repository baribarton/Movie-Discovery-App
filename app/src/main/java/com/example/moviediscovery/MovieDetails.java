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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.palette.graphics.Palette;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Random;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MovieDetails extends AppCompatActivity {
    private static final String TAG = MovieDetails.class.getName();
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_container);

        toolbar = findViewById(R.id.movieDetailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MovieDb movie = (MovieDb) getIntent().getExtras().getSerializable("MOVIE");
        final ImageView appBarImage = findViewById(R.id.app_bar_image);

        // Get an image to display on the AppBar
        String movieImagePath = movie.getBackdropPath();
        if (movieImagePath == null) {
            movieImagePath = movie.getPosterPath();
        }

        //get champion image
        ProgressBar containerProgressBar = findViewById(R.id.containerProgressBar);
        //create new target
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                appBarImage.setImageBitmap(bitmap);

                //generate a unique color from the image
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int color = getColor(palette);
                                setColorTheme(color);
                            }
                        });

                containerProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                appBarImage.setImageDrawable(errorDrawable);
                containerProgressBar.setVisibility(View.GONE);
                Toast.makeText(MovieDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                appBarImage.setImageDrawable(placeHolderDrawable);
            }
        };

        // Keep strong reference to target to prevent garbage collection
        appBarImage.setTag(target);

        //get proper color from image
        Picasso.get()
                .load(RecyclerViewAdapter.BASE_POSTER_PATH + movieImagePath)
                .into(target);

        Log.i("image", RecyclerViewAdapter.BASE_POSTER_PATH + movie.getBackdropPath());
    }

    /**
     * setter for color theme. Also sets all relevant items to the color that is passed
     *
     * @param colorTheme the new color theme
     */
    public void setColorTheme(int colorTheme) {
        changeStatusBarColor(colorTheme);
        toolbar.setBackgroundColor(colorTheme);
        setScrimColor(colorTheme);
    }

    /**
     * changes the status bar color of the current window and sets the color theme of this page for
     * this particular champion
     */
    private void changeStatusBarColor(int newColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(newColor);
        }
    }

    /**
     * sets the collapsing toolbar scrim color
     *
     * @param color the color of the scrim
     */
    private void setScrimColor(int color) {
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(color);
    }

    /**
     * get the proper color to be used for the color theme
     *
     * @param palette palette object
     * @return the color that will be used for the text
     */
    private int getColor(Palette palette) {
        int color = palette.getDarkVibrantColor(Color.parseColor("#08084C"));

        int defaultColor = Color.parseColor("#08084C");

        //this is the order of the colors to get. DarkVibrantColor is the most desired
        int[] colors = {palette.getDarkVibrantColor(defaultColor), palette.getDarkMutedColor(defaultColor)
                , palette.getMutedColor(defaultColor), palette.getVibrantColor(defaultColor)
                , palette.getLightMutedColor(defaultColor), palette.getLightVibrantColor(defaultColor)
                , Color.parseColor("#00574B")};

        //get one of the palette colors
        for (int color1 : colors) {
            if (color != defaultColor) {
                break;
            }
            color = color1;
        }

        float[] hsv = new float[3];
        int brighterColor = color;
        Color.colorToHSV(brighterColor, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);     //a little brighter

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
