package com.busreservation.repository;

import com.busreservation.entity.Location;
import com.busreservation.entity.Seat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for managing bus seat availability across route segments.
 * Thread-safe implementation using ConcurrentHashMap.
 */

public class BusRepository {
    private static BusRepository instance;
    
    // All 40 seats (1A-10D)
    private final List<Seat> seats;
    
    // Track occupied seats per route segment and date
    // Key: "Date_Origin-Destination" (e.g., "2026-02-01_A-B"), Value: Set of occupied seat numbers
    private final Map<String, Set<String>> routeSegmentOccupancy;
    
    /**
     * Private constructor initializes all seats and occupancy tracking.
     */
    private BusRepository() {
        this.seats = initializeSeats();
        this.routeSegmentOccupancy = new ConcurrentHashMap<>();
        initializeRouteSegments();
    }
    
    /**
     * Get singleton instance of BusRepository.
     * @return BusRepository instance
     */
    public static synchronized BusRepository getInstance() {
        if (instance == null) {
            instance = new BusRepository();
        }
        return instance;
    }
    
    /**
     * Initialize all 40 seats (1A to 10D).
     * Seats are arranged in 10 rows with 4 seats per row (A, B, C, D).
     * @return List of all seats
     */
    private List<Seat> initializeSeats() {
        List<Seat> seatList = new ArrayList<>();
        char[] columns = {'A', 'B', 'C', 'D'};
        
        for (int row = 1; row <= 10; row++) {
            for (char column : columns) {
                String seatNumber = row + String.valueOf(column);
                seatList.add(new Seat(seatNumber));
            }
        }
        
        return seatList;
    }
    
    /**
     * Initialize route segments map (now dynamically created with dates).
     */
    private void initializeRouteSegments() {
        // Map is now dynamically populated with date-based keys
    }
    
    /**
     * Get available seats for a specific route and date.
     * A seat is available if it's not occupied on any segment of the route for that date.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param count number of seats needed
     * @param travelDate date of travel (YYYY-MM-DD format)
     * @return List of available seat numbers
     */
    public synchronized List<String> getAvailableSeats(Location origin, Location destination, int count, String travelDate) {
        List<String> segments = getRouteSegments(origin, destination);
        Set<String> occupiedSeats = new HashSet<>();
        
        // Collect all occupied seats across all segments in the route for the specified date
        for (String segment : segments) {
            String dateSegmentKey = travelDate + "_" + segment;
            occupiedSeats.addAll(routeSegmentOccupancy.getOrDefault(dateSegmentKey, Collections.emptySet()));
        }
        
        // Find available seats (return ALL available seats, not limited by request count)
        List<String> availableSeats = seats.stream()
                .map(Seat::getSeatNumber)
                .filter(seatNumber -> !occupiedSeats.contains(seatNumber))
                .collect(Collectors.toList());
        
        return availableSeats;
    }
    
    /**
     * Get available seats for a specific route (without date - defaults to today).
     * Kept for backward compatibility.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param count number of seats needed
     * @return List of available seat numbers
     */
    public synchronized List<String> getAvailableSeats(Location origin, Location destination, int count) {
        String today = java.time.LocalDate.now().toString();
        return getAvailableSeats(origin, destination, count, today);
    }
    
    /**
     * Reserve seats for a specific route and date.
     * Marks the seats as occupied for all segments in the route for the specified date.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param seatNumbers list of seat numbers to reserve
     * @param travelDate date of travel (YYYY-MM-DD format)
     * @return true if reservation successful, false otherwise
     */
    public synchronized boolean reserveSeats(Location origin, Location destination, List<String> seatNumbers, String travelDate) {
        List<String> segments = getRouteSegments(origin, destination);
        
        // Verify all seats are still available before reserving
        List<String> availableSeats = getAvailableSeats(origin, destination, seatNumbers.size(), travelDate);
        if (!availableSeats.containsAll(seatNumbers)) {
            return false; // Some seats are no longer available
        }
        
        // Reserve seats for all segments in the route for the specified date
        for (String segment : segments) {
            String dateSegmentKey = travelDate + "_" + segment;
            Set<String> occupiedSeatsInSegment = routeSegmentOccupancy.computeIfAbsent(
                dateSegmentKey, k -> ConcurrentHashMap.newKeySet()
            );
            occupiedSeatsInSegment.addAll(seatNumbers);
        }
        
        return true;
    }
    
    /**
     * Reserve seats for a specific route (without date - defaults to today).
     * Kept for backward compatibility.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param seatNumbers list of seat numbers to reserve
     * @return true if reservation successful, false otherwise
     */
    public synchronized boolean reserveSeats(Location origin, Location destination, List<String> seatNumbers) {
        String today = java.time.LocalDate.now().toString();
        return reserveSeats(origin, destination, seatNumbers, today);
    }
    
    /**
     * Get all route segments for a given origin and destination.
     * For example, A→C includes segments [A-B, B-C].
     * 
     * @param origin starting location
     * @param destination ending location
     * @return List of segment keys (e.g., ["A-B", "B-C"])
     */
    private List<String> getRouteSegments(Location origin, Location destination) {
        List<String> segments = new ArrayList<>();
        Location[] locations = Location.values();
        
        int originIndex = origin.getOrder();
        int destIndex = destination.getOrder();
        
        if (originIndex < destIndex) {
            // Forward direction (e.g., A→C)
            for (int i = originIndex; i < destIndex; i++) {
                segments.add(locations[i].name() + "-" + locations[i + 1].name());
            }
        } else {
            // Return direction (e.g., D→B)
            for (int i = originIndex; i > destIndex; i--) {
                segments.add(locations[i].name() + "-" + locations[i - 1].name());
            }
        }
        
        return segments;
    }
    
    /**
     * Get total number of seats.
     * @return total seat count (40)
     */
    public int getTotalSeats() {
        return seats.size();
    }
    
    /**
     * Get occupancy information for a specific segment (for debugging/testing).
     * @param segment segment key (e.g., "A-B")
     * @return Set of occupied seat numbers
     */
    public Set<String> getSegmentOccupancy(String segment) {
        return new HashSet<>(routeSegmentOccupancy.getOrDefault(segment, Collections.emptySet()));
    }
    
    /**
     * Reset all seat occupancy (for testing purposes).
     */
    public synchronized void reset() {
        routeSegmentOccupancy.clear();
        initializeRouteSegments();
    }
}
