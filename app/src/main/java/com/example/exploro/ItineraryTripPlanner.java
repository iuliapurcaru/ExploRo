package com.example.exploro;

import java.text.SimpleDateFormat;
import java.util.*;

public class ItineraryTripPlanner {
    private final List<AttractionInfo> attractions;
    private final int numberOfDays;
    private final List<List<Double>> travelTimeMatrix;
    private final String startDate;
    private final int numberOfAdults;
    private final int numberOfStudents;

    public ItineraryTripPlanner(TripInfo tripInfo, List<AttractionInfo> attractions) {
        this.attractions = attractions;
        this.numberOfDays = tripInfo.getNumberOfDays();
        this.travelTimeMatrix = TimeDistanceHandler.calculateTravelTimeMatrix(TimeDistanceHandler.calculateDistanceMatrix(attractions));
        this.startDate = tripInfo.getStartDate();
        this.numberOfAdults = tripInfo.getNumberOfAdults();
        this.numberOfStudents = tripInfo.getNumberOfStudents();
    }

    public List<List<AttractionInfo>> planItinerary() {
        List<List<AttractionInfo>> itinerary = new ArrayList<>();
        for (int i = 0; i < numberOfDays; i++) {
            itinerary.add(new ArrayList<>());
        }

        Calendar calendar = TimeDistanceHandler.parseStartDate(startDate);

        boolean[] visited = new boolean[attractions.size()];
        int currentDay = 0;

        while (currentDay < numberOfDays && hasUnvisitedAttractions(visited)) {
            List<AttractionInfo> dayPlan = itinerary.get(currentDay);
            double currentDayTime = 9.0;
            int currentAttractionIndex = findUnvisitedAttraction(visited);

            while (currentAttractionIndex != -1) {
                AttractionInfo currentAttraction = attractions.get(currentAttractionIndex);

                if (currentDayTime < currentAttraction.getOpeningHour()) {
                    currentDayTime = currentAttraction.getOpeningHour();
                }

                currentDayTime = TimeDistanceHandler.roundHour(currentDayTime);

                if (currentDayTime + currentAttraction.getTimeSpent() > currentAttraction.getClosingHour() ||
                        currentDayTime < currentAttraction.getOpeningHour()) {
                    currentAttractionIndex = findClosestEligibleAttraction(currentAttractionIndex, visited, travelTimeMatrix, currentDayTime);
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

    private int findUnvisitedAttraction(boolean[] visited) {
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                return i;
            }
        }
        return -1;
    }

    private int findClosestEligibleAttraction(int index, boolean[] visited, List<List<Double>> travelTimeMatrix, double currentDayTime) {
        PriorityQueue<AttractionWithTravelTime> queue = new PriorityQueue<>(Comparator.comparingDouble(attraction -> attraction.travelTime));
        for (int i = 0; i < travelTimeMatrix.get(index).size(); i++) {
            if (!visited[i]) {
                AttractionInfo nextAttraction = attractions.get(i);
                double travelTime = travelTimeMatrix.get(index).get(i);
                if (currentDayTime + travelTime >= nextAttraction.getOpeningHour() &&
                        currentDayTime + travelTime + nextAttraction.getTimeSpent() <= nextAttraction.getClosingHour()) {
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
        for (AttractionInfo attraction : attractions) {
            totalPrice += (attraction.getAdultPrice() * numberOfAdults) + (attraction.getStudentPrice() * numberOfStudents);
        }
        return totalPrice;
    }
}
