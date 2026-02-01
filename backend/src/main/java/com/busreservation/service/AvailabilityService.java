package com.busreservation.service;

import com.busreservation.dto.AvailabilityRequestDTO;
import com.busreservation.dto.AvailabilityResponseDTO;
import com.busreservation.entity.Location;
import com.busreservation.repository.BusRepository;
import com.busreservation.util.DateUtil;
import com.busreservation.util.ValidationUtil;

import java.util.List;
import java.time.LocalDate;

/**
 * Service for checking seat availability for routes.
 * Handles availability queries and returns available seats with pricing.
 */
public class AvailabilityService {
    private static AvailabilityService instance;
    
    private final BusRepository busRepository;
    private final PricingService pricingService;
    
    /**
     * Private constructor for singleton pattern.
     */
    private AvailabilityService() {
        this.busRepository = BusRepository.getInstance();
        this.pricingService = PricingService.getInstance();
    }
    
    /**
     * Get singleton instance of AvailabilityService.
     * @return AvailabilityService instance
     */
    public static synchronized AvailabilityService getInstance() {
        if (instance == null) {
            instance = new AvailabilityService();
        }
        return instance;
    }
    
    /**
     * Check availability for a route and return available seats.
     * 
     * @param request availability request with passengers, origin, destination
     * @return availability response with available seats and pricing
     * @throws IllegalArgumentException if request is invalid
     */
    public AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO request) {
        // Validate request
        validateAvailabilityRequest(request);
        
        // Parse locations
        Location origin = Location.valueOf(request.getOrigin().toUpperCase());
        Location destination = Location.valueOf(request.getDestination().toUpperCase());

        // Parse and validate trip date
        LocalDate tripDate = DateUtil.validateTripDate(request.getTripDate());
        
        // Get available seats for this specific date
        List<String> availableSeats = busRepository.getAvailableSeats(origin, destination, tripDate, request.getPassengers());
        
        // Calculate pricing
        double pricePerSeat = pricingService.calculatePrice(origin, destination);
        double totalPrice = pricePerSeat * request.getPassengers();
        
        // Build response
        return new AvailabilityResponseDTO(
            availableSeats,
            totalPrice,
            pricePerSeat,
            request.getPassengers(),
            request.getOrigin(),
            request.getDestination(),
            request.getTripDate()
        );
    }
    
    /**
     * Check if enough seats are available for a route and date.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param tripDate trip date
     * @param passengers number of passengers
     * @return true if enough seats available, false otherwise
     */
    public boolean hasAvailableSeats(Location origin, Location destination, LocalDate tripDate, int passengers) {
        List<String> availableSeats = busRepository.getAvailableSeats(origin, destination, tripDate, passengers);
        return !availableSeats.isEmpty();
    }
    
    /**
     * Get count of available seats for a route and date.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param tripDate trip date
     * @return number of available seats
     */
    public int getAvailableSeatsCount(Location origin, Location destination, LocalDate tripDate) {
        return busRepository.getAvailableSeats(origin, destination, tripDate, busRepository.getTotalSeats()).size();
    }
    
    /**
     * Validate availability request.
     * 
     * @param request availability request to validate
     * @throws IllegalArgumentException if request is invalid
     */
    private void validateAvailabilityRequest(AvailabilityRequestDTO request) {
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

        ValidationUtil.validateTripDate(request.getTripDate());
        
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
