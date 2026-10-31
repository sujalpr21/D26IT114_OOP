import java.util.Scanner;

// Enum for Vending Machine
enum Coin {
    ONE, TWO, FIVE, TEN
}

// Record for Toll Booth
record Vehicle(String number, String type) { }

// Record for MiniBank
record BankInfo(String name, String branch) { }

// Enum for MiniBank Menu
enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    EXIT
}

public class Practical1_VendingMachine_TollBooth_MiniBank {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // ===============================
        // Program 1 : Vending Machine
        // ===============================
        System.out.println("===== VENDING MACHINE =====");
        int price = 15;
        int total = 0;
        while (total < price) {
            System.out.print("Enter Coin (ONE/TWO/FIVE/TEN): ");
            Coin coin = Coin.valueOf(sc.next().toUpperCase());
            int value = switch (coin) {
                case ONE -> 1;
                case TWO -> 2;
                case FIVE -> 5;
                case TEN -> 10;
            };
            total += value;
            System.out.println("Total Paid: " + total);
        }
        System.out.println("Paid. Change: " + (total - price));

        // ===============================
        // Program 2 : Toll Booth
        // ===============================
        System.out.println("\n===== TOLL BOOTH =====");
        int totalToll = 0;
        int bike = 0, car = 0, truck = 0;
        while (true) {
            System.out.print("Enter Vehicle Number (done to stop): ");
            String number = sc.next();
            if (number.equalsIgnoreCase("done"))
                break;
            System.out.print("Enter Vehicle Type (bike/car/truck): ");
            String type = sc.next().toLowerCase();
            Vehicle v = new Vehicle(number, type);
            int toll = switch (v.type()) {
                case "bike" -> {
                    bike++;
                    yield 20;
                }
                case "car" -> {
                    car++;
                    yield 50;
                }
                case "truck" -> {
                    truck++;
                    yield 150;
                }
                default -> {
                    System.out.println("Invalid Type");
                    yield 0;
                }
            };
            totalToll += toll;
        }
        System.out.println("Total Toll: " + totalToll);
        String most;
        if (bike >= car && bike >= truck)
            most = "bike";
        else if (car >= bike && car >= truck)
            most = "car";
        else
            most = "truck";
        System.out.println("Most Frequent: " + most);

        // ===============================
        // Part B : MiniBank
        // ===============================
        System.out.println("\n===== MINIBANK =====");
        BankInfo bank = new BankInfo("ABC Bank", "Ahmedabad");
        System.out.println(bank);
        while (true) {
            System.out.println("\n1. OPEN ACCOUNT");
            System.out.println("2. DEPOSIT");
            System.out.println("3. WITHDRAW");
            System.out.println("4. TRANSFER");
            System.out.println("5. EXIT");
            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();
            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.EXIT;
                default -> null;
            };
            if (option == null) {
                System.out.println("Invalid Choice");
                continue;
            }
            switch (option) {
                case OPEN_ACCOUNT ->
                        System.out.println("Open Account - To be implemented later.");
                case DEPOSIT ->
                        System.out.println("Deposit - To be implemented later.");
                case WITHDRAW ->
                        System.out.println("Withdraw - To be implemented later.");
                case TRANSFER ->
                        System.out.println("Transfer - To be implemented later.");
                case EXIT -> {
                    System.out.println("Thank you for using MiniBank!");
                    sc.close();
                    return;
                }
            }
        }
    }
}
