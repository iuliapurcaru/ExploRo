package com.example.exploro;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.*;

public class ItineraryTripPlanner {
    private final List<AttractionInfo> attractions;
    private final int numberOfDays;
    private final List<List<Double>> travelTimeMatrix;
    private final String startDate;
    private final int numberOfAdults;
    private final int numberOfStudents;

    public ItineraryTripPlanner(List<AttractionInfo> attractions, int numberOfDays, String startDate, int numberOfAdults, int numberOfStudents) {
        this.attractions = attractions;
        this.numberOfDays = numberOfDays;
        this.travelTimeMatrix = calculateTravelTimeMatrix(calculateDistanceMatrix(attractions));
        this.startDate = startDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfStudents = numberOfStudents;
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

    public List<List<AttractionInfo>> planItinerary() {
        List<List<AttractionInfo>> itinerary = new ArrayList<>();
        for (int i = 0; i < numberOfDays; i++) {
            itinerary.add(new ArrayList<>());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
                Date startDateParsed = sdf.parse(startDate);
            if (startDateParsed != null) {
                calendar.setTime(startDateParsed);
            }
        } catch (Exception e) {
            Log.e("ItineraryTripPlanner", "Error parsing start date", e);
        }

        boolean[] visited = new boolean[attractions.size()];
        int currentDay = 0;

        while (currentDay < numberOfDays && hasUnvisitedAttractions(visited)) {
            List<AttractionInfo> dayPlan = itinerary.get(currentDay);
            double currentDayTime = 9.0;
            int currentAttractionIndex = findFirstUnvisitedAttraction(visited);

            while (currentAttractionIndex != -1) {
                AttractionInfo currentAttraction = attractions.get(currentAttractionIndex);

                if (currentDayTime < currentAttraction.getOpeningHour()) {
                    currentDayTime = currentAttraction.getOpeningHour();
                }

                currentDayTime = Math.ceil(currentDayTime);

                // TODO Remove this log statement
                Log.d("ItineraryTripPlanner", "Current time is " + currentDayTime + " and the attraction opens at " + currentAttraction.getOpeningHour() + " and closes at " + currentAttraction.getClosingHour() + " hours");

                if (currentDayTime + currentAttraction.getTimeSpent() > currentAttraction.getClosingHour() ||
                        currentDayTime < currentAttraction.getOpeningHour()) {
                    currentAttractionIndex = findClosestEligibleAttraction(currentAttractionIndex, visited, travelTimeMatrix, currentDayTime);
                    if (currentAttractionIndex == -1) {
                        break;
                    }
                    continue;
                }

                visited[currentAttractionIndex] = true;
                currentAttraction.setVisitTime(currentDayTime);
                dayPlan.add(currentAttraction);
                currentDayTime += currentAttraction.getTimeSpent();

                int nextAttractionIndex = findClosestEligibleAttraction(currentAttractionIndex, visited, travelTimeMatrix, currentDayTime);
                while (nextAttractionIndex != -1 &&
                        (currentDayTime + travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex) < attractions.get(nextAttractionIndex).getOpeningHour() ||
                                currentDayTime + travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex) + attractions.get(nextAttractionIndex).getTimeSpent() > attractions.get(nextAttractionIndex).getClosingHour())) {
                    nextAttractionIndex = findClosestEligibleAttraction(nextAttractionIndex, visited, travelTimeMatrix, currentDayTime);
                }

                if (nextAttractionIndex != -1) {
                    currentDayTime += travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex);
                    currentAttractionIndex = nextAttractionIndex;
                } else {
                    break;
                }

                // TODO Remove this log statement
                Log.d("ItineraryTripPlanner", "Day " + (currentDay + 1) + ": " + currentAttraction.getName() + " - " + currentDayTime + " hours");
                Log.d("ItineraryTripPlanner", currentAttraction.getName() + " -> " + attractions.get(nextAttractionIndex).getName());
            }
            currentDay++;
            calendar.add(Calendar.DAY_OF_MONTH, 1);
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

    private int findClosestEligibleAttraction(int index, boolean[] visited, List<List<Double>> travelTimeMatrix, double currentDayTime) {
        double minTravelTime = Double.MAX_VALUE;
        int closestAttraction = -1;
        for (int i = 0; i < travelTimeMatrix.get(index).size(); i++) {
            if (!visited[i]) {
                AttractionInfo nextAttraction = attractions.get(i);
                double travelTime = travelTimeMatrix.get(index).get(i);

                if (currentDayTime + travelTime >= nextAttraction.getOpeningHour() &&
                        currentDayTime + travelTime + nextAttraction.getTimeSpent() <= nextAttraction.getClosingHour()) {
                    if (travelTime < minTravelTime) {
                        minTravelTime = travelTime;
                        closestAttraction = i;
                    }
                }
            }
        }
        return closestAttraction;
    }

    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (AttractionInfo attraction : attractions) {
            totalPrice += (attraction.getAdultPrice() * numberOfAdults) + (attraction.getStudentPrice() * numberOfStudents);
        }
        return totalPrice;
    }
}
