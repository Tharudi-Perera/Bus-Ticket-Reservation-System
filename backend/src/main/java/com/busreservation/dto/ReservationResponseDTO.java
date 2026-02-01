package com.busreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Data Transfer Object for reservation responses.
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

    @JsonProperty("tripDate")
    private String tripDate;

    public ReservationResponseDTO() {
    }

    public ReservationResponseDTO(String reservationId, List<String> seatNumbers, String origin,
                                String destination, int passengers, double totalPrice, String reservationTime, String tripDate) {
        this.reservationId = reservationId;
        this.seatNumbers = seatNumbers;
        this.origin = origin;
        this.destination = destination;
        this.passengers = passengers;
        this.totalPrice = totalPrice;
        this.reservationTime = reservationTime;
        this.tripDate = tripDate;
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

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }
}
