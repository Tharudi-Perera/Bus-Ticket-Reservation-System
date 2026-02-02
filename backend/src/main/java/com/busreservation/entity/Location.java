package com.busreservation.entity;

/**
 * Enum representing the four bus stop locations.
 * The bus travels in sequence: A → B → C → D (forward) or D → C → B → A (return).
 */
public enum Location {
    A(0, "Location A"),
    B(1, "Location B"),
    C(2, "Location C"),
    D(3, "Location D");

    private final int order;
    private final String displayName;

    Location(int order, String displayName) {
        this.order = order;
        this.displayName = displayName;
    }

    /**
     * Get the sequential order of this location (0-3).
     * @return order number
     */
    public int getOrder() {
        return order;
    }

    /**
     * Get the display name of this location.
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this location comes before another location in the route.
     * @param other the other location
     * @return true if this location comes before the other
     */
    public boolean isBefore(Location other) {
        return this.order < other.order;
    }

    /**
     * Get the distance (number of segments) between two locations.
     * @param from starting location
     * @param to ending location
     * @return number of segments
     */
    public static int getDistance(Location from, Location to) {
        return Math.abs(from.order - to.order);
    }
}
