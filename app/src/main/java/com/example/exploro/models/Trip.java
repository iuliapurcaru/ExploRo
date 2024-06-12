package com.example.exploro.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable {

    private String tripID;
    private final String destinationID;
    private final String startDate;
    private final String endDate;
    private final int numberOfDays;
    private final int numberOfAdults;
    private final int numberOfStudents;
    private final List<String> selectedAttractions = new ArrayList<>();

    public Trip() {
        this.destinationID = "";
        this.startDate = "";
        this.endDate = "";
        this.numberOfDays = 0;
        this.numberOfAdults = 0;
        this.numberOfStudents = 0;
    }

    public Trip(String tripID, String destinationID, String startDate, String endDate, int numberOfDays, int numberOfAdults, int numberOfStudents, List<String> selectedAttractions) {
        this.tripID = tripID;
        this.destinationID = destinationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfDays = numberOfDays;
        this.numberOfAdults = numberOfAdults;
        this.numberOfStudents = numberOfStudents;
        this.selectedAttractions.addAll(selectedAttractions);
    }

    public String getTripID() {
        return tripID;
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
