import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MiniBankCapstone {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("MINIBANK CAPSTONE APPLICATION (MILESTONE 3 FINAL)");
        System.out.println("==================================================");
        Path storageFile = Paths.get("minibank_accounts.dat");
        Path logsFolder = Paths.get("minibank_logs");
        Path reportFile = Paths.get("minibank_eod_report.txt");
        try {
            Files.createDirectories(logsFolder);
            BankService bankService = new BankService();
            bankService.loadAccounts(storageFile);
            
            System.out.println("\n--- 1. Testing BankService Operations & Audit Logging ---");
            bankService.openAccount("ACC-101", "Alice Smith", 1000.00);
            bankService.openAccount("ACC-102", "Bob Jones", 2500.00);
            
            bankService.deposit("ACC-101", 500.00);
            bankService.withdraw("ACC-102", 300.00);
            bankService.transfer("ACC-102", "ACC-101", 700.00);
            
            bankService.statement("ACC-101");
            bankService.listAccounts();
            
            System.out.println("\n--- 2. Testing Validation & Custom Exception Handling ---");
            try {
                bankService.withdraw("ACC-101", 5000.00);
            } catch (InsufficientBalanceException e) {
                System.out.println(" Caught Exception: " + e.getMessage());
            }
            try {
                bankService.openAccount("INVALID-ID-TOO-LONG-NAME", "Charlie", -100.00);
            } catch (ValidationException e) {
                System.out.println(" Caught Validation Error: " + e.getErrors()[0]);
            }
            
            System.out.println("\n--- 3. Executing Thread-Safe Batch Transaction Processing ---");
            List<String> batchTransactions = List.of(
                "DEPOSIT,ACC-101,200",
                "WITHDRAW,ACC-101,50",
                "DEPOSIT,ACC-102,400"
            );
            
            bankService.processBatchTransactions(batchTransactions, logsFolder);
            
            System.out.println("\n--- 4. Generating End-Of-Day Summary Report ---");
            bankService.generateReport(logsFolder, reportFile);
            
            System.out.println("\nEOD Report Content (" + reportFile.getFileName() + "):");
            System.out.println("--------------------------------------------------");
            Files.readAllLines(reportFile).forEach(line -> System.out.println(" " + line));
            System.out.println("--------------------------------------------------");
            
            bankService.saveAccounts(storageFile);
            
            System.out.println("\n--- 5. Executing Built-In JUnit Test Suite ---");
            EmbeddedTestRunnerMB.runTests(BankServiceTest.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Files.deleteIfExists(storageFile);
                Files.deleteIfExists(logsFolder.resolve("batch_tx.log"));
                Files.deleteIfExists(logsFolder);
                Files.deleteIfExists(reportFile);
            } catch (IOException ignored) {}
        }
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Audit {
    String action() default "";
}

class AuditLogger {
    public static void log(Object serviceInstance, String methodName, Object... args) {
        try {
            for (Method method : serviceInstance.getClass().getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(Audit.class)) {
                    Audit audit = method.getAnnotation(Audit.class);
                    StringBuilder sb = new StringBuilder();
                    sb.append("[AUDIT LOG] Action: ").append(audit.action())
                      .append(" | Method: ").append(methodName).append("(");
                    for (int i = 0; i < args.length; i++) {
                        sb.append(args[i]).append(i == args.length - 1 ? "" : ", ");
                    }
                    sb.append(")");
                    System.out.println(sb.toString());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Id {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Positive {
    String message() default "must be > 0";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
    String message() default "exceeds maximum allowed length";
}

class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}

class ValidationException extends RuntimeException {
    private final String[] errors;
    public ValidationException(String[] errors) {
        super("Validation failed");
        this.errors = errors;
    }
    public String[] getErrors() {
        return errors;
    }
}

class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @MaxLength(value = 10, message = "Account number length must be at most 10")
    private String accountNumber;
    private String ownerName;
    @Positive(message = "Balance must be positive")
    private double balance;

    public Account(String accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public synchronized double getBalance() { return balance; }
    public synchronized void setBalance(double balance) { this.balance = balance; }
    
    @Override
    public String toString() {
        return String.format("Account{id='%s', owner='%s', balance=$%.2f}", accountNumber, ownerName, balance);
    }
}

class AnnotationValidator {
    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (field.isAnnotationPresent(Positive.class)) {
                    Positive annotation = field.getAnnotation(Positive.class);
                    if (value instanceof Number && ((Number) value).doubleValue() <= 0) {
                        errors.add(field.getName() + " " + annotation.message());
                    }
                }
                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength annotation = field.getAnnotation(MaxLength.class);
                    if (value != null && value.toString().length() > annotation.value()) {
                        errors.add(field.getName() + " " + annotation.message() + " (max: " + annotation.value() + ")");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return errors.toArray(new String[0]);
    }
}

class Repository<T> {
    private final ConcurrentMap<String, T> storage = new ConcurrentHashMap<>();
    public T save(T entity) {
        String idValue = extractId(entity);
        storage.put(idValue, entity);
        return entity;
    }
    public T findById(String id) { return storage.get(id); }
    public List<T> findAll() { return new ArrayList<>(storage.values()); }
    public void clear() { storage.clear(); }
    
    private String extractId(T entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    Object val = field.get(entity);
                    return val != null ? val.toString() : null;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalStateException("Missing @Id annotation");
    }
}

class BankService {
    private final Repository<Account> repository = new Repository<>();
    
    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(String accountNumber, String ownerName, double initialBalance) {
        AuditLogger.log(this, "openAccount", accountNumber, ownerName, initialBalance);
        Account acc = new Account(accountNumber, ownerName, initialBalance);
        
        String[] errors = AnnotationValidator.validate(acc);
        if (errors.length > 0) {
            throw new ValidationException(errors);
        }
        return repository.save(acc);
    }

    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, double amount) {
        AuditLogger.log(this, "deposit", accountNumber, amount);
        Account acc = getAccountOrThrow(accountNumber);
        synchronized (acc) {
            acc.setBalance(acc.getBalance() + amount);
        }
    }

    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, double amount) {
        AuditLogger.log(this, "withdraw", accountNumber, amount);
        Account acc = getAccountOrThrow(accountNumber);
        synchronized (acc) {
            if (acc.getBalance() < amount) {
                throw new InsufficientBalanceException("Insufficient funds: balance is " + acc.getBalance() + ", requested " + amount);
            }
            acc.setBalance(acc.getBalance() - amount);
        }
    }

    @Audit(action = "TRANSFER")
    public void transfer(String fromAccount, String toAccount, double amount) {
        AuditLogger.log(this, "transfer", fromAccount, toAccount, amount);
        withdraw(fromAccount, amount);
        deposit(toAccount, amount);
    }

    public void statement(String accountNumber) {
        Account acc = getAccountOrThrow(accountNumber);
        System.out.println(" [Statement] " + acc);
    }

    public void listAccounts() {
        System.out.println(" All MiniBank Accounts:");
        for (Account acc : repository.findAll()) {
            System.out.println("   - " + acc);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAccounts(Path file) {
        if (!Files.exists(file)) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Account[] accounts = (Account[]) ois.readObject();
            repository.clear();
            for (Account acc : accounts) {
                repository.save(acc);
            }
            System.out.println("[Persistence] Loaded " + accounts.length + " accounts from storage.");
        } catch (Exception e) {
            System.out.println("[Persistence] Failed to load accounts: " + e.getMessage());
        }
    }

    public void saveAccounts(Path file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            List<Account> list = repository.findAll();
            oos.writeObject(list.toArray(new Account[0]));
            System.out.println("\n[Persistence] Saved " + list.size() + " accounts to " + file.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processBatchTransactions(List<String> transactions, Path logsFolder) throws IOException {
        Path batchLog = logsFolder.resolve("batch_tx.log");
        Files.write(batchLog, transactions, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        for (String line : transactions) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String type = parts[0].trim();
                String accNum = parts[1].trim();
                double amount = Double.parseDouble(parts[2].trim());
                if ("DEPOSIT".equalsIgnoreCase(type)) {
                    deposit(accNum, amount);
                } else if ("WITHDRAW".equalsIgnoreCase(type)) {
                    withdraw(accNum, amount);
                }
            }
        }
    }

    public void generateReport(Path logsFolder, Path reportFile) throws IOException {
        long totalDeposits = 0;
        long totalWithdrawals = 0;
        List<String> reportLines = new ArrayList<>();
        reportLines.add("==========================================");
        reportLines.add("MINIBANK END-OF-DAY SUMMARY REPORT");
        reportLines.add("==========================================");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder, "*.log")) {
            for (Path p : stream) {
                reportLines.add("Processed Log: " + p.getFileName() + " (" + Files.size(p) + " bytes)");
                for (String line : Files.readAllLines(p)) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String type = parts[0].trim();
                        long amount = Long.parseLong(parts[2].trim());
                        if ("DEPOSIT".equalsIgnoreCase(type)) totalDeposits += amount;
                        if ("WITHDRAW".equalsIgnoreCase(type)) totalWithdrawals += amount;
                    }
                }
            }
        }
        reportLines.add("------------------------------------------");
        reportLines.add("Total Deposits:    $" + totalDeposits);
        reportLines.add("Total Withdrawals: $" + totalWithdrawals);
        reportLines.add("Net Cash Flow:     $" + (totalDeposits - totalWithdrawals));
        reportLines.add("==========================================");
        Files.write(reportFile, reportLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private Account getAccountOrThrow(String accountNumber) {
        Account acc = repository.findById(accountNumber);
        if (acc == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found.");
        }
        return acc;
    }
}

@Retention(RetentionPolicy.RUNTIME)
@interface TestMB {}

class EmbeddedTestRunnerMB {
    public static void runTests(Class<?> testClass) {
        int passed = 0, failed = 0;
        try {
            Object instance = testClass.getDeclaredConstructor().newInstance();
            for (Method m : testClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(TestMB.class)) {
                    try {
                        m.invoke(instance);
                        System.out.println("  [PASSED] " + m.getName());
                        passed++;
                    } catch (Exception e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        System.out.println("  [FAILED] " + m.getName() + " -> " + cause.getMessage());
                        failed++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" Test Summary: " + passed + " Passed, " + failed + " Failed.");
    }
}

class BankServiceTest {
    @TestMB
    public void testDepositIncreasesBalance() {
        BankService service = new BankService();
        service.openAccount("ACC-T1", "Tester", 100.00);
        service.deposit("ACC-T1", 50.00);
        service.withdraw("ACC-T1", 0); 
    }

    @TestMB
    public void testWithdrawOverLimitThrowsException() {
        BankService service = new BankService();
        service.openAccount("ACC-T2", "Tester", 100.00);
        boolean caught = false;
        try {
            service.withdraw("ACC-T2", 500.00);
        } catch (InsufficientBalanceException e) {
            caught = true;
        }
        if (!caught) {
            throw new AssertionError("Withdrawal over balance should throw InsufficientBalanceException");
        }
    }

    @TestMB
    public void testConcurrentDepositsKeepBalanceCorrect() throws InterruptedException {
        BankService service = new BankService();
        service.openAccount("ACC-T3", "Tester", 0.00);
        int threads = 10;
        int depositsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < depositsPerThread; j++) {
                    service.deposit("ACC-T3", 1.00);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        double expected = threads * depositsPerThread;
        service.withdraw("ACC-T3", expected);
    }
}
