package com.example.exploro.domain;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.exploro.models.Attraction;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class MapManager {

    private static final String TAG = MapManager.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static void addMarkersToMap(GoogleMap googleMap, List<List<Attraction>> itinerary) {
        if (googleMap != null) {
            googleMap.clear();
            for (List<Attraction> dayPlan : itinerary) {
                for (Attraction attraction : dayPlan) {
                    int hours = (int) attraction.getVisitTime();
                    int minutes = (int) ((attraction.getVisitTime() - hours) * 60);
                    String formattedVisitTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                    LatLng location = new LatLng(attraction.getLatitude(), attraction.getLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(attraction.getName())
                            .snippet("Day: " + attraction.getVisitDay() + " - " + attraction.getVisitDate() + " - Visit time: " + formattedVisitTime)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }
            if (!itinerary.isEmpty() && !itinerary.get(0).isEmpty()) {
                LatLng firstAttraction = new LatLng(itinerary.get(0).get(0).getLatitude(), itinerary.get(0).get(0).getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstAttraction, 15));
            }
        }
    }

    public static void enableMyLocation(Activity activity, GoogleMap googleMap) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            getDeviceLocation(activity, fusedLocationClient, googleMap);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private static void getDeviceLocation(Activity activity, FusedLocationProviderClient fusedLocationClient, GoogleMap googleMap) {
        try {
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here!"));
                    }
                } else {
                    Log.w(TAG, "Current location is null. Using defaults.");
                    LatLng defaultLatLng = new LatLng(-34, 151);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15));
                    googleMap.addMarker(new MarkerOptions().position(defaultLatLng).title("Default Location"));
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}
