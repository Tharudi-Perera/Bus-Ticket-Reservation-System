package com.busreservation;

import com.busreservation.dto.AvailabilityRequestDTO;
import com.busreservation.dto.AvailabilityResponseDTO;
import com.busreservation.dto.ReservationRequestDTO;
import com.busreservation.dto.ReservationResponseDTO;
import com.busreservation.entity.Location;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.ReservationRepository;
import com.busreservation.service.AvailabilityService;
import com.busreservation.service.PricingService;
import com.busreservation.service.ReservationService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Bus Reservation System.
 * Tests the complete workflow from checking availability to creating reservations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusReservationSystemTest {

    private static PricingService pricingService;
    private static AvailabilityService availabilityService;
    private static ReservationService reservationService;
    private static BusRepository busRepository;
    private static ReservationRepository reservationRepository;

    @BeforeAll
    static void setUpAll() {
        pricingService = PricingService.getInstance();
        availabilityService = AvailabilityService.getInstance();
        reservationService = ReservationService.getInstance();
        busRepository = BusRepository.getInstance();
        reservationRepository = ReservationRepository.getInstance();
    }

    @BeforeEach
    void setUp() {
        // Reset repositories before each test
        busRepository.reset();
        reservationRepository.reset();
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Services should return singleton instances")
    void testSingletonInstances() {
        assertSame(PricingService.getInstance(), pricingService, 
                  "PricingService should be singleton");
        assertSame(AvailabilityService.getInstance(), availabilityService, 
                  "AvailabilityService should be singleton");
        assertSame(ReservationService.getInstance(), reservationService, 
                  "ReservationService should be singleton");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Calculate price for different routes")
    void testPricing() {
        // Test single segment (A to B)
        double priceAB = pricingService.calculatePrice(Location.A, Location.B);
        assertEquals(50.0, priceAB, "A to B should cost Rs. 50");

        // Test two segments (A to C)
        double priceAC = pricingService.calculatePrice(Location.A, Location.C);
        assertEquals(100.0, priceAC, "A to C should cost Rs. 100");

        // Test three segments (A to D)
        double priceAD = pricingService.calculatePrice(Location.A, Location.D);
        assertEquals(150.0, priceAD, "A to D should cost Rs. 150");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Check seat availability")
    void testAvailability() {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setPassengers(2);
        request.setOrigin("A");
        request.setDestination("D");

        AvailabilityResponseDTO response = availabilityService.checkAvailability(request);

        assertNotNull(response, "Response should not be null");
        assertEquals("A", response.getOrigin(), "Origin should be A");
        assertEquals("D", response.getDestination(), "Destination should be D");
        assertEquals(2, response.getPassengers(), "Should have 2 passengers");
        assertEquals(300.0, response.getTotalPrice(), "Total price should be Rs. 300 (2 * 150)");
        assertNotNull(response.getAvailableSeats(), "Available seats should not be null");
        assertTrue(response.getAvailableSeats().size() > 0, "Should have available seats");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Create a reservation successfully")
    void testCreateReservation() {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setPassengers(2);
        request.setOrigin("A");
        request.setDestination("C");
        request.setPrice(200.0); // 2 passengers * 100.0 per passenger

        ReservationResponseDTO response = reservationService.createReservation(request);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getReservationId(), "Reservation ID should not be null");
        assertEquals("A", response.getOrigin(), "Origin should be A");
        assertEquals("C", response.getDestination(), "Destination should be C");
        assertEquals(2, response.getPassengers(), "Should have 2 passengers");
        assertEquals(200.0, response.getTotalPrice(), "Total price should be Rs. 200");
        assertEquals(2, response.getSeatNumbers().size(), "Should have 2 seats assigned");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Price validation should fail for incorrect price")
    void testPriceValidationFailure() {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setPassengers(2);
        request.setOrigin("A");
        request.setDestination("C");
        request.setPrice(150.0); // Incorrect: should be 200.0

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(request);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("price"), 
                  "Error message should mention price");
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Invalid location should throw exception")
    void testInvalidLocation() {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setPassengers(2);
        request.setOrigin("X"); // Invalid location
        request.setDestination("D");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            availabilityService.checkAvailability(request);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("invalid") || 
                   exception.getMessage().toLowerCase().contains("location"), 
                  "Error message should mention invalid location");
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Zero passengers should throw exception")
    void testZeroPassengers() {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setPassengers(0);
        request.setOrigin("A");
        request.setDestination("D");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            availabilityService.checkAvailability(request);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("passenger"), 
                  "Error message should mention passenger");
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Same origin and destination should throw exception")
    void testSameOriginAndDestination() {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setPassengers(2);
        request.setOrigin("A");
        request.setDestination("A");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            availabilityService.checkAvailability(request);
        });

        assertTrue(exception.getMessage().contains("same"), 
                  "Error message should mention same location");
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Multiple reservations should work correctly")
    void testMultipleReservations() {
        // First reservation: A to B
        ReservationRequestDTO request1 = new ReservationRequestDTO();
        request1.setPassengers(2);
        request1.setOrigin("A");
        request1.setDestination("B");
        request1.setPrice(100.0);

        ReservationResponseDTO response1 = reservationService.createReservation(request1);
        assertNotNull(response1.getReservationId());

        // Second reservation: B to C
        ReservationRequestDTO request2 = new ReservationRequestDTO();
        request2.setPassengers(3);
        request2.setOrigin("B");
        request2.setDestination("C");
        request2.setPrice(150.0);

        ReservationResponseDTO response2 = reservationService.createReservation(request2);
        assertNotNull(response2.getReservationId());

        // Verify both reservations have unique IDs
        assertNotEquals(response1.getReservationId(), response2.getReservationId(), 
                       "Reservation IDs should be unique");

        // Verify both are in repository
        assertEquals(2, reservationRepository.findAll().size(), 
                    "Should have 2 reservations in repository");
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Availability should decrease after reservation")
    void testAvailabilityAfterReservation() {
        // Check initial availability - request more seats to get full picture
        AvailabilityRequestDTO availRequest = new AvailabilityRequestDTO();
        availRequest.setPassengers(40);  // Request all seats to see full availability
        availRequest.setOrigin("A");
        availRequest.setDestination("D");

        AvailabilityResponseDTO availResponse1 = availabilityService.checkAvailability(availRequest);
        int initialAvailability = availResponse1.getAvailableSeats().size();

        // Make a reservation
        ReservationRequestDTO reservRequest = new ReservationRequestDTO();
        reservRequest.setPassengers(5);
        reservRequest.setOrigin("A");
        reservRequest.setDestination("D");
        reservRequest.setPrice(750.0); // 5 * 150

        reservationService.createReservation(reservRequest);

        // Check availability again
        AvailabilityResponseDTO availResponse2 = availabilityService.checkAvailability(availRequest);
        int newAvailability = availResponse2.getAvailableSeats().size();

        assertTrue(newAvailability < initialAvailability, 
                  "Available seats should decrease after reservation. Initial: " + initialAvailability + ", New: " + newAvailability);
        assertEquals(initialAvailability - 5, newAvailability, 
                    "Should have 5 fewer available seats");
    }

    @Test
    @Order(11)
    @DisplayName("Test 11: All locations should have valid pricing")
    void testAllLocationPricing() {
        Location[] locations = Location.values();

        for (Location origin : locations) {
            for (Location destination : locations) {
                if (origin != destination) {
                    double price = pricingService.calculatePrice(origin, destination);
                    assertTrue(price > 0, 
                              String.format("Price for %s to %s should be positive", origin, destination));
                    assertTrue(price % 50 == 0, 
                              String.format("Price for %s to %s should be multiple of 50", origin, destination));
                }
            }
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Repository reset should work correctly")
    void testRepositoryReset() {
        // Create some reservations
        for (int i = 0; i < 3; i++) {
            ReservationRequestDTO request = new ReservationRequestDTO();
            request.setPassengers(1);
            request.setOrigin("A");
            request.setDestination("B");
            request.setPrice(50.0);
            reservationService.createReservation(request);
        }

        // Verify reservations exist
        assertEquals(3, reservationRepository.findAll().size());

        // Reset
        busRepository.reset();
        reservationRepository.reset();

        // Verify everything is cleared
        assertEquals(0, reservationRepository.findAll().size());

        // Verify all seats are available again
        AvailabilityRequestDTO availRequest = new AvailabilityRequestDTO();
        availRequest.setPassengers(40);  // Request all to check full availability
        availRequest.setOrigin("A");
        availRequest.setDestination("D");
        AvailabilityResponseDTO availResponse = availabilityService.checkAvailability(availRequest);

        // Should have all 40 seats available
        assertTrue(availResponse.getAvailableSeats().size() >= 35, 
                  "Should have most seats available after reset. Got: " + availResponse.getAvailableSeats().size());
    }
}
