import java.util.Arrays; 
 
public class ParallelArraySum { 
    private static final int ARRAY_SIZE = 50_000_000; 
    private static final int THREAD_COUNT = 4; 
    private static final int[] NUMBERS = new int[ARRAY_SIZE]; 
 
    static { 
        Arrays.fill(NUMBERS, 1); 
    } 
 
    static class UnsafeSum { 
        private long total = 0; 
        public void add(long val) { total += val; } 
        public long getTotal() { return total; } 
    } 
 
    static class SynchronizedSum { 
        private long total = 0; 
        public synchronized void add(long val) { total += val; } 
        public synchronized long getTotal() { return total; } 
    } 
 
    static class LocalSumWorker extends Thread { 
        private final int startIndex; 
        private final int endIndex; 
        private long localTotal = 0; 
 
        public LocalSumWorker(int startIndex, int endIndex) { 
            this.startIndex = startIndex; 
            this.endIndex = endIndex; 
        } 
 
        @Override 
        public void run() { 
            for (int i = startIndex; i < endIndex; i++) { 
                localTotal += NUMBERS[i]; 
            } 
        } 
 
        public long getLocalTotal() { return localTotal; } 
    }

    public static void main(String[] args) throws InterruptedException { 
        long expectedTotal = (long) ARRAY_SIZE; 
        int chunkSize = ARRAY_SIZE / THREAD_COUNT; 
 
        System.out.println("Processing " + ARRAY_SIZE + " elements across " + THREAD_COUNT + " threads...\n"); 
 
        UnsafeSum unsafeSum = new UnsafeSum(); 
        Thread[] threadsUnsafe = new Thread[THREAD_COUNT]; 
        for (int i = 0; i < THREAD_COUNT; i++) { 
            final int start = i * chunkSize; 
            final int end = (i == THREAD_COUNT - 1) ? ARRAY_SIZE : start + chunkSize; 
            threadsUnsafe[i] = new Thread(() -> { 
                for (int j = start; j < end; j++) { 
                    unsafeSum.add(NUMBERS[j]); 
                } 
            }); 
        } 
        long startTime = System.nanoTime(); 
        for (Thread t : threadsUnsafe) t.start(); 
        for (Thread t : threadsUnsafe) t.join(); 
        long unsafeDuration = (System.nanoTime() - startTime) / 1_000_000; 
 
        System.out.println("=== 1. UNSYNCHRONIZED RUN ==="); 
        System.out.println("Total Result : " + unsafeSum.getTotal() + " (Expected: " + expectedTotal + ")"); 
        System.out.println("Time Spent   : " + unsafeDuration + " ms\n"); 
 
        SynchronizedSum syncSum = new SynchronizedSum(); 
        Thread[] threadsSync = new Thread[THREAD_COUNT]; 
        for (int i = 0; i < THREAD_COUNT; i++) { 
            final int start = i * chunkSize; 
            final int end = (i == THREAD_COUNT - 1) ? ARRAY_SIZE : start + chunkSize; 
            threadsSync[i] = new Thread(() -> { 
                for (int j = start; j < end; j++) { 
                    syncSum.add(NUMBERS[j]); 
                } 
            }); 
        } 
        startTime = System.nanoTime(); 
        for (Thread t : threadsSync) t.start(); 
        for (Thread t : threadsSync) t.join(); 
        long syncDuration = (System.nanoTime() - startTime) / 1_000_000; 
 
        System.out.println("=== 2. FIX 1: SYNCHRONIZED ACCUMULATOR ==="); 
        System.out.println("Total Result : " + syncSum.getTotal() + " (Expected: " + expectedTotal + ")"); 
        System.out.println("Time Spent   : " + syncDuration + " ms\n"); 
 
        LocalSumWorker[] workers = new LocalSumWorker[THREAD_COUNT]; 
        for (int i = 0; i < THREAD_COUNT; i++) { 
            int start = i * chunkSize; 
            int end = (i == THREAD_COUNT - 1) ? ARRAY_SIZE : start + chunkSize; 
            workers[i] = new LocalSumWorker(start, end);
        } 
        startTime = System.nanoTime(); 
        for (LocalSumWorker w : workers) w.start(); 
        long localFinalTotal = 0; 
        for (LocalSumWorker w : workers) { 
            w.join(); 
            localFinalTotal += w.getLocalTotal(); 
        } 
        long localDuration = (System.nanoTime() - startTime) / 1_000_000; 
 
        System.out.println("=== 3. FIX 2: LOCAL AGGREGATION (LOCAL REDUCTION) ==="); 
        System.out.println("Total Result : " + localFinalTotal + " (Expected: " + expectedTotal + ")"); 
        System.out.println("Time Spent   : " + localDuration + " ms\n"); 
    } 
}
