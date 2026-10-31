import java.util.LinkedList; 
import java.util.Queue; 
 
public class ProducerConsumer { 
    static class BoundedBuffer { 
        private final Queue<Integer> buffer = new LinkedList<>(); 
        private final int capacity;

        public BoundedBuffer(int capacity) { 
            this.capacity = capacity; 
        } 
 
        public synchronized void produce(int item) throws InterruptedException { 
            while (buffer.size() == capacity) { 
                System.out.println("Buffer FULL. Producer is waiting..."); 
                wait(); 
            } 
            buffer.add(item); 
            System.out.println("PRODUCED: " + item + " | Buffer size: " + buffer.size()); 
            notifyAll(); 
        } 
 
        public synchronized int consume() throws InterruptedException { 
            while (buffer.isEmpty()) { 
                System.out.println("Buffer EMPTY. Consumer is waiting..."); 
                wait(); 
            } 
            int item = buffer.remove(); 
            System.out.println("  CONSUMED: " + item + " | Buffer size: " + buffer.size()); 
            notifyAll(); 
            return item; 
        } 
    } 
 
    public static void main(String[] args) throws InterruptedException { 
        int bufferCapacity = 3; 
        int totalItems = 10; 
        BoundedBuffer buffer = new BoundedBuffer(bufferCapacity); 
 
        Thread producer = new Thread(() -> { 
            try { 
                for (int i = 1; i <= totalItems; i++) { 
                    buffer.produce(i); 
                    Thread.sleep(50); 
                } 
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
            } 
        }, "Producer"); 
 
        Thread consumer = new Thread(() -> { 
            try { 
                for (int i = 1; i <= totalItems; i++) { 
                    buffer.consume(); 
                    Thread.sleep(120); 
                } 
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
            } 
        }, "Consumer");

        System.out.println("=== Starting Producer-Consumer (Capacity: " + bufferCapacity + ", Total: " + totalItems + ") ===\n"); 
        producer.start(); 
        consumer.start(); 
 
        producer.join(); 
        consumer.join(); 
        System.out.println("\n=== Completed: All " + totalItems + " items produced and consumed in order ==="); 
    } 
}
