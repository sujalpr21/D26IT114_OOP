public class DeadlockFixDemo { 
    static class Account { 
        private final int id; 
        private int balance; 
 
        public Account(int id, int balance) { 
            this.id = id; 
            this.balance = balance; 
        }
        public int getId() { return id; } 
    } 
 
    public static void unsafeTransfer(Account from, Account to, int amount) { 
        synchronized (from) { 
            System.out.println(Thread.currentThread().getName() + " locked Account " + from.getId()); 
            try { Thread.sleep(50); } catch (InterruptedException ignored) {} 
 
            System.out.println(Thread.currentThread().getName() + " waiting to lock Account " + to.getId()); 
            synchronized (to) { 
                from.balance -= amount; 
                to.balance += amount; 
                System.out.println(Thread.currentThread().getName() + " transferred " + amount); 
            } 
        } 
    } 
 
    public static void safeTransfer(Account acc1, Account acc2, int amount, boolean isAcc1ToAcc2) { 
        Account firstLock = acc1.getId() < acc2.getId() ? acc1 : acc2; 
        Account secondLock = acc1.getId() < acc2.getId() ? acc2 : acc1; 
 
        synchronized (firstLock) { 
            System.out.println(Thread.currentThread().getName() + " locked Account " + firstLock.getId()); 
            try { Thread.sleep(50); } catch (InterruptedException ignored) {} 
 
            synchronized (secondLock) { 
                System.out.println(Thread.currentThread().getName() + " locked Account " + secondLock.getId()); 
                if (isAcc1ToAcc2) { 
                    acc1.balance -= amount; 
                    acc2.balance += amount; 
                } else { 
                    acc2.balance -= amount; 
                    acc1.balance += amount; 
                } 
                System.out.println("  " + Thread.currentThread().getName() + " successfully completed transfer!"); 
            } 
        } 
    } 
 
    public static void main(String[] args) throws InterruptedException { 
        Account accA = new Account(101, 1000); 
        Account accB = new Account(102, 1000); 
 
        System.out.println("=== PART 1: Demonstrating Deadlock (Will freeze without timeout guard) ==="); 
        Thread t1 = new Thread(() -> unsafeTransfer(accA, accB, 100), "Thread-1"); 
        Thread t2 = new Thread(() -> unsafeTransfer(accB, accA, 200), "Thread-2"); 
 
        t1.start(); 
        t2.start();
        t1.join(1000); 
        t2.join(1000); 
 
        if (t1.isAlive() && t2.isAlive()) { 
            System.out.println("\n[!] DEADLOCK DETECTED! Both threads are blocked waiting for each other."); 
            t1.interrupt(); 
            t2.interrupt(); 
        } 
 
        System.out.println("\n=== PART 2: Fixed Version with Lock-Ordering Rule ==="); 
        Thread t3 = new Thread(() -> safeTransfer(accA, accB, 100, true), "Thread-3"); 
        Thread t4 = new Thread(() -> safeTransfer(accA, accB, 200, false), "Thread-4"); 
 
        t3.start(); 
        t4.start(); 
 
        t3.join(); 
        t4.join(); 
 
        System.out.println("\n=== Process completed cleanly without deadlock ==="); 
    } 
}
