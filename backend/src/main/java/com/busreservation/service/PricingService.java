package com.busreservation.service;

import com.busreservation.entity.Location;
import com.busreservation.entity.Route;

/**
 * Service for calculating ticket prices based on routes.
 * Implements the pricing logic: Rs.50 per segment.
 * 
 * Pricing examples:
 * - A→B (1 segment): Rs.50
 * - A→C (2 segments): Rs.100
 * - A→D (3 segments): Rs.150
 */
public class PricingService {
    private static PricingService instance;
    
    // Price per segment in rupees
    private static final double PRICE_PER_SEGMENT = 50.0;
    
    /**
     * Private constructor for singleton pattern.
     */
    private PricingService() {
    }
    
    /**
     * Get singleton instance of PricingService.
     * @return PricingService instance
     */
    public static synchronized PricingService getInstance() {
        if (instance == null) {
            instance = new PricingService();
        }
        return instance;
    }
    
    /**
     * Calculate price for a route.
     * Price is based on the number of segments: Rs.50 per segment.
     * 
     * @param origin starting location
     * @param destination ending location
     * @return price for the route
     * @throws IllegalArgumentException if origin or destination is null or same
     */
    public double calculatePrice(Location origin, Location destination) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Origin and destination cannot be null");
        }
        
        if (origin == destination) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }
        
        return Route.getPrice(origin, destination);
    }
    
    /**
     * Calculate total price for multiple passengers.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param passengers number of passengers
     * @return total price for all passengers
     * @throws IllegalArgumentException if passengers is less than 1
     */
    public double calculateTotalPrice(Location origin, Location destination, int passengers) {
        if (passengers < 1) {
            throw new IllegalArgumentException("Number of passengers must be at least 1");
        }
        
        double pricePerSeat = calculatePrice(origin, destination);
        return pricePerSeat * passengers;
    }
    
    /**
     * Validate that the provided price matches the calculated price.
     * Used during reservation to ensure price integrity.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param passengers number of passengers
     * @param providedPrice price provided by client
     * @return true if price matches, false otherwise
     */
    public boolean validatePrice(Location origin, Location destination, int passengers, double providedPrice) {
        double expectedPrice = calculateTotalPrice(origin, destination, passengers);
        // Allow small floating-point difference (0.01)
        return Math.abs(expectedPrice - providedPrice) < 0.01;
    }
    
    /**
     * Get the price per segment.
     * @return price per segment in rupees
     */
    public double getPricePerSegment() {
        return PRICE_PER_SEGMENT;
    }
    
    /**
     * Calculate the number of segments in a route.
     * 
     * @param origin starting location
     * @param destination ending location
     * @return number of segments
     */
    public int calculateSegments(Location origin, Location destination) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Origin and destination cannot be null");
        }
        
        return Math.abs(destination.getOrder() - origin.getOrder());
    }
}
