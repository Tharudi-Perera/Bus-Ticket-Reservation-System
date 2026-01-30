package com.busreservation.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for availability check requests.
 */
public class AvailabilityRequestDTO {
    
    @JsonProperty("passengers")
    private int passengers;
    
    @JsonProperty("origin")
    private String origin;
    
    @JsonProperty("destination")
    private String destination;
    
    public AvailabilityRequestDTO() {
    }
    
    public AvailabilityRequestDTO(int passengers, String origin, String destination) {
        this.passengers = passengers;
        this.origin = origin;
        this.destination = destination;
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
