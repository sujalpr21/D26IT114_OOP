class DatabaseConnection implements AutoCloseable { 
    private final String connectionId; 
    private boolean isOpen; 
 
    public DatabaseConnection(String connectionId) { 
        this.connectionId = connectionId; 
        this.isOpen = true; 
        System.out.println("[RESOURCE] Connection " + connectionId + " OPENED."); 
    } 
 
    public void executeQuery(String query) throws Exception { 
        if (!isOpen) { 
            throw new IllegalStateException("Connection is closed."); 
        } 
        if (query.contains("FAIL")) { 
            throw new RuntimeException("Primary Error: Query execution failed due to syntax error."); 
        } 
        System.out.println("[RESOURCE] Query executed successfully: " + query); 
    } 
 
    @Override 
    public void close() { 
        this.isOpen = false; 
        System.out.println("[AUTO-CLOSE] Connection " + connectionId + " CLOSED automatically."); 
    } 
}

public class TryWithResourcesDemo { 
    public static void main(String[] args) { 
        System.out.println("=== TEST 1: Normal Execution (No Exception) ==="); 
        try (DatabaseConnection db = new DatabaseConnection("DB-POOL-01")) { 
            db.executeQuery("SELECT * FROM users;"); 
        } catch (Exception e) { 
            System.out.println("Caught Exception: " + e.getMessage()); 
        } 
 
        System.out.println("\n=== TEST 2: Exception Thrown Inside Try Block ==="); 
        try (DatabaseConnection db = new DatabaseConnection("DB-POOL-02")) { 
            db.executeQuery("SELECT * FROM non_existing_table WHERE FAIL;"); 
            System.out.println("This line will not be reached."); 
        } catch (Exception e) { 
            System.out.println("[OUTSIDE CATCH] Caught Reported Exception: " + e.getClass().getSimpleName()); 
            System.out.println("[OUTSIDE CATCH] Message: " + e.getMessage()); 
        } 
    } 
}
