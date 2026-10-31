import java.util.HashMap; 
import java.util.Map; 
 
class OutOfStockException extends Exception { 
    private final int shortfall; 
 
    public OutOfStockException(String item, int requested, int available) { 
        super("Insufficient stock for '" + item + "'. Requested: " + requested + ", Available: " + available); 
        this.shortfall = requested - available; 
    } 
 
    public int getShortfall() { 
        return shortfall; 
    } 
} 
 
class InvalidQuantityException extends Exception { 
    public InvalidQuantityException(int qty) { 
        super("Invalid request quantity (" + qty + "). Quantity must be greater than 0."); 
    } 
} 
 
class Warehouse { 
    private final Map<String, Integer> inventory = new HashMap<>(); 
 
    public Warehouse() { 
        inventory.put("Widget", 15); 
        inventory.put("Gadget", 5); 
        inventory.put("Sensor", 0); 
    } 
 
    public void issue(String item, int qty) throws OutOfStockException, InvalidQuantityException { 
        if (qty <= 0) { 
            throw new InvalidQuantityException(qty); 
        }
        int available = inventory.getOrDefault(item, 0); 
        if (qty > available) { 
            throw new OutOfStockException(item, qty, available); 
        } 
 
        inventory.put(item, available - qty); 
        System.out.println("[SUCCESS] Issued " + qty + " units of '" + item + "'. Remaining: " + (available - qty)); 
    } 
} 
 
public class StockIssueDemo { 
    static class IssueRequest { 
        String item; 
        int qty; 
 
        IssueRequest(String item, int qty) { 
            this.item = item; 
            this.qty = qty; 
        } 
    } 
 
    public static void main(String[] args) { 
        Warehouse warehouse = new Warehouse(); 
 
        IssueRequest[] batchRequests = { 
            new IssueRequest("Widget", 5),    
            new IssueRequest("Gadget", -2),   
            new IssueRequest("Gadget", 10),   
            new IssueRequest("Sensor", 1),    
            new IssueRequest("Widget", 8)     
        }; 
 
        System.out.println("=== PROCESSING WAREHOUSE STOCK REQUESTS ===\n"); 
 
        for (int i = 0; i < batchRequests.length; i++) { 
            IssueRequest req = batchRequests[i]; 
            System.out.println("Request #" + (i + 1) + ": Item=" + req.item + ", Qty=" + req.qty); 
 
            try { 
                warehouse.issue(req.item, req.qty); 
            } catch (InvalidQuantityException e) { 
                System.out.println("  [REPORT - INVALID QTY] " + e.getMessage()); 
            } catch (OutOfStockException e) { 
                System.out.println("  [REPORT - OUT OF STOCK] " + e.getMessage()); 
                System.out.println("  --> Shortfall to fulfill order: " + e.getShortfall() + " units."); 
            } 
            System.out.println("--------------------------------------------------"); 
        } 
 
        System.out.println("=== Processing Complete. All non-failing orders fulfilled. ==="); 
    } 
}
