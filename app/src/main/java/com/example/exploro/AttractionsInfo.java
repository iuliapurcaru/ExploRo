package com.example.exploro;

public class AttractionsInfo {
    private final String id;
    private final String name;
    private final int openingHours;
    private final int closingHours;
    private final int adultPrice;
    private final int studentPrice;
    private final int timeSpent;
    private final double latitude;
    private final double longitude;

    public AttractionsInfo(String id, String name, int openingHours, int closingHours, int adultPrice,
                           int studentPrice, int timeSpent, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
        this.adultPrice = adultPrice;
        this.studentPrice = studentPrice;
        this.timeSpent = timeSpent;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOpeningHours() {
        return openingHours;
    }

    public int getClosingHours() {
        return closingHours;
    }

    public int getAdultPrice() {
        return adultPrice;
    }

    public int getStudentPrice() {
        return studentPrice;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
