import java.util.List;
import java.util.Scanner;

abstract class Account {
    protected final String number;
    protected double balance;

    Account(String number, double balance) {
        this.number = number;
        this.balance = balance;
    }

    abstract String getAccountType();
    abstract double interestRate();
    abstract boolean withdraw(double amount);

    void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.printf("[%s] Deposited $%.2f. New Balance: $%.2f%n", number, amount, balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Balance: $%.2f | Interest: %.1f%%",
                number, getAccountType(), balance, interestRate() * 100);
    }
}

class SavingsAccount extends Account {
    private final double minimumBalance;

    SavingsAccount(String n, double b, double min) {
        super(n, b);
        this.minimumBalance = min;
    }

    @Override
    String getAccountType() { return "Savings"; }

    @Override
    double interestRate() { return 0.04; }

    @Override
    boolean withdraw(double amount) {
        if (amount <= 0 || balance - amount < minimumBalance) {
            System.out.printf("[%s] Withdrawal of $%.2f rejected! Minimum balance of $%.2f must be maintained.%n",
                    number, amount, minimumBalance);
            return false;
        }
        balance -= amount;
        System.out.printf("[%s] Withdrawn $%.2f. New Balance: $%.2f%n", number, amount, balance);
        return true;
    }
}

class CurrentAccount extends Account {
    private final double overdraftLimit;

    CurrentAccount(String n, double b, double limit) {
        super(n, b);
        this.overdraftLimit = limit;
    }

    @Override
    String getAccountType() { return "Current"; }

    @Override
    double interestRate() { return 0.0; }

    @Override
    boolean withdraw(double amount) {
        if (amount <= 0 || balance - amount < -overdraftLimit) {
            System.out.printf("[%s] Withdrawal of $%.2f rejected! Exceeds overdraft limit of $%.2f.%n",
                    number, amount, overdraftLimit);
            return false;
        }
        balance -= amount;
        System.out.printf("[%s] Withdrawn $%.2f (Overdraft allowed). New Balance: $%.2f%n", number, amount, balance);
        return true;
    }
}

class FixedDepositAccount extends Account {
    FixedDepositAccount(String n, double b) {
        super(n, b);
    }

    @Override
    String getAccountType() { return "Fixed Deposit"; }

    @Override
    double interestRate() { return 0.07; }

    @Override
    boolean withdraw(double amount) {
        System.out.printf("[%s] Withdrawal of $%.2f rejected! Withdrawals are locked for Fixed Deposit accounts until maturity.%n",
                number, amount);
        return false;
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
        System.out.println("=== Practical 05: Inheritance & Polymorphism (Account Hierarchy) ===");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\nSelect Account Type to Test:");
            System.out.println("1: Savings Account (4% interest, requires minimum balance)");
            System.out.println("2: Current Account (0% interest, allows overdraft)");
            System.out.println("3: Fixed Deposit (7% interest, premature withdrawal locked)");
            String typeChoice = prompt(scanner, "Enter selection (1-3)", "1");

            Account account;
            switch (typeChoice) {
                case "2" -> {
                    String num = prompt(scanner, "Enter Account Number", "CUR501");
                    double bal = Double.parseDouble(prompt(scanner, "Enter Initial Balance", "500.0"));
                    double limit = Double.parseDouble(prompt(scanner, "Enter Overdraft Limit", "200.0"));
                    account = new CurrentAccount(num, bal, limit);
                }
                case "3" -> {
                    String num = prompt(scanner, "Enter Account Number", "FD501");
                    double bal = Double.parseDouble(prompt(scanner, "Enter Deposit Principal", "5000.0"));
                    account = new FixedDepositAccount(num, bal);
                }
                default -> {
                    String num = prompt(scanner, "Enter Account Number", "SAV501");
                    double bal = Double.parseDouble(prompt(scanner, "Enter Initial Balance", "1000.0"));
                    double min = Double.parseDouble(prompt(scanner, "Enter Minimum Balance", "100.0"));
                    account = new SavingsAccount(num, bal, min);
                }
            }

            System.out.println("\nAccount Created: " + account);

            double depAmount = Double.parseDouble(prompt(scanner, "\nEnter amount to deposit", "150.0"));
            account.deposit(depAmount);

            double withAmount = Double.parseDouble(prompt(scanner, "Enter amount to withdraw", "300.0"));
            account.withdraw(withAmount);

            System.out.println("\nFinal Account State: " + account);

            System.out.println("\n--- Polymorphism Demonstration with All Account Types ---");
            List<Account> sampleAccounts = List.of(
                    new SavingsAccount("MB501", 1000, 100),
                    new CurrentAccount("MB502", 100, 500),
                    new FixedDepositAccount("MB503", 2000)
            );
            for (Account acc : sampleAccounts) {
                System.out.println("Polymorphic call: " + acc);
            }
        }
    }
}
