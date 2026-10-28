import java.util.Scanner;

class Customer {
    private final String name;
    private final String email;
    private final String mobile;

    Customer(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    String getName() { return name; }
    String getEmail() { return email; }
    String getMobile() { return mobile; }

    @Override
    public String toString() {
        return name + " (" + email + ", " + mobile + ")";
    }
}

class Account {
    private final String accountNumber;
    private final Customer owner;
    private double balance;

    Account(String number, Customer owner, double openingBalance) {
        this.accountNumber = number;
        this.owner = owner;
        this.balance = openingBalance;
    }

    Account(String number, String name, String email, String mobile) {
        this(number, new Customer(name, email, mobile), 0.0);
    }

    void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.printf("Successfully deposited $%.2f. New Balance: $%.2f%n", amount, balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return false;
        }
        if (amount > balance) {
            System.out.printf("Withdrawal failed! Insufficient balance ($%.2f) for withdrawal of $%.2f%n", balance, amount);
            return false;
        }
        balance -= amount;
        System.out.printf("Successfully withdrew $%.2f. Remaining Balance: $%.2f%n", amount, balance);
        return true;
    }

    double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return String.format("Account[%s] Owner: %s | Balance: $%.2f", accountNumber, owner, balance);
    }
}

public class MiniBank {
    private static String prompt(Scanner scanner, String message, String defaultValue) {
        System.out.print(message + " (default '" + defaultValue + "'): ");
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            return line.isEmpty() ? defaultValue : line;
        }
        System.out.println(defaultValue);
        return defaultValue;
    }

    public static void main(String[] args) {
        System.out.println("=== Practical 02: Classes, Constructors & Account Operations ===");

        try (Scanner scanner = new Scanner(System.in)) {
            String accNum = prompt(scanner, "Enter Account Number", "MB101");
            String name = prompt(scanner, "Enter Customer Name", "Asha");
            String email = prompt(scanner, "Enter Email", "asha@example.com");
            String mobile = prompt(scanner, "Enter Mobile Number", "9876543210");
            String balStr = prompt(scanner, "Enter Opening Balance", "500.0");

            double openingBal = 500.0;
            try {
                openingBal = Double.parseDouble(balStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid format, using default 500.0.");
            }

            Customer customer = new Customer(name, email, mobile);
            Account account = new Account(accNum, customer, openingBal);
            System.out.println("\nAccount Created:");
            System.out.println(account);

            String depStr = prompt(scanner, "\nEnter amount to deposit", "200.0");
            try {
                double depAmount = Double.parseDouble(depStr);
                account.deposit(depAmount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid deposit amount.");
            }
            
            String withStr = prompt(scanner, "Enter amount to withdraw", "150.0");
            try {
                double withAmount = Double.parseDouble(withStr);
                account.withdraw(withAmount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid withdrawal amount.");
            }

            System.out.println("\nFinal Account Summary:");
            System.out.println(account);
        }
    }
}
