package com.busreservation.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for availability check responses.
 */
public class AvailabilityResponseDTO {
    
    @JsonProperty("availableSeats")
    private List<String> availableSeats;
    
    @JsonProperty("totalPrice")
    private double totalPrice;
    
    @JsonProperty("pricePerSeat")
    private double pricePerSeat;
    
    @JsonProperty("passengers")
    private int passengers;
    
    @JsonProperty("origin")
    private String origin;
    
    @JsonProperty("destination")
    private String destination;
    
    public AvailabilityResponseDTO() {
    }
    
    public List<String> getAvailableSeats() {
        return availableSeats;
    }
    
    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public double getPricePerSeat() {
        return pricePerSeat;
    }
    
    public void setPricePerSeat(double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
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
}
