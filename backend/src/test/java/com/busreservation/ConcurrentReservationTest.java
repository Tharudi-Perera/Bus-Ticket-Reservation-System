package com.busreservation;

import com.busreservation.entity.Location;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.ReservationRepository;
import com.busreservation.service.AvailabilityService;
import com.busreservation.service.PricingService;
import com.busreservation.service.ReservationService;
import com.busreservation.dto.ReservationRequestDTO;
import com.busreservation.dto.ReservationResponseDTO;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrent reservation tests to verify thread safety and race condition handling.
 * 
 * These tests simulate multiple users trying to book seats simultaneously
 * to ensure the system prevents overselling and maintains data integrity.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConcurrentReservationTest {

    private static BusRepository busRepository;
    private static ReservationRepository reservationRepository;
    private static PricingService pricingService;
    private static AvailabilityService availabilityService;
    private static ReservationService reservationService;

    @BeforeAll
    public static void setup() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONCURRENT RESERVATION TESTING - Race Condition Verification");
        System.out.println("=".repeat(80));
        
        // Initialize services
        busRepository = BusRepository.getInstance();
        reservationRepository = ReservationRepository.getInstance();
        pricingService = PricingService.getInstance();
        availabilityService = AvailabilityService.getInstance();
        reservationService = ReservationService.getInstance();
    }

    @BeforeEach
    public void resetSystem() {
        // Reset repositories before each test
        busRepository.reset();
        reservationRepository.reset();
        System.out.println("\n✓ System reset complete");
    }

    /**
     * Test Scenario 1: Last 5 Seats - 2 Concurrent Users
     * 
     * Setup: Book 35 seats first (leaving 5 available)
     * Test: 2 users simultaneously try to book 5 seats each
     * Expected: Only 1 user succeeds, the other fails
     */
    @Test
    @Order(1)
    public void testScenario1_LastFiveSeats_TwoUsers() throws InterruptedException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO 1: Last 5 Seats - 2 Concurrent Users Each Booking 5 Seats");
        System.out.println("-".repeat(80));

        // Step 1: Book 35 seats to leave only 5 available
        System.out.println("\n📋 Setup: Booking 35 seats (A → B) to leave 5 available...");
        ReservationRequestDTO setupRequest = new ReservationRequestDTO();
        setupRequest.setPassengers(35);
        setupRequest.setOrigin("A");
        setupRequest.setDestination("B");
        setupRequest.setPrice(1750.0); // 35 * 50

        ReservationResponseDTO setupResponse = reservationService.createReservation(setupRequest);
        assertNotNull(setupResponse);
        assertEquals(35, setupResponse.getSeatNumbers().size());
        System.out.println("✓ Setup complete: 35 seats booked, 5 seats remaining");

        // Step 2: Prepare concurrent booking attempts
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<ReservationResponseDTO> successfulReservations = new CopyOnWriteArrayList<>();
        List<String> errorMessages = new CopyOnWriteArrayList<>();

        // Step 3: Launch concurrent booking threads
        System.out.println("\n🚀 Launching 2 concurrent users...");
        for (int i = 0; i < numThreads; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready

                    System.out.println("   User " + userId + ": Attempting to book 5 seats...");
                    
                    ReservationRequestDTO request = new ReservationRequestDTO();
                    request.setPassengers(5);
                    request.setOrigin("A");
                    request.setDestination("B");
                    request.setPrice(250.0); // 5 * 50

                    ReservationResponseDTO response = reservationService.createReservation(request);
                    
                    successCount.incrementAndGet();
                    successfulReservations.add(response);
                    System.out.println("   User " + userId + ": ✓ SUCCESS - Booked seats: " + response.getSeatNumbers());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                    errorMessages.add("User " + userId + ": Thread interrupted");
                } catch (IllegalStateException e) {
                    failureCount.incrementAndGet();
                    errorMessages.add("User " + userId + ": " + e.getMessage());
                    System.out.println("   User " + userId + ": ✗ FAILED - " + e.getMessage());
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    errorMessages.add("User " + userId + ": Unexpected error - " + e.getMessage());
                    System.out.println("   User " + userId + ": ✗ ERROR - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Step 4: Start all threads simultaneously
        Thread.sleep(100); // Give threads time to reach startLatch.await()
        startLatch.countDown(); // Release all threads at once
        
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Step 5: Verify results
        System.out.println("\n📊 Results:");
        System.out.println("   Successful bookings: " + successCount.get());
        System.out.println("   Failed bookings: " + failureCount.get());

        assertTrue(completed, "All threads should complete within timeout");
        assertEquals(1, successCount.get(), "Only 1 user should successfully book 5 seats");
        assertEquals(1, failureCount.get(), "1 user should fail due to insufficient seats");

        // Verify no duplicate seats were assigned
        if (!successfulReservations.isEmpty()) {
            Set<String> allSeats = successfulReservations.stream()
                    .flatMap(r -> r.getSeatNumbers().stream())
                    .collect(Collectors.toSet());
            
            long totalSeatsAssigned = successfulReservations.stream()
                    .mapToLong(r -> r.getSeatNumbers().size())
                    .sum();
            
            assertEquals(totalSeatsAssigned, allSeats.size(), 
                "No duplicate seats should be assigned across reservations");
            System.out.println("   ✓ No duplicate seats detected");
        }

        System.out.println("✓ Scenario 1 PASSED: System correctly handled race condition");
    }

    /**
     * Test Scenario 2: Last 1 Seat - 5 Concurrent Users
     * 
     * Setup: Book 39 seats first (leaving 1 available)
     * Test: 5 users simultaneously try to book 1 seat each
     * Expected: Only 1 user succeeds, 4 others fail
     */
    @Test
    @Order(2)
    public void testScenario2_LastOneSeat_FiveUsers() throws InterruptedException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO 2: Last 1 Seat - 5 Concurrent Users");
        System.out.println("-".repeat(80));

        // Step 1: Book 39 seats
        System.out.println("\n📋 Setup: Booking 39 seats (A → D) to leave 1 available...");
        ReservationRequestDTO setupRequest = new ReservationRequestDTO();
        setupRequest.setPassengers(39);
        setupRequest.setOrigin("A");
        setupRequest.setDestination("D");
        setupRequest.setPrice(5850.0); // 39 * 150

        ReservationResponseDTO setupResponse = reservationService.createReservation(setupRequest);
        assertNotNull(setupResponse);
        System.out.println("✓ Setup complete: 39 seats booked, 1 seat remaining");

        // Step 2: Prepare concurrent attempts
        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> successfulUsers = new CopyOnWriteArrayList<>();

        // Step 3: Launch threads
        System.out.println("\n🚀 Launching 5 concurrent users competing for last seat...");
        for (int i = 0; i < numThreads; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    ReservationRequestDTO request = new ReservationRequestDTO();
                    request.setPassengers(1);
                    request.setOrigin("A");
                    request.setDestination("D");
                    request.setPrice(150.0);

                    ReservationResponseDTO response = reservationService.createReservation(request);
                    
                    successCount.incrementAndGet();
                    successfulUsers.add("User " + userId + " - Seat: " + response.getSeatNumbers().get(0));
                    System.out.println("   User " + userId + ": ✓ WON - Got seat " + response.getSeatNumbers().get(0));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failureCount.incrementAndGet();
                    System.out.println("   User " + userId + ": ✗ LOST - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify
        System.out.println("\n📊 Results:");
        System.out.println("   Winner: " + (successfulUsers.isEmpty() ? "None" : successfulUsers.get(0)));
        System.out.println("   Losers: " + failureCount.get());

        assertTrue(completed, "All threads should complete");
        assertEquals(1, successCount.get(), "Exactly 1 user should win the last seat");
        assertEquals(4, failureCount.get(), "4 users should fail");
        
        System.out.println("✓ Scenario 2 PASSED: Only one user got the last seat");
    }

    /**
     * Test Scenario 3: 10 Concurrent Users with Random Booking Sizes
     * 
     * Test: 10 users simultaneously try to book random numbers of seats
     * Expected: No overselling, total booked ≤ 40 seats
     */
    @Test
    @Order(3)
    public void testScenario3_TenConcurrentUsers_RandomBookings() throws InterruptedException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO 3: 10 Concurrent Users - Random Booking Sizes");
        System.out.println("-".repeat(80));

        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<ReservationResponseDTO> allReservations = new CopyOnWriteArrayList<>();

        Random random = new Random(42); // Fixed seed for reproducibility
        int[] requestedSeats = new int[numThreads];

        System.out.println("\n🚀 Launching 10 concurrent users with random requests...");
        for (int i = 0; i < numThreads; i++) {
            requestedSeats[i] = random.nextInt(8) + 1; // 1-8 seats per user
            final int userId = i + 1;
            final int seatsToBook = requestedSeats[i];

            executor.submit(() -> {
                try {
                    startLatch.await();

                    ReservationRequestDTO request = new ReservationRequestDTO();
                    request.setPassengers(seatsToBook);
                    request.setOrigin("B");
                    request.setDestination("D");
                    request.setPrice(seatsToBook * 100.0); // B→D = 2 segments * 50

                    ReservationResponseDTO response = reservationService.createReservation(request);
                    
                    successCount.incrementAndGet();
                    allReservations.add(response);
                    System.out.println("   User " + userId + ": ✓ Booked " + seatsToBook + " seats");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failureCount.incrementAndGet();
                    System.out.println("   User " + userId + ": ✗ Failed to book " + seatsToBook + " seats - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = endLatch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        // Calculate total seats requested and booked
        int totalRequested = Arrays.stream(requestedSeats).sum();
        int totalBooked = allReservations.stream()
                .mapToInt(r -> r.getSeatNumbers().size())
                .sum();

        System.out.println("\n📊 Results:");
        System.out.println("   Total seats requested: " + totalRequested);
        System.out.println("   Total seats booked: " + totalBooked);
        System.out.println("   Successful bookings: " + successCount.get());
        System.out.println("   Failed bookings: " + failureCount.get());

        assertTrue(completed, "All threads should complete");
        assertTrue(totalBooked <= 40, "Total booked seats should not exceed 40");

        // Verify no duplicate seats
        Set<String> uniqueSeats = allReservations.stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .collect(Collectors.toSet());

        assertEquals(totalBooked, uniqueSeats.size(), 
            "No duplicate seats should be assigned (found " + (totalBooked - uniqueSeats.size()) + " duplicates)");
        System.out.println("   ✓ No duplicate seats detected");
        System.out.println("   ✓ No overselling occurred");
        
        System.out.println("✓ Scenario 3 PASSED: System handled concurrent bookings correctly");
    }

    @AfterAll
    public static void summary() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONCURRENT TESTING COMPLETE");
        System.out.println("=".repeat(80));
        System.out.println("✓ All race condition tests passed");
        System.out.println("✓ Thread-safe operations verified");
        System.out.println("✓ No overselling detected");
        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Test Scenario 4: Date-Based Concurrent Reservations
     * 
     * Setup: Test concurrent bookings on different dates
     * Test: Multiple users booking seats on the same date and different dates simultaneously
     * Expected: Each date should have independent seat availability
     */
    @Test
    @Order(4)
    public void testScenario4_DateBasedConcurrentReservations() throws InterruptedException {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO 4: Date-Based Concurrent Reservations");
        System.out.println("-".repeat(80));

        String date1 = "2026-02-15";
        String date2 = "2026-02-16";

        int numThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<ReservationResponseDTO> allReservations = new CopyOnWriteArrayList<>();

        System.out.println("\n🚀 Launching 4 concurrent users (2 for each date)...");
        
        // 2 users booking 10 seats each on date1, 2 users booking 10 seats each on date2
        String[] dates = {date1, date1, date2, date2};
        
        for (int i = 0; i < numThreads; i++) {
            final int userId = i + 1;
            final String bookingDate = dates[i];
            
            executor.submit(() -> {
                try {
                    startLatch.await();

                    System.out.println("   User " + userId + ": Attempting to book 10 seats for " + bookingDate);
                    
                    ReservationRequestDTO request = new ReservationRequestDTO();
                    request.setPassengers(10);
                    request.setOrigin("A");
                    request.setDestination("C");
                    request.setPrice(1000.0); // 10 * 100
                    request.setTravelDate(bookingDate);

                    ReservationResponseDTO response = reservationService.createReservation(request);
                    
                    successCount.incrementAndGet();
                    allReservations.add(response);
                    System.out.println("   User " + userId + ": ✓ SUCCESS on " + bookingDate);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("   User " + userId + ": ✗ FAILED - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        Thread.sleep(100);
        startLatch.countDown();
        
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("\n📊 Results:");
        System.out.println("   Successful bookings: " + successCount.get());
        System.out.println("   Failed bookings: " + failureCount.get());

        assertTrue(completed, "All threads should complete");
        assertEquals(4, successCount.get(), 
            "All 4 bookings should succeed (2 on each date with independent availability)");
        assertEquals(0, failureCount.get(), "No failures expected with different dates");

        System.out.println("   ✓ Date-based seat isolation working correctly");
        System.out.println("✓ Scenario 4 PASSED: Date-based concurrent reservations handled correctly");
    }
}
