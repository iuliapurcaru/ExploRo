package com.example.exploro.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkUtils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }

        NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

}
