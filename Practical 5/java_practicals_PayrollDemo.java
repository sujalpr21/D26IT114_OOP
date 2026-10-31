abstract class Employee { 
    String name; 
    int id; 
 
    Employee(String name, int id) { 
        this.name = name; 
        this.id = id; 
    }
    abstract double monthlySalary(); 
} 
 
class FullTime extends Employee { 
    double salary; 
 
    FullTime(String name, int id, double salary) { 
        super(name, id); 
        this.salary = salary; 
    } 
 
    double monthlySalary() { 
        return salary; 
    } 
} 
 
class PartTime extends Employee { 
    double hours, rate; 
 
    PartTime(String name, int id, double hours, double rate) { 
        super(name, id); 
        this.hours = hours; 
        this.rate = rate; 
    } 
 
    double monthlySalary() { 
        return hours * rate; 
    } 
} 
 
class Intern extends Employee { 
    double stipend;
    Intern(String name, int id, double stipend) { 
        super(name, id); 
        this.stipend = stipend; 
    } 
 
    double monthlySalary() { 
        return stipend; 
    } 
} 

public class PayrollDemo { 
    public static void main(String[] args) { 
        Employee[] employees = { 
            new FullTime("Alice", 101, 50000), 
            new PartTime("Bob", 102, 80, 300), 
            new Intern("Charlie", 103, 15000), 
            new FullTime("David", 104, 60000) 
        }; 
 
        double total = 0; 
 
        for (Employee e : employees) { 
            double salary = e.monthlySalary(); 
            total += salary; 
 
            System.out.printf("%s (ID: %d): %.2f", e.name, e.id, salary); 
 
            if (e instanceof Intern) { 
                System.out.print(" - Intern"); 
            } 
 
            System.out.println(); 
        }
        System.out.printf("Total Payroll: %.2f%n", total); 
    } 
}
