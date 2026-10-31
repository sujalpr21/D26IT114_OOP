public class SeatBookingRace { 
    static class UnsafeBookingSystem { 
        private int seatsLeft = 5; 
        private int successfulBookings = 0; 
 
        public boolean book(String passengerName) { 
            if (seatsLeft > 0) { 
                try { Thread.sleep(10); } catch (InterruptedException ignored) {} 
                seatsLeft--; 
                successfulBookings++; 
                System.out.println("[" + passengerName + "] BOOKED. Seats remaining: " + seatsLeft); 
                return true; 
            } else { 
                System.out.println("[" + passengerName + "] FAILED. Sold out!"); 
                return false; 
            } 
        } 
 
        public int getSuccessfulBookings() { return successfulBookings; } 
    } 
 
    static class SafeBookingSystem { 
        private int seatsLeft = 5; 
        private int successfulBookings = 0; 
 
        public synchronized boolean book(String passengerName) { 
            if (seatsLeft > 0) { 
                try { Thread.sleep(10); } catch (InterruptedException ignored) {} 
                seatsLeft--; 
                successfulBookings++; 
                System.out.println("[" + passengerName + "] BOOKED. Seats remaining: " + seatsLeft); 
                return true; 
            } else { 
                System.out.println("[" + passengerName + "] FAILED. Sold out!"); 
                return false; 
            } 
        } 
 
        public synchronized int getSuccessfulBookings() { return successfulBookings; } 
    } 
 
    public static void main(String[] args) throws InterruptedException { 
        System.out.println("=== 1. UNSYNCHRONIZED BOOKING (Oversell expected) ==="); 
        UnsafeBookingSystem unsafeSystem = new UnsafeBookingSystem(); 
        Thread[] threads1 = new Thread[10]; 
        for (int i = 0; i < 10; i++) { 
            String name = "Passenger-" + (i + 1); 
            threads1[i] = new Thread(() -> unsafeSystem.book(name)); 
            threads1[i].start(); 
        } 
        for (Thread t : threads1) t.join(); 
        System.out.println("Total Successful Bookings: " + unsafeSystem.getSuccessfulBookings() + " / 5 seats\n"); 
 
        System.out.println("=== 2. SYNCHRONIZED BOOKING (Exact 5 expected) ==="); 
        SafeBookingSystem safeSystem = new SafeBookingSystem(); 
        Thread[] threads2 = new Thread[10]; 
        for (int i = 0; i < 10; i++) { 
            String name = "Passenger-" + (i + 1); 
            threads2[i] = new Thread(() -> safeSystem.book(name)); 
            threads2[i].start(); 
        } 
        for (Thread t : threads2) t.join(); 
        System.out.println("Total Successful Bookings: " + safeSystem.getSuccessfulBookings() + " / 5 seats"); 
    } 
}
