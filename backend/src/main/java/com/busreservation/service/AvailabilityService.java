package com.busreservation.service;

import com.busreservation.dto.AvailabilityRequestDTO;
import com.busreservation.dto.AvailabilityResponseDTO;
import com.busreservation.entity.Location;
import com.busreservation.repository.BusRepository;

import java.util.List;

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
        
        // Get available seats
        List<String> availableSeats = busRepository.getAvailableSeats(origin, destination, request.getPassengers());
        
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
            request.getDestination()
        );
    }
    
    /**
     * Check if enough seats are available for a route.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param passengers number of passengers
     * @return true if enough seats available, false otherwise
     */
    public boolean hasAvailableSeats(Location origin, Location destination, int passengers) {
        List<String> availableSeats = busRepository.getAvailableSeats(origin, destination, passengers);
        return !availableSeats.isEmpty();
    }
    
    /**
     * Get count of available seats for a route.
     * 
     * @param origin starting location
     * @param destination ending location
     * @return number of available seats
     */
    public int getAvailableSeatsCount(Location origin, Location destination) {
        return busRepository.getAvailableSeats(origin, destination, busRepository.getTotalSeats()).size();
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
