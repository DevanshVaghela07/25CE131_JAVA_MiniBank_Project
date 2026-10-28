import java.util.Scanner;

public class MiniBank {
    record BankInfo(String name, String branch) {}
    enum MenuOption { OPEN_ACCOUNT, DEPOSIT, WITHDRAW, CHECK_BALANCE, EXIT }

    public static void main(String[] args) {
        BankInfo bank = new BankInfo("MiniBank", "Main Branch");
        System.out.println("=== " + bank.name() + " (" + bank.branch() + ") - Practical 01 ===");

        String accountHolder = "Guest";
        double balance = 0.0;
        boolean hasAccount = false;

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\n--- Menu ---");
                System.out.println("1 Open account | 2 Deposit | 3 Withdraw | 4 Check Balance | 0 Exit");
                System.out.print("Enter choice (0-4): ");
                
                int choice;
                try {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;
                    choice = Integer.parseInt(line);
                } catch (NumberFormatException exception) {
                    System.out.println("Invalid choice. Please enter a number.");
                    continue;
                }

                MenuOption option = switch (choice) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.CHECK_BALANCE;
                    case 0 -> MenuOption.EXIT;
                    default -> null;
                };

                if (option == null) {
                    System.out.println("Invalid option. Please choose between 0 and 4.");
                    continue;
                }

                switch (option) {
                    case OPEN_ACCOUNT -> {
                        System.out.print("Enter account holder name: ");
                        accountHolder = scanner.nextLine().trim();
                        if (accountHolder.isEmpty()) accountHolder = "Customer";
                        System.out.print("Enter opening deposit amount: ");
                        try {
                            double initial = Double.parseDouble(scanner.nextLine().trim());
                            if (initial >= 0) {
                                balance = initial;
                                hasAccount = true;
                                System.out.printf("Account created successfully for %s with balance $%.2f%n", accountHolder, balance);
                            } else {
                                System.out.println("Opening balance cannot be negative.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount entered. Account creation aborted.");
                        }
                    }
                    case DEPOSIT -> {
                        if (!hasAccount) {
                            System.out.println("No account found! Please open an account first (Option 1).");
                            break;
                        }
                        System.out.print("Enter deposit amount: ");
                        try {
                            double amount = Double.parseDouble(scanner.nextLine().trim());
                            if (amount > 0) {
                                balance += amount;
                                System.out.printf("Deposited $%.2f. New Balance: $%.2f%n", amount, balance);
                            } else {
                                System.out.println("Deposit amount must be positive.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount.");
                        }
                    }
                    case WITHDRAW -> {
                        if (!hasAccount) {
                            System.out.println("No account found! Please open an account first (Option 1).");
                            break;
                        }
                        System.out.print("Enter withdrawal amount: ");
                        try {
                            double amount = Double.parseDouble(scanner.nextLine().trim());
                            if (amount <= 0) {
                                System.out.println("Withdrawal amount must be positive.");
                            } else if (amount > balance) {
                                System.out.printf("Insufficient funds! Current balance: $%.2f%n", balance);
                            } else {
                                balance -= amount;
                                System.out.printf("Withdrawn $%.2f. Remaining Balance: $%.2f%n", amount, balance);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount.");
                        }
                    }
                    case CHECK_BALANCE -> {
                        if (!hasAccount) {
                            System.out.println("No account open yet. Please open an account first.");
                        } else {
                            System.out.printf("Account Holder: %s | Balance: $%.2f%n", accountHolder, balance);
                        }
                    }
                    case EXIT -> {
                        System.out.println("Thank you for using MiniBank! Goodbye.");
                        running = false;
                    }
                }
            }
        }
    }
}
