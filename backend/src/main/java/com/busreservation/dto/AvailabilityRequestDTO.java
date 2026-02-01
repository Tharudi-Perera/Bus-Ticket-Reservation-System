package com.busreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for availability check requests.
 */
public class AvailabilityRequestDTO {
    @JsonProperty("passengers")
    private int passengers;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("travelDate")
    private String travelDate; // Format: YYYY-MM-DD

    public AvailabilityRequestDTO() {
    }

    public AvailabilityRequestDTO(int passengers, String origin, String destination) {
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
    }

    public AvailabilityRequestDTO(int passengers, String origin, String destination, String travelDate) {
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
        this.travelDate = travelDate;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    @Override
    public String toString() {
        return "AvailabilityRequestDTO{" +
                "passengers=" + passengers +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", travelDate='" + travelDate + '\'' +
                '}';
    }
}
