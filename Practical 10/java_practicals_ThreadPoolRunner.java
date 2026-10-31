import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 
import java.util.concurrent.TimeUnit; 
 
public class ThreadPoolRunner { 
    public static void main(String[] args) { 
        int totalTasks = 10; 
        int poolSize = 3; 
 
        ExecutorService executor = Executors.newFixedThreadPool(poolSize); 
 
        System.out.println("=== Submitting " + totalTasks + " tasks to pool of size " + poolSize + " ==="); 
 
        for (int i = 1; i <= totalTasks; i++) { 
            final int taskId = i; 
            executor.submit(() -> { 
                String threadName = Thread.currentThread().getName(); 
                System.out.println("Task " + taskId + " STARTED by " + threadName); 
                try { 
                    Thread.sleep(150); 
                } catch (InterruptedException e) { 
                    Thread.currentThread().interrupt(); 
                } 
                System.out.println("  Task " + taskId + " FINISHED by " + threadName); 
            }); 
        } 
 
        executor.shutdown(); 
        System.out.println("Executor shut down initiated. Awaiting task completion...\n");
        try { 
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) { 
                executor.shutdownNow(); 
            } 
        } catch (InterruptedException e) { 
            executor.shutdownNow(); 
        } 
 
        System.out.println("=== All tasks completed successfully ==="); 
    } 
}
