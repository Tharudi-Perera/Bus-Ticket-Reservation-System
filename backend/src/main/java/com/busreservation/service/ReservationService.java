package com.busreservation.service;

import com.busreservation.dto.ReservationRequestDTO;
import com.busreservation.dto.ReservationResponseDTO;
import com.busreservation.entity.Location;
import com.busreservation.entity.Reservation;
import com.busreservation.entity.Route;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for handling ticket reservations.
 * Manages the complete reservation workflow including validation,
 * seat allocation, and reservation persistence.
 */
public class ReservationService {
    private static ReservationService instance;
    
    private final BusRepository busRepository;
    private final ReservationRepository reservationRepository;
    private final PricingService pricingService;
    private final AvailabilityService availabilityService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Private constructor for singleton pattern.
     */
    private ReservationService() {
        this.busRepository = BusRepository.getInstance();
        this.reservationRepository = ReservationRepository.getInstance();
        this.pricingService = PricingService.getInstance();
        this.availabilityService = AvailabilityService.getInstance();
    }
    
    /**
     * Get singleton instance of ReservationService.
     * @return ReservationService instance
     */
    public static synchronized ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }
    
    /**
     * Create a new reservation.
     * Validates the request, allocates seats, and persists the reservation.
     * 
     * @param request reservation request with passengers, origin, destination, price
     * @return reservation response with reservation details
     * @throws IllegalArgumentException if request is invalid
     * @throws IllegalStateException if seats cannot be allocated
     */
    public synchronized ReservationResponseDTO createReservation(ReservationRequestDTO request) {
        // Validate request
        validateReservationRequest(request);
        
        // Parse locations
        Location origin = Location.valueOf(request.getOrigin().toUpperCase());
        Location destination = Location.valueOf(request.getDestination().toUpperCase());
        
        // Validate price
        if (!pricingService.validatePrice(origin, destination, request.getPassengers(), request.getPrice())) {
            throw new IllegalArgumentException("Invalid price. Expected: " + 
                pricingService.calculateTotalPrice(origin, destination, request.getPassengers()));
        }
        
        // Check availability
        if (!availabilityService.hasAvailableSeats(origin, destination, request.getPassengers())) {
            throw new IllegalStateException("Not enough seats available for the requested route");
        }
        
        // Get available seats
        List<String> availableSeats = busRepository.getAvailableSeats(origin, destination, request.getPassengers());
        
        if (availableSeats.isEmpty()) {
            throw new IllegalStateException("Failed to allocate seats. Please try again.");
        }
        
        // Reserve seats in repository
        boolean reserved = busRepository.reserveSeats(origin, destination, availableSeats);
        
        if (!reserved) {
            throw new IllegalStateException("Failed to reserve seats. They may have been taken by another user.");
        }
        
        // Create route and reservation
        Route route = new Route(origin, destination);
        Reservation reservation = new Reservation(route, availableSeats, request.getPassengers(), request.getPrice());
        
        // Save reservation
        reservationRepository.save(reservation);
        
        // Build response
        return new ReservationResponseDTO(
            reservation.getReservationId(),
            availableSeats,
            request.getOrigin(),
            request.getDestination(),
            request.getPassengers(),
            request.getPrice(),
            DATE_FORMATTER.format(reservation.getReservationTime())
        );
    }
    
    /**
     * Get a reservation by ID.
     * 
     * @param reservationId the reservation ID
     * @return reservation response if found
     * @throws IllegalArgumentException if reservation not found
     */
    public ReservationResponseDTO getReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        
        return new ReservationResponseDTO(
            reservation.getReservationId(),
            reservation.getSeatNumbers(),
            reservation.getRoute().getOrigin().name(),
            reservation.getRoute().getDestination().name(),
            reservation.getPassengerCount(),
            reservation.getTotalPrice(),
            DATE_FORMATTER.format(reservation.getReservationTime())
        );
    }
    
    /**
     * Get all reservations.
     * 
     * @return list of all reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    /**
     * Cancel a reservation (for testing/admin purposes).
     * Note: In production, you'd implement proper cancellation logic with seat release.
     * 
     * @param reservationId the reservation ID
     * @return true if cancelled, false if not found
     */
    public boolean cancelReservation(String reservationId) {
        return reservationRepository.deleteById(reservationId);
    }
    
    /**
     * Validate reservation request.
     * 
     * @param request reservation request to validate
     * @throws IllegalArgumentException if request is invalid
     */
    private void validateReservationRequest(ReservationRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        
        if (request.getPassengers() < 1) {
            throw new IllegalArgumentException("Number of passengers must be at least 1");
        }
        
        if (request.getPassengers() > 40) {
            throw new IllegalArgumentException("Number of passengers cannot exceed 40");
        }
        
        if (request.getOrigin() == null || request.getOrigin().trim().isEmpty()) {
            throw new IllegalArgumentException("Origin cannot be null or empty");
        }
        
        if (request.getDestination() == null || request.getDestination().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be null or empty");
        }
        
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        
        // Validate locations exist
        try {
            Location origin = Location.valueOf(request.getOrigin().toUpperCase());
            Location destination = Location.valueOf(request.getDestination().toUpperCase());
            
            if (origin == destination) {
                throw new IllegalArgumentException("Origin and destination cannot be the same");
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("No enum constant")) {
                throw new IllegalArgumentException("Invalid location. Must be one of: A, B, C, D");
            }
            throw e;
        }
    }
}
