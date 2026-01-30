package com.busreservation.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for reservation responses.
 */
public class ReservationResponseDTO {
    
    @JsonProperty("reservationId")
    private String reservationId;
    
    @JsonProperty("seatNumbers")
    private List<String> seatNumbers;
    
    @JsonProperty("origin")
    private String origin;
    
    @JsonProperty("destination")
    private String destination;
    
    @JsonProperty("passengers")
    private int passengers;
    
    @JsonProperty("totalPrice")
    private double totalPrice;
    
    @JsonProperty("reservationTime")
    private String reservationTime;
    
    public ReservationResponseDTO() {
    }
    
    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    
    public List<String> getSeatNumbers() {
        return seatNumbers;
    }
    
    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
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
    
    public int getPassengers() {
        return passengers;
    }
    
    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getReservationTime() {
        return reservationTime;
    }
    
    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }
}
