import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Practical7_Annotations {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("PART A: PRACTICE PROGRAMS (LAB-NN)");
        System.out.println("==================================================");
        System.out.println("\n--- Program 1: Form Validator ---");
        SignupForm invalidForm = new SignupForm("", "superlongusername123");
        
        System.out.println("Validating SignupForm with invalid values...");
        List<String> formErrors = FormValidator.validate(invalidForm);
        System.out.println("Validation Errors Found (" + formErrors.size() + "):");
        for (String error : formErrors) {
            System.out.println(" - " + error);
        }
        
        System.out.println("\n--- Program 2: Mini Test Runner ---");
        MiniTestRunner.runTests(TestSuite.class);
        System.out.println("\n--- Program 3: CSV Column Mapper ---");
        String[] headers = {"user_id", "full_name"};
        String[] rowData = {"USR-101", "Alice Smith"};
        
        System.out.println("Headers:  [user_id, full_name]");
        System.out.println("Data Row: [USR-101, Alice Smith]");
        
        UserProfile user = ColumnMapper.map(UserProfile.class, headers, rowData);
        System.out.println("\nMapped UserProfile Object:");
        System.out.println(" " + user);
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {
    String message() default "Field cannot be blank";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
    String message() default "Field exceeds maximum length";
}

class SignupForm {
    @NotBlank(message = "Username is required")
    @MaxLength(value = 10, message = "Username must not exceed 10 characters")
    private String username;
    @NotBlank(message = "Email is required")
    private String email;

    public SignupForm(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

class FormValidator {
    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (field.isAnnotationPresent(NotBlank.class)) {
                    NotBlank annotation = field.getAnnotation(NotBlank.class);
                    if (value == null || value.toString().trim().isEmpty()) {
                        errors.add(field.getName() + ": " + annotation.message());
                    }
                }
                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength annotation = field.getAnnotation(MaxLength.class);
                    if (value != null && value.toString().length() > annotation.value()) {
                        errors.add(field.getName() + ": " + annotation.message() + " (max: " + annotation.value() + ")");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return errors;
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Run {}

class TestSuite {
    @Run
    public void testOne() {
        System.out.println("  -> [PASSED] Executed testOne()");
    }
    public void helperMethod() {
        System.out.println("  -> Executed helperMethod() (Should not run)");
    }
    @Run
    public void testTwo() {
        System.out.println("  -> [PASSED] Executed testTwo()");
    }
}

class MiniTestRunner {
    public static void runTests(Class<?> clazz) {
        int runCount = 0;
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Run.class)) {
                    method.invoke(instance);
                    runCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total @Run methods executed: " + runCount);
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Column {
    String name();
}

class UserProfile {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "age")
    private String age; 

    @Override
    public String toString() {
        return "UserProfile{userId='" + userId + "', fullName='" + fullName + "', age='" + age + "'}";
    }
}

class ColumnMapper {
    public static <T> T map(Class<T> clazz, String[] headers, String[] rowData) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column col = field.getAnnotation(Column.class);
                    String columnName = col.name();
                    int columnIndex = -1;
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].equalsIgnoreCase(columnName)) {
                            columnIndex = i;
                            break;
                        }
                    }
                    field.setAccessible(true);
                    if (columnIndex != -1 && columnIndex < rowData.length) {
                        field.set(instance, rowData[columnIndex]);
                    } else {
                        System.out.println("[Warning] Column '" + columnName + "' not found in headers. Field '" + field.getName() + "' set to null.");
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map column data to object", e);
        }
    }
}
