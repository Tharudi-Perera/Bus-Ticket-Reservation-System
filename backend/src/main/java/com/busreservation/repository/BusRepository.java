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
    
    // Track occupied seats per route segment (e.g., "A-B", "B-C")
    // Key: "Origin-Destination", Value: Set of occupied seat numbers
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
     * Initialize all possible route segments with empty occupancy.
     */
    private void initializeRouteSegments() {
        Location[] locations = Location.values();
        
        // Forward segments: A→B, A→C, A→D, B→C, B→D, C→D
        for (int i = 0; i < locations.length; i++) {
            for (int j = i + 1; j < locations.length; j++) {
                String segment = locations[i].name() + "-" + locations[j].name();
                routeSegmentOccupancy.put(segment, ConcurrentHashMap.newKeySet());
            }
        }
        
        // Return segments: D→C, D→B, D→A, C→B, C→A, B→A
        for (int i = locations.length - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                String segment = locations[i].name() + "-" + locations[j].name();
                routeSegmentOccupancy.put(segment, ConcurrentHashMap.newKeySet());
            }
        }
    }
    
    /**
     * Get available seats for a specific route.
     * A seat is available if it's not occupied on any segment of the route.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param count number of seats needed
     * @return List of available seat numbers, or empty list if not enough available
     */
    public synchronized List<String> getAvailableSeats(Location origin, Location destination, int count) {
        List<String> segments = getRouteSegments(origin, destination);
        Set<String> occupiedSeats = new HashSet<>();
        
        // Collect all occupied seats across all segments in the route
        for (String segment : segments) {
            occupiedSeats.addAll(routeSegmentOccupancy.getOrDefault(segment, Collections.emptySet()));
        }
        
        // Find available seats
        List<String> availableSeats = seats.stream()
                .map(Seat::getSeatNumber)
                .filter(seatNumber -> !occupiedSeats.contains(seatNumber))
                .limit(count)
                .collect(Collectors.toList());
        
        return availableSeats.size() >= count ? availableSeats : Collections.emptyList();
    }
    
    /**
     * Reserve seats for a specific route.
     * Marks the seats as occupied for all segments in the route.
     * 
     * @param origin starting location
     * @param destination ending location
     * @param seatNumbers list of seat numbers to reserve
     * @return true if reservation successful, false otherwise
     */
    public synchronized boolean reserveSeats(Location origin, Location destination, List<String> seatNumbers) {
        List<String> segments = getRouteSegments(origin, destination);
        
        // Verify all seats are still available before reserving
        List<String> availableSeats = getAvailableSeats(origin, destination, seatNumbers.size());
        if (!availableSeats.containsAll(seatNumbers)) {
            return false; // Some seats are no longer available
        }
        
        // Reserve seats for all segments in the route
        for (String segment : segments) {
            Set<String> occupiedSeatsInSegment = routeSegmentOccupancy.get(segment);
            occupiedSeatsInSegment.addAll(seatNumbers);
        }
        
        return true;
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
