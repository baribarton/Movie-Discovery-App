package com.example.moviediscovery;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * Utilities class for static methods that can be used anywhere in the app
 */
public class Utils {
    public static final String GENERAL_ERROR_MESSAGE = "Something went wrong";
    public static final String CONNECTIVITY_ERROR_MESSAGE = "You must be connected to the internet";

    /**
     * Checks if the network is available.
     *
     * @return true if the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Changes the status bar color of the current window
     *
     * @param activity The current activity
     * @param newColor The color to change the status bar to
     */
    public static void setStatusBarColor(Activity activity, int newColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(newColor);
        }
    }
}
