package com.example.exploro.domain;

import com.example.exploro.models.Attraction;
import com.example.exploro.models.Trip;

import java.text.SimpleDateFormat;
import java.util.*;

public class ItineraryTripPlanner {
    private final List<Attraction> attractions;
    private final int numberOfDays;
    private final List<List<Double>> travelTimeMatrix;
    private final String startDate;
    private final int numberOfAdults;
    private final int numberOfStudents;

    public ItineraryTripPlanner(Trip trip, List<Attraction> attractions) {
        this.attractions = attractions;
        this.numberOfDays = trip.getNumberOfDays();
        this.travelTimeMatrix = TimeDistanceManager.calculateTravelTimeMatrix(TimeDistanceManager.calculateDistanceMatrix(attractions));
        this.startDate = trip.getStartDate();
        this.numberOfAdults = trip.getNumberOfAdults();
        this.numberOfStudents = trip.getNumberOfStudents();
    }

    public List<List<Attraction>> planItinerary() {
        List<List<Attraction>> itinerary = new ArrayList<>();
        for (int i = 0; i < numberOfDays; i++) {
            itinerary.add(new ArrayList<>());
        }

        Calendar calendar = PlanningManager.parseStartDate(startDate);

        boolean[] visited = new boolean[attractions.size()];
        int currentDay = 0;

        while (currentDay < numberOfDays && hasUnvisitedAttractions(visited)) {
            List<Attraction> dayPlan = itinerary.get(currentDay);
            double currentDayTime = 9.0;
            int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            int currentAttractionIndex = findEarliestOpeningAttraction(visited, dayOfWeek);

            while (currentAttractionIndex != -1) {
                Attraction currentAttraction = attractions.get(currentAttractionIndex);

                double openingHour = currentAttraction.getOpeningHour(dayOfWeek);
                double closingHour = currentAttraction.getClosingHour(dayOfWeek);

                if (currentDayTime < openingHour) {
                    currentDayTime = openingHour;
                }

                currentDayTime = TimeDistanceManager.roundHour(currentDayTime);

                if (currentDayTime + currentAttraction.getTimeSpent() > closingHour || currentDayTime < openingHour) {
                    currentAttractionIndex = findClosestEligibleAttraction(currentAttractionIndex, visited, travelTimeMatrix, currentDayTime, dayOfWeek);
                    if (currentAttractionIndex == -1) {
                        break;
                    }
                    continue;
                }

                visited[currentAttractionIndex] = true;
                currentAttraction.setVisitDay(currentDay + 1);
                currentAttraction.setVisitDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime()));
                currentAttraction.setVisitTime(currentDayTime);
                dayPlan.add(currentAttraction);

                currentDayTime += currentAttraction.getTimeSpent();

                int nextAttractionIndex = findClosestEligibleAttraction(currentAttractionIndex, visited, travelTimeMatrix, currentDayTime, dayOfWeek);
                while (nextAttractionIndex != -1 &&
                        (currentDayTime + travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex) < attractions.get(nextAttractionIndex).getOpeningHour(dayOfWeek) ||
                                currentDayTime + travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex) + attractions.get(nextAttractionIndex).getTimeSpent() > attractions.get(nextAttractionIndex).getClosingHour(dayOfWeek))) {
                    nextAttractionIndex = findClosestEligibleAttraction(nextAttractionIndex, visited, travelTimeMatrix, currentDayTime, dayOfWeek);
                }

                if (nextAttractionIndex != -1) {
                    currentDayTime += travelTimeMatrix.get(currentAttractionIndex).get(nextAttractionIndex);
                    currentAttractionIndex = nextAttractionIndex;
                } else {
                    break;
                }
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

    private int findEarliestOpeningAttraction(boolean[] visited, int dayOfWeek) {
        int earliestAttractionIndex = -1;
        double earliestOpeningTime = Double.MAX_VALUE;

        for (int i = 0; i < attractions.size(); i++) {
            if (!visited[i]) {
                double openingTime = attractions.get(i).getOpeningHour(dayOfWeek);
                if (openingTime < earliestOpeningTime) {
                    earliestOpeningTime = openingTime;
                    earliestAttractionIndex = i;
                }
            }
        }

        return earliestAttractionIndex;
    }

    private int findClosestEligibleAttraction(int index, boolean[] visited, List<List<Double>> travelTimeMatrix, double currentDayTime, int dayOfWeek) {
        PriorityQueue<AttractionWithTravelTime> queue = new PriorityQueue<>(Comparator.comparingDouble(attraction -> attraction.travelTime));
        for (int i = 0; i < travelTimeMatrix.get(index).size(); i++) {
            if (!visited[i]) {
                Attraction nextAttraction = attractions.get(i);
                double travelTime = travelTimeMatrix.get(index).get(i);
                if (currentDayTime + travelTime >= nextAttraction.getOpeningHour(dayOfWeek) &&
                        currentDayTime + travelTime + nextAttraction.getTimeSpent() <= nextAttraction.getClosingHour(dayOfWeek)) {
                    queue.add(new AttractionWithTravelTime(i, travelTime));
                }
            }
        }
        AttractionWithTravelTime nextAttraction = queue.poll();
        return (nextAttraction != null) ? nextAttraction.index : -1;
    }

    private static class AttractionWithTravelTime {
        int index;
        double travelTime;

        AttractionWithTravelTime(int index, double travelTime) {
            this.index = index;
            this.travelTime = travelTime;
        }
    }


    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (Attraction attraction : attractions) {
            totalPrice += (attraction.getAdultPrice() * numberOfAdults) + (attraction.getStudentPrice() * numberOfStudents);
        }
        return totalPrice;
    }
}
