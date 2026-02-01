package com.busreservation.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for reservation requests.
 */
public class ReservationRequestDTO {
    
    @JsonProperty("passengers")
    private int passengers;
    
    @JsonProperty("origin")
    private String origin;
    
    @JsonProperty("destination")
    private String destination;
    
    @JsonProperty("price")
    private double price;
    
    @JsonProperty("travelDate")
    private String travelDate; // Format: YYYY-MM-DD
    
    public ReservationRequestDTO() {
    }
    
    public ReservationRequestDTO(int passengers, String origin, String destination, double price) {
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
    }
    
    public ReservationRequestDTO(int passengers, String origin, String destination, double price, String travelDate) {
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
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
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getTravelDate() {
        return travelDate;
    }
    
    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }
}
