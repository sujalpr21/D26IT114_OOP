import java.util.Objects;

class Fraction { 
    private int num; 
    private int den; 
    
    public Fraction(int num, int den) { 
        int g = gcd(Math.abs(num), Math.abs(den)); 
        this.num = num / g; 
        this.den = den / g; 
        if (this.den < 0) { 
            this.num = -this.num; 
            this.den = -this.den; 
        } 
    } 
    
    private int gcd(int a, int b) { 
        while (b != 0) { 
            int t = b; 
            b = a % b; 
            a = t; 
        } 
        return a; 
    } 
    
    @Override 
    public String toString() { 
        return num + "/" + den; 
    } 
    
    @Override 
    public boolean equals(Object obj) { 
        if (this == obj) return true; 
        if (!(obj instanceof Fraction)) return false; 
        Fraction f = (Fraction) obj; 
        return num == f.num && den == f.den; 
    } 
    
    @Override 
    public int hashCode() { 
        return Objects.hash(num, den); 
    } 
}

public class Practical3_3_Fractions { 
    public static void main(String[] args) { 
        Fraction f1 = new Fraction(1, 2); 
        Fraction f2 = new Fraction(2, 4); 
        Fraction f3 = new Fraction(3, 6); 
        
        System.out.println(f1); 
        System.out.println(f2); 
        System.out.println(f3); 
        System.out.println(f1.equals(f2)); 
        System.out.println(f2.equals(f3)); 
        System.out.println(f1.equals(f3)); 
    } 
}
