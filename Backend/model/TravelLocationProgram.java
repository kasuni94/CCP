package com.EduExplore.System.model;

import jakarta.persistence.Entity;

@Entity
public class TravelLocationProgram extends Program {

    private String travelDate;  // Date of the travel
    private int travelers;  // Number of travelers
    private String travelType;  // Leisure, Business
    private String accommodationPreference;  // Accommodation preference
    private String guideName;  // Name of the travel guide

    // Getters and Setters
    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public int getTravelers() {
        return travelers;
    }

    public void setTravelers(int travelers) {
        this.travelers = travelers;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getAccommodationPreference() {
        return accommodationPreference;
    }

    public void setAccommodationPreference(String accommodationPreference) {
        this.accommodationPreference = accommodationPreference;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public void setAccommodation(String accommodation) {
        this.accommodationPreference = accommodation;
    }

}
