package com.busreservation.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a route between two locations with pricing information.
 * Pricing is based on the number of segments traveled.
 */
public class Route {
    private final Location origin;
    private final Location destination;
    private final double price;

    // Static pricing map for all possible routes
    private static final Map<String, Double> ROUTE_PRICES = new HashMap<>();

    static {
        // Forward routes (A → D)
        ROUTE_PRICES.put("A-B", 50.0);
        ROUTE_PRICES.put("A-C", 100.0);
        ROUTE_PRICES.put("A-D", 150.0);
        ROUTE_PRICES.put("B-C", 50.0);
        ROUTE_PRICES.put("B-D", 100.0);
        ROUTE_PRICES.put("C-D", 50.0);

        // Return routes (D → A) - same prices
        ROUTE_PRICES.put("D-C", 50.0);
        ROUTE_PRICES.put("D-B", 100.0);
        ROUTE_PRICES.put("D-A", 150.0);
        ROUTE_PRICES.put("C-B", 50.0);
        ROUTE_PRICES.put("C-A", 100.0);
        ROUTE_PRICES.put("B-A", 50.0);
    }

    /**
     * Create a route between two locations.
     * @param origin starting location
     * @param destination ending location
     */
    public Route(Location origin, Location destination) {
        if (origin == destination) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }
        this.origin = origin;
        this.destination = destination;
        this.price = calculatePrice(origin, destination);
    }

    /**
     * Calculate the price for a route based on locations.
     * @param origin starting location
     * @param destination ending location
     * @return price for the route
     */
    private static double calculatePrice(Location origin, Location destination) {
        String routeKey = origin.name() + "-" + destination.name();
        Double price = ROUTE_PRICES.get(routeKey);
        if (price == null) {
            throw new IllegalArgumentException("Invalid route: " + routeKey);
        }
        return price;
    }

    /**
     * Get the price for a specific route.
     * @param origin starting location
     * @param destination ending location
     * @return price for the route
     */
    public static double getPrice(Location origin, Location destination) {
        return calculatePrice(origin, destination);
    }

    /**
     * Get the origin location.
     * @return origin
     */
    public Location getOrigin() {
        return origin;
    }

    /**
     * Get the destination location.
     * @return destination
     */
    public Location getDestination() {
        return destination;
    }

    /**
     * Get the price for this route.
     * @return price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Check if this route overlaps with another route.
     * Two routes overlap if they share any segment.
     * @param other the other route
     * @return true if routes overlap
     */
    public boolean overlapsWith(Route other) {
        int thisMin = Math.min(origin.getOrder(), destination.getOrder());
        int thisMax = Math.max(origin.getOrder(), destination.getOrder());
        int otherMin = Math.min(other.origin.getOrder(), other.destination.getOrder());
        int otherMax = Math.max(other.origin.getOrder(), other.destination.getOrder());

        return !(thisMax < otherMin || thisMin > otherMax);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return origin == route.origin && destination == route.destination;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination);
    }

    @Override
    public String toString() {
        return origin.name() + " → " + destination.name() + " (Rs. " + price + ")";
    }
}
