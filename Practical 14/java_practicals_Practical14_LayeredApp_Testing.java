import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Practical14_LayeredApp_Testing {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("PART A: PRACTICE PROGRAMS (LAB-NN)");
        System.out.println("==========================================");
        System.out.println("\n--- Program 1: Layered App Interactive Menu Demo ---");
        TaskService service = new TaskService();
        System.out.println("1. Adding initial tasks via TaskService...");
        service.addTask("Review Java Code");
        service.addTask("Write Unit Tests");
        System.out.println("Current Task List (Count: " + service.getTaskCount() + "):");
        for (Task t : service.getAllTasks()) {
            System.out.println(" - " + t);
        }
        System.out.println("\n2. Simulating invalid user action...");
        try {
            service.addTask(""); 
        } catch (IllegalArgumentException e) {
            System.out.println(" Caught expected exception: " + e.getMessage());
        }
        
        System.out.println("\n--- Program 1: Executing JUnit Test Suite ---");
        EmbeddedTestRunner14.runTests(TaskServiceTest.class);
        System.out.println("\n--- Program 2: Save & Reload Data Test ---");
        EmbeddedTestRunner14.runTests(TaskServicePersistenceTest.class);
    }
}

class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String description;
    public Task(int id, String description) {
        this.id = id;
        this.description = description;
    }
    public int getId() { return id; }
    public String getDescription() { return description; }
    @Override
    public String toString() {
        return "Task{id=" + id + ", description='" + description + "'}";
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id && (description != null ? description.equals(task.description) : task.description == null);
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(id) + (description != null ? description.hashCode() : 0);
    }
}

class TaskService implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;
    public Task addTask(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        Task newTask = new Task(nextId++, description.trim());
        tasks.add(newTask);
        return newTask;
    }
    public int getTaskCount() { return tasks.size(); }
    public List<Task> getAllTasks() { return new ArrayList<>(tasks); }
}

@Retention(RetentionPolicy.RUNTIME)
@interface Test {}

class EmbeddedTestRunner14 {
    public static void runTests(Class<?> testClass) {
        int passed = 0;
        int failed = 0;
        try {
            Object testInstance = testClass.getDeclaredConstructor().newInstance();
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    try {
                        method.invoke(testInstance);
                        System.out.println("  [PASSED] " + method.getName());
                        passed++;
                    } catch (Exception e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        System.out.println("  [FAILED] " + method.getName() + " -> " + cause.getMessage());
                        failed++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" Test Summary for " + testClass.getSimpleName() + ": " + passed + " Passed, " + failed + " Failed");
    }
    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
    }
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}

class TaskServiceTest {
    @Test
    public void testAddTaskIncreasesCount() {
        TaskService service = new TaskService();
        EmbeddedTestRunner14.assertEquals(0, service.getTaskCount(), "Initial count should be 0");
        service.addTask("Build prototype");
        EmbeddedTestRunner14.assertEquals(1, service.getTaskCount(), "Count should increase to 1 after adding task");
        service.addTask("Run tests");
        EmbeddedTestRunner14.assertEquals(2, service.getTaskCount(), "Count should increase to 2 after adding second task");
    }
    @Test
    public void testInvalidTaskThrowsException() {
        TaskService service = new TaskService();
        boolean exceptionThrown = false;
        try {
            service.addTask("");
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        EmbeddedTestRunner14.assertTrue(exceptionThrown, "Adding empty task description should throw IllegalArgumentException");
    }
}

class TaskServicePersistenceTest {
    @Test
    public void testSaveAndReloadDataUnchanged() {
        String testFile = "tasks_test.dat";
        TaskService originalService = new TaskService();
        originalService.addTask("Task 1 - Persistent");
        originalService.addTask("Task 2 - Persistent");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(testFile))) {
            oos.writeObject(originalService);
        } catch (IOException e) {
            throw new RuntimeException("Save failed", e);
        }
        TaskService reloadedService;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(testFile))) {
            reloadedService = (TaskService) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Reload failed", e);
        } finally {
            new java.io.File(testFile).delete();
        }
        EmbeddedTestRunner14.assertEquals(originalService.getTaskCount(), reloadedService.getTaskCount(), "Task counts must match after reloading");
        EmbeddedTestRunner14.assertEquals(originalService.getAllTasks(), reloadedService.getAllTasks(), "Tasks list content must remain unchanged");
    }
}
