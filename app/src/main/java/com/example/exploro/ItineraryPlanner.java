package com.example.exploro;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ItineraryPlanner {

    private final double START_HOUR = 9.0;
    private final double END_HOUR = 18.0;
    private final List<AttractionInfo> attractions;
    private final int numberOfDays;
    private final List<List<Double>> travelTimeMatrix;

    public ItineraryPlanner(List<AttractionInfo> attractions, int numberOfDays) {
        this.attractions = attractions;
        this.numberOfDays = numberOfDays;
        this.travelTimeMatrix = calculateTravelTimeMatrix(calculateDistanceMatrix(attractions));
    }

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
        double averageSpeed = 4.0;

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

    public List<List<AttractionInfo>> planItinerary() {
        List<List<AttractionInfo>> itinerary = new ArrayList<>();
        for (int i = 0; i < numberOfDays; i++) {
            itinerary.add(new ArrayList<>());
        }

        boolean[] visited = new boolean[attractions.size()];
        int currentDay = 0;

        while (currentDay < numberOfDays && hasUnvisitedAttractions(visited)) {
            List<AttractionInfo> dayPlan = itinerary.get(currentDay);
            double currentDayTime = START_HOUR;
            int currentAttractionIndex = findFirstUnvisitedAttraction(visited);

            while (currentDayTime < END_HOUR && currentAttractionIndex != -1) {
                visited[currentAttractionIndex] = true;
                AttractionInfo currentAttraction = attractions.get(currentAttractionIndex);
                dayPlan.add(currentAttraction);
                currentDayTime += currentAttraction.getTimeSpent();

                int nextAttractionIndex = findClosestAttraction(currentAttractionIndex, visited, travelTimeMatrix, currentDayTime);
                if (nextAttractionIndex != -1 && currentDayTime + travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex) + attractions.get(nextAttractionIndex).getTimeSpent() <= END_HOUR) {
                    currentDayTime += travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex);
                    currentAttractionIndex = nextAttractionIndex;
                } else {
                    break;
                }

                Log.d("ItineraryPlanner", "Day " + (currentDay + 1) + ": " + currentAttraction.getName() + " - " + currentDayTime + " hours");
                Log.d("ItineraryPlanner", currentAttraction.getName() + " -> " +  attractions.get(nextAttractionIndex).getName());
            }
            currentDay++;
        }

        return itinerary;
    }

    private boolean hasUnvisitedAttractions(boolean[] visited) {
        for (boolean visitStatus : visited) {
            if (!visitStatus) {
                return true;
            }
        }
        return false;
    }

    private int findFirstUnvisitedAttraction(boolean[] visited) {
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                return i;
            }
        }
        return -1;
    }

    private int findClosestAttraction(int index, boolean[] visited, List<List<Double>> travelTimeMatrix, double currentDayTime) {
        double minTravelTime = Double.MAX_VALUE;
        int closestAttraction = -1;
        for (int i = 0; i < travelTimeMatrix.get(index).size(); i++) {
            if (!visited[i] && travelTimeMatrix.get(index).get(i) < minTravelTime) {
                if (currentDayTime + travelTimeMatrix.get(index).get(i) <= END_HOUR) {
                    minTravelTime = travelTimeMatrix.get(index).get(i);
                    closestAttraction = i;
                }
            }
        }
        return closestAttraction;
    }

}
