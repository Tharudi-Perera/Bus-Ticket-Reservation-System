package com.busreservation.entity;

import java.util.Objects;

/**
 * Represents a bus seat with a unique identifier.
 * Seats are numbered from 1A to 10D (40 total seats in rows of 4).
 */
public class Seat {
    private final String seatNumber;
    private boolean occupied;

    /**
     * Create a new seat with the given number.
     * @param seatNumber seat identifier (e.g., "1A", "5C", "10D")
     */
    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.occupied = false;
    }

    /**
     * Get the seat number.
     * @return seat number
     */
    public String getSeatNumber() {
        return seatNumber;
    }

    /**
     * Check if the seat is occupied.
     * @return true if occupied
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Set the occupied status of the seat.
     * @param occupied true to mark as occupied
     */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return Objects.equals(seatNumber, seat.seatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatNumber);
    }

    @Override
    public String toString() {
        return seatNumber + (occupied ? " (occupied)" : " (available)");
    }
}
