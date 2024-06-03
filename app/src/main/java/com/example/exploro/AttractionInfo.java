package com.example.exploro;

public class AttractionInfo {
    private final String id;
    private final String name;
    private final double openingHours;
    private final double closingHours;
    private final int adultPrice;
    private final int studentPrice;
    private final double timeSpent;
    private final double latitude;
    private final double longitude;
    private int visitDay;
    private String visitDate;
    private double visitTime;

    public AttractionInfo(String id, String name, double openingHours, double closingHours, int adultPrice,
                          int studentPrice, double timeSpent, double latitude, double longitude) {
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

    public double getOpeningHour() {
        return openingHours;
    }

    public double getClosingHour() {
        return closingHours;
    }

    public int getAdultPrice() {
        return adultPrice;
    }

    public int getStudentPrice() {
        return studentPrice;
    }

    public double getTimeSpent() {
        return timeSpent;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getVisitDay() {
        return visitDay;
    }

    public void setVisitDay(int visitDay) {
        this.visitDay = visitDay;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public double getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(double visitTime) {
        this.visitTime = visitTime;
    }
}
