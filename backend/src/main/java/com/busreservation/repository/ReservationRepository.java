package com.busreservation.repository;

import com.busreservation.entity.Reservation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for managing reservations.
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class ReservationRepository {
    private static ReservationRepository instance;
    
    // Store reservations by ID
    private final Map<String, Reservation> reservations;
    
    /**
     * Private constructor initializes the reservations map.
     */
    private ReservationRepository() {
        this.reservations = new ConcurrentHashMap<>();
    }
    
    /**
     * Get singleton instance of ReservationRepository.
     * @return ReservationRepository instance
     */
    public static synchronized ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }
    
    /**
     * Save a new reservation.
     * 
     * @param reservation the reservation to save
     * @return the saved reservation
     */
    public Reservation save(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
        return reservation;
    }
    
    /**
     * Find a reservation by ID.
     * 
     * @param reservationId the reservation ID
     * @return Optional containing the reservation if found
     */
    public Optional<Reservation> findById(String reservationId) {
        return Optional.ofNullable(reservations.get(reservationId));
    }
    
    /**
     * Get all reservations.
     * 
     * @return List of all reservations
     */
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }
    
    /**
     * Delete a reservation by ID.
     * 
     * @param reservationId the reservation ID
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String reservationId) {
        return reservations.remove(reservationId) != null;
    }
    
    /**
     * Check if a reservation exists.
     * 
     * @param reservationId the reservation ID
     * @return true if exists, false otherwise
     */
    public boolean exists(String reservationId) {
        return reservations.containsKey(reservationId);
    }
    
    /**
     * Get total number of reservations.
     * 
     * @return count of reservations
     */
    public int count() {
        return reservations.size();
    }
    
    /**
     * Clear all reservations (for testing purposes).
     */
    public void clear() {
        reservations.clear();
    }
}
