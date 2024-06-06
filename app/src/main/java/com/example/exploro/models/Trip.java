package com.example.exploro.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable {

    private String tripId;
    private final String destinationID;
    private final String startDate;
    private final String endDate;
    private final int numberOfDays;
    private final int numberOfAdults;
    private final int numberOfStudents;
    private final List<String> selectedAttractions = new ArrayList<>();

    public Trip(String tripId, String destinationID, String startDate, String endDate, int numberOfDays, int numberOfAdults, int numberOfStudents, List<String> selectedAttractions) {
        this.tripId = tripId;
        this.destinationID = destinationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfDays = numberOfDays;
        this.numberOfAdults = numberOfAdults;
        this.numberOfStudents = numberOfStudents;
        this.selectedAttractions.addAll(selectedAttractions);
    }
    public Trip(String destinationID, String startDate, String endDate, int numberOfDays, int numberOfAdults, int numberOfStudents, List<String> selectedAttractions) {
        this.destinationID = destinationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfDays = numberOfDays;
        this.numberOfAdults = numberOfAdults;
        this.numberOfStudents = numberOfStudents;
        this.selectedAttractions.addAll(selectedAttractions);
    }

    public String getTripID() {
        return tripId;
    }

    public String getDestinationID() {
        return destinationID;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public List<String> getSelectedAttractions() {
        return selectedAttractions;
    }
}
