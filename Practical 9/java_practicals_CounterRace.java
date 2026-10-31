public class CounterRace { 
    private static final int THREAD_COUNT = 10; 
    private static final int INCREMENTS_PER_THREAD = 10_000; 
    
    static class UnsafeCounter { 
        private int count = 0; 
        public void increment() { count++; } 
        public int getCount() { return count; } 
    } 
    
    static class SafeCounter { 
        private int count = 0; 
        public synchronized void increment() { count++; } 
        public synchronized int getCount() { return count; } 
    } 
 
    public static void main(String[] args) throws InterruptedException { 
        int expectedTotal = THREAD_COUNT * INCREMENTS_PER_THREAD; 
        UnsafeCounter unsafeCounter = new UnsafeCounter(); 
        Thread[] unsafeThreads = new Thread[THREAD_COUNT]; 
        for (int i = 0; i < THREAD_COUNT; i++) { 
            unsafeThreads[i] = new Thread(() -> { 
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) { 
                    unsafeCounter.increment(); 
                } 
            }); 
            unsafeThreads[i].start(); 
        } 
        for (Thread t : unsafeThreads) t.join(); 
 
        System.out.println("=== UNSYNCHRONIZED RUN ==="); 
        System.out.println("Expected total : " + expectedTotal); 
        System.out.println("Actual total   : " + unsafeCounter.getCount()); 
        System.out.println("Result         : " + (unsafeCounter.getCount() == expectedTotal ? "PASSED" : "FAILED (Race Condition Detected)")); 
 
        SafeCounter safeCounter = new SafeCounter(); 
        Thread[] safeThreads = new Thread[THREAD_COUNT]; 
        for (int i = 0; i < THREAD_COUNT; i++) { 
            safeThreads[i] = new Thread(() -> { 
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) { 
                    safeCounter.increment(); 
                } 
            }); 
            safeThreads[i].start(); 
        } 
        for (Thread t : safeThreads) t.join(); 
 
        System.out.println("\n=== SYNCHRONIZED RUN ==="); 
        System.out.println("Expected total : " + expectedTotal); 
        System.out.println("Actual total   : " + safeCounter.getCount()); 
        System.out.println("Result         : " + (safeCounter.getCount() == expectedTotal ? "PASSED (Exact Match)" : "FAILED")); 
    } 
}
