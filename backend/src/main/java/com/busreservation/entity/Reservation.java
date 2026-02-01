package com.busreservation.entity;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a bus ticket reservation.
 */
public class Reservation {
    private final String reservationId;
    private final LocalDate tripDate;
    private final Route route;
    private final List<String> seatNumbers;
    private final int passengerCount;
    private final double totalPrice;
    private final LocalDateTime reservationTime;

    /**
     * Create a new reservation.
     * @param tripDate the date of the trip
     * @param seatNumbers list of assigned seat numbers
     * @param passengerCount number of passengers
     * @param totalPrice total price for the reservation
     */
    public Reservation(LocalDate tripDate, Route route, List<String> seatNumbers, int passengerCount, double totalPrice) {
        this.reservationId = generateReservationId();
        this.route = route;
        this.tripDate = tripDate;
        this.seatNumbers = seatNumbers;
        this.passengerCount = passengerCount;
        this.totalPrice = totalPrice;
        this.reservationTime = LocalDateTime.now();
    }

    /**
     * Generate a unique reservation ID.
     * @return reservation ID
     */
    private String generateReservationId() {
        return "RSV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Get the reservation ID.
     * @return reservation ID
     */
    public String getReservationId() {
        return reservationId;
    }

    public LocalDate getTripDate() { 
        return tripDate; 
    }


    /**
     * Get the route.
     * @return route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Get the list of seat numbers.
     * @return seat numbers
     */
    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    /**
     * Get the passenger count.
     * @return passenger count
     */
    public int getPassengerCount() {
        return passengerCount;
    }

    /**
     * Get the total price.
     * @return total price
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Get the reservation time.
     * @return reservation time
     */
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + reservationId + '\'' +
                ", tripDate=" + tripDate +
                ", route=" + route +
                ", seats=" + seatNumbers +
                ", passengers=" + passengerCount +
                ", totalPrice=" + totalPrice +
                ", time=" + reservationTime +
                '}';
    }
}
