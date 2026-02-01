package com.busreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for reservation requests.
 */
public class ReservationRequestDTO {
    @JsonProperty("passengers")
    private int passengers;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("tripDate")
    private String tripDate;

    public ReservationRequestDTO() {
    }

    public ReservationRequestDTO(int passengers, String origin, String destination, Double price, String tripDate) {
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    @Override
    public String toString() {
        return "ReservationRequestDTO{" +
                "passengers=" + passengers +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", price=" + price +
                ", tripDate='" + tripDate + '\'' +
                '}';
    }
}
