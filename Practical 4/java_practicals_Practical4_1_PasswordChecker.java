class PasswordChecker {
    public static boolean checkLength(String pw) { 
        return pw.length() >= 8;
    }

    public static boolean checkUppercase(String pw) { 
        return pw.matches(".*[A-Z].*");
    }

    public static boolean checkDigit(String pw) { 
        return pw.matches(".*[0-9].*");
    }

    public static boolean checkSpecial(String pw) {
        return pw.matches(".*[^a-zA-Z0-9].*");
    }

    public static String strength(String pw) { 
        int count = 0;
        if (checkLength(pw)) count++;
        if (checkUppercase(pw)) count++;
        if (checkDigit(pw)) count++;
        if (checkSpecial(pw)) count++;
        if (count <= 1) return "Weak";
        else if (count <= 3) return "Medium";
        else return "Strong";
    }
}

public class Practical4_1_PasswordChecker {
    public static void main(String[] args) {
        String[] passwords = { 
            "abc", "abcdefghi", "Abcdefgh", "Abcd1234", "Abcd1234!"
        };
        for (String pw : passwords) {
            System.out.println("Password: " + pw);
            System.out.println("Length >= 8: " + PasswordChecker.checkLength(pw));
            System.out.println("Uppercase letter: " + PasswordChecker.checkUppercase(pw));
            System.out.println("Digit: " + PasswordChecker.checkDigit(pw));
            System.out.println("Special character: " + PasswordChecker.checkSpecial(pw));
            System.out.println("Strength: " + PasswordChecker.strength(pw));
            System.out.println();
        }
    }
}
