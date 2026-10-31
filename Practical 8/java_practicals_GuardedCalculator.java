import java.util.Scanner;

class DivideByZeroException extends Exception { 
    public DivideByZeroException(String message) { 
        super(message); 
    } 
} 
 
public class GuardedCalculator { 
    private static int attemptCounter = 0; 
 
    public static double calculate(double num1, double num2, char op) throws DivideByZeroException { 
        switch (op) { 
            case '+': return num1 + num2; 
            case '-': return num1 - num2; 
            case '*': return num1 * num2; 
            case '/': 
                if (num2 == 0) { 
                    throw new DivideByZeroException("Error: Cannot divide " + num1 + " by zero."); 
                } 
                return num1 / num2; 
            default: 
                throw new IllegalArgumentException("Error: Invalid operator '" + op + "'. Use +, -, *, or /."); 
        } 
    } 
 
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); 
        boolean success = false; 
 
        System.out.println("=== GUARDED CALCULATOR ==="); 
 
        while (!success) { 
            attemptCounter++; 
            try { 
                System.out.print("\nEnter first number: "); 
                String input1 = scanner.nextLine(); 
                double num1 = Double.parseDouble(input1); 
 
                System.out.print("Enter operator (+, -, *, /): "); 
                String opInput = scanner.nextLine(); 
                if (opInput.isEmpty()) { 
                    throw new IllegalArgumentException("Error: Operator cannot be empty."); 
                } 
                char op = opInput.charAt(0); 
 
                System.out.print("Enter second number: "); 
                String input2 = scanner.nextLine(); 
                double num2 = Double.parseDouble(input2); 
 
                double result = calculate(num1, num2, op); 
                System.out.println("\nSUCCESS: " + num1 + " " + op + " " + num2 + " = " + result); 
                success = true; 
 
            } catch (NumberFormatException e) { 
                System.out.println("CATCH: Invalid number format. Please enter numeric digits only."); 
            } catch (DivideByZeroException e) { 
                System.out.println("CATCH (Custom Exception): " + e.getMessage()); 
            } catch (IllegalArgumentException e) { 
                System.out.println("CATCH: " + e.getMessage()); 
            } finally { 
                System.out.println("FINALLY: Attempt #" + attemptCounter + " logged."); 
            } 
        } 
 
        scanner.close(); 
        System.out.println("=== Program Terminated Successfully ==="); 
    } 
}
