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

    @JsonProperty("tripDate")
    private String tripDate;

    @JsonProperty("destination")
    private String destination;

    public AvailabilityRequestDTO() {
    }

    public AvailabilityRequestDTO(int passengers, String origin, String destination, String tripDate) {
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
        this.tripDate = tripDate;
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

        public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    @Override
    public String toString() {
        return "AvailabilityRequestDTO{" +
                "passengers=" + passengers +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", tripDate='" + tripDate + '\'' +
                '}';
    }
}
