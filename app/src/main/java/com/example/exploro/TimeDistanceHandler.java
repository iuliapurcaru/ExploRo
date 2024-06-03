package com.example.exploro;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimeDistanceHandler {

    public static double haversineFormula(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double rad = 6371;

        return rad * c;
    }

    public static List<List<Double>> calculateDistanceMatrix(List<AttractionInfo> attractions) {
        int n = attractions.size();
        List<List<Double>> distanceMatrix = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            List<Double> distances = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distances.add(0.0);
                } else {
                    double distance = haversineFormula(attractions.get(i).getLatitude(), attractions.get(i).getLongitude(),
                            attractions.get(j).getLatitude(), attractions.get(j).getLongitude());
                    distances.add(distance);
                }
            }
            distanceMatrix.add(distances);
        }
        return distanceMatrix;
    }

    public static List<List<Double>> calculateTravelTimeMatrix(List<List<Double>> distanceMatrix) {
        int n = distanceMatrix.size();
        List<List<Double>> travelTimeMatrix = new ArrayList<>(n);
        double averageSpeed = 3.0;

        for (int i = 0; i < n; i++) {
            List<Double> travelTimes = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    travelTimes.add(0.0);
                } else {
                    double travelTime = distanceMatrix.get(i).get(j) / averageSpeed;
                    travelTimes.add(travelTime);
                }
            }
            travelTimeMatrix.add(travelTimes);
        }
        return travelTimeMatrix;
    }

    public static Calendar parseStartDate(String startDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date startDateParsed = dateFormat.parse(startDate);
            if (startDateParsed != null) {
                calendar.setTime(startDateParsed);
            }
        } catch (Exception e) {
            Log.e("ItineraryTripPlanner", "Error parsing start date", e);
        }
        return calendar;
    }

    public static double roundHour(double time) {
        int wholePart = (int) time;
        double fractionalPart = time - wholePart;

        if (fractionalPart == 0.0) {
            return wholePart;
        } else if (fractionalPart <= 0.5) {
            return wholePart + 0.5;
        } else {
            return wholePart + 1.0;
        }
    }

}
